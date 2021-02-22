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


	@ConfigSection(
		name = "Player Ranks",
		description = "Show Lms ranks/scores of players",
		position = 4,
		closedByDefault = false
	)
	String playerRanks = "playerRanks";

	@ConfigItem(
		keyName = "showPlayerRank",
		name = "Rank Visual",
		description = "Options for displaying other players lms rank",
		section = playerRanks,
		position = 0
	)
	default RankVisual rankVisual()
	{
		return RankVisual.NONE;
	}

	@ConfigItem(
		keyName = "metricColour",
		name = "Text Colour",
		description = "Colour of the metric text",
		section = playerRanks,
		position = 1
	)
	default Color metricColour()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "metricHeatmap",
		name = "Heatmap",
		description = "Colour metrics differently based on score",
		section = playerRanks,
		position = 2
	)
	default boolean metricHeatmap()
	{
		return true;
	}

	@ConfigItem(
		keyName = "rankMetric",
		name = "Metric",
		description = "Display the LMS Score or Rank of players",
		section = playerRanks,
		position = 3
	)
	default RankMetric rankMetric()
	{
		return RankMetric.SCORE;
	}
}