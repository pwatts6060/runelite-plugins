package com.lootingbag;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.VarClientInt;
import net.runelite.api.VarClientStr;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarClientIntChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Looting Bag"
)
public class LootingBagPlugin extends Plugin
{
	public static final int LOOTING_BAG_CONTAINER = 516;

	private static final int LOOTING_BAG_SUPPLIES_SETTING_VARBIT_ID = 15310;

	private static final Map<String, Integer> AmountTextToInt = ImmutableMap.of(
		"One", 1,
		"Two", 2,
		"Both", 2,
		"Five", 5
	);

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private LootingBagOverlay overlay;

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Getter
	private LootingBag lootingBag;

	private PickupAction lastPickUpAction;

	private int lastItemIdUsedOnLootingBag;

	/**
	 * Used to keep track of whether the deposit X input is open.
	 */
	private boolean depositingX;

	/**
	 * The last amount entered into the deposit X interface
	 */
	private int lastDepositedXAmount;

	private int lastInputTypeValue;

	private Item[] lastInventoryItems;

	private ArrayList<PickupAction> possibleSuppliesPickupActions;

	@Override
	protected void startUp()
	{
		lootingBag = new LootingBag(client, itemManager);
		overlayManager.add(overlay);
		possibleSuppliesPickupActions = new ArrayList<>();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		possibleSuppliesPickupActions.removeIf(action -> {
			if (action.getTicksSincePickup() >= 1) {
				// Whatever we picked up went into our looting bag
				log.debug("Item was not supply and got added to looting bag: " + getItemName(action.getItemId()));
				lootingBag.addItem(
					action.getItemId(),
					action.getQuantity());
				return true;
			}

			action.incrementTicksSincePickup();
			return false;
		});
	}

