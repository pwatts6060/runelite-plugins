package com.entrana;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("entrana")
public interface EntranaConfig extends Config
{
	@ConfigItem(
		keyName = "color",
		name = "Prohibited color",
		description = "The color to outline prohibited items"
	)
	default Color color()
	{
		return Color.ORANGE;
	}
}
