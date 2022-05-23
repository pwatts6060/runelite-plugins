package com.bank;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javafx.scene.input.KeyCode;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup(BankPlugin.CONFIG_GROUP_NAME)
public interface BankConfig extends Config
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
		return new Keybind(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK);
	}
}
