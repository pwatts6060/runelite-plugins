package com.lmsnotifier;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(LMSPlugin.CONFIG_GROUP_KEY)
public interface LMSConfig extends Config
{
	String POINT_SAFE_KEY = "pointToSafeZone";

	@ConfigItem(
		keyName = POINT_SAFE_KEY,
		name = "Extended Safe Zone hint arrow",
		description = "Always show safe zone hint arrow even at distance"
	)
	default boolean pointToSafeZone()
	{
		return true;
	}

	@ConfigItem(
		keyName = "notifyGameStart",
		name = "Game Start Notification",
		description = "Notifies you when LMS starts"
	)
	default boolean notifiesGameStart()
	{
		return true;
	}

	@ConfigItem(
		keyName = "highlightChests",
		name = "Chest Highlight",
		description = "When to highlight chests"
	)
	default ChestHightlight highlightChests()
	{
		return ChestHightlight.HAS_KEY;
	}

	@ConfigItem(
		keyName = "highlightChestsType",
		name = "Chest Highlight Type",
		description = "Tile or Hull highlight for chests"
	)
	default ChestHightlightType highlightChestType()
	{
		return ChestHightlightType.HULL;
	}

	@ConfigItem(
		keyName = "chestColour",
		name = "Chest Colour",
		description = "Chest highlight colour"
	)
	default Color chestHighlightColour()
	{
		return Color.CYAN;
	}
}