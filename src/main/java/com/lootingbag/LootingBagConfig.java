package com.lootingbag;

import static com.lootingbag.LootingBagConfig.CONFIG_GROUP;
import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(CONFIG_GROUP)
public interface LootingBagConfig extends Config
{
	String CONFIG_GROUP = "lootingbagvalue";
	String priceKey = "priceType";

	@ConfigItem(
		keyName = "textColor",
		name = "Text color",
		description = "Color of the text overlay on looting bag"
	)
	default Color textColor() {
		return Color.WHITE;
	}

	@ConfigItem(
		keyName = "bagValue",
		name = "Bag Value",
		description = "Display looting bag value"
	)
	default boolean bagValue() {
		return true;
	}

	@ConfigItem(
		keyName = "freeSlots",
		name = "Free slots",
		description = "Display number of free slots in bag"
	)
	default boolean freeSlots() {
		return true;
	}
}
