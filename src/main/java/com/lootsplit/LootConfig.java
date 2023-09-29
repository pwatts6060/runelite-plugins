package com.lootsplit;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("pk-loot-splitter")
public interface LootConfig extends Config
{
	@ConfigItem(
		keyName = "minLoot",
		name = "Minimum value (k) to split",
		description = "The value (k) of a loot key that should be split. Not applied to manual loot entry."
	)
	default int minimumLoot()
	{
		return 100;
	}
}
