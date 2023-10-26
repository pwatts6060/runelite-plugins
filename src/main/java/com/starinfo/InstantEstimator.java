package com.starinfo;

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Player;

public class InstantEstimator
{
	public static final int NOT_FETCHED = -1;

	public void refreshEstimate(Star star)
	{
		int[] ticks = getTicksEstimates(star);
		star.setTierTicksEstimate(ticks);
	}

	private int[] getTicksEstimates(Star star)
	{
		if (star.getHealth() < 0) {
			return null;
		}
		int startTier = star.getTier();
		if (startTier < 0) {
			return null;
		}
		int tier = startTier;
		int[] ticks = new int[startTier];
		int totalTicks = 0;
		while (tier > 0) {
			double healthScale = tier == startTier ? (star.getHealth() / 100.0) : 1;

			totalTicks += (int) Math.ceil(healthScale * TierData.tickTime);
			tier--;
			ticks[tier] = totalTicks;
		}
		return ticks;
	}
}
