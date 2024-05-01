package com.lmsnotifier;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.game.ItemManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ItemUpgrade {

    private final Client client;
    private final LMSConfig config;
    private final LMSPlugin plugin;
    private ItemManager itemManager;

    @Inject
    private ItemUpgrade(Client client, LMSConfig config, LMSPlugin plugin, ItemManager itemManager) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.itemManager = itemManager;
    }

    private static Set<Integer> ignoreIds;
    static {
        ignoreIds = new HashSet<>(Arrays.asList(
                ItemID.OPAL_DRAGON_BOLTS_E_27192,
                ItemID.BONES,
                ItemID.DIAMOND_BOLTS_E_23649,
                ItemID.AHRIMS_STAFF_23653,
                ItemID.ANCIENT_STAFF_20431,
                ItemID.DRAGON_DAGGER_20407,
                ItemID.MYSTIC_ROBE_BOTTOM_20426,
                ItemID.MYSTIC_ROBE_BOTTOM_DARK_27159,
                ItemID.MYSTIC_ROBE_BOTTOM_LIGHT_27161,
                ItemID.MYSTIC_ROBE_TOP_20425,
                ItemID.MYSTIC_ROBE_TOP_DARK_27158,
                ItemID.MYSTIC_ROBE_TOP_LIGHT_27160,
                ItemID.CLIMBING_BOOTS_20578,
                ItemID.SPIRIT_SHIELD_23599,
                ItemID.RUNE_CROSSBOW_23601,
                ItemID.GHOSTLY_HOOD_27166,
                ItemID.GHOSTLY_ROBE_27167,
                ItemID.GHOSTLY_ROBE_27168,
                ItemID.IMBUED_GUTHIX_CAPE_23603,
                ItemID.IMBUED_ZAMORAK_CAPE_23605,
                ItemID.IMBUED_SARADOMIN_CAPE_23607,
                ItemID.RUNE_PLATELEGS_20422,
                ItemID.BLACK_DHIDE_BODY_20423,
                ItemID.SANFEW_SERUM4_23559,
                ItemID.SANFEW_SERUM3_23561,
                ItemID.SANFEW_SERUM2_23563,
                ItemID.SANFEW_SERUM1_23565,
                ItemID.SHARK_20390,
                ItemID.BERSERKER_RING_23595,
                ItemID.HELM_OF_NEITIZNOT_23591,
                ItemID.ABYSSAL_WHIP_20405,
                ItemID.AMULET_OF_GLORY_20586,
                ItemID.DRAGON_ARROW_20389,
                ItemID.ROPE_20587
        ));
    }

    // set of item ids that player has had access to during this game.
    private final Set<Integer> foundItems = new HashSet<>();

    public boolean notifyItem(ItemSpawned itemSpawned, ItemContainer inv, ItemContainer equip) {
        int id = itemSpawned.getItem().getId();

        if (ignoreIds.contains(id) || foundItems.contains(id) || plugin.floorItems.stream().anyMatch(itemSpawned1 -> itemSpawned1.getTileItem().getId() == id)) {
            return false;
        }

        if (id == ItemID.MORRIGANS_JAVELIN_23619 && itemSpawned.getItem().getQuantity() < 5) {
            return false;
        }

        if (has(id, inv, equip)) {
            return false;
        }
        return true;
    }

    private boolean has(int id, ItemContainer inv, ItemContainer equip) {
        return inv.contains(id) || equip.contains(id);
    }

    public void reset() {
        foundItems.clear();
        for (LMSPlugin.FloorItem floorItem : plugin.floorItems) {
            plugin.infoBoxManager.removeInfoBox(floorItem.getInfoBox());
        }
        plugin.floorItems.clear();
    }

    public void droppedItem(int itemId) {
        foundItems.add(itemId);
        Iterator<LMSPlugin.FloorItem> it = plugin.floorItems.iterator();
        while (it.hasNext()) {
            LMSPlugin.FloorItem floorItem = it.next();
            if (floorItem.getTileItem().getId() == itemId) {
                plugin.infoBoxManager.removeInfoBox(floorItem.getInfoBox());
                it.remove();
            }
        }
    }

    public void despawn(ItemDespawned event) {
        Iterator<LMSPlugin.FloorItem> it = plugin.floorItems.iterator();
        while (it.hasNext()) {
            LMSPlugin.FloorItem floorItem = it.next();
            if (floorItem.getTileItem() == event.getItem() || floorItem.getTile() == event.getTile()) {
                plugin.infoBoxManager.removeInfoBox(floorItem.getInfoBox());
                it.remove();
            }
        }
    }
}
