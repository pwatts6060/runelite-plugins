package com.instantnotify;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@PluginDescriptor(
        name = "Instant Idle Notify"
)
public class InstantNotifyPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private Notifier notifier;

    @Inject
    private InstantNotifyConfig config;

    @Inject
    private ClientThread clientThread;

    private Map<Integer, Integer> itemAmounts;
    private Map<Integer, Integer> changedItems = new HashMap<>();
    private int lastTickDelay;
    private int lastTick;
    private int cyclesRepeated;

    @Override
    protected void startUp() throws Exception {
        log.info("Instant Idle Notify started!");
        lastTickDelay = -1;
        cyclesRepeated = 0;
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Instant Idle Notify stopped!");
        itemAmounts = null;
        changedItems.clear();
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged) {
        if (itemContainerChanged.getContainerId() == InventoryID.INVENTORY.getId()) {
            invChange(itemContainerChanged);
        }
    }

    private void loadInv() {
        itemAmounts = new HashMap<>();
        ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
        if (itemContainer == null) {
            return;
        }
        for (Item item : itemContainer.getItems()) {
            if (item.getId() >= 0)
                itemAmounts.merge(item.getId(), item.getQuantity(), Integer::sum);
        }
    }

    private void invChange(ItemContainerChanged itemContainerChanged) {
        if (itemAmounts == null) {
            loadInv();
            return;
        }

        if (client.getWidget(WidgetInfo.BANK_CONTAINER) != null) {
            return;
        }

        // load new inv
        Map<Integer, Integer> newItemAmounts = new HashMap<>();
        Map<Integer, Integer> newChangedItems = new HashMap<>();
        for (Item item : itemContainerChanged.getItemContainer().getItems()) {
            if (item.getId() < 0)
                continue;
            newItemAmounts.merge(item.getId(), item.getQuantity(), Integer::sum);
        }

        // compute changed items
        newItemAmounts.forEach((key, value) -> {
            int dif = value - itemAmounts.getOrDefault(key, 0);
            if (dif != 0) {
                newChangedItems.put(key, dif);
            }
        });
        itemAmounts.forEach((key, value) -> {
            if (!newItemAmounts.containsKey(key)) {
                int dif = newItemAmounts.getOrDefault(key, 0) - value;
                if (dif != 0) {
                    newChangedItems.put(key, dif);
                }
            }
        });

        int tickDelay = client.getTickCount() - lastTick;

        // if the same type of item exchange happened and delay is same as before
        // effectively requires at least 3 of the action to have lastTickDelay and tickDelay match
        if (changedItems.equals(newChangedItems) && lastTickDelay == tickDelay) {
            cyclesRepeated++;
            // check if current action no longer possible
            for (Map.Entry<Integer, Integer> entry : newChangedItems.entrySet()) {
                int id = entry.getKey();
                int amount = entry.getValue();
                if (amount < 0 && newItemAmounts.getOrDefault(id, 0) < -amount) {
                    playNotification();
                }
            }
        } else {
            cyclesRepeated = 0;
        }

        lastTickDelay = tickDelay;
        lastTick = client.getTickCount();
        itemAmounts = newItemAmounts;
        changedItems = newChangedItems;
    }

    private void playNotification() {
        NotificationType type = config.notificationType();
        if (type == NotificationType.RUNELITE || type == NotificationType.BOTH) {
            notifier.notify("Instant Idle Notify");
        }
        if (type == NotificationType.SOUND || type == NotificationType.BOTH) {
            clientThread.invoke(() -> client.playSoundEffect(config.soundId(), config.volume()));
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
        String menuOption = menuOptionClicked.getMenuOption();
        if (menuOption.equals("Drop")
                || menuOption.startsWith("Withdraw")
                || menuOption.startsWith("Deposit")
                || menuOption.equals("Eat")
                || menuOption.equals("Scatter")
                || menuOption.equals("Bury")
                || menuOption.equals("Drink")) {
            lastTickDelay = -1; // Reset the current action
        }
    }

    @Subscribe
    protected void onConfigChanged(ConfigChanged configChanged) {
        if (!InstantNotifyConfig.configGroup.equals(configChanged.getGroup())) {
            return;
        }
        if (InstantNotifyConfig.soundId.equals(configChanged.getKey())) {
            clientThread.invoke(() -> client.playSoundEffect(config.soundId(), config.volume()));
        }
    }

    @Provides
    InstantNotifyConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(InstantNotifyConfig.class);
    }
}
