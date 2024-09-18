package com.counter;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import java.awt.Color;

@ConfigGroup("counter")
public interface CounterConfig extends Config
{
	@ConfigItem(
			position = 1,
			keyName = "enableCounter",
			name = "Counter",
			description = "Enable counter"
	)
	default boolean enableCounter()
	{
		return true;
	}

	@ConfigItem(
			position = 2,
			keyName = "highlightCurrentTile",
			name = "Enable True Tile Overlay",
			description = "Highlights true player tile using the counter colors (replacement for tile indicator plugin setting)"
	)
	default boolean highlightCurrentTile()
	{
		return false;
	}


	@Range(
			min = 16
	)
	@ConfigItem(
			position = 3,
			keyName = "boxWidth",
			name = "Default Box Size (Alt + Right Click Box)",
			description = "Configure the default length and width of the box. Use alt + right click on the box to reset to the size specified"
	)
	default int boxWidth()
	{
		return 25;
	}

	@Range(
			min = 1
	)
	@ConfigItem(
			position = 4,
			keyName = "tickCount",
			name = "Tick Count",
			description = "The tick on which the color changes"
	)
	default int tickCount()
	{
		return 1;
	}

	@ConfigSection(
			name = "Number Settings",
			description = "Change Number settings",
			position = 5
	)
	String NumberSettings = "Number Settings";

	@ConfigItem(
			position = 1,
			keyName = "showCount",
			name = "Show Counter Tick Number",
			description = "Shows current number on the counter",
			section = NumberSettings
	)
	default boolean showCount()
	{
		return false;
	}

	@ConfigItem(
			position = 2,
			keyName = "showPlayerCount",
			name = "Show Number Above Player",
			description = "Shows number above the player",
			section = NumberSettings
	)
	default boolean showPlayerNumber()
	{
		return false;
	}

	@ConfigItem(
			position = 3,
			keyName = "disableFontScaling",
			name = "Disable Font Size Scaling (counter number Only)",
			description = "Disables font size scaling for counter number",
			section = NumberSettings
	)
	default boolean disableFontScaling()
	{
		return false;
	}

	@Range(
			min = 8,
			max = 50
	)
	@ConfigItem(
			position = 4,
			keyName = "fontSize",
			name = "Font Size (Overhead Number Only)",
			description = "Change the font size of the overhead number",
			section = NumberSettings
	)
	default int fontSize()
	{
		return 15;
	}

