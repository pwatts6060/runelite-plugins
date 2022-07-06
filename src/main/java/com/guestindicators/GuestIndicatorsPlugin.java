package com.guestindicators;

import com.google.inject.Provides;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.clan.ClanTitle;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.party.PartyService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.Color;

import static com.guestindicators.OrigConfig.*;
import static net.runelite.api.MenuAction.ITEM_USE_ON_PLAYER;
import static net.runelite.api.MenuAction.PLAYER_EIGTH_OPTION;
import static net.runelite.api.MenuAction.PLAYER_FIFTH_OPTION;
import static net.runelite.api.MenuAction.PLAYER_FIRST_OPTION;
import static net.runelite.api.MenuAction.PLAYER_FOURTH_OPTION;
import static net.runelite.api.MenuAction.PLAYER_SECOND_OPTION;
import static net.runelite.api.MenuAction.PLAYER_SEVENTH_OPTION;
import static net.runelite.api.MenuAction.PLAYER_SIXTH_OPTION;
import static net.runelite.api.MenuAction.PLAYER_THIRD_OPTION;
import static net.runelite.api.MenuAction.RUNELITE_PLAYER;
import static net.runelite.api.MenuAction.WALK;
import static net.runelite.api.MenuAction.WIDGET_TARGET_ON_PLAYER;

