package com.starinfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.runelite.api.Player;
import net.runelite.client.hiscore.HiscoreManager;

public class InstantEstimator
{
	public static final int NOT_FETCHED = -1;

	private final StarInfoPlugin plugin;
	private final HiscoreManager hiscoreManager;
	private final ScheduledExecutorService scheduledExecutorService;

	private final Map<String, PlayerInfo> playerInfo = new HashMap<>();
	private final Map<String, Integer> playerSpecTicks = new HashMap<>();

	@Inject
	public InstantEstimator(StarInfoPlugin plugin, HiscoreManager hiscoreManager, ScheduledExecutorService scheduledExecutorService)
	{
		this.plugin = plugin;
		this.hiscoreManager = hiscoreManager;
		this.scheduledExecutorService = scheduledExecutorService;
	}

	public void reset() {
		playerInfo.clear();
		playerSpecTicks.clear();
	}

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
			TierData tierData = TierData.get(tier);
			if (tierData == null) {
				return null;
			}
			double healthScale = tier == startTier ? (star.getHealth() / 100.0) : 1;

			totalTicks += (int) Math.ceil(healthScale * tierData.tickTime);
			tier--;
			ticks[tier] = totalTicks;
		}
		return ticks;
	}

	public void performedSpec(Player player)
	{
		playerSpecTicks.put(player.getName(), plugin.client.getTickCount());
	}
}
