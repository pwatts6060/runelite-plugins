package com.lmsnotifier;

import java.time.Duration;
import java.time.Instant;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class LMSRank
{
	final int rank;
	final int score;
	final Instant instant;

	boolean isTimedOut()
	{
		return Duration.between(instant, Instant.now()).compareTo(Duration.ofMinutes(LMSHiscores.CACHE_TIME_MINUTES)) > 0;
	}
}
