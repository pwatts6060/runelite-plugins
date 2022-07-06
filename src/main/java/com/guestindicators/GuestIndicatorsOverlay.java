/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * Copyright (c) 2019, Jordan Atwood <nightfirecat@protonmail.com>
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.clan.ClanTitle;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.Text;

import static com.guestindicators.OrigConfig.*;

@Singleton
public class GuestIndicatorsOverlay extends Overlay
{
	private static final int ACTOR_OVERHEAD_TEXT_MARGIN = 40;
	private static final int ACTOR_HORIZONTAL_TEXT_MARGIN = 10;

	private final GuestIndicatorsService guestIndicatorsService;
	private final GuestIndicatorsConfig config;
	private final ConfigManager configManager;
	private final ChatIconManager chatIconManager;
	private final ClanGuestTracker clanGuestTracker;

	@Inject
	private GuestIndicatorsOverlay(GuestIndicatorsConfig config, GuestIndicatorsService guestIndicatorsService,
								   ConfigManager configManager, ChatIconManager chatIconManager, ClanGuestTracker clanGuestTracker)
	{
		this.config = config;
		this.guestIndicatorsService = guestIndicatorsService;
		this.configManager = configManager;
		this.chatIconManager = chatIconManager;
		this.clanGuestTracker = clanGuestTracker;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		guestIndicatorsService.forEachPlayer((player, color) -> renderPlayerOverlay(graphics, player, color));
		return null;
	}

	private void renderPlayerOverlay(Graphics2D graphics, Player actor, Color color)
	{
		String drawPlayerNamesConfig = configManager.getConfiguration(groupName, PLAYER_NAME_POSITION, String.class);
		if ("DISABLED".equals(drawPlayerNamesConfig))
		{
			return;
		}

		final int zOffset;
		switch (drawPlayerNamesConfig)
		{
			case "MODEL_CENTER":
			case "MODEL_RIGHT":
				zOffset = actor.getLogicalHeight() / 2;
				break;
			default:
				zOffset = actor.getLogicalHeight() + ACTOR_OVERHEAD_TEXT_MARGIN;
		}

		final String name = Text.sanitize(actor.getName());
		Point textLocation = actor.getCanvasTextLocation(graphics, name, zOffset);

		if (drawPlayerNamesConfig.equals("MODEL_RIGHT"))
		{
			textLocation = actor.getCanvasTextLocation(graphics, "", zOffset);

			if (textLocation == null)
			{
				return;
			}

			textLocation = new Point(textLocation.getX() + ACTOR_HORIZONTAL_TEXT_MARGIN, textLocation.getY());
		}

		if (textLocation == null)
		{
			return;
		}

		boolean other = configManager.getConfiguration(groupName, HIGHLIGHT_OTHERS, Boolean.class);

		BufferedImage rankImage = null;
		if (actor.isFriendsChatMember()
			&& (boolean) configManager.getConfiguration(groupName, HIGHLIGHT_FRIENDS_CHAT, Boolean.class)
			&& (boolean) configManager.getConfiguration(groupName, SHOW_FRIENDS_CHAT_RANKS, Boolean.class)
			|| actor.isClanMember()
			&& (boolean) configManager.getConfiguration(groupName, HIGHLIGHT_CLAN_MEMBERS, Boolean.class)
			&& (boolean) configManager.getConfiguration(groupName, SHOW_CLAN_CHAT_RANKS, Boolean.class))
		{
			// handled by Player Indicators
			other = false;
		}
		else if (clanGuestTracker.isClanGuest(actor) && config.highlightClanGuests()
			&& (boolean) configManager.getConfiguration(groupName, SHOW_CLAN_CHAT_RANKS, Boolean.class))
		{
			ClanTitle clanTitle = guestIndicatorsService.getClanTitle(actor);
			if (clanTitle != null)
			{
				rankImage = chatIconManager.getRankImage(clanTitle);
			}
		}
		else if (config.showGuestClanChatRanks()
			&& (clanGuestTracker.isGuestClanMember(actor) && config.highlightGuestClanMembers()
			|| clanGuestTracker.isGuestClanGuest(actor) && clanGuestTracker.isGuestClanGuest(actor)))
		{
			ClanTitle clanTitle = guestIndicatorsService.getGuestClanTitle(actor);
			if (clanTitle != null)
			{
				rankImage = chatIconManager.getRankImage(clanTitle);
			}
		}

		if (rankImage != null)
		{
			final int imageWidth = rankImage.getWidth();
			final int imageTextMargin;
			final int imageNegativeMargin;

			if (drawPlayerNamesConfig.equals("MODEL_RIGHT"))
			{
				imageTextMargin = imageWidth;
				imageNegativeMargin = other ? imageWidth : 0;
			}
			else
			{
				imageTextMargin = imageWidth / 2;
				imageNegativeMargin = other ? imageWidth : imageWidth / 2;
			}

			final int textHeight = graphics.getFontMetrics().getHeight() - graphics.getFontMetrics().getMaxDescent();
			final Point imageLocation = new Point(textLocation.getX() - imageNegativeMargin - 1, textLocation.getY() - textHeight / 2 - rankImage.getHeight() / 2);
			OverlayUtil.renderImageLocation(graphics, imageLocation, rankImage);

			// move text
			if (!other)
			{
				textLocation = new Point(textLocation.getX() + imageTextMargin, textLocation.getY());
			}
		}

		OverlayUtil.renderTextLocation(graphics, textLocation, name, color);
	}
}