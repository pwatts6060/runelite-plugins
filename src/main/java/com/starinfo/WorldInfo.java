package com.starinfo;

import javax.inject.Inject;
import net.runelite.client.game.WorldService;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldType;

public class WorldInfo
{
	@Inject
	private WorldService worldService;

	public String worldMsg(Star star) {
		World world = worldService.getWorlds().findWorld(star.getWorld());
		if (world == null) {
			return "";
		}
		String msg = "";
		if (!world.getTypes().contains(WorldType.MEMBERS)) {
			msg += " f2p";
		}
		boolean pvp = world.getTypes().contains(WorldType.PVP);
		if (pvp) {
			msg += " PvP";
		}
		if (world.getTypes().contains(WorldType.HIGH_RISK)) {
			msg += " High Risk";
		}
		boolean highRisk = world.getTypes().contains(WorldType.HIGH_RISK);

		int skillLevel = 0;
		if (world.getTypes().contains(WorldType.SKILL_TOTAL)) {
			skillLevel = Integer.parseInt(world.getActivity().split(" ")[0]);
		}


		return "";
	}
}
