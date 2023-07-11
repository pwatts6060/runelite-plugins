package com.peek;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

public class PeekOverlay extends Overlay
{
	private static final NumberFormat TIME_FORMATTER = DecimalFormat.getInstance(Locale.US);

	static
	{
		((DecimalFormat) TIME_FORMATTER).applyPattern("#0.0");
	}

	private final Client client;
	private final PeekPlugin plugin;
	private final PeekConfig config;

	@Inject
	private PeekOverlay(Client client, PeekPlugin plugin, PeekConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		for (Map.Entry<BossLair, GameObject> entry : plugin.getLairToObject().entrySet())
		{
			BossLair lair = entry.getKey();
			GameObject object = entry.getValue();
			int world = client.getWorld();
			PeekInfo peekInfo = plugin.getBossToWorlds().get(lair).get(world);
			if (peekInfo == null)
			{
				continue;
			}

			double timeSince = (Instant.now().toEpochMilli() - peekInfo.timeMs) / 1000.0;
			if (timeSince > config.cutoffTime())
			{
				plugin.getBossToWorlds().get(lair).remove(world);
				continue;
			}

			if (client.getLocalPlayer().getWorldLocation().distanceTo2D(object.getWorldLocation()) > 25)
			{
				continue;
			}

			String s = TIME_FORMATTER.format(timeSince);
			Color color = peekInfo.active ? config.activityColour() : config.emptyColour();

			Shape clickBox = object.getClickbox();
			if (clickBox == null)
			{
				continue;
			}
			OverlayUtil.renderPolygon(graphics, clickBox, color, ColorUtil.colorWithAlpha(color.brighter(), 40), new BasicStroke(1f));

			Point textLocation = object.getCanvasTextLocation(graphics, s, 0);
			if (textLocation == null)
			{
				continue;
			}
			OverlayUtil.renderTextLocation(graphics, textLocation, s, Color.WHITE);
		}
		return null;
	}
}
