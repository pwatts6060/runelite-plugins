package com.butler;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

public class TickTimer extends InfoBox
{
	protected int ticksLeft;

	public int tick() {
		return --ticksLeft;
	}

	public TickTimer(BufferedImage image, @Nonnull Plugin plugin, int ticks)
	{
		super(image, plugin);
		ticksLeft = ticks;
	}

	@Override
	public String getText()
	{
		return Integer.toString(ticksLeft);
	}

	@Override
	public Color getTextColor()
	{
		return null;
	}
}