	@ConfigItem(
			position = 5,
			keyName = "countColor",
			name = "Number Color",
			description = "Configures the color of number",
			section = NumberSettings
	)
	default Color NumberColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
			position = 6,
			keyName = "fontType",
			name = "Font Type",
			description = "Change the font of the number",
			section = NumberSettings
	)
	default FontTypes fontType() { return FontTypes.REGULAR; }

	@ConfigSection(
			name = "True Tile Overlay Settings",
			description = "Settings only applied to True Tile Overlay",
			position = 6
	)
	String TileSettings = "True Tile Overlay Settings";

	@Alpha
	@ConfigItem(
			position = 1,
			keyName = "currentTileFillColor",
			name = "True Tile Fill Color",
			description = "Fill color of the true tile overlay",
			section = TileSettings
	)
	default Color currentTileFillColor()
	{
		return new Color(0, 0, 0, 50);
	}

	@ConfigItem(
			position = 2,
			keyName = "currentTileBorderWidth",
			name = "True Tile Border Width",
			description = "Border size of the true tile overlay",
			section = TileSettings
	)
	default double currentTileBorderWidth()
	{
		return 2;
	}

	@ConfigItem(
			position = 3,
			keyName = "changeFillColor",
			name = "Enable Tile Fill Color",
			description = "Makes the tile fill color change with the counter",
			section = TileSettings
	)
	default boolean changeFillColor()
	{
		return false;
	}

	@Range(
			min = 0,
			max = 255
	)
	@ConfigItem(
			position = 4,
			keyName = "changeFillColorOpacity",
			name = "Fill Color Opacity",
			description = "Opacity of the tile fill counter color if the option above is enabled. Otherwise, the opacity is determined by the True Tile Fill Color setting",
			section = TileSettings
	)
	default int changeFillColorOpacity()
	{
		return 50;
	}

	@ConfigSection(
			name = "Color Settings",
			description = "Change the colors and number of colors to cycle through",
			position = 7
	)
	String ColorSettings = "Color Settings";


	@Range(
			min = 2,
			max = 10
	)
	@ConfigItem(
			position = 1,
			keyName = "colorCycle",
			name = "Number of Colors",
			description = "The number of colors it cycles through",
			section = ColorSettings
	)
	default int colorCycle()
	{
		return 2;
	}

	@Alpha
	@ConfigItem(
			position = 2,
			keyName = "tickColor",
			name = "Tick Color",
			description = "Configures the color of tick",
			section = ColorSettings
	)
	default Color getTickColor()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
			position = 3,
			keyName = "tockColor",
			name = "Tock Color",
			description = "Configures the color of tock",
			section = ColorSettings
	)
	default Color getTockColor()
	{
		return Color.GRAY;
	}

	@Alpha
	@ConfigItem(
			position = 4,
			keyName = "tick3Color",
			name = "3rd Tick Color",
			description = "Configures the color of 3rd tick if enabled",
			section = ColorSettings
	)
	default Color getTick3Color()
	{
		return Color.DARK_GRAY;
	}
	@Alpha
	@ConfigItem(
			position = 5,
			keyName = "tick4Color",
			name = "4th Tick Color",
			description = "Configures the color of the 4th tick if enabled",
			section = ColorSettings
	)
	default Color getTick4Color()
	{
		return Color.BLACK;
	}
	@Alpha
	@ConfigItem(
			position = 6,
			keyName = "tick5Color",
			name = "5th Tick Color",
			description = "Configures the color of the 5th tick if enabled",
			section = ColorSettings
	)
	default Color getTick5Color()
	{
		return new Color(112, 131, 255);
	}
	@Alpha
	@ConfigItem(
			position = 7,
			keyName = "tick6Color",
			name = "6th Tick Color",
			description = "Configures the color of the 6th tick if enabled",
			section = ColorSettings
	)
	default Color getTick6Color()
	{
		return new Color(0, 23, 171);
	}
	@Alpha
	@ConfigItem(
			position = 8,
			keyName = "tick7Color",
			name = "7th Tick Color",
			description = "Configures the color of the 7th tick if enabled",
			section = ColorSettings
	)
	default Color getTick7Color()
	{
		return new Color(107, 255, 124);
	}
	@Alpha
	@ConfigItem(
			position = 9,
			keyName = "tick8Color",
			name = "8th Tick Color",
			description = "Configures the color of the 8th tick if enabled",
			section = ColorSettings
	)
	default Color getTick8Color()
	{
		return new Color(0, 191, 22);
	}
	@Alpha
	@ConfigItem(
			position = 10,
			keyName = "tick9Color",
			name = "9th Tick Color",
			description = "Configures the color of the 9th tick if enabled",
			section = ColorSettings
	)
	default Color getTick9Color()
	{
		return new Color(255, 105, 94);
	}
	@Alpha
	@ConfigItem(
			position = 11,
			keyName = "tick10Color",
			name = "10th Tick Color",
			description = "Configures the color of the 10th tick if enabled",
			section = ColorSettings
	)
	default Color getTick10Color()
	{
		return new Color(255, 17, 0);
	}

	@ConfigSection(
			name = "Hotkey Settings",
			description = "Settings that use hotkeys",
			position = 8
	)
	String HotkeySettings = "Hotkey Settings";

	@ConfigItem(
			position = 1,
			keyName = "tickResetHotkey",
			name = "Tick Cycle Reset Hotkey",
			description = "Hotkey to reset the tick cycle back to 0",
			section = HotkeySettings
	)
	default Keybind tickResetHotkey() {
		return Keybind.NOT_SET;
	}

}

