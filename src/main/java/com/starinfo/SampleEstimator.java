package com.starinfo;

import java.util.LinkedList;
import java.util.Queue;
import lombok.AllArgsConstructor;

public class SampleEstimator
{
	private final StarInfoPlugin plugin;
	private Star lastStar;
	private int lastHealth;
	private int lastTier;
	private int lastHealthTick;
	private final Queue<HealthInfo> healthInfoList = new LinkedList<>();

	public SampleEstimator(StarInfoPlugin plugin)
	{
		this.plugin = plugin;
	}

	private int getTicksEstimate()
	{
		int totalHealthChange = 0;
		int totalTicks = 0;
		for (HealthInfo healthInfo : healthInfoList)
		{
			totalHealthChange += healthInfo.healthChange;
			totalTicks += healthInfo.ticksBetween;
		}
		double healthPerTick = (double) totalHealthChange / totalTicks;
		return (int) (lastStar.getHealth() / healthPerTick);
	}

	public void update(Star star)
	{
		if (!star.equals(lastStar) || lastTier != star.getTier())
		{
			lastHealth = -2;
			lastTier = -2;
			lastHealthTick = -1;
			lastStar = star;
			healthInfoList.clear();
			star.setLayerSampleTicks(-1);
		}

		int health = star.getHealth();
		if (health < 0)
		{
			return;
		}
		if (health == lastHealth)
		{
			if (star.getLayerSampleTicks() > 0)
			{
				star.setLayerSampleTicks(Math.max(0, star.getLayerSampleTicks() - 1));
			}
			return;
		}

		int healthChange = lastHealth < 0 ? 0 : lastHealth - health;
		int ticksBetween = lastHealthTick < 0 ? 0 : plugin.client.getTickCount() - lastHealthTick;
		healthInfoList.add(new HealthInfo(healthChange, ticksBetween));
		while (healthInfoList.size() > 10)
		{
			healthInfoList.remove();
		}

		if (healthInfoList.size() >= 5)
		{
			star.setLayerSampleTicks(getTicksEstimate());
		}

		lastHealth = star.getHealth();
		lastTier = star.getTier();
		lastHealthTick = plugin.client.getTickCount();
	}

	@AllArgsConstructor
	private static class HealthInfo
	{
		final int healthChange;
		final int ticksBetween;
	}
}
