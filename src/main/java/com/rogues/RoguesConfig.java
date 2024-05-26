package com.rogues;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("rogues-chest")
public interface RoguesConfig extends Config
{
	@ConfigItem(
		keyName = "playSound",
		name = "Play Respawn Sounds",
		description = "Whether to play respawn/warning sounds"
	)
	default boolean playSound()
	{
		return true;
	}

	@ConfigItem(
		keyName = "shouldNotify",
		name = "Respawn notification",
		description = "Whether to notify chest respawns"
	)
	default boolean shouldNotify()
	{
		return true;
	}

	@ConfigItem(
		keyName = "soundId",
		name = "Respawn Sound",
		description = "The sound id to play when a chest respawns"
	)
	default int soundId()
	{
		return 3813;
	}

	@ConfigItem(
		keyName = "warnsoundId",
		name = "Warning Respawn Sound",
		description = "The sound id to play just before a chest respawns"
	)
	default int warnSoundId()
	{
		return 3813;
	}

	@Range(
		max = 15
	)
	@ConfigItem(
		keyName = "warnTime",
		name = "Warning Time",
		description = "Number of ticks before chest respawns to play warning sound, 0 to disable"
	)
	default int warnTime()
	{
		return 1;
	}
}
