package com.lmsnotifier;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

class LMSOverlay extends Overlay
{
	private final LMSConfig config;
	private final LMSPlugin plugin;

	@Inject
	private LMSOverlay(LMSConfig config, LMSPlugin plugin)
	{
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
		for (GameObject object : plugin.crates)
		{
			if (object != null)
			{
				if (config.highlightChestType().equals(ChestHightlightType.HULL))
				{
					OverlayUtil.renderPolygon(graphics, object.getConvexHull(), config.chestHighlightColour());
				}
				else if (config.highlightChestType().equals(ChestHightlightType.TILE))
				{
					OverlayUtil.renderPolygon(graphics, object.getCanvasTilePoly(), config.chestHighlightColour());
				}
			}
		}
	}
}
