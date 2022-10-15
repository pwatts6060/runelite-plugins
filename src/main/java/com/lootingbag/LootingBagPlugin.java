package com.lootingbag;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
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
	private static final Set<Integer> FEROX_REGION = ImmutableSet.of(12600, 12344);
	private static final int LOOTING_BAG_SIZE = 28;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private LootingBagOverlay overlay;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	@Inject
	private LootingBagConfig config;

	private final Map<Integer, Integer> bagItems = new HashMap<>();
	private int freeSlots = -1;
	private long value = -1;
	private boolean atleast = false; // true if looting bag might be more valuable than value suggests

	private PickupAction lastPickUpAction;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		if (widgetLoaded.getGroupId() != WidgetInfo.LOOTING_BAG_CONTAINER.getGroupId())
		{
			return;
		}
		updateValue();
	}

	private void updateValue()
	{
		ItemContainer itemContainer = client.getItemContainer(LOOTING_BAG_CONTAINER);
		if (itemContainer == null) {
			value = 0;
			freeSlots = LOOTING_BAG_SIZE;
			return;
		}
		long newValue = 0;
		bagItems.clear();
		freeSlots = LOOTING_BAG_SIZE;
		for (Item item : itemContainer.getItems()) {
			if (item.getId() >= 0) {
				bagItems.merge(item.getId(), item.getQuantity(), Integer::sum);
				newValue += getPrice(item.getId()) * item.getQuantity();
				freeSlots--;
			}
		}
		value = newValue;
		atleast = false;
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event) {
		if (event.getContainerId() != LOOTING_BAG_CONTAINER) {
			return;
		}
		updateValue();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if (event.getMenuAction() != MenuAction.GROUND_ITEM_THIRD_OPTION) {
			return;
		}
		if (!event.getMenuOption().equals("Take")) {
			return;
		}
		WorldPoint point = WorldPoint.fromScene(client, event.getParam0(), event.getParam1(), client.getPlane());
		lastPickUpAction = new PickupAction(event.getId(), point);
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned event) {
		if (value < 0) {
			return;
		}

		// not in wilderness or ferox -> can't pickup items directly into looting bag
		if (client.getVarbitValue(Varbits.IN_WILDERNESS) == 0
			&& !FEROX_REGION.contains(client.getLocalPlayer().getWorldLocation().getRegionID())) {
			return;
		}

		// doesn't have open looting bag
		ItemContainer inv = client.getItemContainer(InventoryID.INVENTORY);
		if (inv == null || !inv.contains(ItemID.LOOTING_BAG_22586)) {
			return;
		}

		if (lastPickUpAction == null) {
			return;
		}

		// not on same tile
		if (!event.getTile().getWorldLocation().equals(client.getLocalPlayer().getWorldLocation())) {
			return;
		}

		if (!event.getTile().getWorldLocation().equals(lastPickUpAction.getWorldPoint())) {
			return;
		}

		int itemId = event.getItem().getId();

		if (itemId != lastPickUpAction.getItemId()) {
			return;
		}

		ItemComposition itemComposition = itemManager.getItemComposition(itemId);

		if (!itemComposition.isTradeable()) {
			return;
		}

		int quantity = event.getItem().getQuantity();
		if (quantity >= 65535) {
			atleast = true;
		}
		value += getPrice(itemId) * quantity;

		if (!bagItems.containsKey(itemId) || !itemComposition.isStackable())
		{
			freeSlots--;
		}
		bagItems.merge(itemId, quantity, Integer::sum);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (value > 0 && event.getGroup().equals(RuneLiteConfig.GROUP_NAME)
				&& event.getKey().equals("useWikiItemPrices")) {
			clientThread.invokeLater(() -> {
				long newValue = 0;
				for (Map.Entry<Integer, Integer> entry : bagItems.entrySet()) {
					int itemId = entry.getKey();
					int quantity = entry.getValue();
					newValue += getPrice(itemId) * quantity;
				}
				value = newValue;
			});
		}
	}

	public long getPrice(int itemId) {
		return itemManager.getItemPrice(itemId);
	}

	@Provides
	LootingBagConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LootingBagConfig.class);
	}

	public String getValueText()
	{
		if (value < 0)
		{
			return "Check";
		}
		String text = atleast ? ">" : "";
		if (value >= 10_000_000)
		{
			return text + value / 1_000_000 + "M";
		}
		if (value >= 100_000)
		{
			return text + value / 1000 + "k";
		}
		return text + value;
	}

	public String getFreeSlotsText() {
		if (freeSlots < 0) {
			return "Check";
		}
		return Integer.toString(freeSlots);
	}
}
