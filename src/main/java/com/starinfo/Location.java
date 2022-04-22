package com.starinfo;

import net.runelite.api.coords.WorldPoint;

public class Location
{
	public static final Location UNKNOWN_LOCATION = new Location(0, 0, 0, "Unknown location");

	private static final Location[] values = new Location[]{
		new Location(3443, 3018, 0, "Dwarven Mine"),
		new Location(3348, 3030, 0, "Mining Guild"),
		new Location(3355, 2906, 0, "West Falador mine"),
		new Location(3474, 2882, 0, "Taverley"),
		new Location(3280, 2940, 0, "Crafting Guild"),
		new Location(3241, 2974, 0, "Rimmington mine"),
		new Location(3238, 2822, 0, "South Crandor mine"),
		new Location(3296, 2835, 0, "North Crandor mine"),
		new Location(3221, 2736, 0, "North Brimhaven mine"),
		new Location(3143, 2742, 0, "South Brimhaven mine"),
		new Location(3037, 2845, 0, "Karamja Jungle mine"),
		new Location(2999, 2827, 0, "Shilo Village mine"),
		new Location(2964, 2571, 0, "Feldip Hunter area"),
		new Location(2993, 2630, 0, "Rantz's cave"),
		new Location(2858, 2567, 0, "Corsair Cove"),
		new Location(2886, 2483, 0, "Corsair Cove Resource Area"),
		new Location(2842, 2468, 0, "Myths' Guild"),
		new Location(2792, 2200, 0, "Isle of Souls mine"),
		new Location(3814, 3774, 0, "Fossil Island mine"),
		new Location(3801, 3818, 0, "Volcanic Mine"),
		new Location(2969, 3686, 0, "Mos Le'Harmless"),
		new Location(3699, 2683, 0, "Rellekka mine"),
		new Location(3683, 2727, 0, "Keldagrim entrance mine"),
		new Location(3887, 2528, 0, "Miscellania mine"),
		new Location(3814, 2393, 0, "Jatizso mine"),
		new Location(3832, 2375, 0, "Central Fremennik Isles mine"),
		new Location(3938, 2139, 0, "Lunar Isle mine"),
		new Location(3493, 1778, 0, "Hosidius mine"),
		new Location(3648, 1597, 0, "Shayzien mine"),
		new Location(3709, 1769, 0, "Piscarilius mine"),
		new Location(3853, 1760, 0, "Dense essence mine"),
		new Location(3840, 1437, 0, "Lovakite mine"),
		new Location(3747, 1534, 0, "Lovakengj"),
		new Location(3434, 2804, 0, "Catherby"),
		new Location(3086, 2602, 0, "Yanille"),
		new Location(3141, 2624, 0, "Port Khazard mine"),
		new Location(3333, 2705, 0, "Legends' Guild mine"),
		new Location(3478, 2589, 0, "Coal Trucks"),
		new Location(3233, 2608, 0, "South-east Ardougne mine"),
		new Location(3651, 1210, 0, "Kebos Lowlands mine"),
		new Location(3817, 1279, 0, "Mount Karuulm mine"),
		new Location(3816, 1322, 0, "Mount Karuulm"),
		new Location(3564, 1258, 0, "Mount Quidamortem"),
		new Location(3298, 3296, 0, "Al Kharid mine"),
		new Location(3164, 3276, 0, "Al Kharid"),
		new Location(3160, 3424, 0, "Uzer mine"),
		new Location(2910, 3171, 0, "Desert quarry"),
		new Location(2867, 3316, 0, "Agility Pyramid mine"),
		new Location(2889, 3434, 0, "Nardah"),
		new Location(3267, 3341, 0, "Duel Arena"),
		new Location(3155, 3230, 0, "East Lumbridge Swamp mine"),
		new Location(3150, 3153, 0, "West Lumbridge Swamp mine"),
		new Location(3235, 3094, 0, "Draynor Village"),
		new Location(3408, 3258, 0, "Varrock"),
		new Location(3353, 3290, 0, "South-east Varrock mine"),
		new Location(3362, 3175, 0, "South-west Varrock mine"),
		new Location(3485, 3505, 0, "Canifis"),
		new Location(3219, 3500, 0, "Burgh de Rott"),
		new Location(3233, 3451, 0, "Abandoned Mine"),
		new Location(3214, 3650, 0, "Ver Sinhaza"),
		new Location(3340, 3635, 0, "Daeyalt essence mine"),
		new Location(3635, 2341, 0, "Piscatoris mine"),
		new Location(3490, 2444, 0, "Grand Tree"),
		new Location(3436, 2448, 0, "Tree Gnome Stronghold"),
		new Location(3158, 2269, 0, "Isafdar mine"),
		new Location(3269, 2318, 0, "Arandar mine"),
		new Location(3163, 2329, 0, "Lletya"),
		new Location(6055, 3274, 0, "Trahaearn mine"),
		new Location(3409, 2173, 0, "Mynydd mine"),
		new Location(3569, 3108, 0, "South Wilderness mine"),
		new Location(3593, 3018, 0, "South-west Wilderness mine"),
		new Location(3756, 3093, 0, "Bandit Camp mine"),
		new Location(3887, 3057, 0, "Lava Maze runite mine"),
		new Location(3932, 3188, 0, "Resource Area"),
		new Location(3962, 3091, 0, "Mage Arena"),
		new Location(3940, 3049, 0, "Pirates' Hideout mine"),
		UNKNOWN_LOCATION,
	};

	public final WorldPoint location;
	public final String description;

	Location(int x, int y, int plane, String description)
	{
		this.location = new WorldPoint(x, y, plane);
		this.description = description;
	}

	public static Location forLocation(WorldPoint location)
	{
		for (Location loc : values)
		{
			if (loc.location.equals(location))
			{
				return loc;
			}
		}
		return UNKNOWN_LOCATION;
	}
}
