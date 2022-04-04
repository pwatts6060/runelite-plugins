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
		ItemID.HAM_CLOAK
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
		ItemID.SPOILS_OF_WAR
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
		if (slot == EquipmentInventorySlot.AMMO.getSlotIdx()) {
			return false;
		}
		if (slot == EquipmentInventorySlot.AMULET.getSlotIdx()) {
			return false;
		}

		ItemComposition itemComposition = itemManager.getItemComposition(id);
		String name = itemComposition.getName().toLowerCase();
		if (name.contains("necklace"))
		{
			return false;
		}
		if (name.contains("bracelet"))
		{
			return false;
		}
		if (name.contains("amulet"))
		{
			return false;
		}
		if (name.contains("ring"))
		{
			return false;
		}

		int sum = stats.getAcrush()
			+ stats.getAstab()
			+ stats.getAslash()
			+ stats.getAmagic()
			+ stats.getArange()
			+ stats.getDcrush()
			+ stats.getDmagic()
			+ stats.getDrange()
			+ stats.getDslash()
			+ stats.getDstab();
		return sum > 0;
	}
}
