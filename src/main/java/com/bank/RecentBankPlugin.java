package com.bank;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.bank.BankSearch;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.*;

@Slf4j
@PluginDescriptor(
	name = "Recently Banked Items"
)
public class RecentBankPlugin extends Plugin {
	public static final String CONFIG_GROUP_NAME = "RecentlyBankedItems";
	private static final String ON_RECENT = "Show Recent";
	private static final String OFF_RECENT = "Hide Recent";
	private static final int ITEMS_PER_ROW = 8;
	private static final int ITEM_VERTICAL_SPACING = 36;
	private static final int ITEM_HORIZONTAL_SPACING = 48;
	private static final int ITEM_ROW_START = 51;

	private static final List<Integer> recentIds = new LinkedList<>();
	private static List<Integer> lockedIds = new LinkedList<>();
	private static final String RECENT_ID_KEY = "recentlyBankedIds";
	private static final String LOCKED_ID_KEY = "lockedIds";
	private static final Map<Integer, Integer> bankItemsToAmount = new HashMap<>();

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ConfigManager configManager;

	@Inject
	private RecentBankConfig config;

	@Inject
	private BankSearch bankSearch;

	@Inject
	private KeyManager keyManager;

	@Inject
	private Gson gson;

	@Inject
	private ItemManager itemManager;

	@Override
	protected void startUp() throws Exception {
		keyManager.registerKeyListener(keyListener);
		load();
		log.info("Recently Banked Items started!");
	}

	private void load() {
		String json = configManager.getRSProfileConfiguration(CONFIG_GROUP_NAME, RECENT_ID_KEY);
		if (!Strings.isNullOrEmpty(json)) {
			recentIds.clear();
			recentIds.addAll(gson.fromJson(json, new TypeToken<List<Integer>>() {
			}.getType()));
		}

		json = configManager.getRSProfileConfiguration(CONFIG_GROUP_NAME, LOCKED_ID_KEY);
		if (!Strings.isNullOrEmpty(json)) {
			lockedIds.clear();
			lockedIds.addAll(gson.fromJson(json, new TypeToken<List<Integer>>() {
			}.getType()));
		}
	}

	@Override
	protected void shutDown() throws Exception {
		save();
		reset();
		keyManager.unregisterKeyListener(keyListener);
		clientThread.invokeLater(() -> bankSearch.reset(false));
		log.info("Recently Banked Items stopped!");
	}

	public void reset()
	{
		recentIds.clear();
		lockedIds.clear();
		bankItemsToAmount.clear();
	}

	@Provides
	RecentBankConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RecentBankConfig.class);
	}

	private final KeyListener keyListener = new KeyListener() {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (config.toggleKeybind().matches(e)) {
				Widget bankContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
				if (bankContainer == null || bankContainer.isSelfHidden()) {
					return;
				}

				configManager.setConfiguration(CONFIG_GROUP_NAME, RecentBankConfig.VIEW_TOGGLE, !config.recentViewToggled());
				e.consume();
			}

			if (config.toggleLockKeybind().matches(e)) {
				Widget bankContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
				if (bankContainer == null || bankContainer.isSelfHidden()) {
					return;
				}

				configManager.setConfiguration(CONFIG_GROUP_NAME, RecentBankConfig.LOCK_TOGGLE, !config.lockToggled());
				e.consume();
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}
	};

	private void toggleLock() {
		if (config.lockToggled()) {
			lockedIds = new ArrayList<>(recentIds);
		}
		clientThread.invokeLater(() -> bankSearch.layoutBank());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState().equals(GameState.LOGIN_SCREEN)) {
			save();
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event) {
		if (event.getContainerId() != InventoryID.BANK.getId()) {
			return;
		}
		ItemContainer bank = event.getItemContainer();
		boolean setRecent = !bankItemsToAmount.isEmpty();
		boolean refresh = false;
		Set<Integer> missing = new HashSet<>(bankItemsToAmount.keySet());
		for (Item item : bank.getItems()) {
			int id = getItemId(item.getId());
			if (id < 0) {
				continue;
			}
			int amount = item.getQuantity();
			missing.remove(id);
			if (bankItemsToAmount.getOrDefault(id, -1) != amount) {
				if (setRecent) {
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
		if (refresh && config.recentViewToggled()) {
			bankSearch.layoutBank();
		}
	}

	private int getItemId(int itemId) {
		ItemComposition itemComposition = itemManager.getItemComposition(itemId);
		boolean isPlaceholder = itemComposition.getPlaceholderTemplateId() != -1;
		return isPlaceholder ? itemComposition.getPlaceholderId() : itemComposition.getId();
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		if (event.getType() != MenuAction.CC_OP.getId() || !event.getOption().equals("Show menu")
				|| (event.getActionParam1() >> 16) != WidgetID.BANK_GROUP_ID) {
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
			|| (event.getParam1() >> 16) != WidgetID.BANK_GROUP_ID
			|| !(event.getMenuOption().equals(ON_RECENT)) && !(event.getMenuOption().equals(OFF_RECENT)))
		{
			return;
		}
		configManager.setConfiguration(CONFIG_GROUP_NAME, RecentBankConfig.VIEW_TOGGLE, !config.recentViewToggled());
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (RecentBankConfig.VIEW_TOGGLE.equals(event.getKey())) {
			clientThread.invokeLater(this::toggleView);
		}
		if (RecentBankConfig.LOCK_TOGGLE.equals(event.getKey())) {
			toggleLock();
		}
	}

	public void toggleView() {
		if (config.recentViewToggled()) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "later Set varbit cur bank tab 0", "");
			client.setVarbit(Varbits.CURRENT_BANK_TAB, 0);
		}
		bankSearch.layoutBank();
		client.runScript(ScriptID.UPDATE_SCROLLBAR,
				WidgetInfo.BANK_SCROLLBAR.getId(),
				WidgetInfo.BANK_ITEM_CONTAINER.getId(),
				0);
	}

	public void updateBankTitle() {
		Widget bankTitle = client.getWidget(WidgetInfo.BANK_TITLE_BAR);
		if (bankTitle != null && config.recentViewToggled()) {
			bankTitle.setText("Recent Items" + (config.lockToggled() ? " (locked)" : ""));
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event) {
		if (event.getScriptId() != ScriptID.BANKMAIN_BUILD || !config.recentViewToggled()) {
			return;
		}

		updateBankTitle();

		Widget itemContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
		if (itemContainer == null) {
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
					|| child.getText().contains("Tab")) {
				child.setHidden(true);
			}
		}

		List<Integer> targetIds = config.lockToggled() ? lockedIds : recentIds;

		// hide non-recent items
		for (Widget child : containerChildren) {
			if (child.getItemId() != -1 && !child.isHidden() && !targetIds.contains(getItemId(child.getItemId()))) {
				child.setHidden(true);
				child.revalidate();
			}
		}

		items = 0;
		for (int itemId : targetIds) {
			for (Widget child : containerChildren) {
				if (child.isHidden() || getItemId(child.getItemId()) != itemId) {
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
	}

	@Subscribe
	public void onClientShutdown(ClientShutdown event) {
		save();
	}

	public void save() {
		configManager.setRSProfileConfiguration(CONFIG_GROUP_NAME, RECENT_ID_KEY, gson.toJson(recentIds));
		if (config.lockToggled()) {
			configManager.setRSProfileConfiguration(CONFIG_GROUP_NAME, LOCKED_ID_KEY, gson.toJson(lockedIds));
		} else {
			configManager.unsetRSProfileConfiguration(CONFIG_GROUP_NAME, LOCKED_ID_KEY);
		}
	}
}
