package com.entrana;

import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;

public class ProhibitedItems
{
	private static final Set<Integer> allowedIds = ImmutableSet.of(
		ItemID.BOOTS_OF_LIGHTNESS,
		ItemID.BOOTS_OF_LIGHTNESS_89,
		ItemID.SPOTTED_CAPE,
		ItemID.SPOTTED_CAPE_10073,
		ItemID.SPOTTIER_CAPE,
		ItemID.SPOTTIER_CAPE_10074,
		ItemID.ARCANE_GRIMOIRE,
		ItemID.MAGIC_SECATEURS,
		ItemID.BOOK_OF_BALANCE,
		ItemID.BOOK_OF_BALANCE_OR,
		ItemID.BOOK_OF_DARKNESS,
		ItemID.BOOK_OF_DARKNESS_OR,
		ItemID.BOOK_OF_LAW,
		ItemID.BOOK_OF_LAW_OR,
		ItemID.BOOK_OF_WAR,
		ItemID.BOOK_OF_WAR_OR,
		ItemID.HOLY_BOOK,
		ItemID.HOLY_BOOK_OR,
		ItemID.UNHOLY_BOOK,
		ItemID.UNHOLY_BOOK_OR,
		ItemID.BLUE_WIZARD_HAT,
		ItemID.BLUE_WIZARD_HAT_T,
		ItemID.BLUE_WIZARD_HAT_G,
		ItemID.BLUE_WIZARD_ROBE,
		ItemID.BLUE_WIZARD_ROBE_T,
		ItemID.BLUE_WIZARD_ROBE_G,
		ItemID.WIZARD_HAT,
		ItemID.BLACK_WIZARD_HAT_T,
		ItemID.BLACK_WIZARD_HAT_G,
		ItemID.BLACK_ROBE,
		ItemID.BLACK_WIZARD_ROBE_T,
		ItemID.BLACK_WIZARD_ROBE_G,
		ItemID.ZAMORAK_MONK_BOTTOM,
		ItemID.ZAMORAK_MONK_TOP,
		ItemID.HAM_ROBE,
		ItemID.HAM_SHIRT,
		ItemID.HAM_HOOD,
		ItemID.HAM_BOOTS,
		ItemID.HAM_GLOVES,
		ItemID.HAM_CLOAK,
		ItemID.GREY_BOOTS,
		ItemID.GREY_ROBE_TOP,
		ItemID.GREY_ROBE_BOTTOMS,
		ItemID.GREY_HAT,
		ItemID.GREY_GLOVES,
		ItemID.RED_BOOTS,
		ItemID.RED_ROBE_TOP,
		ItemID.RED_ROBE_BOTTOMS,
		ItemID.RED_HAT,
		ItemID.RED_GLOVES,
		ItemID.YELLOW_BOOTS,
		ItemID.YELLOW_ROBE_TOP,
		ItemID.YELLOW_ROBE_BOTTOMS,
		ItemID.YELLOW_HAT,
		ItemID.YELLOW_GLOVES,
		ItemID.TEAL_BOOTS,
		ItemID.TEAL_ROBE_TOP,
		ItemID.TEAL_ROBE_BOTTOMS,
		ItemID.TEAL_HAT,
		ItemID.TEAL_GLOVES,
		ItemID.PURPLE_BOOTS,
		ItemID.PURPLE_ROBE_TOP,
		ItemID.PURPLE_ROBE_BOTTOMS,
		ItemID.PURPLE_HAT,
		ItemID.PURPLE_GLOVES,
		ItemID.BLACK_CAPE,
		ItemID.BLUE_CAPE,
		ItemID.YELLOW_CAPE,
		ItemID.GREEN_CAPE,
		ItemID.PURPLE_CAPE,
		ItemID.ORANGE_CAPE,
		ItemID.FREMENNIK_CYAN_CLOAK,
		ItemID.FREMENNIK_BROWN_CLOAK,
		ItemID.FREMENNIK_BLUE_CLOAK,
		ItemID.FREMENNIK_GREEN_CLOAK,
		ItemID.FREMENNIK_BROWN_SHIRT,
		ItemID.FREMENNIK_GREY_SHIRT,
		ItemID.FREMENNIK_BEIGE_SHIRT,
		ItemID.FREMENNIK_RED_SHIRT,
		ItemID.FREMENNIK_BLUE_SHIRT,
		ItemID.FREMENNIK_RED_CLOAK,
		ItemID.FREMENNIK_GREY_CLOAK,
		ItemID.FREMENNIK_YELLOW_CLOAK,
		ItemID.FREMENNIK_TEAL_CLOAK,
		ItemID.FREMENNIK_PURPLE_CLOAK,
		ItemID.FREMENNIK_PINK_CLOAK,
		ItemID.FREMENNIK_BLACK_CLOAK,
		ItemID.FREMENNIK_BOOTS,
		ItemID.FREMENNIK_ROBE,
		ItemID.FREMENNIK_SKIRT,
		ItemID.FREMENNIK_HAT,
		ItemID.FREMENNIK_GLOVES,
		ItemID.SPIKED_BOOTS,
		ItemID.LEATHER_BOOTS,
		ItemID.LEATHER_GLOVES,
		ItemID.CLIMBING_BOOTS,
		ItemID.CLIMBING_BOOTS_G,
		ItemID.LEATHER_VAMBRACES,
		ItemID.GUTHIX_CLOAK,
		ItemID.ZAMORAK_CLOAK,
		ItemID.SARADOMIN_CLOAK,
		ItemID.ARMADYL_CLOAK,
		ItemID.ANCIENT_CLOAK,
		ItemID.BANDOS_CLOAK,
		ItemID.TEAM1_CAPE,
		ItemID.TEAM2_CAPE,
		ItemID.TEAM3_CAPE,
		ItemID.TEAM4_CAPE,
		ItemID.TEAM5_CAPE,
		ItemID.TEAM6_CAPE,
		ItemID.TEAM7_CAPE,
		ItemID.TEAM8_CAPE,
		ItemID.TEAM9_CAPE,
		ItemID.TEAM10_CAPE,
		ItemID.TEAM11_CAPE,
		ItemID.TEAM12_CAPE,
		ItemID.TEAM13_CAPE,
		ItemID.TEAM14_CAPE,
		ItemID.TEAM15_CAPE,
		ItemID.TEAM16_CAPE,
		ItemID.TEAM17_CAPE,
		ItemID.TEAM18_CAPE,
		ItemID.TEAM19_CAPE,
		ItemID.TEAM20_CAPE,
		ItemID.TEAM21_CAPE,
		ItemID.TEAM22_CAPE,
		ItemID.TEAM23_CAPE,
		ItemID.TEAM24_CAPE,
		ItemID.TEAM25_CAPE,
		ItemID.TEAM26_CAPE,
		ItemID.TEAM27_CAPE,
		ItemID.TEAM28_CAPE,
		ItemID.TEAM29_CAPE,
		ItemID.TEAM30_CAPE,
		ItemID.TEAM31_CAPE,
		ItemID.TEAM32_CAPE,
		ItemID.TEAM33_CAPE,
		ItemID.TEAM34_CAPE,
		ItemID.TEAM35_CAPE,
		ItemID.TEAM36_CAPE,
		ItemID.TEAM37_CAPE,
		ItemID.TEAM38_CAPE,
		ItemID.TEAM39_CAPE,
		ItemID.TEAM40_CAPE,
		ItemID.TEAM41_CAPE,
		ItemID.TEAM42_CAPE,
		ItemID.TEAM43_CAPE,
		ItemID.TEAM44_CAPE,
		ItemID.TEAM45_CAPE,
		ItemID.TEAM46_CAPE,
		ItemID.TEAM47_CAPE,
		ItemID.TEAM48_CAPE,
		ItemID.TEAM49_CAPE,
		ItemID.TEAM50_CAPE,
		ItemID.TEAM_CAPE_I,
		ItemID.TEAM_CAPE_X,
		ItemID.TEAM_CAPE_ZERO,
		ItemID.GHOSTLY_BOOTS,
		ItemID.GHOSTLY_GLOVES,
		ItemID.ICE_GLOVES,
		ItemID.GLOVES_OF_SILENCE,
		ItemID.PINK_BOOTS,
		ItemID.GREEN_BOOTS,
		ItemID.BLUE_BOOTS,
		ItemID.CREAM_BOOTS,
		ItemID.TURQUOISE_BOOTS,
		ItemID.PINK_ROBE_TOP,
		ItemID.GREEN_ROBE_TOP,
		ItemID.BLUE_ROBE_TOP,
		ItemID.CREAM_ROBE_TOP,
		ItemID.TURQUOISE_ROBE_TOP,
		ItemID.PINK_ROBE_BOTTOMS,
		ItemID.GREEN_ROBE_BOTTOMS,
		ItemID.BLUE_ROBE_BOTTOMS,
		ItemID.CREAM_ROBE_BOTTOMS,
		ItemID.TURQUOISE_ROBE_BOTTOMS,
		ItemID.PINK_HAT,
		ItemID.GREEN_HAT,
		ItemID.BLUE_HAT,
		ItemID.CREAM_HAT,
		ItemID.TURQUOISE_HAT
	);
	private static final Set<Integer> prohibitedIds = ImmutableSet.of(
		ItemID.CUTTHROAT_FLAG,
		ItemID.GILDED_SMILE_FLAG,
		ItemID.BRONZE_FIST_FLAG,
		ItemID.LUCKY_SHOT_FLAG,
		ItemID.TREASURE_FLAG,
		ItemID.PHASMATYS_FLAG,
		ItemID.BANNER,
		ItemID.BANNER_8652,
		ItemID.BANNER_8654,
		ItemID.BANNER_8656,
		ItemID.BANNER_8658,
		ItemID.BANNER_8660,
		ItemID.BANNER_8662,
		ItemID.BANNER_8664,
		ItemID.BANNER_8666,
		ItemID.BANNER_8668,
		ItemID.BANNER_8670,
		ItemID.BANNER_8672,
		ItemID.BANNER_8674,
		ItemID.BANNER_8676,
		ItemID.BANNER_8678,
		ItemID.BANNER_8680,
		ItemID.ARCEUUS_BANNER,
		ItemID.GREEN_BANNER,
		ItemID.HOSIDIUS_BANNER,
		ItemID.LOVAKENGJ_BANNER,
		ItemID.PISCARILIUS_BANNER,
		ItemID.PROTEST_BANNER,
		ItemID.SHATTERED_BANNER,
		ItemID.SHAYZIEN_BANNER,
		ItemID.TRAILBLAZER_BANNER,
		ItemID.SARADOMIN_BANNER_11891,
		ItemID.ZAMORAK_BANNER_11892,
		ItemID.MAGIC_BUTTERFLY_NET,
		ItemID.ANCIENT_HILT,
		ItemID.SARADOMIN_HILT,
		ItemID.BANDOS_HILT,
		ItemID.ZAMORAK_HILT,
		ItemID.ARMADYL_HILT,
		ItemID.GODSWORD_BLADE,
		ItemID.GODSWORD_SHARD_1,
		ItemID.GODSWORD_SHARD_2,
		ItemID.GODSWORD_SHARD_3,
		ItemID.HAM_JOINT,
		ItemID.SWIFT_BLADE,
		ItemID.IMCANDO_HAMMER,
		ItemID.CANNON_BASE,
		ItemID.CANNON_STAND,
		ItemID.CANNON_BARRELS,
		ItemID.CANNON_FURNACE,
		ItemID.CANNON_BASE_OR,
		ItemID.CANNON_STAND_OR,
		ItemID.CANNON_BARRELS_OR,
		ItemID.CANNON_FURNACE_OR,
		ItemID.BABY_IMPLING_JAR,
		ItemID.YOUNG_IMPLING_JAR,
		ItemID.GOURMET_IMPLING_JAR,
		ItemID.EARTH_IMPLING_JAR,
		ItemID.ESSENCE_IMPLING_JAR,
		ItemID.ECLECTIC_IMPLING_JAR,
		ItemID.NATURE_IMPLING_JAR,
		ItemID.MAGPIE_IMPLING_JAR,
		ItemID.NINJA_IMPLING_JAR,
		ItemID.DRAGON_IMPLING_JAR,
		ItemID.LUCKY_IMPLING_JAR,
		ItemID.CRYSTAL_IMPLING_JAR,
		ItemID.SUPPLY_CRATE,
		ItemID.SPOILS_OF_WAR,
		ItemID.BUTTERFLY_NET
	);
	private static final Map<Integer, Boolean> cachedIds = new HashMap<>();
	private final ItemManager itemManager;

