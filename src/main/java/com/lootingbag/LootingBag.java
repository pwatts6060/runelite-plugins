package com.lootingbag;

import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.Varbits;
import net.runelite.client.game.ItemManager;

@Slf4j
public class LootingBag
{
	private static final int LOOTING_BAG_SIZE = 28;

	private static final Set<Integer> FEROX_REGION = ImmutableSet.of(12600, 12344);

	private final ItemManager itemManager;

	private final Client client;

	private final Map<Integer, Integer> items;

	@Getter
	private boolean isSynced;

	@Getter
	private boolean isQuantityOfItemsAccurate = true;

	@Getter
	private long valueOfItems = 0;

	public LootingBag(Client client, ItemManager itemManager)
	{
		this.client = client;
		this.itemManager = itemManager;
		this.items = new HashMap<>();
		this.isSynced = false;
	}

	public void addItem(
			int itemId,
			int quantity) {
		addItem(itemId, quantity, true);
	}

	public void addItem(
			int itemId,
			int quantity,
			boolean isQuantityConfirmed) {

		// Check that we can deposit any item into looting bag
		// 		E.g. We're in the wilderness
		if (!canDepositItems()) {
			return;
		}

		// Check that the item can go in the looting bag
		//		E.g. It's tradeable
		if (!canItemGoInLootingBag(itemId)) {
			log.debug("Item can not go in looting bag: " + itemManager.getItemComposition(itemId).getName());
			return;
		}

		ItemComposition itemComposition = itemManager.getItemComposition(itemId);

		// Check that we have room in the looting bag
		if (getFreeSlots() == 0
				&& (!itemComposition.isStackable() || !items.containsKey(itemId))) {
			return;
		}

		if (!isQuantityConfirmed) {
			isQuantityOfItemsAccurate = false;
		}

		log.debug("Successfully added item to looting bag: " + itemComposition.getName() + "x" + quantity);
		items.merge(itemId, quantity, Integer::sum);
		calculateValueOfItems();
	}

	public int getFreeSlots()
	{
		return LOOTING_BAG_SIZE -
			items.keySet().stream()
				.mapToInt(itemId ->
					itemManager.getItemComposition(itemId).isStackable()
						? 1
						: items.get(itemId)
				).sum();
	}

	public void syncItems(ItemContainer lootingBagContainer) {
		items.clear();

		// The looting bag container will be null when it is empty
		if (lootingBagContainer == null) {
			isSynced = true;
			calculateValueOfItems();
			return;
		}

		items.putAll(Arrays.stream(lootingBagContainer.getItems())
			.reduce(
				new HashMap<>(),
				(map, item) -> {
					map.merge(item.getId(), item.getQuantity(), Integer::sum);
					return map;
				},
				(map1, map2) -> {
					map1.putAll(map2);
					return map1;
				}));

		calculateValueOfItems();
		isQuantityOfItemsAccurate = true;
		isSynced = true;
	}

	private boolean canItemGoInLootingBag(int itemId) {
		return isItemTradeable(itemId)
			|| isItemTradeable(itemManager.getItemComposition(itemId).getLinkedNoteId());
	}

	private boolean isItemTradeable(int itemId) {
		ItemComposition itemComposition = itemManager.getItemComposition(itemId);

		return itemComposition.isTradeable() // GE tradeable items
			|| itemComposition.getName().matches("Ensouled [a-z]+ head")
			|| GeUntradables.ItemIds.contains(itemId);
	}

	private boolean canDepositItems() {
		// Can't deposit items into looting bag if not in wilderness or Ferox
		return client.getVarbitValue(Varbits.IN_WILDERNESS) != 0
			|| FEROX_REGION.contains(client.getLocalPlayer().getWorldLocation().getRegionID());
	}

	private void calculateValueOfItems() {
		valueOfItems = items.keySet().stream()
			.mapToLong(itemId -> getPriceOfItem(itemId, items.get(itemId)))
			.sum();
	}

	private long getPriceOfItem(int itemId, int quantity) {
		return itemManager.getItemPrice(itemId) * (long) quantity;
	}
}
