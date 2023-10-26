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

	private static final int Y_ADJUST = 12;
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
		setPriority(OverlayPriority.LOW);
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

		if (config.colorStar())
		{
			Shape shape = star.getObject().getConvexHull();
			if (shape != null)
			{
				OverlayUtil.renderPolygon(graphics, shape, getStarColor());
			}
		}

		int health = star.getHealth();
		// health bar
		Point starLocation = star.getObject().getCanvasLocation(190);
		if (starLocation != null && !config.hideHealthBar() && health > 0)
		{
			try
			{
				starLocation = new Point(starLocation.getX() - config.healthBarWidth() / 2, starLocation.getY() - config.healthBarHeight() - 14);
				Color prevColor = graphics.getColor();
				graphics.setColor(config.getHpColorBack());
				graphics.fillRect(starLocation.getX(), starLocation.getY(), config.healthBarWidth(), config.healthBarHeight());
				graphics.setColor(config.getHpColorFore());
				graphics.fillRect(starLocation.getX(), starLocation.getY(), health * config.healthBarWidth() / 100, config.healthBarHeight());
				graphics.setColor(prevColor);
			}
			catch (Exception e)
			{
				return null;
			}
		}

		// Tier and Health Percent
		int yOff = 0;

		String text = "T" + star.getTier();
		if (health >= 0 && config.showPercent())
		{
			text += " " + health + "%";
		}
		starLocation = star.getObject().getCanvasTextLocation(graphics, text, 190);
		if (starLocation != null)
		{
			try
			{
				starLocation = new Point(starLocation.getX(), starLocation.getY() + yOff);
				overlayText(graphics, starLocation, text);
			}
			catch (Exception e)
			{
				return null;
			}
		}

		if (config.showMiners() && star.getMiners() != null && !star.getMiners().equals(Star.UNKNOWN_MINERS))
		{
			if (config.compact())
			{
				text = star.getMiners() + "M";
			}
			else
			{
				text = "Miners: " + star.getMiners();
			}
			starLocation = star.getObject().getCanvasTextLocation(graphics, text, 190);
			if (starLocation != null)
			{
				try
				{
					yOff += Y_ADJUST;
					starLocation = new Point(starLocation.getX(), starLocation.getY() + yOff);
					overlayText(graphics, starLocation, text);
				}
				catch (Exception e)
				{
					return null;
				}
			}
		}

		// time estimate
		if (!config.estimateLayerTime().equals(EstimateConfig.NONE))
		{
			int ticks = -1;
			if (star.getTierTicksEstimate() != null && star.getTier() <= star.getTierTicksEstimate().length) {
				ticks = star.getTierTicksEstimate()[star.getTier() - 1];
			}
			text = config.compact() ? "" : "Layer: ";
			if (config.estimateLayerTime().equals(EstimateConfig.TICKS))
			{
				text += ticks;
			}
			else
			{
				int seconds = (ticks % 100) * 3 / 5;
				int minutes = ticks / 100;
				text += minutes + ":" + String.format("%02d", seconds);
			}
			starLocation = star.getObject().getCanvasTextLocation(graphics, text, 190);
			if (starLocation != null && ticks >= 0)
			{
				try
				{
					yOff += Y_ADJUST;
					starLocation = new Point(starLocation.getX(), starLocation.getY() + yOff);
					overlayText(graphics, starLocation, text);
				}
				catch (Exception e)
				{
					return null;
				}
			}
		}

		if (!config.estimateDeathTime().equals(EstimateConfig.NONE) && star.getTierTicksEstimate() != null)
		{
			int ticks = star.getTierTicksEstimate()[0];
			text = config.compact() ? "" : "Dead: ";
			if (config.estimateDeathTime().equals(EstimateConfig.TICKS))
			{
				text += ticks;
			}
			else
			{
				int seconds = (ticks % 100) * 3 / 5;
				int minutes = ticks / 100;
				text += minutes + ":" + String.format("%02d", seconds);
			}
			starLocation = star.getObject().getCanvasTextLocation(graphics, text, 190);
			if (starLocation != null)
			{
				try
				{
					yOff += Y_ADJUST;
					starLocation = new Point(starLocation.getX(), starLocation.getY() + yOff);
					overlayText(graphics, starLocation, text);
				}
				catch (Exception e)
				{
					return null;
				}
			}
		}

		return null;
	}

	private void overlayText(Graphics2D graphics, Point starLocation, String text)
	{
		if (config.thickOutline())
		{
			renderThickOutlineText(graphics, starLocation, text, textColor);
		}
		else
		{
			OverlayUtil.renderTextLocation(graphics, starLocation, text, textColor);
		}
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