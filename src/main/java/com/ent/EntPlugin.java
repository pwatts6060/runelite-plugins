package com.ent;

import com.ent.discord.DiscordWebhook;
import com.ent.discord.DiscordWebhookBody;
import com.google.inject.Provides;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Ent Trimmer"
)
public class EntPlugin extends Plugin
{
	private static final int TRIM_ANIM = 10505;
	private static final int STUN_ANIM = 848;
	private static final int ENT_FINISH_ANIM = 10540;
	private static final String TIME_TO_GO_MSG = "Almost time to go!";
	private static final String PERFECT_MSG = "My cut is perfect, leave it alone!";
	private static final int TTG_DESPAWN_TIME = 45; // 45 ticks
	private static final Pattern pattern = Pattern.compile("Well done, you've given \\d entlings haircuts!");
	private static final String LOGGED_OFF_ENT_MSG = "As you were not present when the entlings appeared you are not eligible for rewards.";

	public EntStats entStats = new EntStats();
	private double lastEntUniqueId;
	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Inject

	private Client client;
	@Inject
	private EntConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private EntOverlay3D overlay3D;

	@Inject
	private EntOverlay2D overlay2D;

	@Inject
	private Hooks hooks;

	@Inject
	private DiscordWebhook discordWebhook;

	private boolean shouldDraw(Renderable renderable, boolean drawingUI) {
		if (!(renderable instanceof NPC))
		{
			return true;
		}
		NPC npc = (NPC) renderable;
		Ent ent = entStats.ents.get(npc);
		if (ent == null) {
			return true;
		}

		int highestPriority = 0;
		for (Ent e : entStats.ents.values()) {
			if (e.perfect)
				continue;
			if (e.trimType != null && e.trimType.priority > highestPriority) {
				highestPriority = e.trimType.priority;
			}
		}

		return !hideEnt(ent, highestPriority);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay3D);
		overlayManager.add(overlay2D);
		hooks.registerRenderableDrawListener(drawListener);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay3D);
		overlayManager.remove(overlay2D);
		hooks.unregisterRenderableDrawListener(drawListener);
		entStats.clear();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged e)
	{
		GameState gameState = e.getGameState();
		if (gameState == GameState.LOGIN_SCREEN || gameState == GameState.CONNECTION_LOST || gameState == GameState.HOPPING)
		{
			entStats.clear();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned e)
	{
		NPC npc = e.getNpc();
		if (npc.getId() == NpcID.ENTLING || npc.getId() == NpcID.PRUNED_ENTLING)
		{
			entStats.add(npc);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned e)
	{
		if (entStats.ents.isEmpty())
		{
			return;
		}
		entStats.remove(e.getNpc());
	}

	@Subscribe
	public void onNpcChanged(NpcChanged e)
	{
		if (entStats.ents.isEmpty())
		{
			return;
		}

		if (e.getNpc().getId() == NpcID.PRUNED_ENTLING && entStats.ents.containsKey(e.getNpc()))
		{
			entStats.ents.get(e.getNpc()).perfect = true;
		}
	}

	@Subscribe
	public void onGameTick(GameTick e)
	{
		if (entStats.ticksAlive >= 0)
		{
			entStats.ticksAlive++;
		}
		if (entStats.despawnTime > 0)
		{
			entStats.despawnTime--;
		}
	}

	@Subscribe
	public void onOverheadTextChanged(OverheadTextChanged e)
	{
		if (entStats.ents.isEmpty() || !(e.getActor() instanceof NPC))
		{
			return;
		}

		NPC npc = (NPC) e.getActor();
		Ent ent = entStats.ents.get(npc);
		if (ent == null)
		{
			return;
		}

		String text = Text.removeTags(e.getOverheadText());
		if (text.equals(TIME_TO_GO_MSG))
		{
			entStats.ttg = true;
			entStats.despawnTime = TTG_DESPAWN_TIME;
		}
		else if (text.equals(PERFECT_MSG))
		{
			ent.perfect = true;
		}
		else if (ent.trimType == null)
		{
			for (TrimType trimType : TrimType.values)
			{
				if (text.equals(trimType.overheadText))
				{
					ent.trimType = trimType;
				}
			}
		}
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged e)
	{
		if (entStats.ents.isEmpty())
		{
			return;
		}
		if (e.getSource() instanceof Player && e.getTarget() instanceof NPC)
		{
			NPC target = (NPC) e.getTarget();
			if (target.getId() != NpcID.ENTLING)
			{
				return;
			}

			Ent ent = entStats.ents.get(target);
			if (ent == null)
			{
				return;
			}

			entStats.playerEntMap.put((Player) e.getSource(), ent);
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged e)
	{
		if (entStats.ents.isEmpty())
		{
			return;
		}

		Actor a = e.getActor();

		if (a instanceof NPC) {
			onNpcAnim(a);
		} else if (a instanceof Player) {
			onPlayerAnim(a);
		}
	}

	private void onPlayerAnim(Actor a)
	{
		if (a.getInteracting() != null && a.getInteracting() instanceof Player)
		{
			return;
		}

		Ent ent;
		if (a.getInteracting() == null)
		{
			ent = entStats.playerEntMap.get((Player) a);
		}
		else
		{
			NPC npc = (NPC) a.getInteracting();
			ent = entStats.ents.get(npc);
		}

		if (ent == null)
		{
			return;
		}

		if (a.getAnimation() == TRIM_ANIM)
		{
			ent.trims++;
			if (a == client.getLocalPlayer()) {
				entStats.eligible = true;
			}
		}
		else if (a.getAnimation() == STUN_ANIM && a == client.getLocalPlayer())
		{
			entStats.eligible = true;
		}
	}

	private void onNpcAnim(Actor a)
	{
		NPC npc = (NPC) a;
		if (npc.getId() != NpcID.PRUNED_ENTLING && npc.getId() != NpcID.ENTLING)
		{
			return;
		}

		if (npc.getAnimation() != ENT_FINISH_ANIM) {
			return;
		}

		Ent ent = entStats.ents.get(npc);
		if (ent == null) {
			return;
		}

		discordEntStats(entStats.getPerfectCutCount());
	}

	@Subscribe
	public void onChatMessage(ChatMessage e) {
		if (!e.getType().equals(ChatMessageType.GAMEMESSAGE) || entStats.ents.isEmpty()) {
			return;
		}

		String msg = Text.removeTags(e.getMessage());

		Matcher matcher = pattern.matcher(msg);
		if (matcher.find()) {
			int count = Integer.parseInt(matcher.group());
			discordEntStats(count);
		} else if (msg.equalsIgnoreCase(LOGGED_OFF_ENT_MSG)) {
			discordEntStats(entStats.getPerfectCutCount());
		}
	}

	private void discordEntStats(int count)
	{
		if (config.discordWebhookUrl().isEmpty() || lastEntUniqueId == entStats.uniqueId)
		{
			return;
		}
		StringBuffer sb = new StringBuffer();

		// UTC time
		// Get current UTC time
		LocalTime utcTime = LocalTime.now(ZoneOffset.UTC).minusSeconds(entStats.ticksAlive * 6 / 10).truncatedTo(ChronoUnit.SECONDS); // Truncate to seconds to remove fractional seconds
		// Define the desired format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		// Format the time as "HH:mm:ss"
		String formattedTime = utcTime.format(formatter);
		// Output the formatted UTC time
		sb.append(formattedTime + "\n");

		// total cut
		sb.append(count + "/" + entStats.ents.size() + "\n");

		// times
		sb.append("Ticks seen: " + entStats.ticksAlive + "\n");
		sb.append(entStats.ttg ? ("TTG reached - Ticks until despawn: " + entStats.despawnTime + "\n") : "TTG/despawn time unknown\n");

		// average ent position
		int x = 0;
		int y = 0;
		for (Ent e : entStats.ents.values()) {
			x += e.npc.getWorldLocation().getX();
			y += e.npc.getWorldLocation().getY();
		}
		x /= entStats.ents.size();
		y /= entStats.ents.size();
		String locUrl = "https://explv.github.io/?centreX="+x+"&centreY="+y+"&centreZ=0&zoom=11";
		sb.append(locUrl).append("\n");

		// Cut types and counts
		for (Ent e : entStats.ents.values()) {
			sb.append(e.trimType == null ? "n/a" : e.trimType.shortText).append(" ")
				.append(e.trims).append(" ")
				.append(e.perfect ? "Yes" : "No").append("\n");
		}

		lastEntUniqueId = entStats.uniqueId;
		DiscordWebhookBody discordWebhookBody = new DiscordWebhookBody();
		discordWebhookBody.setContent(sb.toString());
		discordWebhook.sendWebhook(discordWebhookBody, config.includeScreenshot());
	}

	@Provides
	EntConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EntConfig.class);
	}

	public boolean hideEnt(Ent ent, int highestPriority)
	{
		if (entStats.eligible) {
			// don't hide any ents until player has cut or stalled on at least one
			if (ent.perfect && config.hidePerfect()) {
				return true;
			}
			if (config.hideNonPriority() && (ent.perfect || ent.trimType != null && ent.trimType.priority != highestPriority)) {
				return true;
			}
		}
		return false;
	}
}
