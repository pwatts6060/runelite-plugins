package com.lootingbag;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("lootingbagvalue")
public interface LootingBagConfig extends Config
{
	@ConfigItem(
		keyName = "textColor",
		name = "Text color",
		description = "Color of the text overlay on looting bag"
	)
	default Color textColor() {
		return Color.WHITE;
	}
}
