package com.starinfo;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class Star
{
	private static final int T1_ID = ObjectID.CRASHED_STAR_41229;
	private static final int T9_ID = ObjectID.CRASHED_STAR;

	@Getter
	private final WorldPoint worldPoint;
	@Getter
	@Setter
	private NPC npc;
	@Getter
	@Setter
	private GameObject object;
	@Setter
	@Getter
	private int miners;
	@Getter
	private final String location;

	public Star(NPC npc)
	{
		this.npc = npc;
		this.worldPoint = npc.getWorldLocation();
		this.location = Location.forLocation(worldPoint);
	}

	public Star(GameObject gameObject)
	{
		this.object = gameObject;
		this.worldPoint = gameObject.getWorldLocation();
		this.location = Location.forLocation(worldPoint);
	}

	public int getTier()
	{
		if (object == null)
		{
			return -1;
		}
		return getTier(object.getId());
	}

	static int getTier(int id)
	{
		if (id < T9_ID || id > T1_ID)
		{
			return -1;
		}
		return 1 + T1_ID - id;
	}
}