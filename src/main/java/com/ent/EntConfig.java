package com.ent;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

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
		keyName = "displayTextOnHiddenEnts",
		name = "Display Text on Hidden Ents",
		description = "Whether to keep the text display above hidden ents",
		position = 7
	)
	default boolean displayTextOnHiddenEnts()
	{
		return false;
	}

	@ConfigSection(
		name = "Advanced",
		description = "Advanced settings that can be safely ignored",
		position = 8,
		closedByDefault = true
	)
	String advanced = "Advanced";

	@ConfigItem(
		keyName = "discordWebhook",
		name = "Discord webhook url",
		description = "Webhook to post ent statistics too",
		section = advanced,
		position = 9
	)
	default String discordWebhookUrl()
	{
		return "";
	}

	@ConfigItem(
		keyName = "includeScreenshot",
		name = "Include Screenshot",
		description = "Whether to post an ingame screenshot for webhook posts",
		section = advanced,
		position = 10
	)
	default boolean includeScreenshot()
	{
		return false;
	}
}
