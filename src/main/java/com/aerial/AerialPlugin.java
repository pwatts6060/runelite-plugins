package com.aerial;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
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
	static final int BIRD_PROJECTILE = 1632;
	static final int GLOVE_NO_BIRD = 22816;
	static final int GLOVE_WITH_BIRD = 22817;

	public static Map<Integer, Integer> distToTicks = null;

	@Inject
	private Client client;

	@Inject
	private AerialConfig config;

	@Inject
	private AerialOverlay aerialOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(aerialOverlay);
		distToTicks = new HashMap<>();
		distToTicks.put(1, 0);
		distToTicks.put(2, 1);
		distToTicks.put(3, 1);
		distToTicks.put(4, 2);
		distToTicks.put(5, 3);
		distToTicks.put(6, 3);
		distToTicks.put(7, 4);
		distToTicks.put(8, 4);
		distToTicks.put(9, 5);
		distToTicks.put(10, 5);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(aerialOverlay);
		distToTicks = null;
		pointToEndTick.clear();
	}

	private final Map<Integer, Integer> pointToEndTick = new HashMap<>();

	@Subscribe
	public void onGameTick(GameTick event) {
		int weaponId = client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON);
		if (weaponId != GLOVE_WITH_BIRD && weaponId != GLOVE_NO_BIRD) {
			return;
		}

		for (Projectile p : client.getProjectiles()) {
			if (p.getId() != BIRD_PROJECTILE) {
				continue;
			}
			if (p.getInteracting() == null || !p.getInteracting().getName().equals(client.getLocalPlayer().getName())) {
				continue;
			}
			WorldPoint point = WorldPoint.fromLocal(client, new LocalPoint(p.getX1(), p.getY1()));
			int distance = point.distanceTo2D(WorldPoint.fromLocal(client, p.getTarget()));

			int hash = getPointHash(point);
			if (pointToEndTick.containsKey(hash)) {
				continue;
			}

			pointToEndTick.put(hash, client.getTickCount() + distToTicks.getOrDefault(distance, -1));
		}


		for(Iterator<Map.Entry<Integer, Integer>> it = pointToEndTick.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<Integer, Integer> entry = it.next();
			if (client.getTickCount() + 1 == entry.getValue() && config.warningSound()) {
				client.playSoundEffect(3813);
			}

			if (client.getTickCount() == entry.getValue() && config.idleSound()) {
				client.playSoundEffect(3815);
			}

			if (client.getTickCount() >= entry.getValue() + 6) {
				it.remove();
			}
		}
	}

	private static int getPointHash(WorldPoint point) {
		return point.getX() << 15 + point.getY();
	}

	@Provides
	AerialConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AerialConfig.class);
	}
}
