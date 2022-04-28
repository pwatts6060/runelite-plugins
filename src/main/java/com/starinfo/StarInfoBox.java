package com.starinfo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

public class StarInfoBox extends InfoBox
{
	private final Star star;

	public StarInfoBox(BufferedImage image, @Nonnull Plugin plugin, Star star)
	{
		super(image, plugin);
		this.star = star;
	}

	@Override
	public String getText()
	{
		String text = "T" + star.getTier();
		if (!star.getMiners().equals(Star.UNKNOWN_MINERS))
		{
			text += " " + star.getMiners() + "M";
		}
		return text;
	}

	@Override
	public Color getTextColor()
	{
		return Color.WHITE;
	}
}