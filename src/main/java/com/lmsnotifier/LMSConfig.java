package com.lmsnotifier;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(LMSPlugin.CONFIG_GROUP_KEY)
public interface LMSConfig extends Config
{
	static final String POINT_SAFE_KEY = "pointToSafeZone";

	@ConfigItem(
		keyName = POINT_SAFE_KEY,
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