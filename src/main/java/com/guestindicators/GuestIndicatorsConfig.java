package com.guestindicators;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.Color;

@ConfigGroup("guestindicators")
public interface GuestIndicatorsConfig extends Config
{
	@ConfigSection(
		name = "Highlight Options",
		description = "Toggle highlighted players by type (self, friends, etc.) and choose their highlight colors",
		position = 99
	)
	String highlightSection = "section";

	@ConfigItem(
		position = 10,
		keyName = "drawClanChatGuestNames",
		name = "Highlight clan guests",
		description = "Configures whether or not clan guests should be highlighted",
		section = highlightSection
	)
	default boolean highlightClanGuests()
	{
		return false;
	}

	@ConfigItem(
		position = 11,
		keyName = "clanChatGuestColor",
		name = "Clan guest",
		description = "Color of clan guests",
		section = highlightSection
	)
	default Color getClanGuestColor()
	{
		return new Color(20, 115, 34);
	}

	@ConfigItem(
		position = 12,
		keyName = "drawGuestClanChatMemberNames",
		name = "Highlight guest clan members",
		description = "Configures whether or not guest clan members should be highlighted",
		section = highlightSection
	)
	default boolean highlightGuestClanMembers()
	{
		return false;
	}

	@ConfigItem(
		position = 13,
		keyName = "guestClanChatMemberColor",
		name = "Guest clan member",
		description = "Color of guest clan members",
		section = highlightSection
	)
	default Color getGuestClanMemberColor()
	{
		return new Color(224, 152, 0);
	}

	@ConfigItem(
		position = 14,
		keyName = "drawGuestClanChatGuestNames",
		name = "Highlight guest clan guests",
		description = "Configures whether or not guest clan guests should be highlighted",
		section = highlightSection
	)
	default boolean highlightGuestClanGuests()
	{
		return false;
	}

	@ConfigItem(
		position = 15,
		keyName = "guestClanChatGuestColor",
		name = "Guest Clan guest",
		description = "Color of guest clan guests",
		section = highlightSection
	)
	default Color getGuestClanGuestColor()
	{
		return new Color(202, 196, 147);
	}

	@ConfigItem(
		position = 16,
		keyName = "guestClanchatMenuIcons",
		name = "Show guest clan chat ranks",
		description = "Add guest clan chat rank to right click menu and next to player names"
	)
	default boolean showGuestClanChatRanks()
	{
		return false;
	}
}
