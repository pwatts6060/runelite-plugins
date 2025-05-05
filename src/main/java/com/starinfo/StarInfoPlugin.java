/*
 * Copyright (c) 2022, Cute Rock
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.starinfo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.AnimationID;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import static net.runelite.api.ItemID.*;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.NullNpcID;
import net.runelite.api.Player;
import net.runelite.api.PlayerComposition;
import net.runelite.api.Renderable;
import net.runelite.api.Skill;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WorldType;
import net.runelite.api.coords.Angle;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.kit.KitType;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ColorUtil;

@PluginDescriptor(
	name = "Star Info",
	description = "Displays tier, number of miners, health % above shooting stars"
)
public class StarInfoPlugin extends Plugin
{
	private static final int VARBIT_STAR_DISCOVERY = 15351; //  Star discovery buff: Returns the amount of bonus stardust the player will receive from stars.
	private static final int NPC_ID = NullNpcID.NULL_10629;
	private static final int MAX_PLAYER_LOAD_DIST = 13;
	private static final Queue<Star> despawnQueue = new LinkedList<>();

	private static final Set<Integer> dragonPickSpecAnims = ImmutableSet.of(
		7138, // Dragon pickaxe
		334, // Dragon pickaxe (upgraded)
		8781, // Dragon pickaxe (or) (Trailblazer) / Infernal pickaxe (or)
		8330, // Dragon pickaxe (or)
		8329, // Crystal pickaxe
		3410 // infernal pickaxe
		// 3rd age pickaxe
	);

	private static final Map<Integer, Double> pickAnims = ImmutableMap.<Integer, Double>builder().
		put(AnimationID.MINING_CRASHEDSTAR_ADAMANT, 3.0).
		put(AnimationID.MINING_TRAILBLAZER_PICKAXE, 17.0 / 6).
		put(AnimationID.MINING_CRASHEDSTAR_BLACK, 5.0).
		put(AnimationID.MINING_CRASHEDSTAR_BRONZE, 8.0).
		put(AnimationID.MINING_CRASHEDSTAR_GILDED, 3.0).
		put(AnimationID.MINING_CRASHEDSTAR_CRYSTAL, 2.75).
		put(AnimationID.MINING_CRASHEDSTAR_3A, 17.0 / 6).
		put(AnimationID.MINING_CRASHEDSTAR_DRAGON, 17.0 / 6).
		put(AnimationID.MINING_CRASHEDSTAR_DRAGON_OR, 17.0 / 6).
		put(AnimationID.MINING_CRASHEDSTAR_DRAGON_OR_TRAILBLAZER, 17.0 / 6).
		put(AnimationID.MINING_CRASHEDSTAR_DRAGON_UPGRADED, 17.0 / 6).
		put(AnimationID.MINING_CRASHEDSTAR_INFERNAL, 17.0 / 6).
		put(AnimationID.MINING_CRASHEDSTAR_MITHRIL, 5.0).
		put(AnimationID.MINING_CRASHEDSTAR_IRON, 7.0).
		put(AnimationID.MINING_CRASHEDSTAR_RUNE, 3.0).
		put(AnimationID.MINING_CRASHEDSTAR_STEEL, 6.0).
		put(AnimationID.MINING_TRAILBLAZER_PICKAXE_3, 17.0 / 6).
		put(7138, 17.0 / 6).
		put(334, 17.0 / 6).
		put(8781, 17.0 / 6).
		put(8330, 17.0 / 6).
		put(3410, 17.0 / 6).
		put(8329, 2.75).
		build();

	private static final int MINING_CACHE_TIME = 13; // count player as a miner if they have done mining anim within this many ticks ago
	private static final Map<String, Integer> playerLastMined = new HashMap<>();

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Inject
	private StarInfoOverlay starOverlay;

	public final List<Star> stars = new ArrayList<>();

	@Getter
	private double xpPerHour = -1;

	@Getter
	private double dustPerHour = -1;

	private boolean hintArrowShown = false;

	public int layerTimer = 0;

	public int bonusCount;

	private int lastStarTime;

	@Inject
	private InfoBoxManager infoBoxManager;

	private StarInfoBox infoBox;
	
	private BonusCounter bonusCounter;

	private BufferedImage BONUS_IMAGE;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	Client client;

	@Inject
	ClientThread clientThread;

	@Inject
	private StarInfoConfig starConfig;

	@Inject
	private WorldInfo worldInfo;

	@Inject
	private Hooks hooks;

	@Inject
	private Notifier notifier;

	@Provides
	StarInfoConfig
	provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StarInfoConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(starOverlay);
		starOverlay.updateConfig();
		hooks.registerRenderableDrawListener(drawListener);
		BONUS_IMAGE = itemManager.getImage(STARDUST, 175, false);
		lastStarTime = -101 * starConfig.discoveryHideTime(); // make the star discovery hidden on first login

		clientThread.invoke(() ->
		{
			if (client.getGameState() != GameState.LOGGED_IN)
			{
				return;
			}

			bonusCount = client.getVarbitValue(VARBIT_STAR_DISCOVERY);
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		clear();
		refresh();
		overlayManager.remove(starOverlay);
		infoBox = null;
		removeBonusCounter();
		hooks.unregisterRenderableDrawListener(drawListener);
	}

	private void clear()
	{
		playerLastMined.clear();
		stars.clear();
		layerTimer = 0;
	}

	private boolean shouldDraw(Renderable renderable, boolean b)
	{
		if (!(renderable instanceof NPC))
		{
			return true;
		}

		NPC npc = (NPC) renderable;
		return npc.getId() != NPC_ID;
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (event.getNpc().getId() != NPC_ID)
		{
			return;
		}
		for (Star star : stars)
		{
			if (star.getWorldPoint().equals(event.getNpc().getWorldLocation()))
			{
				star.setNpc(event.getNpc());
				refresh();
				return;
			}
		}
		Star star = new Star(event.getNpc(), client.getWorld());
		worldInfo.update(star);
		stars.add(0, star);
		refresh();
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() != VARBIT_STAR_DISCOVERY)
		{
			return;
		}

		bonusCount = event.getValue();

		if (!starConfig.showStarDiscovery() || bonusCount == 0)
		{
			removeBonusCounter();
			return;
		}

		updateBonusCounter();
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (event.getNpc().getId() != NPC_ID)
		{
			return;
		}
		for (Star star : stars)
		{
			if (star.getWorldPoint().equals(event.getNpc().getWorldLocation()))
			{
				star.setNpc(null);
				refresh();
				return;
			}
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		int tier = Star.getTier(event.getGameObject().getId());
		if (tier < 0)
		{
			return;
		}

		boolean newStar = false;
		Star star = null;
		for (Star s : stars)
		{
			if (s.getWorldPoint().equals(event.getGameObject().getWorldLocation()))
			{
				s.setObject(event.getGameObject());
				s.resetHealth();
				star = s;
				layerTimer = 0;
				despawnQueue.remove(star);
				break;
			}
		}
		if (star == null)
		{
			star = new Star(event.getGameObject(), client.getWorld());
			worldInfo.update(star);
			stars.add(0, star);
			newStar = true;

			if (starConfig.showStarDiscovery())
			{
				updateBonusCounter();
			}
		}

		if (newStar && starConfig.addToChat())
		{
			client.addChatMessage(ChatMessageType.CONSOLE, "", star.getMessage(), "");
		}
		refresh();
	}

	private boolean nextToStar(Player p, Star star)
	{
		WorldArea areaH = new WorldArea(star.getWorldPoint().dx(-1), 4, 2);
		WorldArea areaV = new WorldArea(star.getWorldPoint().dy(-1), 2, 4);
		return p.getWorldLocation().isInArea2D(areaH, areaV);
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		int tier = Star.getTier(event.getGameObject().getId());
		if (tier < 0)
		{
			return;
		}

		for (Star star : stars)
		{
			if (event.getGameObject().equals(event.getGameObject()) || event.getGameObject().getWorldLocation().equals(star.getWorldPoint()))
			{
				despawnQueue.add(star);
				if (nextToStar(client.getLocalPlayer(), star)) {
					notifier.notify(starConfig.notifyTierChange(), "Star layer mined (Shooting Star Info)");
				}
				break;
			}
		}
	}

	void updateMiners(Star star)
	{
		int distToStar = client.getLocalPlayer().getWorldLocation().distanceTo(new WorldArea(star.getWorldPoint(), 2, 2));
		if (distToStar > MAX_PLAYER_LOAD_DIST)
		{
			star.setMiners(Star.UNKNOWN_MINERS);
			return;
		}
		WorldArea areaH = new WorldArea(star.getWorldPoint().dx(-1), 4, 2);
		WorldArea areaV = new WorldArea(star.getWorldPoint().dy(-1), 2, 4);
		int count = 0;
		int tickCount = client.getTickCount();
//		List<PlayerInfo> miners = new ArrayList<>();
		for (Player p : client.getPlayers())
		{
			if (!p.getWorldLocation().isInArea2D(areaH, areaV)) // Skip players not next to the star
			{
				continue;
			}
			if (!facingObject(p.getWorldLocation(), p.getOrientation(), star.getWorldPoint()))
			{
				continue;
			}
			if (pickAnims.containsKey(p.getAnimation())) // count anyone that is doing mining animation
			{
				count++;
				playerLastMined.put(p.getName(), tickCount);
//				miners.add(new PlayerInfo(p.getName()));
				continue;
			}
			if (p.getHealthRatio() < 0 || !playerLastMined.containsKey(p.getName()))
			{
				continue;
			}
			int ticksSinceMinedLast = tickCount - playerLastMined.get(p.getName());
			if (ticksSinceMinedLast < MINING_CACHE_TIME)
			{
				count++;
//				miners.add(new PlayerInfo(p.getName()));
			}
		}
		if (starConfig.addT0Estimate() || starConfig.estimateDeathTime() != EstimateConfig.NONE || starConfig.estimateLayerTime() != EstimateConfig.NONE)
		{
			refreshEstimate(star);
		}
		layerTimer += 1;
		star.setMiners(Integer.toString(count));
	}

	public void refreshEstimate(Star star)
	{
		int[] ticks = getTicksEstimates(star);
		star.setTierTicksEstimate(ticks);
	}

	private int[] getTicksEstimates(Star star)
	{
		if (star.getHealth() < 0) {
			return null;
		}
		int startTier = star.getTier();
		if (startTier < 0) {
			return null;
		}
		int tier = startTier;
		int[] ticks = new int[startTier];
		int totalTicks = 0;
		while (tier > 0) {
			TierData tierData = TierData.get(tier);
			if (tierData == null) {
				return null;
			}
			double healthScale = tier == startTier ? (star.getHealth() / 100.0) : 1;

			totalTicks += (int) Math.ceil(healthScale * tierData.tickTime);
			tier--;
			ticks[tier] = totalTicks;
		}
		return ticks;
	}

	private double getPickTicks(Player p)
	{
		int animId = p.getAnimation();
		if (animId == AnimationID.MINING_CRYSTAL_PICKAXE) {
			// animation id is shared for active/inactive so need to look at equipment
			// note that they may not be wielding the pickaxe at all, in which case we can't know for sure
			int weaponId = p.getPlayerComposition().getEquipmentId(KitType.WEAPON);
			if (weaponId == ItemID.CRYSTAL_PICKAXE_INACTIVE) {
				return 17.0 / 6;
			}
		}
		return pickAnims.getOrDefault(p.getAnimation(), 17.0 / 6);
	}

	private double prospectorXpMulti(PlayerComposition playerComposition)
	{
		double multi = 1.0;
		if (playerComposition.getEquipmentId(KitType.HEAD) == GOLDEN_PROSPECTOR_HELMET
			|| playerComposition.getEquipmentId(KitType.HEAD) == PROSPECTOR_HELMET)
		{
			multi += 0.004;
		}
		if (playerComposition.getEquipmentId(KitType.BOOTS) == GOLDEN_PROSPECTOR_BOOTS
			|| playerComposition.getEquipmentId(KitType.HEAD) == PROSPECTOR_BOOTS)
		{
			multi += 0.002;
		}
		if (playerComposition.getEquipmentId(KitType.TORSO) == GOLDEN_PROSPECTOR_JACKET
			|| playerComposition.getEquipmentId(KitType.HEAD) == PROSPECTOR_JACKET
			|| playerComposition.getEquipmentId(KitType.HEAD) == VARROCK_ARMOUR_4)
		{
			multi += 0.008;
		}
		if (playerComposition.getEquipmentId(KitType.LEGS) == GOLDEN_PROSPECTOR_LEGS
			|| playerComposition.getEquipmentId(KitType.HEAD) == PROSPECTOR_LEGS)
		{
			multi += 0.006;
		}
		if (multi > 1.019) { // check for full set bonus
			multi += 0.005;
		}
		return multi;
	}

	private boolean facingObject(WorldPoint p1, int orientation, WorldPoint p2)
	{
		Direction dir = new Angle(orientation).getNearestDirection();
		WorldPoint dif = p2.dx(-p1.getX()).dy(-p1.getY());
		switch (dir)
		{
			case NORTH:
				return dif.getY() > 0;
			case SOUTH:
				return dif.getY() < 0;
			case EAST:
				return dif.getX() > 0;
			case WEST:
				return dif.getX() < 0;
		}
		return false;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged state)
	{
		if (state.getGameState() == GameState.HOPPING || state.getGameState() == GameState.LOGGING_IN)
		{
			clear();
			refresh();
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (stars.isEmpty())
		{
			checkDiscoveryRemoval();
			return;
		}
		lastStarTime = client.getTickCount();
		Iterator<Star> it = stars.iterator();
		boolean refresh = false;
		while (it.hasNext())
		{
			Star star = it.next();
			if (despawnQueue.contains(star) || client.getLocalPlayer().getWorldLocation().distanceTo(star.getWorldPoint()) > starConfig.removeDistance())
			{
				layerTimer = 0;
				it.remove();
				refresh = true;
				despawnQueue.remove(star);
				lastStarTime = client.getTickCount();
			}
		}

		if (!stars.isEmpty())
		{
			Star star = stars.get(0);
			updateMiners(star);
			refreshHintArrow();
		}

		if (starConfig.xpPerHour() || starConfig.dustPerHour())
		{
			updateXpDustPerHour();
		}

		if (refresh)
		{
			refresh();
		}
	}

	private void checkDiscoveryRemoval()
	{
		if (bonusCounter != null && client.getTickCount() - lastStarTime >= starConfig.discoveryHideTime() * 100) {
			removeBonusCounter();
		}
	}

	private void updateXpDustPerHour() {
		Player player = client.getLocalPlayer();
		if (player == null || stars.isEmpty()) {
			dustPerHour = -1;
			xpPerHour = -1;
			return;
		}

		Star star = stars.get(0);
		TierData tierData = TierData.get(star.getTier());
		if (tierData == null || !nextToStar(star, player.getWorldLocation())) {
			dustPerHour = -1;
			xpPerHour = -1;
			return;
		}

		int animId = player.getAnimation();
		if (!pickAnims.containsKey(animId)) {
			dustPerHour = -1;
			xpPerHour = -1;
			return;
		}

		int level = client.getBoostedSkillLevel(Skill.MINING);
		boolean members = client.getWorldType().contains(WorldType.MEMBERS);
		ItemContainer equip = client.getItemContainer(InventoryID.EQUIPMENT);
		if (members && equip != null) {
			Item ringItem = equip.getItem(EquipmentInventorySlot.RING.getSlotIdx());
			if (ringItem != null && ringItem.getId() == ItemID.CELESTIAL_RING) {
				level += 4;
			}
		}

		double ticks = getPickTicks(player);
		double chance = tierData.getChance(level);
		double dustPerTick = chance / ticks;
		double xp = members ? tierData.xp : tierData.xp / 2.0; // f2p is half xp
		double xpPerTick = dustPerTick * xp;
		if (members) {
			xpPerTick *= prospectorXpMulti(player.getPlayerComposition());
		}
		xpPerHour = xpPerTick * 6000;
		dustPerHour = dustPerTick * (1 + tierData.doubleDustChance) * 6000;
	}

	public void toggleHintArrow(boolean show) {
		if (show) {
			if (!stars.isEmpty() && !hintArrowShown) {
				Star star = stars.get(0);
				client.setHintArrow(star.getWorldPoint());
				hintArrowShown = true;
			}
		} else if (hintArrowShown) {
			client.clearHintArrow();
			hintArrowShown = false;
		}
	}

	public void refreshHintArrow() {
//		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Refresh hint arrow", "");
		if (stars.isEmpty())
		{
			if (starConfig.showHintArrow())
			{
				toggleHintArrow(false);
			}
		}
		else
		{
			Star star = stars.get(0);
			if (starConfig.showHintArrow())
			{
				if (nextToStar(star, client.getLocalPlayer().getWorldLocation())) {
					toggleHintArrow(false);
				} else {
					toggleHintArrow(true);
				}
			}
		}
	}

	public void refresh()
	{
		if (stars.isEmpty())
		{
			if (starConfig.showInfoBox())
			{
				infoBoxManager.removeInfoBox(infoBox);
			}
		}
		else
		{
			Star star = stars.get(0);
			if (starConfig.showInfoBox())
			{
				infoBoxManager.removeInfoBox(infoBox);
				infoBox = new StarInfoBox(itemManager.getImage(25547), this, star);
				infoBoxManager.addInfoBox(infoBox);
			}
		}
		refreshHintArrow();
	}

	private void updateBonusCounter()
	{
		String tooltip = ColorUtil.wrapWithColorTag("Star Discovery", new Color(255, 119, 0)) + "</br>"
				+ ColorUtil.wrapWithColorTag("Bonus stardust: ", Color.YELLOW) + bonusCount;

		if (bonusCounter != null)
		{
			bonusCounter.setCount(bonusCount);
			bonusCounter.setTooltip(tooltip);
			return;
		}

		removeBonusCounter();

		if (bonusCount == 0)
		{
			return;
		}

		bonusCounter = new BonusCounter(BONUS_IMAGE, this, bonusCount);
		bonusCounter.setTooltip(tooltip);
		infoBoxManager.addInfoBox(bonusCounter);
	}

	private void removeBonusCounter()
	{
		if (bonusCounter == null)
		{
			return;
		}

		infoBoxManager.removeInfoBox(bonusCounter);
		bonusCounter = null;
	}

	private boolean nextToStar(Star star, WorldPoint worldPoint) {
		WorldArea areaH = new WorldArea(star.getWorldPoint().dx(-1), 4, 2);
		WorldArea areaV = new WorldArea(star.getWorldPoint().dy(-1), 2, 4);
		return worldPoint.isInArea2D(areaH, areaV);
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (stars.isEmpty() || event.getType() != MenuAction.EXAMINE_OBJECT.getId() || !starConfig.copyToClipboard())
		{
			return;
		}

		final Tile tile = client.getTopLevelWorldView().getScene().getTiles()[client.getTopLevelWorldView().getPlane()][event.getActionParam0()][event.getActionParam1()];
		final TileObject tileObject = findTileObject(tile, event.getIdentifier());

		if (tileObject == null || !tile.getWorldLocation().equals(stars.get(0).getWorldPoint()) || Star.getTier(tileObject.getId()) < 0)
		{
			return;
		}

		client.createMenuEntry(-1)
			.setOption("Copy")
			.setTarget(event.getTarget())
			.setParam0(event.getActionParam0())
			.setParam1(event.getActionParam1())
			.setIdentifier(event.getIdentifier())
			.setType(MenuAction.RUNELITE)
			.onClick(menuEntry -> copy(menuEntry, false));

		client.createMenuEntry(-2)
			.setOption("Detailed copy")
			.setTarget(event.getTarget())
			.setParam0(event.getActionParam0())
			.setParam1(event.getActionParam1())
			.setIdentifier(event.getIdentifier())
			.setType(MenuAction.RUNELITE)
			.onClick(menuEntry -> copy(menuEntry, true));
	}

	private void copy(MenuEntry menuEntry, boolean detailed)
	{
		if (stars.isEmpty())
		{
			return;
		}
		Star star = stars.get(0);
		String content = "W" + star.getWorld() + " T" + star.getTier() + " ";
		if (star.getHealth() >= 0)
		{
			content += star.getHealth() + "% ";
		}
		if (!star.getMiners().equals(Star.UNKNOWN_MINERS))
		{
			content += "- " + star.getMiners() + " Miners - ";
		}
		content += star.getLocation().getDescription();
		content += star.getWorldInfo();
		content += " " + DiscordTimeStamp.relativeTimeNow();
		if (detailed && star.getTierTicksEstimate() != null) {
			for (int i = star.getTierTicksEstimate().length - 1; i >= 0; i--) {
				content += "\nT"+i+" estimate: " + DiscordTimeStamp.relativeTimeNowPlus(star.getTierTicksEstimate()[i]);
			}
		}
		else if (star.getTierTicksEstimate() != null && starConfig.addT0Estimate())
		{
			content += " T0 estimate: " + DiscordTimeStamp.relativeTimeNowPlus(star.getTierTicksEstimate()[0]);
		}

		final StringSelection stringSelection = new StringSelection(content);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Copied star information to clipboard.", "");
	}

	private TileObject findTileObject(Tile tile, int id)
	{
		if (tile == null)
		{
			return null;
		}
		for (GameObject object : tile.getGameObjects())
		{
			if (object != null && object.getId() == id)
			{
				return object;
			}
		}
		return null;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("starinfoplugin"))
		{
			return;
		}
		switch (event.getKey())
		{
			case StarInfoConfig.TEXT_COLOR_KEY:
				starOverlay.updateConfig();
				break;
			case StarInfoConfig.INFO_BOX_KEY:
				if (starConfig.showInfoBox())
				{
					refresh();
				}
				else
				{
					infoBoxManager.removeInfoBox(infoBox);
					infoBox = null;
				}
				break;
			case StarInfoConfig.BONUS_INFO_BOX_KEY:
				if (starConfig.showStarDiscovery())
				{
					updateBonusCounter();
				}
				else
				{
					removeBonusCounter();
				}
				break;
			case StarInfoConfig.HINT_ARROW_KEY:
				if (starConfig.showHintArrow())
				{
					refresh();
				}
				else
				{
					toggleHintArrow(false);
				}
				break;
		}
	}
}