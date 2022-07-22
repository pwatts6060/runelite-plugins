package com.starinfo;

public class TierData
{
	private static final TierData[] data = new TierData[] {
		new TierData(1200, 1200),
		new TierData(700, 1900),
		new TierData(430, 2330),
		new TierData(250, 2580),
		new TierData(175, 2755),
		new TierData(80, 2835),
		new TierData(40, 2875),
		new TierData(40, 2915),
		new TierData(15, 2930)
	};

	public final int layerDust;
	public final int totalDust;

	TierData(int layerDust, int totalDust)
	{
		this.layerDust = layerDust;
		this.totalDust = totalDust;
	}

	public static TierData get(int tier)
	{
		if (tier < 1 || tier > data.length) {
			return null;
		}
		return data[tier - 1];
	}
}
