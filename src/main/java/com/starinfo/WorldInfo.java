package com.starinfo;

import javax.inject.Inject;
import net.runelite.client.game.WorldService;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldType;

public class WorldInfo
{
	@Inject
	private WorldService worldService;

	public void update(Star star) {
		if (worldService.getWorlds() == null) {
			star.setWorldInfo("");
			return;
		}
		World world = worldService.getWorlds().findWorld(star.getWorld());
		if (world == null) {
			star.setWorldInfo("");
			return;
		}

		String msg = "";
		if (!world.getTypes().contains(WorldType.MEMBERS)) {
			msg += " f2p";
		}

		boolean pvp = world.getTypes().contains(WorldType.PVP);
		if (pvp) {
			msg += " PvP";
		}

		if ((pvp || star.getLocation().isWildy()) && world.getTypes().contains(WorldType.HIGH_RISK)) {
			msg += " High Risk";
		}

		if (world.getTypes().contains(WorldType.SKILL_TOTAL)) {
			msg += " " + world.getActivity();
		}

		if (!msg.isEmpty()) {
			msg = " -" + msg;
		}
		star.setWorldInfo(msg);
	}
}
