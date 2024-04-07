package com.rogues;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Rogues Chest"
)
public class RoguesPlugin extends Plugin
{
	public static final int LOOTABLE_ID = ObjectID.CHEST_26757;
	public static final int RESPAWN_TIME = 22;
	public static final WorldPoint EASTERN_CHEST_POINT = new WorldPoint(3297, 3940, 0);
	private static final int LOOT_ANIM = 536;

	public Map<WorldPoint, Integer> respawnMap = null;

	@Inject
	private Client client;

	@Inject
	private RoguesConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private RoguesOverlay overlay;

	@Override
	protected void startUp() throws Exception
	{
		respawnMap = new HashMap<>();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		respawnMap.clear();
		respawnMap = null;
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		int curTick = client.getTickCount();
		for (Map.Entry<WorldPoint, Integer> entry : respawnMap.entrySet())
		{
			WorldPoint worldPoint = entry.getKey();
			if (config.ignoreEastChest() && worldPoint.equals(EASTERN_CHEST_POINT))
			{
				continue;
			}
			int respawnTick = entry.getValue();
			if (curTick == respawnTick)
			{
				if (config.shouldNotify())
				{
					notifier.notify("Chest respawned");
				}
				if (config.playSound())
				{
					client.playSoundEffect(config.soundId());
				}
			}
			else if (curTick + config.warnTime() == respawnTick && config.playSound())
			{
				client.playSoundEffect(config.warnSoundId());
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOADING && respawnMap != null) {
			respawnMap.clear();
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		if (event.getGameObject().getId() == LOOTABLE_ID)
		{
			respawnMap.put(event.getGameObject().getWorldLocation(), client.getTickCount() + RESPAWN_TIME);
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		Actor actor = event.getActor();
		if (actor.getAnimation() != LOOT_ANIM) {
			return;
		}
		WorldPoint chestLoc = null;

		for (Map.Entry<WorldPoint, Integer> entry : respawnMap.entrySet())
		{
			WorldPoint worldPoint = entry.getKey();

			if (actor.getWorldLocation().distanceTo2D(worldPoint) <= 1) {
				chestLoc = worldPoint;
				break;
			}
		}

		if (chestLoc != null) {
			respawnMap.put(chestLoc, client.getTickCount() + RESPAWN_TIME);
		}
	}

	@Provides
	RoguesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RoguesConfig.class);
	}

	public static int pointHash(WorldPoint worldPoint)
	{
		return worldPoint.getX() << 16 + worldPoint.getY() << 2 + worldPoint.getPlane();
	}
}
