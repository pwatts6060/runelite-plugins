package com.lootsplit;

import java.util.ArrayList;
import java.util.List;

public class PlayerInfo
{
	public final String name;
	public final List<TimePeriod> timePeriods = new ArrayList<>();

	public PlayerInfo(String name)
	{
		this.name = name;
		TimePeriod timePeriod = new TimePeriod(System.currentTimeMillis());
		timePeriods.add(timePeriod);
	}
}
