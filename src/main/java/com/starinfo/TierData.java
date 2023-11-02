package com.starinfo;

public class TierData
{
	private static final TierData[] data = new TierData[] {
		new TierData(0.29 * 255, 0.46 * 255, 32, 0, 700), //t1
		new TierData( 0.29 * 255, 0.46 * 255, 32, 0, 700), //t2
		new TierData( 0.29 * 255, 0.46 * 255, 32, 0, 700), //t3
		new TierData( 0.29 * 255, 0.46 * 255, 32, 0, 700), //t4
		new TierData( 0.29 * 255, 0.46 * 255, 32, 0, 700), //t5
		new TierData( 0.29 * 255, 0.46 * 255, 32, 0, 700), //t6
		new TierData(0.29 * 255, 0.46 * 255, 32, 0, 700), //t7
		new TierData(0.29 * 255, 0.46 * 255, 32, 0, 700), //t8
		new TierData(0.29 * 255, 0.46 * 255, 32, 0, 700) //t9
	};

	public final double lowChance;
	public final double highChance;
	public final double xp;
	public final double doubleDustChance;
	public final int tickTime;

	TierData(double lowChance, double highChance, double xp, double doubleDustChance, int tickTime)
	{
		this.lowChance = lowChance;
		this.highChance = highChance;
		this.xp = xp;
		this.doubleDustChance = doubleDustChance;
		this.tickTime = tickTime;
	}

	public static TierData get(int tier)
	{
		if (tier < 1 || tier > data.length) {
			return null;
		}
		return data[tier - 1];
	}

	public double getChance(int level) {
		return (1 + Math.floor(lowChance * (99 - level) / 98) + Math.floor(highChance * (level - 1) / 98)) / 256;
	}
}
