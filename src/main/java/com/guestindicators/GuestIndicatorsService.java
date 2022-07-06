/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.guestindicators;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.api.clan.ClanRank;
import net.runelite.api.clan.ClanSettings;
import net.runelite.api.clan.ClanTitle;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Color;
import java.util.function.BiConsumer;

import static com.guestindicators.OrigConfig.*;
import net.runelite.client.party.PartyService;

@Singleton
public class GuestIndicatorsService
{
	private final Client client;
	private final GuestIndicatorsConfig config;
	private final ConfigManager configManager;
	private final ClanGuestTracker clanGuestTracker;
	private final PartyService partyService;

	@Inject
	private GuestIndicatorsService(Client client, GuestIndicatorsConfig config, ConfigManager configManager, ClanGuestTracker clanGuestTracker, PartyService partyService)
	{
		this.config = config;
		this.client = client;
		this.configManager = configManager;
		this.clanGuestTracker = clanGuestTracker;
		this.partyService = partyService;
	}

	public void forEachPlayer(final BiConsumer<Player, Color> consumer)
	{
		if (!config.highlightClanGuests() && !config.highlightGuestClanMembers()
			&& !config.highlightGuestClanGuests())
		{
			return;
		}

		final Player localPlayer = client.getLocalPlayer();

		for (Player player : client.getPlayers())
		{
			if (player == null || player.getName() == null || player == localPlayer
				|| partyService.isInParty()
				&& (boolean) configManager.getConfiguration(groupName, HIGHLIGHT_PARTY_MEMBERS, Boolean.class)
				&& partyService.getMemberByDisplayName(player.getName()) != null
				|| player.isFriend() && (boolean) configManager.getConfiguration(groupName, DRAW_FRIEND_NAMES, Boolean.class)
				|| player.isFriendsChatMember() && (boolean) configManager.getConfiguration(groupName, HIGHLIGHT_FRIENDS_CHAT, Boolean.class)
				|| (boolean) configManager.getConfiguration(groupName, HIGHLIGHT_TEAM_MEMBERS, Boolean.class)
				&& localPlayer.getTeam() > 0 && localPlayer.getTeam() == player.getTeam()
				|| player.isClanMember() && (boolean) configManager.getConfiguration(groupName, HIGHLIGHT_CLAN_MEMBERS, Boolean.class))
			{
				continue;
			}

			if (config.highlightGuestClanMembers() && clanGuestTracker.isGuestClanMember(player))
			{
				consumer.accept(player, config.getGuestClanMemberColor());
			}
			else if (config.highlightClanGuests() && clanGuestTracker.isClanGuest(player))
			{
				consumer.accept(player, config.getClanGuestColor());
			}
			else if (config.highlightGuestClanGuests() && clanGuestTracker.isGuestClanGuest(player))
			{
				consumer.accept(player, config.getGuestClanGuestColor());
			}
		}
	}

	ClanTitle getClanTitle(Player player)
	{
		return getChatTitle(player, client.getClanChannel(), client.getClanSettings());
	}

	ClanTitle getGuestClanTitle(Player player)
	{
		return getChatTitle(player, client.getGuestClanChannel(), client.getGuestClanSettings());
	}

	private ClanTitle getChatTitle(Player player, ClanChannel clanChannel, ClanSettings clanSettings)
	{
		if (clanChannel == null || clanSettings == null)
		{
			return null;
		}

		ClanChannelMember member = clanChannel.findMember(player.getName());
		if (member == null)
		{
			return null;
		}

		ClanRank rank = member.getRank();
		return clanSettings.titleForRank(rank);
	}
}
