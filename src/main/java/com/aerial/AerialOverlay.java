package com.aerial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

public class AerialOverlay extends Overlay
{
	Client client;

	AerialPlugin plugin;

	AerialConfig config;

	static final int[] distances = new int[] {1, 3, 4, 6, 8 };

	static final WorldArea ignoreArea = new WorldArea(1360, 3627, 16, 32, 0);

	@Inject
	public AerialOverlay(Client client, AerialPlugin plugin, AerialConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		renderRadii(graphics);
		renderIdleTimer(graphics);

		return null;
	}

	private void renderRadii(Graphics2D graphics)
	{
		if (!config.drawRadius()) {
			return;
		}

		if (!plugin.isGloveEquipped()) {
			return;
		}

		WorldPoint center = client.getLocalPlayer().getWorldLocation();
		for (int dist : distances) {
			square(center, graphics, dist);
		}
	}

	private void renderIdleTimer(Graphics2D graphics)
	{
		if (config.idleTimerRenderLoc() == TimerRenderLocation.NONE) {
			return;
		}

		// No active bird projectiles to track
		if (plugin.getPointToEndTick().isEmpty())
		{
			return;
		}

		// No start tick for projectile tracked
		if (plugin.getTimerStartTick() < 0)
		{
			return;
		}

		LocalPoint pLocPoint = client.getLocalPlayer().getLocalLocation();
		int plane = client.getLocalPlayer().getWorldView().getPlane();
		if (pLocPoint == null)
		{
			return;
		}
		int clientTick = client.getTickCount();
		float percent = (float) (clientTick - plugin.getTimerStartTick()) / (plugin.getTimerCompleteTick() - plugin.getTimerStartTick() + 1);

		Point point = null;
		switch (config.idleTimerRenderLoc()) {
			case POINTER:
				point = client.getMouseCanvasPosition();
				break;
			case PLAYER:
				point = Perspective.localToCanvas(client, pLocPoint, plane);
				break;
		}
		if (point == null || percent > 1.0f)
		{
			return;
		}

		Color fillColor = percent < 1.0f ? Color.YELLOW : Color.GREEN;
		ProgressPieComponent pie = new ProgressPieComponent();
		pie.setPosition(point);
		pie.setBorderColor(fillColor);
		pie.setFill(fillColor);
		pie.setProgress(percent);
		pie.render(graphics);
	}

	private void square(WorldPoint center, Graphics2D graphics, int dist)
	{
		int cx = center.getX();
		int cy = center.getY();

		//top side
		for (int i = -dist; i <= dist; i++) {
			renderWorldPointBorders(graphics, new WorldPoint(cx + i, cy + dist, center.getPlane()), true, false, false, false);
		}

		//bottom side
		for (int i = -dist; i <= dist; i++) {
			renderWorldPointBorders(graphics, new WorldPoint(cx + i, cy - dist, center.getPlane()), false, false, true, false);
		}

		//left side
		for (int i = -dist; i <= dist; i++) {
			renderWorldPointBorders(graphics, new WorldPoint(cx - dist, cy - i, center.getPlane()), false, false, false, true);
		}

		//right side
		for (int i = -dist; i <= dist; i++) {
			renderWorldPointBorders(graphics, new WorldPoint(cx + dist, cy + i, center.getPlane()), false, true, false, false);
		}

	}

	private void renderWorldPointBorders(Graphics2D graphics, WorldPoint worldPoint, boolean topBorder, boolean rightBorder, boolean bottomBorder, boolean leftBorder)
	{
		if (ignoreArea.contains2D(worldPoint)) {
			return;
		}

		LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);

		if (localPoint == null)
		{
			return;
		}

		int plane = worldPoint.getPlane();

		graphics.setColor(config.borderColor());
		graphics.setStroke(new BasicStroke(config.borderWidth()));

		if (topBorder)
		{
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() + 64, localPoint.getWorldView()), plane);

			if (canvasPointA != null)
			{
				int x1 = canvasPointA.getX();
				int y1 = canvasPointA.getY();

				Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() + 64, localPoint.getWorldView()), plane);

				if (canvasPointB != null)
				{
					int x2 = canvasPointB.getX();
					int y2 = canvasPointB.getY();

					graphics.drawLine(x1, y1, x2, y2);
				}
			}
		}

		if (rightBorder)
		{
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() - 64, localPoint.getWorldView()), plane);

			if (canvasPointA != null)
			{
				int x1 = canvasPointA.getX();
				int y1 = canvasPointA.getY();

				Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() + 64, localPoint.getWorldView()), plane);

				if (canvasPointB != null)
				{
					int x2 = canvasPointB.getX();
					int y2 = canvasPointB.getY();

					graphics.drawLine(x1, y1, x2, y2);
				}
			}
		}

		if (bottomBorder)
		{
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() - 64, localPoint.getWorldView()), plane);

			if (canvasPointA != null)
			{
				int x1 = canvasPointA.getX();
				int y1 = canvasPointA.getY();

				Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() - 64, localPoint.getWorldView()), plane);

				if (canvasPointB != null)
				{
					int x2 = canvasPointB.getX();
					int y2 = canvasPointB.getY();

					graphics.drawLine(x1, y1, x2, y2);
				}
			}
		}

		if (leftBorder)
		{
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() - 64, localPoint.getWorldView()), plane);

			if (canvasPointA != null)
			{
				int x1 = canvasPointA.getX();
				int y1 = canvasPointA.getY();

				Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() + 64, localPoint.getWorldView()), plane);

				if (canvasPointB != null)
				{
					int x2 = canvasPointB.getX();
					int y2 = canvasPointB.getY();

					graphics.drawLine(x1, y1, x2, y2);
				}
			}
		}
	}
}
