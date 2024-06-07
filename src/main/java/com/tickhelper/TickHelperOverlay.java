package com.tickhelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayUtil;

public class TickHelperOverlay extends Overlay
{
	private final Client client;
	private final TickHelperPlugin plugin;

	@Inject
	public TickHelperOverlay(Client client, TickHelperPlugin plugin) {
		this.client = client;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.getActionTimer() > 0) {
			String text = Integer.toString(plugin.getActionTimer());
			Point textLocation = client.getLocalPlayer().getCanvasTextLocation(graphics,
				text, client.getLocalPlayer().getLogicalHeight() + 40);

			if (textLocation != null)
			{
				graphics.setFont(FontManager.getRunescapeBoldFont());
				OverlayUtil.renderTextLocation(graphics, textLocation, text, Color.RED);
			}
		}
//		if (plugin.getBlockTimer() > 0) {
//			String text = Integer.toString(plugin.getBlockTimer());
//			Point textLocation = client.getLocalPlayer().getCanvasTextLocation(graphics,
//				text, client.getLocalPlayer().getLogicalHeight() + 80);
//
//			if (textLocation != null)
//			{
//				graphics.setFont(FontManager.getRunescapeBoldFont());
//				OverlayUtil.renderTextLocation(graphics, textLocation, text, Color.BLACK);
//			}
//		}
		return null;
	}
}
