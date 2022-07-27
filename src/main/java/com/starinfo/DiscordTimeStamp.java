package com.starinfo;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DiscordTimeStamp
{
	public static String relativeTimeNow()
	{
		long number = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toEpochSecond();
		return "<t:" + number + ":R>";
	}

	public static String relativeTimeNowPlus(int ticks)
	{
		int seconds = (int) (ticks * 0.6);
		long number = ZonedDateTime.ofInstant(Instant.now().plusSeconds(seconds), ZoneId.systemDefault()).toEpochSecond();
		return "<t:" + number + ":R>";
	}
}
