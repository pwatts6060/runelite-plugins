package com.starinfo;

public class TierData
{
	private static final TierData[] data = new TierData[] {
		new TierData(1200, 1200, 0.3 * 255, 1 * 255, 12, 0.02),
		new TierData(700, 1900, 0.3 * 255, 0.57 * 255, 22, 0.06),
		new TierData(430, 2330, 0.3 * 255, 0.49 * 255, 26, 0.12),
		new TierData(300, 2630, 0.3 * 255, 0.44 * 255, 31, 0.20),
		new TierData(175, 2805, 0.23 * 255, 0.3 * 255, 48, 0.30),
		new TierData(80, 2885, 0.15 * 255, 0.2 * 255, 74, 0.42),
		new TierData(40, 2925, 0.09 * 255, 0.13 * 255, 123, 0.56),
		new TierData(40, 2965, 0.07 * 255, 0.1 * 255, 162, 0.72),
		new TierData(15, 2980, 0.06 * 255, 0.07 * 255, 244, 0.90)
	};

	public final int layerDust;
	public final int totalDust;
	public final double lowChance;
	public final double highChance;
	public final double xp;
	public final double doubleDustChance;

	TierData(int layerDust, int totalDust, double lowChance, double highChance, double xp, double doubleDustChance)
	{
		this.layerDust = layerDust;
		this.totalDust = totalDust;
		this.lowChance = lowChance;
		this.highChance = highChance;
		this.xp = xp;
		this.doubleDustChance = doubleDustChance;
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
