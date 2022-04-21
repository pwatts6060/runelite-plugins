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
		return "T" + star.getTier() + " " + star.getMiners() + "M";
	}

	@Override
	public Color getTextColor()
	{
		return Color.WHITE;
	}
}