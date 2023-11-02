package com.starinfo;

public class TierData
{
	private static final TierData[] data = new TierData[] {
		new TierData(0.3 * 255, 1 * 255, 12, 0.02, 2402), //t1
		new TierData( 0.3 * 255, 0.57 * 255, 22, 0.06, 1883), //t2
		new TierData( 0.3 * 255, 0.49 * 255, 26, 0.12, 1398), //t3
		new TierData( 0.3 * 255, 0.44 * 255, 31, 0.20, 924), //t4
		new TierData( 0.23 * 255, 0.3 * 255, 48, 0.30, 1006), //t5
		new TierData( 0.15 * 255, 0.2 * 255, 74, 0.42, 682), //t6
		new TierData(0.09 * 255, 0.13 * 255, 123, 0.56, 679), //t7
		new TierData(0.07 * 255, 0.1 * 255, 162, 0.72, 545), //t8
		new TierData(0.06 * 255, 0.07 * 255, 244, 0.90, 459) //t9
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
