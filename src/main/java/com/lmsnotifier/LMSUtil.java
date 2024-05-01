package com.lmsnotifier;

import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

class LMSUtil
{
	static int distSquared(LocalPoint point1, LocalPoint point2)
	{
		int dx = point1.getX() - point2.getX();
		int dy = point1.getY() - point2.getY();
		return dx * dx + dy * dy;
	}

	static int bearing(WorldPoint a, WorldPoint b) {
		int dx = b.getX() - a.getX();
		int dy = b.getY() - a.getY();
		double angle = Math.atan2(dy, dx);
		double bearing = 90 - Math.round(Math.toDegrees(angle));
		if (bearing < 0) {
			bearing += 360;
		}
		return (int) bearing;
	}
}