	public ProhibitedItems(ItemManager itemManager)
	{
		this.itemManager = itemManager;
	}

	boolean isProhibited(int id)
	{
		if (allowedIds.contains(id))
		{
			return false;
		}
		if (prohibitedIds.contains(id))
		{
			return true;
		}
		if (cachedIds.containsKey(id))
		{
			return cachedIds.get(id);
		}
		boolean prohibited = false;
		if (isCombatGear(id))
		{
			prohibited = true;
		}
		cachedIds.put(id, prohibited);
		return prohibited;
	}

	private boolean isCombatGear(int id)
	{
		ItemStats itemStats = itemManager.getItemStats(id, false);
		if (itemStats == null || !itemStats.isEquipable())
		{
			return false;
		}

		ItemEquipmentStats stats = itemStats.getEquipment();
		int slot = itemStats.getEquipment().getSlot();
		if (slot == EquipmentInventorySlot.AMMO.getSlotIdx())
		{
			return false;
		}
		if (slot == EquipmentInventorySlot.AMULET.getSlotIdx())
		{
			return false;
		}
		if (slot == EquipmentInventorySlot.RING.getSlotIdx())
		{
			return false;
		}

		ItemComposition itemComposition = itemManager.getItemComposition(id);
		String name = itemComposition.getName().toLowerCase();
		if (name.contains("bracelet"))
		{
			return false;
		}
		if (stats.getAcrush() > 0)
		{
			return true;
		}
		if (stats.getAstab() > 0)
		{
			return true;
		}
		if (stats.getAslash() > 0)
		{
			return true;
		}
		if (stats.getAmagic() > 0)
		{
			return true;
		}
		if (stats.getArange() > 0)
		{
			return true;
		}
		if (stats.getDmagic() > 0)
		{
			return true;
		}
		if (stats.getDrange() > 0)
		{
			return true;
		}
		if (stats.getDslash() > 0)
		{
			return true;
		}
		if (stats.getDstab() > 0)
		{
			return true;
		}
		return false;
	}
}
