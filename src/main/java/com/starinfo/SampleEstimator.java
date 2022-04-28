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
	private int extraTicks;
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
			System.out.println(healthInfo.healthChange + " " + healthInfo.ticksBetween);
		}
		System.out.println(totalHealthChange + " " + totalTicks);
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
			extraTicks = 0;
			lastStar = star;
			healthInfoList.clear();
			star.setEstimateTicks(-1);
		}

		int health = star.getHealth();
		if (health < 0)
		{
			return;
		}
		if (health == lastHealth)
		{
			if (star.getEstimateTicks() > 0)
			{
				extraTicks++;
				star.setEstimateTicks(Math.max(0, star.getEstimateTicks() - extraTicks));
			}
			return;
		}
		extraTicks = 0;

		int healthChange = lastHealth < 0 ? 0 : lastHealth - health;
		int ticksBetween = lastHealthTick < 0 ? 0 : plugin.client.getTickCount() - lastHealthTick;
		healthInfoList.add(new HealthInfo(healthChange, ticksBetween));
		while (healthInfoList.size() > 10)
		{
			healthInfoList.remove();
		}

		if (healthInfoList.size() >= 5)
		{
			star.setEstimateTicks(getTicksEstimate());
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