@Slf4j
@PluginDescriptor(
	name = "Guest Indicators"
)
public class GuestIndicatorsPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private GuestIndicatorsConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private GuestIndicatorsOverlay guestIndicatorsOverlay;

	@Inject
	private GuestIndicatorsTileOverlay guestIndicatorsTileOverlay;

	@Inject
	private GuestIndicatorsMinimapOverlay guestIndicatorsMinimapOverlay;

	@Inject
	private GuestIndicatorsService guestIndicatorsService;

	@Inject
	private Client client;

	@Inject
	private ChatIconManager chatIconManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClanGuestTracker clanGuestTracker;

	@Inject
	private PartyService partyService;

	@Provides
	GuestIndicatorsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GuestIndicatorsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(guestIndicatorsOverlay);
		overlayManager.add(guestIndicatorsTileOverlay);
		overlayManager.add(guestIndicatorsMinimapOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(guestIndicatorsOverlay);
		overlayManager.remove(guestIndicatorsTileOverlay);
		overlayManager.remove(guestIndicatorsMinimapOverlay);
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		if (client.isMenuOpen())
		{
			return;
		}

		MenuEntry[] menuEntries = client.getMenuEntries();

		for (MenuEntry entry : menuEntries)
		{
			MenuAction type = entry.getType();

			if (type == WALK
				|| type == WIDGET_TARGET_ON_PLAYER
				|| type == ITEM_USE_ON_PLAYER
				|| type == PLAYER_FIRST_OPTION
				|| type == PLAYER_SECOND_OPTION
				|| type == PLAYER_THIRD_OPTION
				|| type == PLAYER_FOURTH_OPTION
				|| type == PLAYER_FIFTH_OPTION
				|| type == PLAYER_SIXTH_OPTION
				|| type == PLAYER_SEVENTH_OPTION
				|| type == PLAYER_EIGTH_OPTION
				|| type == RUNELITE_PLAYER)
			{
				Player[] players = client.getCachedPlayers();
				Player player = null;

				int identifier = entry.getIdentifier();

				// 'Walk here' identifiers are offset by 1 because the default
				// identifier for this option is 0, which is also a player index.
				if (type == WALK)
				{
					identifier--;
				}

				if (identifier >= 0 && identifier < players.length)
				{
					player = players[identifier];
				}

				if (player == null)
				{
					continue;
				}

				GuestIndicatorsPlugin.Decorations decorations = getDecorations(player);

				if (decorations == null)
				{
					continue;
				}

				String oldTarget = entry.getTarget();
				String newTarget = decorateTarget(oldTarget, decorations);

				entry.setTarget(newTarget);
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (config.highlightClanGuests())
		{
			clanGuestTracker.updateClan(false);
		}
		if (config.highlightGuestClanGuests() || config.highlightGuestClanGuests())
		{
			clanGuestTracker.updateClan(true);
		}
	}

	private GuestIndicatorsPlugin.Decorations getDecorations(Player player)
	{
		if (player.isFriend() && (boolean) configManager.getConfiguration(groupName, DRAW_FRIEND_NAMES, Boolean.class)
			|| player.isFriendsChatMember() && (boolean) configManager.getConfiguration(groupName, HIGHLIGHT_FRIENDS_CHAT, Boolean.class)
			|| player.getTeam() > 0 && client.getLocalPlayer().getTeam() == player.getTeam()
			&& (boolean) configManager.getConfiguration(groupName, HIGHLIGHT_TEAM_MEMBERS, Boolean.class)
			|| player.isClanMember() && (boolean) configManager.getConfiguration(groupName, HIGHLIGHT_CLAN_MEMBERS, Boolean.class))
		{
			return null;
		}
		boolean isPartyMember = partyService.isInParty() &&
			player.getName() != null &&
			(boolean) configManager.getConfiguration(groupName, HIGHLIGHT_PARTY_MEMBERS, Boolean.class) &&
			partyService.getMemberByDisplayName(player.getName()) != null;
		if (isPartyMember)
		{
			return null;
		}

		int image = -1;
		Color color = null;

		if (config.highlightClanGuests() && clanGuestTracker.isClanGuest(player))
		{
			color = config.getClanGuestColor();
			if (configManager.getConfiguration(groupName, SHOW_CLAN_CHAT_RANKS, Boolean.class))
			{
				ClanTitle clanTitle = guestIndicatorsService.getClanTitle(player);
				if (clanTitle != null)
				{
					image = chatIconManager.getIconNumber(clanTitle);
				}
			}
		}
		else if (config.highlightGuestClanMembers() && clanGuestTracker.isGuestClanMember(player))
		{
			color = config.getGuestClanMemberColor();

			if (config.showGuestClanChatRanks())
			{
				ClanTitle clanTitle = guestIndicatorsService.getGuestClanTitle(player);
				if (clanTitle != null)
				{
					image = chatIconManager.getIconNumber(clanTitle);
				}
			}
		}
		else if (config.highlightGuestClanGuests() && clanGuestTracker.isGuestClanGuest(player))
		{
			color = config.getGuestClanGuestColor();

			if (config.showGuestClanChatRanks())
			{
				ClanTitle clanTitle = guestIndicatorsService.getGuestClanTitle(player);
				if (clanTitle != null)
				{
					image = chatIconManager.getIconNumber(clanTitle);
				}
			}
		}

		if (image == -1 && color == null)
		{
			return null;
		}

		return new GuestIndicatorsPlugin.Decorations(image, color);
	}

	private String decorateTarget(String oldTarget, GuestIndicatorsPlugin.Decorations decorations)
	{
		String newTarget = oldTarget;

		if (decorations.getColor() != null && (boolean) configManager.getConfiguration(groupName, COLOR_PLAYER_MENU, Boolean.class))
		{
			// strip out existing <col...
			int idx = oldTarget.indexOf('>');
			if (idx != -1)
			{
				newTarget = oldTarget.substring(idx + 1);
			}

			newTarget = ColorUtil.prependColorTag(newTarget, decorations.getColor());
		}

		if (decorations.getImage() != -1)
		{
			newTarget = "<img=" + decorations.getImage() + ">" + newTarget;
		}

		return newTarget;
	}

	private Player findPlayer(String name)
	{
		for (Player player : client.getPlayers())
		{
			if (player.getName().equals(name))
			{
				return player;
			}
		}
		return null;
	}

	@Value
	private static class Decorations
	{
		private final int image;
		private final Color color;
	}
}
