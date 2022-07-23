package com.starinfo;

import java.time.Duration;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerInfo
{
	public String username;
	public int level;
	public double pickTicks;
	public boolean ring;
	Instant instant;

	boolean isTimedOut()
	{
		return Duration.between(instant, Instant.now()).compareTo(Duration.ofMinutes(InstantEstimator.CACHE_TIME_MINUTES)) > 0;
	}
}
