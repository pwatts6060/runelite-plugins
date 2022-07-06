/*
 * Copyright (c) 2022, Patrick <https://github.com/pwatts6060>
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

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.api.clan.ClanRank;

@Singleton
public class ClanGuestTracker
{
	@Inject
	private Client client;

	private Map<String, ClanRank> clanPlayersToRank = new HashMap<>();
	private Map<String, ClanRank> guestClanPlayersToRank = new HashMap<>();

	void updateClan(boolean isGuestClan)
	{
		ClanChannel clanChannel = isGuestClan ? client.getGuestClanChannel() : client.getClanChannel();
		Map<String, ClanRank> playersToRank = isGuestClan ? guestClanPlayersToRank : clanPlayersToRank;
		if (clanChannel == null)
		{
			playersToRank.clear();
			return;
		}

		Map<String, ClanRank> updatedMap = new HashMap<>(playersToRank.size() * 4 / 3);
		for (ClanChannelMember member : clanChannel.getMembers())
		{
			updatedMap.put(member.getName(), member.getRank());
		}

		if (isGuestClan)
		{
			guestClanPlayersToRank = updatedMap;
		}
		else
		{
			clanPlayersToRank = updatedMap;
		}
	}

	boolean isClanGuest(Player player)
	{
		if (clanPlayersToRank.containsKey(player.getName()))
		{
			return clanPlayersToRank.get(player.getName()).equals(ClanRank.GUEST);
		}
		return false;
	}

	boolean isGuestClanGuest(Player player)
	{
		if (guestClanPlayersToRank.containsKey(player.getName()))
		{
			return guestClanPlayersToRank.get(player.getName()).equals(ClanRank.GUEST);
		}
		return false;
	}

	public boolean isGuestClanMember(Player player)
	{
		if (guestClanPlayersToRank.containsKey(player.getName()))
		{
			return !guestClanPlayersToRank.get(player.getName()).equals(ClanRank.GUEST);
		}
		return false;
	}
}
