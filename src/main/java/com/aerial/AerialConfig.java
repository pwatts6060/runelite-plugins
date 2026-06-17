package com.aerial;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("aerial-fishing")
public interface AerialConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "idleSound",
		name = "Play Idle Sound",
		description = "Plays a sound when you can click another spot."
	)
	default boolean idleSound()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "warningSound",
		name = "Play Pre-Idle Sound",
		description = "Plays a sound one tick before you can click another spot."
	)
	default boolean warningSound()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "idleTimerRenderLoc",
		name = "Time-To-Idle Timer Location",
		description = "Configure where to render the timer overlay for Time-To-Idle."
	)
	default TimerRenderLocation idleTimerRenderLoc()
	{
		return TimerRenderLocation.PLAYER;
	}

	@ConfigItem(
		position = 3,
		keyName = "drawBorder",
		name = "Draw Distance Squares",
		description = "Toggle overlay to help spot closer fishing spots."
	)
	default boolean drawRadius()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = "borderColor",
		name = "Border color",
		description = "Color of the overlay's border."
	)
	@Alpha
	default Color borderColor()
	{
		return new Color(0x7AFFFF00, true);
	}

	@ConfigItem(
		position = 5,
		keyName = "borderWidth",
		name = "Border width",
		description = "Width of the overlay's border."
	)
	@Range(
		min = 1
	)
	default int borderWidth()
	{
		return 1;
	}
}
