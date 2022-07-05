package com.entrana;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;

import java.util.HashMap;
import java.util.Map;

public class ProhibitedItems
{
	private static final Map<Integer, Boolean> cachedIds = new HashMap<>();
	private final ItemManager itemManager;

	public ProhibitedItems(ItemManager itemManager)
	{
		this.itemManager = itemManager;
	}

	boolean isProhibited(int id)
	{
		if (AllowedIDs.contains(id))
		{
			return false;
		}
		if (ProhibitedIDs.contains(id))
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
		if (stats.getStr() > 0)
		{
			return true;
		}
		return false;
	}
}
