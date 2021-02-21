package com.lmsnotifier;

import net.runelite.api.coords.LocalPoint;

public class LMSUtil
{
	static int distSquared(LocalPoint point1, LocalPoint point2)
	{
		int dx = point1.getX() - point2.getX();
		int dy = point1.getY() - point2.getY();
		return dx * dx + dy * dy;
	}

	public static void interpolateColor(int colour1, int colour2, float proportion)
	{

	}
}
