package com.starinfo;

public class TierData
{
	private static final TierData[] data = new TierData[] {
		new TierData(1200, 1200, 0.3, 1),
		new TierData(700, 1900, 0.3, 0.57),
		new TierData(430, 2330, 0.3, 0.49),
		new TierData(250, 2580, 0.3, 0.44),
		new TierData(175, 2755, 0.23, 0.3),
		new TierData(80, 2835, 0.15, 0.2),
		new TierData(40, 2875, 0.09, 0.13),
		new TierData(40, 2915, 0.07, 0.1),
		new TierData(15, 2930, 0.06, 0.07)
	};

	public final int layerDust;
	public final int totalDust;
	public final double lowChance;
	public final double highChance;

	TierData(int layerDust, int totalDust, double lowChance, double highChance)
	{
		this.layerDust = layerDust;
		this.totalDust = totalDust;
		this.lowChance = lowChance;
		this.highChance = highChance;
	}

	public static TierData get(int tier)
	{
		if (tier < 1 || tier > data.length) {
			return null;
		}
		return data[tier - 1];
	}

	public double getChance(int level) {
		return lowChance + (level - 1) * (highChance - lowChance) / 98;
	}
}
