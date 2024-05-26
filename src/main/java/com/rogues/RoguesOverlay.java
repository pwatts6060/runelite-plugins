package com.rogues;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Scene;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class RoguesOverlay extends Overlay
{
	private final RoguesPlugin plugin;
	private final Client client;
	private final RoguesConfig config;

	@Inject
	private RoguesOverlay(
		Client client,
		RoguesPlugin plugin,
		RoguesConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		for (Map.Entry<WorldPoint, Integer> entry : plugin.respawnMap.entrySet())
		{
			WorldPoint worldPoint = entry.getKey();
			Integer respawnTick = entry.getValue();

			int ticks = 1 + respawnTick - client.getTickCount();

			Color color = ticks <= 0 ? ColorScheme.PROGRESS_COMPLETE_COLOR : ColorScheme.PROGRESS_ERROR_COLOR;
			LocalPoint lp = LocalPoint.fromWorld(client.getTopLevelWorldView(), worldPoint);
			if (lp == null)
			{
				continue;
			}

			Polygon poly = Perspective.getCanvasTilePoly(client, lp);
			if (poly == null)
			{
				continue;
			}
			OverlayUtil.renderPolygon(graphics, poly, color);

			String label = Integer.toString(ticks);
			Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, lp, label, 0);
			if (canvasTextLocation == null || ticks < 0)
			{
				continue;
			}
			OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, color);
		}
		return null;
	}
}
