package com.lmsnotifier;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

class LMSOverlay extends Overlay
{
	private static final int MAX_DISTANCE = 2350;
	private final Client client;
	private final LMSConfig config;
	private final LMSPlugin plugin;

	@Inject
	private LMSOverlay(Client client, LMSConfig config, LMSPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.inGame)
		{
			return null;
		}

		if (plugin.highlightChests())
		{
			renderChests(graphics);
		}

		return null;
	}

	private void renderChests(Graphics2D graphics)
	{
		LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
		for (TileObject object : plugin.chests.values())
		{
			if (object.getLocalLocation().distanceTo(playerLocation) >= MAX_DISTANCE)
			{
				continue;
			}

			if (config.highlightChestType().equals(ChestHightlightType.CLICK_BOX))
			{
				Shape shape = object.getClickbox();
				if (shape != null)
				{
					Color color = config.chestHighlightColour();
					Color clickBoxColor = ColorUtil.colorWithAlpha(color, color.getAlpha() / 12);

					graphics.setColor(color);
					graphics.draw(shape);
					graphics.setColor(clickBoxColor);
					graphics.fill(shape);
				}
			}
			else if (config.highlightChestType().equals(ChestHightlightType.TILE))
			{
				Shape shape = object.getCanvasTilePoly();
				if (shape != null)
				{
					OverlayUtil.renderPolygon(graphics, shape, config.chestHighlightColour());
				}
			}
			else if (config.highlightChestType().equals(ChestHightlightType.HULL))
			{
				Shape shape = ((GameObject) object).getConvexHull();
				if (shape != null)
				{
					OverlayUtil.renderPolygon(graphics, shape, config.chestHighlightColour());
				}
			}
		}
	}
}
