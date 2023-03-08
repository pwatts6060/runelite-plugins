package com.aerial;

import static com.aerial.AerialPlugin.GLOVE_NO_BIRD;
import static com.aerial.AerialPlugin.GLOVE_WITH_BIRD;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.kit.KitType;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class AerialOverlay extends Overlay
{
	Client client;

	AerialConfig config;

	static final int[] distances = new int[] {1, 3, 4, 6, 8 };

	static final WorldArea ignoreArea = new WorldArea(1360, 3627, 16, 32, 0);

	@Inject
	public AerialOverlay(Client client, AerialConfig config)
	{
		this.client = client;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.drawRadius()) {
			return null;
		}

		int weaponId = client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON);
		if (weaponId != GLOVE_WITH_BIRD && weaponId != GLOVE_NO_BIRD) {
			return null;
		}

		WorldPoint center = client.getLocalPlayer().getWorldLocation();
		for (int dist : distances) {
			square(center, graphics, dist);
		}

		return null;
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
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() + 64), plane);

			if (canvasPointA != null)
			{
				int x1 = canvasPointA.getX();
				int y1 = canvasPointA.getY();

				Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() + 64), plane);

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
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() - 64), plane);

			if (canvasPointA != null)
			{
				int x1 = canvasPointA.getX();
				int y1 = canvasPointA.getY();

				Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() + 64), plane);

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
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() - 64), plane);

			if (canvasPointA != null)
			{
				int x1 = canvasPointA.getX();
				int y1 = canvasPointA.getY();

				Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() + 64, localPoint.getY() - 64), plane);

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
			Point canvasPointA = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() - 64), plane);

			if (canvasPointA != null)
			{
				int x1 = canvasPointA.getX();
				int y1 = canvasPointA.getY();

				Point canvasPointB = Perspective.localToCanvas(client, new LocalPoint(localPoint.getX() - 64, localPoint.getY() + 64), plane);

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
