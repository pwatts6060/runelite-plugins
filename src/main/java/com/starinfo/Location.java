package com.starinfo;

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;

public class Location
{
	private static final Map<Point, String> LOCATION_NAMES = new HashMap<Point, String>()
	{
		{
	    /*
	    ASGARNIA
	     */
			put(new Point(2974, 3241), "Rimmington mine");
			put(new Point(2940, 3280), "Crafting guild");
			put(new Point(2906, 3355), "West Falador mine");
			put(new Point(3030, 3348), "East Falador bank");
			put(new Point(3018, 3443), "North Dwarven Mine entrance");
			put(new Point(2882, 3474), "Taverley house portal");
	    /*
	    CRANDOR/KARAMJA
	     */
			put(new Point(2736, 3221), "Brimhaven northwest gold mine");
			put(new Point(2742, 3143), "Brimhaven south dungeon entrance");
			put(new Point(2845, 3037), "Nature Altar mine north of Shilo");
			put(new Point(2827, 2999), "Shilo Village gem mine");
			put(new Point(2835, 3296), "North Crandor");
			put(new Point(2822, 3238), "South Crandor");
	    /*
	    DESERT
	     */
			put(new Point(3296, 3298), "Al Kharid mine");
			put(new Point(3276, 3164), "Al Kharid bank");
			put(new Point(3341, 3267), "Duel Arena");
			put(new Point(3424, 3160), "Nw of Uzer (Eagle's Eyrie)");
			put(new Point(3434, 2889), "Nardah bank");
			put(new Point(3316, 2867), "Agility Pyramid mine");
			put(new Point(3171, 2910), "Desert Quarry mine");
	    /*
	    FELDIP HILLS/ISLE OF SOULS
	     */
			put(new Point(2567, 2858), "Corsair Cove bank");
			put(new Point(2483, 2886), "Corsair Resource Area");
			put(new Point(2468, 2842), "Myths' Guild");
			put(new Point(2571, 2964), "Feldip Hills (aks fairy ring)");
			put(new Point(2630, 2993), "Rantz cave");
			put(new Point(2200, 2792), "Soul Wars south mine");
	    /*
	    FOSSIL ISLAND/MOS LE HARMLESS
	     */
			put(new Point(3818, 3801), "Fossil Island Volcanic Mine entrance");
			put(new Point(3774, 3814), "Fossil Island rune rocks");
			put(new Point(3686, 2969), "Mos Le'Harmless west bank");
	    /*
	    FREMENNIK/LUNAR ISLE
	     */
			put(new Point(2727, 3683), "Keldagrim entrance mine");
			put(new Point(2683, 3699), "Rellekka mine");
			put(new Point(2393, 3814), "Jatizso mine entrance");
			put(new Point(2375, 3832), "Neitiznot south of rune rock");
			put(new Point(2528, 3887), "Miscellania mine (cip fairy ring)");
			put(new Point(2139, 3938), "Lunar Isle mine entrance");
	    /*
	    KANDARIN
	     */
			put(new Point(2602, 3086), "Yanille bank");
			put(new Point(2624, 3141), "Port Khazard mine");
			put(new Point(2608, 3233), "Ardougne Monastary");
			put(new Point(2705, 3333), "South of Legends' Guild");
			put(new Point(2804, 3434), "Catherby bank");
			put(new Point(2589, 3478), "Coal Trucks west of Seers'");
	    /*
	    KOUREND
	     */
			put(new Point(1778, 3493), "Hosidius mine");
			put(new Point(1769, 3709), "Port Piscarilius mine in Kourend");
			put(new Point(1597, 3648), "Shayzien mine south of Kourend Castle");
			put(new Point(1534, 3747), "South Lovakengj bank");
			put(new Point(1437, 3840), "Lovakite mine");
			put(new Point(1760, 3853), "Arceuus dense essence mine");
	    /*
	    KEBOS LOWLANDS
	     */
			put(new Point(1322, 3816), "Mount Karuulm bank");
			put(new Point(1279, 3817), "Mount Karuulm mine");
			put(new Point(1210, 3651), "Kebos Swamp mine");
			put(new Point(1258, 3564), "Chambers of Xeric bank");
	    /*
	    MISTHALIN
	     */
			put(new Point(3258, 3408), "Varrock east bank");
			put(new Point(3290, 3353), "South-east Varrock mine");
			put(new Point(3175, 3362), "Champions' Guild mine");
			put(new Point(3094, 3235), "Draynor Village");
			put(new Point(3153, 3150), "West Lumbridge Swamp mine");
			put(new Point(3230, 3155), "East Lumbridge Swamp mine");
	    /*
	    MORYTANIA
	     */
			put(new Point(3635, 3340), "Darkmeyer ess. mine entrance");
			put(new Point(3650, 3214), "Theatre of Blood bank");
			put(new Point(3505, 3485), "Canifis bank");
			put(new Point(3500, 3219), "Burgh de Rott bank");
			put(new Point(3451, 3233), "Abandoned Mine west of Burgh");
	    /*
	    PISCATORIS/GNOME STRONGHOLD
	     */
			put(new Point(2444, 3490), "West of Grand Tree");
			put(new Point(2448, 3436), "Gnome Stronghold spirit tree");
			put(new Point(2341, 3635), "Piscatoris (akq fairy ring)");
	    /*
	    TIRANNWN
	     */
			put(new Point(2329, 3163), "Lletya");
			put(new Point(2269, 3158), "Isafdar runite rocks");
			put(new Point(3274, 6055), "Priffdinas Zalcano entrance");
			put(new Point(2318, 3269), "Arandar mine north of Lleyta");
			put(new Point(2173, 3409), "Mynydd nw of Priffdinas");
	    /*
	    WILDERNESS
	     */
			put(new Point(3108, 3569), "Mage of Zamorak mine (lvl 7 Wildy)");
			put(new Point(3018, 3593), "Skeleton mine (lvl 10 Wildy)");
			put(new Point(3093, 3756), "Hobgoblin mine (lvl 30 Wildy)");
			put(new Point(3057, 3887), "Lava maze runite mine (lvl 46 Wildy)");
			put(new Point(3049, 3940), "Pirates' Hideout (lvl 53 Wildy)");
			put(new Point(3091, 3962), "Mage Arena bank (lvl 56 Wildy)");
			put(new Point(3188, 3932), "Wilderness Resource Area");
		}
	};

	public static String forLocation(WorldPoint location)
	{
		Point point = new Point(location.getX(), location.getY());
		return LOCATION_NAMES.getOrDefault(point, "Unknown location: " + point.getX() + ", " + point.getY());
	}
}
