package com.lootingbag;

import com.google.inject.Inject;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;

public class LootingBagOverlay extends WidgetItemOverlay
{
	private final LootingBagPlugin plugin;
	private final LootingBagConfig config;

	@Inject
	LootingBagOverlay(LootingBagPlugin plugin, LootingBagConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		showOnInventory();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		if (itemId != ItemID.LOOTING_BAG && itemId != ItemID.LOOTING_BAG_22586) {
			return;
		}
		graphics.setFont(FontManager.getRunescapeSmallFont());
		if (config.bagValue()) {
			renderText(graphics, widgetItem.getCanvasBounds(), 0, plugin.getValueText());
		}
		if (config.freeSlots()) {
			renderText(graphics, widgetItem.getCanvasBounds(), -12, plugin.getFreeSlotsText());
		}
	}

	private void renderText(Graphics2D graphics, Rectangle bounds, int yOff, String text)
	{
		final TextComponent textComponent = new TextComponent();
		textComponent.setPosition(new Point(bounds.x - 1, bounds.y + bounds.height - 1 + yOff));
		textComponent.setColor(config.textColor());
		textComponent.setText(text);
		textComponent.render(graphics);
	}
}
