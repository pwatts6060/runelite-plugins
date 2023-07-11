package com.peek;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("wilderness-boss-peek")
public interface PeekConfig extends Config
{
	@ConfigItem(
		keyName = "activityColour",
		name = "Activity Colour",
		description = "The colour to highlight active boss lairs",
		position = 1
	)
	default Color activityColour()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "emptyColour",
		name = "Empty Colour",
		description = "The colour to highlight empty boss lairs",
		position = 2
	)
	default Color emptyColour()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "cutoffTime",
		name = "Cutoff Time",
		description = "The time in seconds to remember peek information",
		position = 3
	)
	default int cutoffTime()
	{
		return 300;
	}

	@ConfigItem(
		keyName = "activityToChatbox",
		name = "Active peeks to Chatbox",
		description = "Add active worlds to game chat preserved across world hops",
		position = 4
	)
	default boolean addActiveScoutsToChat()
	{
		return false;
	}

	@ConfigItem(
		keyName = "emptyToChatbox",
		name = "Empty peeks to Chatbox",
		description = "Add empty worlds to game chat preserved across world hops",
		position = 5
	)
	default boolean addEmptyScoutsToChat()
	{
		return false;
	}
}
