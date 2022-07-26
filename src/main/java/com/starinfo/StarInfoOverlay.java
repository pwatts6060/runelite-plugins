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

package com.starinfo;

import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

public class StarInfoOverlay extends Overlay
{

	private final StarInfoPlugin plugin;
	private final StarInfoConfig config;
	private Color textColor;

	@Inject
	StarInfoOverlay(StarInfoPlugin plugin, StarInfoConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.HIGHEST);
	}

	public void updateConfig()
	{
		textColor = config.getTextColor();
	}

	@Override
	public Dimension render(final Graphics2D graphics)
	{
		if (plugin.stars.isEmpty())
		{
			return null;
		}
		Star star = plugin.stars.get(0);

		String text = "T" + star.getTier();

		if (config.showMiners() && star.getMiners() != null && !star.getMiners().equals(Star.UNKNOWN_MINERS))
		{
			text += " " + star.getMiners() + "M";
		}

		int health = star.getHealth();

		// Health Percent
		if (health >= 0 && config.showPercent())
		{
			text += " " + health + "%";
		}

		// Stardust
		if (!config.showDust().equals(DustConfig.NO_DISPLAY) && health >= 0)
		{
			TierData tierData = TierData.get(star.getTier());
			if (tierData != null)
			{
				int layerDust = tierData.layerDust;
				int number;
				if (config.showDust().equals(DustConfig.TOTAL_STARDUST))
				{

					number = tierData.totalDust - (100 - health) * layerDust / 100;
				}
				else
				{
					number = health * layerDust / 100;
				}
				text += " " + number + " SD";
			}
		}

		// time estimate
		if (!config.estimateTime().equals(EstimateConfig.NONE) && star.getEstimateTicks() >= 0)
		{
			int ticks = star.getEstimateTicks();
			if (config.estimateTime().equals(EstimateConfig.TICKS))
			{
				text += " " + ticks;
			}
			else
			{
				int seconds = (ticks % 100) * 3 / 5;
				int minutes = ticks / 100;
				text += " " + minutes + ":" + String.format("%02d", seconds);
			}
		}

		if (!config.estimateTime().equals(EstimateConfig.NONE) && star.getFullEstimateTicks() >= 0)
		{
			int ticks = star.getFullEstimateTicks();
			if (config.estimateTime().equals(EstimateConfig.TICKS))
			{
				text += " " + ticks;
			}
			else
			{
				int seconds = (ticks % 100) * 3 / 5;
				int minutes = ticks / 100;
				text += " " + minutes + ":" + String.format("%02d", seconds);
			}
		}

		Point starLocation = star.getObject().getCanvasTextLocation(graphics, text, 190);

		if (starLocation != null)
		{
			try
			{
				if (config.thickOutline())
				{
					renderThickOutlineText(graphics, starLocation, text, textColor);
				}
				else
				{
					OverlayUtil.renderTextLocation(graphics, starLocation, text, textColor);
				}

				if (config.colorStar())
				{
					Shape shape = star.getObject().getConvexHull();
					if (shape != null)
					{
						OverlayUtil.renderPolygon(graphics, shape, getStarColor());
					}
				}
			}
			catch (Exception e)
			{
				return null;
			}
		}
		return null;
	}

	private Color getStarColor()
	{
		if (plugin.stars.isEmpty())
		{
			return Color.RED;
		}
		Star star = plugin.stars.get(0);
		int level = plugin.client.getBoostedSkillLevel(Skill.MINING);
		if (level < star.getTier() * 10)
		{
			return Color.RED;
		}
		return Color.GREEN;
	}

	private static void renderThickOutlineText(Graphics2D graphics, Point txtLoc, String text, Color color)
	{
		if (Strings.isNullOrEmpty(text))
		{
			return;
		}

		int x = txtLoc.getX();
		int y = txtLoc.getY();

		graphics.setColor(Color.BLACK);
		graphics.drawString(text, x + 1, y + 1);
		graphics.drawString(text, x + 1, y);
		graphics.drawString(text, x + 1, y - 1);
		graphics.drawString(text, x, y + 1);
		graphics.drawString(text, x, y - 1);
		graphics.drawString(text, x - 1, y + 1);
		graphics.drawString(text, x - 1, y);
		graphics.drawString(text, x - 1, y - 1);

		graphics.setColor(ColorUtil.colorWithAlpha(color, 0xFF));
		graphics.drawString(text, x, y);
	}
}