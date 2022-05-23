package com.bank;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup(RecentBankPlugin.CONFIG_GROUP_NAME)
public interface RecentBankConfig extends Config
{
	String VIEW_TOGGLE = "viewToggle";
	String TOGGLE_KEYBIND = "toggleKeybind";

	@ConfigItem(
		keyName = VIEW_TOGGLE,
		name = "Toggle Recent View",
		description = "Toggle showing recently banked items in bank"
	)
	default boolean recentViewToggled()
	{
		return false;
	}

	@ConfigItem(
		keyName = TOGGLE_KEYBIND,
		name = "Toggle Keybind",
		description = "Keybind to toggle recent items view in bank"
	)
	default Keybind toggleKeybind()
	{
		return new Keybind(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK);
	}
}