	@Subscribe
	public void onVarClientIntChanged(VarClientIntChanged event) {
		if (event.getIndex() == VarClientInt.INPUT_TYPE) {
			int value = client.getVarcIntValue(VarClientInt.INPUT_TYPE);
			String text = client.getVarcStrValue(VarClientStr.INPUT_TEXT);

			// Number input closed
			if (value == 0) {
				// Make sure we were depositing X
				if (depositingX && !text.equals("")) {
					lastDepositedXAmount = Integer.parseInt(text);
				}

				// If the number input was previously open but is now closed,
				//		We are no longer depositing
				if (lastInputTypeValue == 7) {
					depositingX = false;
				}
			}

			lastInputTypeValue = value;
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.LOOTING_BAG)
		{
			// We can use the ItemContainer as a source of truth!
			ItemContainer lootingBagContainer = client.getItemContainer(LOOTING_BAG_CONTAINER);
			lootingBag.syncItems(lootingBagContainer);
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event) {
		if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
			handleInventoryUpdated(event.getItemContainer());
		}

		if (event.getContainerId() == LOOTING_BAG_CONTAINER) {
			lootingBag.syncItems(event.getItemContainer());
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		Widget widget = event.getWidget();

		// Select amount to deposit in looting bag menu
		if (event.getMenuAction() == MenuAction.WIDGET_CONTINUE
				&& widget != null
				&& widget.getParentId() == ComponentID.DIALOG_OPTION_OPTIONS
				&& depositingX) {
			handleAmountToDepositSelection(widget.getText());
		}

		// Use an item on another item
		if (event.getMenuAction() == MenuAction.WIDGET_TARGET_ON_WIDGET
				&& event.getMenuOption().equals("Use")) {
			Widget selectedWidget = client.getSelectedWidget();
			if (selectedWidget == null)
			{
				return;
			}

			log.debug("Using item " + itemManager.getItemComposition(selectedWidget.getItemId()).getName()
				+ " on item " + itemManager.getItemComposition(event.getItemId()).getName());
			handleItemUsedOnItem(selectedWidget.getItemId(), event.getItemId());
			return;
		}

		// Take an item off the ground
		if (event.getMenuAction() == MenuAction.GROUND_ITEM_THIRD_OPTION
				&& event.getMenuOption().equals("Take")) {
			WorldPoint point = WorldPoint.fromScene(client, event.getParam0(), event.getParam1(), client.getPlane());
			lastPickUpAction = new PickupAction(event.getId(), point);
		}
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned event) {
		// Check if player has open looting bag in inventory
		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		if (inventory == null || !inventory.contains(ItemID.LOOTING_BAG_22586)) {
			return;
		}

		// Check that this event matches the last pickup action
		if (lastPickUpAction == null || !lastPickUpAction.matchesItemDespawnEvent(event)) {
			return;
		}

		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		WorldPoint groundItemLocation = event.getTile().getWorldLocation();

		// Check that the item despawned on the same tile the player is on
		if (!playerLocation.equals(groundItemLocation)) {
			return;
		}

		int itemId = event.getItem().getId();
		int quantity = event.getItem().getQuantity();
		ItemComposition itemComposition = itemManager.getItemComposition(itemId);

		// This might be a "supply"
		if (doSuppliesGoIntoInventory() && !itemComposition.isStackable()) {
			log.debug("Possibly picked up a supply: " + itemComposition.getName());
			lastPickUpAction.setQuantity(quantity);
			possibleSuppliesPickupActions.add(lastPickUpAction);
			return;
		}

		// We've picked up an item into our looting bag!
		lastPickUpAction = null;
		boolean isQuantityConfirmed = quantity < 65535;
		lootingBag.addItem(
			event.getItem().getId(),
			quantity,
			isQuantityConfirmed
		);
	}

	private void handleInventoryUpdated(ItemContainer inventory) {

		// We've deposited X
		if (lastDepositedXAmount > 0) {
			int numAddedToInventory = getNumberOfItemsAddedToInventory(
				inventory,
				lastItemIdUsedOnLootingBag
			);

			// The amount of the item we expected got removed from the inventory
			if (numAddedToInventory == -lastDepositedXAmount) {
				lootingBag.addItem(lastItemIdUsedOnLootingBag, lastDepositedXAmount);
				lastDepositedXAmount = 0;
				depositingX = false;
			}
		}



		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		if (doSuppliesGoIntoInventory()) {
			OptionalInt matchingActionIndex = IntStream.range(0, possibleSuppliesPickupActions.size())
				.filter(index -> {
					PickupAction action = possibleSuppliesPickupActions.get(index);
					if (!action.getWorldPoint().equals(playerLocation)) {
						return true;
					}

					int numAddedToInventory = getNumberOfItemsAddedToInventory(
						inventory,
						action.getItemId()
					);

					if (numAddedToInventory > 0) {
						log.debug(numAddedToInventory + " " + getItemName(action.getItemId()) + " got added to inventory");
					}

					// We picked up a supply, and it didn't go into the looting bag
					if (numAddedToInventory == 1) {
						log.debug("Supply got added to inventory: " + getItemName(action.getItemId()));
						return true;
					}

					return false;
				})
				.findFirst();

			if (matchingActionIndex.isPresent())
			{
				List<PickupAction> subListToRemove = possibleSuppliesPickupActions.subList(0, matchingActionIndex.getAsInt() + 1);
				log.debug("Clearing the following pickup actions: " + new Gson().toJson(subListToRemove.stream().map(action -> getItemName(action.getItemId())).collect(Collectors.toList())));
				subListToRemove.clear();
			}
		}

		lastInventoryItems = inventory.getItems();
	}

	private int getNumberOfItemsAddedToInventory(ItemContainer inventory, int itemId) {
		int lastCount = Arrays.stream(lastInventoryItems)
			.filter(item -> item.getId() == itemId)
			.mapToInt(Item::getQuantity)
			.sum();
		int newCount = inventory.count(itemId);
		return newCount - lastCount;
	}

	private void handleItemUsedOnItem(int itemId1, int itemId2) {
		if (!isLootingBag(itemId1) && !isLootingBag(itemId2)) {
			return;
		}

		int itemId = isLootingBag(itemId1)
			? itemId2
			: itemId1;

		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		if (inventory == null) {
			log.error("Could not get inventory ItemContainer when using item on looting bag.");
			return;
		}

		int count = inventory.count(itemId);
		if (count == 1)
		{
			lootingBag.addItem(itemId, 1);
			return;
		}

		lastItemIdUsedOnLootingBag = itemId;
		lastDepositedXAmount = count;
	}

	private void handleAmountToDepositSelection(String amountText) {
		if (AmountTextToInt.containsKey(amountText)) {
			int amount = AmountTextToInt.get(amountText);
			lootingBag.addItem(lastItemIdUsedOnLootingBag, amount);
			return;
		}

		if (amountText.equals("All"))
		{
			ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
			if (inventory == null) {
				log.error("Could not get inventory ItemContainer when selected put 'All' into looting bag.");
				return;
			}

			int amount = inventory.count(lastItemIdUsedOnLootingBag);
			lootingBag.addItem(lastItemIdUsedOnLootingBag, amount);
			return;
		}

		if (amountText.equals("X")) {
			depositingX = true;
			return;
		}

		log.error("Unknown item amount '{}' selected when depositing to looting bag.", amountText);
	}

	@Provides
	LootingBagConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LootingBagConfig.class);
	}

	private String getItemName(int itemId) {
		return itemManager.getItemComposition(itemId).getName();
	}

	private boolean doSuppliesGoIntoInventory() {
		return client.getVarbitValue(LOOTING_BAG_SUPPLIES_SETTING_VARBIT_ID) == 1;
	}

	private boolean isLootingBag(int itemId)
	{
		return itemId == ItemID.LOOTING_BAG
			|| itemId == ItemID.LOOTING_BAG_22586;
	}
}
