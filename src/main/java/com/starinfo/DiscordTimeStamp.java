package com.starinfo;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DiscordTimeStamp
{
	public static String relativeTimeNow()
	{
		long number = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toEpochSecond();
		return "<t:"+number+":R>";
	}
}
