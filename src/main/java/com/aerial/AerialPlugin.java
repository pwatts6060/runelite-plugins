package com.aerial;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Projectile;
import net.runelite.api.SoundEffectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.SpotanimID;
import net.runelite.api.kit.KitType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Aerial Fishing"
)
public class AerialPlugin extends Plugin
{
	private static final int FRENZIED_DURATION = 3;
	public static Map<Integer, Integer> distToTicks = null;

	@Inject
	private Client client;

	@Inject
	private AerialConfig config;

	@Inject
	private AerialOverlay aerialOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Getter
	private int timerCompleteTick = -1;
	@Getter
	private int timerStartTick = -1;

	private WorldPoint frenziedPoint = null;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(aerialOverlay);
		distToTicks = new HashMap<>();
		distToTicks.put(1, 0);
		distToTicks.put(2, 0);
		distToTicks.put(3, 1);
		distToTicks.put(4, 1);
		distToTicks.put(5, 2);
		distToTicks.put(6, 3);
		distToTicks.put(7, 3);
		distToTicks.put(8, 4);
		distToTicks.put(9, 4);
		distToTicks.put(10, 5);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(aerialOverlay);
		distToTicks = null;
		frenziedPoint = null;
		pointToEndTick.clear();
	}

	@Getter
	private final Map<Integer, Integer> pointToEndTick = new HashMap<>();

	@Subscribe
	public void onGameTick(GameTick event) {
		if (!isGloveEquipped()) {
			resetState();
			return;
		}

		for (Projectile p : client.getProjectiles()) {
			if (p.getId() != SpotanimID.AERIAL_FISHING_TRAVEL) {
				continue;
			}
			if (p.getTargetActor() == null || !Objects.equals(p.getTargetActor().getName(), client.getLocalPlayer().getName())) {
				continue;
			}

			int hash = p.getStartCycle();
			if (pointToEndTick.containsKey(hash)) {
				continue;
			}

			WorldPoint point = p.getSourcePoint();
			int distanceToTicks;
			if (point.equals(frenziedPoint)) {
				distanceToTicks = FRENZIED_DURATION - 1; // duration is offset by 1 tick
			} else {
				int distance = point.distanceTo2D(p.getTargetPoint());
				distanceToTicks = distToTicks.getOrDefault(distance, -1);
			}

			timerCompleteTick = client.getTickCount() + distanceToTicks;
			timerStartTick = client.getTickCount();
			pointToEndTick.put(hash, timerCompleteTick);
		}


		for(Iterator<Map.Entry<Integer, Integer>> it = pointToEndTick.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<Integer, Integer> entry = it.next();
			if (client.getTickCount() + 1 == entry.getValue() && config.warningSound()) {
				client.playSoundEffect(SoundEffectID.TOWN_CRIER_BELL_DING);
			}

			if (client.getTickCount() == entry.getValue() && config.idleSound()) {
				client.playSoundEffect(3815);
			}

			if (client.getTickCount() >= entry.getValue() + 6) {
				it.remove();
			}
		}
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned event)
	{
		if (event.getNpc().getId() == NpcID.FISHING_SPOT_AERIAL_LARGE) {
			WorldPoint swTilePoint = event.getNpc().getWorldLocation();
			int offset = event.getNpc().getComposition().getSize()/2;
			frenziedPoint = swTilePoint.dx(offset).dy(offset); // offset to the center of the
		}
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned event) {
		if (event.getNpc().getId() == NpcID.FISHING_SPOT_AERIAL_LARGE) {
			frenziedPoint = null;
		}
	}

	public boolean isGloveEquipped()
	{
		int weaponId = client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON);

		// Whether worn weapon is the aerial fishing glove
        return weaponId == ItemID.AERIAL_FISHING_GLOVES_BIRD || weaponId == ItemID.AERIAL_FISHING_GLOVES_NO_BIRD;
    }

	protected void resetState() {
		pointToEndTick.clear();
		timerCompleteTick = -1;
		timerStartTick = -1;
		frenziedPoint = null;
	}

	@Provides
	AerialConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AerialConfig.class);
	}
}
