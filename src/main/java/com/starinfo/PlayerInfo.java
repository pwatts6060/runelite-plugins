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
	public int level = InstantEstimator.NOT_FETCHED;
	public int timeAtBoost = -1;
	public double pickTicks = -1;
	public boolean ring = false;
	Instant instant = Instant.now();

	boolean isTimedOut()
	{
		return Duration.between(instant, Instant.now()).compareTo(Duration.ofMinutes(InstantEstimator.CACHE_TIME_MINUTES)) > 0;
	}
}
