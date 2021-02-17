package com.lmsnotifier;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(LMSPlugin.CONFIG_GROUP_KEY)
public interface LMSConfig extends Config
{
	String POINT_SAFE_KEY = "pointToSafeZone";

	@ConfigItem(
		keyName = POINT_SAFE_KEY,
		name = "Extended Safe Zone hint arrow",
		description = "Always show safe zone hint arrow even at distance",
		position = 0
	)
	default boolean pointToSafeZone()
	{
		return true;
	}

	@ConfigItem(
		keyName = "notifyGameStart",
		name = "Game Start Notification",
		description = "Notifies you when LMS starts",
		position = 1
	)
	default boolean notifiesGameStart()
	{
		return true;
	}

	@ConfigSection(
		name = "Chests",
		description = "Chest highlighting options",
		position = 2,
		closedByDefault = false
	)
	String chestList = "chestList";

	@ConfigItem(
		keyName = "highlightChests",
		name = "Highlight",
		description = "When to highlight chests",
		section = chestList,
		position = 0
	)
	default ChestHightlight highlightChests()
	{
		return ChestHightlight.HAS_KEY;
	}

	@ConfigItem(
		keyName = "highlightChestsType",
		name = "Highlight Type",
		description = "Tile, Hull, or Clickbox highlight for chests",
		section = chestList,
		position = 1
	)
	default ChestHightlightType highlightChestType()
	{
		return ChestHightlightType.CLICK_BOX;
	}

	@ConfigItem(
		keyName = "chestColour",
		name = "Colour",
		description = "Chest highlight colour",
		section = chestList,
		position = 2
	)
	default Color chestColour()
	{
		return Color.CYAN;
	}

	@Range(
		min = 1,
		max = 50
	)
	@ConfigItem(
		keyName = "chestRadius",
		name = "Radius",
		description = "Radius of squares to highlight chests within",
		section = chestList,
		position = 3
	)
	default int chestRadius()
	{
		return 25;
	}

	@ConfigSection(
		name = "Loot Crates",
		description = "Loot Crate highlighting options",
		position = 3,
		closedByDefault = false
	)
	String lootCrateList = "lootCrateList";

	@ConfigItem(
		keyName = "lootCrateHighlight",
		name = "Highlight",
		description = "Options for highlighting loot crates",
		section = lootCrateList,
		position = 0
	)
	default LootCrateHightlight lootCrateHighlightType()
	{
		return LootCrateHightlight.CLICK_BOX;
	}

	@ConfigItem(
		keyName = "lootCrateColour",
		name = "Colour",
		description = "Loot Crate highlight colour",
		section = lootCrateList,
		position = 1
	)
	default Color lootCrateColour()
	{
		return Color.GREEN;
	}

	@Range(
		min = 1,
		max = 50
	)
	@ConfigItem(
		keyName = "lootCrateRadius",
		name = "Radius",
		description = "Radius of squares to highlight loot crates within",
		section = lootCrateList,
		position = 2
	)
	default int lootCrateRadius()
	{
		return 25;
	}
}