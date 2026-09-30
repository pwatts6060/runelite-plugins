package com.lootingbag;

import com.google.common.collect.ImmutableSet;
import net.runelite.client.game.ItemManager;

import static net.runelite.api.ItemID.*;

public class RoguesChestItems {
	private final ItemManager itemManager;
	final ImmutableSet<Object[]> ITEM_POOL;

	public RoguesChestItems(ItemManager itemManager)
	{
		this.itemManager = itemManager;

		ITEM_POOL = ImmutableSet.of(
			/*
				[0] ITEM_ID
				[1] NOTED (0/1)
				[2] COMMON_NAME
				[3] MEDIUM_DIARY_QUANTITY
				[4] HARD_DIARY_QUANTITY
			 */
			// https://oldschool.runescape.wiki/w/Chest_(Rogues%27_Castle)
			// "COMMON_NAME" field sourced/confirmed from in-game messages directly.

			// The following items are gained from the chest *unnoted*.
			new Object[] {NATURE_RUNE, 0, "nature runes", 40, 50},
			new Object[] {LAW_RUNE, 0,"law runes", 40, 50},
			new Object[] {COINS, 0, "coins", 4500, 5625},
			new Object[] {BLIGHTED_ANCIENT_ICE_SACK, 0, "blighted ancient ice sacks", 13, 16},
			new Object[] {PRAYER_POTION2, 0, "prayer potion", 1, 1},
			new Object[] {CHAOS_RUNE, 0, "chaos runes", 60, 75},
			new Object[] {DEATH_RUNE, 0, "death runes", 50, 62},

			// The following items are gained from the chest *noted*.
			new Object[] {RED_SPIDERS_EGGS, 1, "red spider eggs", 6, 7},
			new Object[] {COAL, 1, "coal", 20, 25},
			new Object[] {VILE_ASHES, 1, "vile ashes", 15, 18},
			// NOTE: Uncut diamond can either be dropped at 3/5 for med diary,
			// or 3/6 for hard diary. There is no way to distinguish these two
			// drops from the chat, so we have to compromise here and just
			// use the lower of the two drop values.
			new Object[] {UNCUT_DIAMOND, 1, "uncut diamonds", 3, 3},
			new Object[] {UNCUT_EMERALD, 1, "uncut emeralds", 10, 12},
			new Object[] {IRON_ORE, 1, "iron ore", 40, 50},
			new Object[] {BLIGHTED_MANTA_RAY, 1, "blighted manta rays", 20, 25},
			new Object[] {BLIGHTED_ANGLERFISH, 1, "blighted anglerfish", 15, 18},
			new Object[] {UNCUT_SAPPHIRE, 1, "uncut sapphires", 15, 18},
			new Object[] {DRAGONSTONE, 1, "dragonstones", 2, 2}
			// Clue scroll (hard) is ignored, can't be put in a looting bag
		);
	}

	private Integer getNotedId(Integer id) {
		return itemManager.getItemComposition(id).getLinkedNoteId();
	}

	public Object[] getItemFromMatchedString(String match) {
		for (Object[] item : ITEM_POOL) {
			String commonName = (String) item[2];
			if (commonName.equals(match)) {
				// Check if the item ID should be in noted form
				if ((Integer) item[1] == 1) {
					Object[] notedItem = item.clone();
					notedItem[0] = getNotedId((Integer) item[0]);
					return notedItem;
				}
				return item;
			}
		}
		return null;
	}
}