package com.entrana;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

@Singleton
public class ContainerHighlight extends WidgetItemOverlay
{
	private final ItemManager itemManager;
	private final EntranaConfig config;
	private final ProhibitedItems prohibitedItems;

	@Inject
	private ContainerHighlight(ItemManager itemManager, EntranaConfig config)
	{
		this.itemManager = itemManager;
		this.config = config;
		this.prohibitedItems = new ProhibitedItems(itemManager);
		showOnInventory();
		showOnEquipment();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
	{
		int interfaceId = WidgetUtil.componentToInterface(itemWidget.getWidget().getId());
		if (!prohibitedItems.isProhibited(itemId) || (interfaceId != InterfaceID.EQUIPMENT
			&& interfaceId != InterfaceID.INVENTORY
			&& interfaceId != InterfaceID.DEPOSIT_BOX))
		{
			return;
		}
		final Rectangle bounds = itemWidget.getCanvasBounds();
		final BufferedImage outline = itemManager.getItemOutline(itemId, itemWidget.getQuantity(), config.color());
		graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
	}

	private BufferedImage overlay(BufferedImage image, Color color)
	{
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage overlayed = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = overlayed.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.setComposite(AlphaComposite.SrcAtop);
		g.setColor(color);
		g.fillRect(0, 0, w, h);
		g.dispose();
		return overlayed;
	}
}
