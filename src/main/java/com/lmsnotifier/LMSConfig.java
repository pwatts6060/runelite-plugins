package com.lmsnotifier;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface LMSConfig extends Config
{
	@ConfigItem(
		keyName = "pointToSafeZone",
		name = "Extended Safe Zone hint arrow",
		description = "Always show safe zone hint arrow even at distance"
	)
	default boolean pointToSafeZone()
	{
		return true;
	}


	@ConfigItem(
		keyName = "notifyGameStart",
		name = "Game Start Notification",
		description = "Notifies you when LMS starts"
	)
	default boolean notifiesGameStart()
	{
		return true;
	}
}