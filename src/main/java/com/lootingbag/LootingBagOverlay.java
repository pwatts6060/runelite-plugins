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
			renderText(graphics, widgetItem.getCanvasBounds(), 0, getValueText());
		}
		if (config.freeSlots()) {
			renderText(graphics, widgetItem.getCanvasBounds(), -12, getFreeSlotsText());
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

	private String getFreeSlotsText()
	{
		LootingBag lootingBag = plugin.getLootingBag();
		if (!lootingBag.isSynced())
		{
			return "Check";
		}

		return Integer.toString(lootingBag.getFreeSlots());
	}

	private String getValueText()
	{
		LootingBag lootingBag = plugin.getLootingBag();
		if (!lootingBag.isSynced())
		{
			return "Check";
		}

		long lootingBagValue = lootingBag.getValueOfItems();
		String text = lootingBag.isQuantityOfItemsAccurate() ? "" : ">";
		if (lootingBagValue >= 10_000_000)
		{
			return text + lootingBagValue / 1_000_000 + "M";
		}
		if (lootingBagValue >= 100_000)
		{
			return text + lootingBagValue / 1000 + "k";
		}
		return text + lootingBagValue;
	}
}
