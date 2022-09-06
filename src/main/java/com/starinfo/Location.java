package com.starinfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;

public class Location
{
	private static final Map<Point, Location> locations = new HashMap<>();

	private final Point point;
	@Getter
	private final String description;
	@Getter
	private final boolean wildy;

	static {
		List<Location> locationList = Arrays.asList(
			/*
			ASGARNIA
			 */
			new Location(new Point(2974, 3241), "Rimmington mine"),
			new Location(new Point(2940, 3280), "Crafting guild"),
			new Location(new Point(2906, 3355), "West Falador mine"),
			new Location(new Point(3030, 3348), "East Falador bank"),
			new Location(new Point(3018, 3443), "North Dwarven Mine entrance"),
			new Location(new Point(2882, 3474), "Taverley house portal"),
			/*
			CRANDOR/KARAMJA
			 */
			new Location(new Point(2736, 3221), "Brimhaven northwest gold mine"),
			new Location(new Point(2742, 3143), "Brimhaven south dungeon entrance"),
			new Location(new Point(2845, 3037), "Nature Altar mine north of Shilo"),
			new Location(new Point(2827, 2999), "Shilo Village gem mine"),
			new Location(new Point(2835, 3296), "North Crandor"),
			new Location(new Point(2822, 3238), "South Crandor"),
			/*
			DESERT
			 */
			new Location(new Point(3296, 3298), "Al Kharid mine"),
			new Location(new Point(3276, 3164), "Al Kharid bank"),
			new Location(new Point(3351, 3281), "Mage Training Arena entrance"),
			new Location(new Point(3424, 3160), "Nw of Uzer (Eagle's Eyrie)"),
			new Location(new Point(3434, 2889), "Nardah bank"),
			new Location(new Point(3316, 2867), "Agility Pyramid mine"),
			new Location(new Point(3171, 2910), "Desert Quarry mine"),
			/*
			FELDIP HILLS/ISLE OF SOULS
			 */
			new Location(new Point(2567, 2858), "Corsair Cove bank"),
			new Location(new Point(2483, 2886), "Corsair Resource Area"),
			new Location(new Point(2468, 2842), "Myths' Guild"),
			new Location(new Point(2571, 2964), "Feldip Hills (aks fairy ring)"),
			new Location(new Point(2630, 2993), "Rantz cave"),
			new Location(new Point(2200, 2792), "Soul Wars south mine"),
			/*
			FOSSIL ISLAND/MOS LE HARMLESS
			 */
			new Location(new Point(3818, 3801), "Fossil Island Volcanic Mine entrance"),
			new Location(new Point(3774, 3814), "Fossil Island rune rocks"),
			new Location(new Point(3686, 2969), "Mos Le'Harmless west bank"),
			/*
			FREMENNIK/LUNAR ISLE
			 */
			new Location(new Point(2727, 3683), "Keldagrim entrance mine"),
			new Location(new Point(2683, 3699), "Rellekka mine"),
			new Location(new Point(2393, 3814), "Jatizso mine entrance"),
			new Location(new Point(2375, 3832), "Neitiznot south of rune rock"),
			new Location(new Point(2528, 3887), "Miscellania mine (cip fairy ring)"),
			new Location(new Point(2139, 3938), "Lunar Isle mine entrance"),
			/*
			KANDARIN
			 */
			new Location(new Point(2602, 3086), "Yanille bank"),
			new Location(new Point(2624, 3141), "Port Khazard mine"),
			new Location(new Point(2608, 3233), "Ardougne Monastery"),
			new Location(new Point(2705, 3333), "South of Legends' Guild"),
			new Location(new Point(2804, 3434), "Catherby bank"),
			new Location(new Point(2589, 3478), "Coal Trucks west of Seers'"),
			/*
			KOUREND
			 */
			new Location(new Point(1778, 3493), "Hosidius mine"),
			new Location(new Point(1769, 3709), "Port Piscarilius mine in Kourend"),
			new Location(new Point(1597, 3648), "Shayzien mine south of Kourend Castle"),
			new Location(new Point(1534, 3747), "South Lovakengj bank"),
			new Location(new Point(1437, 3840), "Lovakite mine"),
			new Location(new Point(1760, 3853), "Arceuus dense essence mine"),
			/*
			KEBOS LOWLANDS
			 */
			new Location(new Point(1322, 3816), "Mount Karuulm bank"),
			new Location(new Point(1279, 3817), "Mount Karuulm mine"),
			new Location(new Point(1210, 3651), "Kebos Swamp mine"),
			new Location(new Point(1258, 3564), "Chambers of Xeric bank"),
			/*
			MISTHALIN
			 */
			new Location(new Point(3258, 3408), "Varrock east bank"),
			new Location(new Point(3290, 3353), "South-east Varrock mine"),
			new Location(new Point(3175, 3362), "Champions' Guild mine"),
			new Location(new Point(3094, 3235), "Draynor Village"),
			new Location(new Point(3153, 3150), "West Lumbridge Swamp mine"),
			new Location(new Point(3230, 3155), "East Lumbridge Swamp mine"),
			/*
			MORYTANIA
			 */
			new Location(new Point(3635, 3340), "Darkmeyer ess. mine entrance"),
			new Location(new Point(3650, 3214), "Theatre of Blood bank"),
			new Location(new Point(3505, 3485), "Canifis bank"),
			new Location(new Point(3500, 3219), "Burgh de Rott bank"),
			new Location(new Point(3451, 3233), "Abandoned Mine west of Burgh"),
			/*
			PISCATORIS/GNOME STRONGHOLD
			 */
			new Location(new Point(2444, 3490), "West of Grand Tree"),
			new Location(new Point(2448, 3436), "Gnome Stronghold spirit tree"),
			new Location(new Point(2341, 3635), "Piscatoris (akq fairy ring)"),
			/*
			TIRANNWN
			 */
			new Location(new Point(2329, 3163), "Lletya"),
			new Location(new Point(2269, 3158), "Isafdar runite rocks"),
			new Location(new Point(3274, 6055), "Prifddinas Zalcano entrance"),
			new Location(new Point(2318, 3269), "Arandar mine north of Lleyta"),
			new Location(new Point(2173, 3409), "Mynydd nw of Prifddinas"),
			/*
			WILDERNESS
			 */
			new Location(new Point(3108, 3569), "Mage of Zamorak mine (lvl 7 Wildy)", true),
			new Location(new Point(3018, 3593), "Skeleton mine (lvl 10 Wildy)", true),
			new Location(new Point(3093, 3756), "Hobgoblin mine (lvl 30 Wildy)", true),
			new Location(new Point(3057, 3887), "Lava maze runite mine (lvl 46 Wildy)", true),
			new Location(new Point(3049, 3940), "Pirates' Hideout (lvl 53 Wildy)", true),
			new Location(new Point(3091, 3962), "Mage Arena bank (lvl 56 Wildy)", true),
			new Location(new Point(3188, 3932), "Wilderness Resource Area", true)
		);

		for (Location location : locationList) {
			locations.put(location.point, location);
		}
	}

	public Location(Point point, String description, boolean wildy)
	{
		this.point = point;
		this.description = description;
		this.wildy = wildy;
	}

	public Location(Point point, String description)
	{
		this(point, description, false);
	}

	public static Location forLocation(WorldPoint worldPoint)
	{
		Point point = new Point(worldPoint.getX(), worldPoint.getY());
		Location location = locations.get(point);
		if (location == null) {
			return new Location(point, "Unknown location: " + point.getX() + ", " + point.getY(), false);
		}
		return location;
	}
}
