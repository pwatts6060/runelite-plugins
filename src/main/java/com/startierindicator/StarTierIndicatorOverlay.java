/*
 * Copyright (c) 2022, Cute Rock
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

package com.startierindicator;

import javax.inject.Inject;
import java.awt.*;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class StarTierIndicatorOverlay extends Overlay
{

	public class Star
	{
		public final GameObject starObject;
		public final String tier;
		public final WorldPoint location;

		Star(GameObject starObject, String tier, WorldPoint location)
		{
			this.starObject = starObject;
			this.tier = tier;
			this.location = location;
		}
	}

	private final Client client;
	private final StarTierIndicatorConfig config;
	private Star star;
	private Color textColor;

	@Inject
	StarTierIndicatorOverlay(Client client, StarTierIndicatorConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.HIGHEST);
	}

	public Star getStar()
	{
		return star;
	}

	public void removeStar()
	{
		star = null;
	}

	public void setStar(GameObject star, String tier)
	{
		this.star = new Star(star, tier, star.getWorldLocation());
	}

	public void update()
	{
		if (star == null)
		{
			return;
		}
		if (client.getLocalPlayer().getWorldLocation().distanceTo(star.location) > 32)
		{
			removeStar();
		}
	}

	public void updateConfig()
	{
		textColor = config.getTextColor();
	}

	@Override
	public Dimension render(final Graphics2D graphics)
	{
		if (star == null)
		{
			return null;
		}

		Point starLocation = star.starObject.getCanvasTextLocation(graphics, star.tier, 0);

		if (starLocation != null)
		{
			try
			{
				OverlayUtil.renderTextLocation(graphics, star.starObject.getCanvasTextLocation(graphics, star.tier, 190), star.tier, textColor);
			}
			catch (Exception e)
			{
				return null;
			}
		}
		return null;
	}
}
