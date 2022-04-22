package com.starinfo;

import java.util.HashMap;
import java.util.Map;

public class TierData
{
	private static final Map<Integer, TierData> map = new HashMap<Integer, TierData>()
	{
		{
			put(1, new TierData(1200, 1200));
			put(2, new TierData(700, 1900));
			put(3, new TierData(430, 2330));
			put(4, new TierData(250, 2580));
			put(5, new TierData(175, 2755));
			put(6, new TierData(80, 2835));
			put(7, new TierData(40, 2875));
			put(8, new TierData(40, 2915));
			put(9, new TierData(15, 2930));
		}
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
		return map.get(tier);
	}
}
