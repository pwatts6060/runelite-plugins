package com.bank;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup(RecentBankPlugin.CONFIG_GROUP_NAME)
public interface RecentBankConfig extends Config {
    String VIEW_TOGGLE = "viewToggle";
    String LOCK_TOGGLE = "lockToggle";
    String TOGGLE_KEYBIND = "toggleKeybind";
    String LOCK_KEYBIND = "lockKeybind";

    @ConfigItem(
            keyName = VIEW_TOGGLE,
            name = "Toggle Recent View",
            position = 1,
            description = "Toggle showing recently banked items in bank"
    )
    default boolean recentViewToggled() {
        return false;
    }

    @ConfigItem(
            keyName = TOGGLE_KEYBIND,
            name = "Toggle View Keybind",
            position = 2,
            description = "Keybind to toggle recent items view in bank"
    )
    default Keybind toggleKeybind() {
        return new Keybind(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK);
    }

    @ConfigItem(
            keyName = LOCK_TOGGLE,
            name = "Lock Recent View",
            position = 3,
            description = "Delays withdrawing/depositing reordering the recent bank view until unlocked"
    )
    default boolean lockToggled() {
        return false;
    }

    @ConfigItem(
            keyName = LOCK_KEYBIND,
            name = "Toggle Lock Keybind",
            position = 4,
            description = "Keybind to toggle locking items in recent view so they don't update"
    )
    default Keybind toggleLockKeybind() {
        return new Keybind(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK);
    }
}
