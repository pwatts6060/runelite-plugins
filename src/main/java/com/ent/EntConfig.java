package com.ent;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("ent-trimmer")
public interface EntConfig extends Config
{
	@ConfigItem(
		keyName = "highlightPriority",
		name = "Highlight Priority Ents",
		description = "Established priority order is Top/Mullet/Back and Sides/Back. Top cuts first, then back cuts.",
		position = 0
	)
	default boolean highlightPriority()
	{
		return true;
	}

	@ConfigItem(
		keyName = "priorityColor",
		name = "Priority Highlight Color",
		description = "The color to highlight the priority ents",
		position = 1
	)
	default Color priorityColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
		keyName = "hideNonPriority",
		name = "Hide Non-Priority Ents",
		description = "Hide the NPCs so non-priority ents can't be clicked until priority ents are perfect. No ents are hidden until you cut at least one.",
		position = 2
	)
	default boolean hideNonPriority()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hidePerfect",
		name = "Hide Perfect Ents",
		description = "Hide the NPCs so non-priority ents can't be clicked until priority ents are perfect. No ents are hidden until you cut at least one.",
		position = 3
	)
	default boolean hidePerfect()
	{
		return true;
	}

	@ConfigItem(
		keyName = "displayCutOption",
		name = "Display Trim options",
		description = "Display the correct trim options above ents",
		position = 4
	)
	default boolean displayTrimOptions()
	{
		return true;
	}

	@ConfigItem(
		keyName = "displayTrimCount",
		name = "Display Trim Count",
		description = "Display the number of total trims above ents",
		position = 5
	)
	default boolean displayTrimCount()
	{
		return true;
	}

	@ConfigItem(
		keyName = "textColor",
		name = "Text Color",
		description = "Color of the trim option / total trim text about ents",
		position = 6
	)
	default Color textColor()
	{
		return Color.WHITE;
	}

	@ConfigItem(
		keyName = "displayTextOnHiddenEnts",
		name = "Display Text on Hidden Ents",
		description = "Whether to keep the text display above hidden ents",
		position = 7
	)
	default boolean displayTextOnHiddenEnts()
	{
		return false;
	}
}
