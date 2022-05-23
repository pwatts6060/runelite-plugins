package com.bank;

import com.google.inject.Provides;
import java.awt.event.ContainerEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.ScriptID;
import net.runelite.api.SpriteID;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.bank.BankSearch;

@Slf4j
@PluginDescriptor(
	name = "Recently Banked Items"
)
public class BankPlugin extends Plugin
{
	public static final String CONFIG_GROUP_NAME = "Recently Banked Items";
	private static final String ON_RECENT = "Show Recent";
	private static final String OFF_RECENT = "Hide Recent";
	private static final int ITEMS_PER_ROW = 8;
	private static final int ITEM_VERTICAL_SPACING = 36;
	private static final int ITEM_HORIZONTAL_SPACING = 48;
	private static final int ITEM_ROW_START = 51;

	private static final List<Integer> recentIds = new LinkedList<>();
	private static final Map<Integer, Integer> bankItemsToAmount = new HashMap<>();

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ConfigManager configManager;

	@Inject
	private BankConfig config;

	@Inject
	private BankSearch bankSearch;

	@Inject
	private KeyManager keyManager;

	@Override
	protected void startUp() throws Exception
	{
		keyManager.registerKeyListener(toggleHotKeyListener);
		log.info("Recently Banked Items started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		recentIds.clear();
		bankItemsToAmount.clear();
		keyManager.unregisterKeyListener(toggleHotKeyListener);
		clientThread.invokeLater(() -> bankSearch.reset(false));
		log.info("Recently Banked Items stopped!");
	}

	@Provides
	BankConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankConfig.class);
	}

	private final KeyListener toggleHotKeyListener = new KeyListener()
	{
		@Override
		public void keyTyped(KeyEvent e)
		{
		}

		@Override
		public void keyPressed(KeyEvent e)
		{
			Keybind keybind = config.toggleKeybind();
			if (keybind.matches(e))
			{
				Widget bankContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
				if (bankContainer == null || bankContainer.isSelfHidden())
				{
					return;
				}

				toggleView(true, true);
				e.consume();
			}
		}

		@Override
		public void keyReleased(KeyEvent e)
		{
		}
	};

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.BANK.getId())
		{
			return;
		}
		ItemContainer bank = event.getItemContainer();
		boolean setRecent = !bankItemsToAmount.isEmpty();
		boolean refresh = false;
		Set<Integer> missing = new HashSet<>(bankItemsToAmount.keySet());
		for (Item item : bank.getItems())
		{
			int id = item.getId();
			int amount = item.getQuantity();
			missing.remove(id);
			if (bankItemsToAmount.getOrDefault(id, 0) != amount)
			{
				if (setRecent)
				{
					recentIds.remove((Integer) id);
					recentIds.add(0, id);
					refresh = true;
				}
				bankItemsToAmount.put(id, amount);
			}
		}
		for (int id : missing)
		{
			bankItemsToAmount.remove(id);
			recentIds.remove((Integer) id);
			recentIds.add(0, id);
			refresh = true;
		}
		if (refresh && config.recentViewToggled())
		{
			bankSearch.layoutBank();
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (event.getType() != MenuAction.CC_OP.getId() || !event.getOption().equals("Show menu")
			|| (event.getActionParam1() >> 16) != WidgetID.BANK_GROUP_ID)
		{
			return;
		}

		client.createMenuEntry(1)
			.setOption(config.recentViewToggled() ? OFF_RECENT : ON_RECENT)
			.setTarget("")
			.setType(MenuAction.RUNELITE)
			.setIdentifier(event.getIdentifier())
			.setParam0(event.getActionParam0())
			.setParam1(event.getActionParam1());
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if ((event.getMenuAction() != MenuAction.RUNELITE)
			|| (event.getWidgetId() >> 16) != WidgetID.BANK_GROUP_ID
			|| !(event.getMenuOption().equals(ON_RECENT)) && !(event.getMenuOption().equals(OFF_RECENT)))
		{
			return;
		}
		toggleView(true, false);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (BankConfig.VIEW_TOGGLE.equals(event.getKey()))
		{
			toggleView(false, true);
		}
	}

	private void toggleView(boolean changeConfig, boolean invokeLater)
	{
		if (changeConfig)
		{
			configManager.setConfiguration(CONFIG_GROUP_NAME, BankConfig.VIEW_TOGGLE, !config.recentViewToggled());
		}

		if (invokeLater)
		{
			clientThread.invokeLater(() -> bankSearch.layoutBank());
		}
		else
		{
			bankSearch.layoutBank();
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() != ScriptID.BANKMAIN_BUILD || !config.recentViewToggled())
		{
			return;
		}

		Widget itemContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
		if (itemContainer == null)
		{
			return;
		}

		int items = 0;

		Widget[] containerChildren = itemContainer.getDynamicChildren();

		// sort the child array as the items are not in the displayed order
		Arrays.sort(containerChildren, Comparator.comparingInt(Widget::getOriginalY)
			.thenComparingInt(Widget::getOriginalX));

		for (Widget child : containerChildren)
		{
			if (child.getItemId() != -1 && !child.isHidden())
			{
				// calculate correct item position as if this was a normal tab
				int adjYOffset = (items / ITEMS_PER_ROW) * ITEM_VERTICAL_SPACING;
				int adjXOffset = (items % ITEMS_PER_ROW) * ITEM_HORIZONTAL_SPACING + ITEM_ROW_START;

				if (child.getOriginalY() != adjYOffset || child.getOriginalX() != adjXOffset)
				{
					child.setOriginalY(adjYOffset);
					child.setOriginalX(adjXOffset);
					child.revalidate();
				}

				items++;
			}

			// separator line or tab text
			if (child.getSpriteId() == SpriteID.RESIZEABLE_MODE_SIDE_PANEL_BACKGROUND
				|| child.getText().contains("Tab"))
			{
				child.setHidden(true);
			}
		}

		// hide non recent items
		for (Widget child : containerChildren)
		{
			if (child.getItemId() != -1 && !child.isHidden() && !recentIds.contains(child.getItemId()))
			{
				child.setHidden(true);
				child.revalidate();
			}
		}

		items = 0;
		for (int itemId : recentIds)
		{
			for (Widget child : containerChildren)
			{
				if (child.isHidden() || child.getItemId() != itemId)
				{
					continue;
				}

				// calculate correct item position as if this was a normal tab
				int adjYOffset = (items / ITEMS_PER_ROW) * ITEM_VERTICAL_SPACING;
				int adjXOffset = (items % ITEMS_PER_ROW) * ITEM_HORIZONTAL_SPACING + ITEM_ROW_START;

				if (child.getOriginalY() != adjYOffset || child.getOriginalX() != adjXOffset)
				{
					child.setOriginalY(adjYOffset);
					child.setOriginalX(adjXOffset);
					child.revalidate();
				}

				items++;
				break;
			}
		}

		// set scroll bar to top
		clientThread.invokeLater(() ->
			client.runScript(ScriptID.UPDATE_SCROLLBAR,
				WidgetInfo.BANK_SCROLLBAR.getId(),
				WidgetInfo.BANK_ITEM_CONTAINER.getId(),
				0)
		);
	}
}
