package com.starinfo;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class Star
{
	public static final String UNKNOWN_MINERS = "?";

	private static final int[] TIER_IDS = new int[]{
		ObjectID.CRASHED_STAR_41229,
		ObjectID.CRASHED_STAR_41228,
		ObjectID.CRASHED_STAR_41227,
		ObjectID.CRASHED_STAR_41226,
		ObjectID.CRASHED_STAR_41225,
		ObjectID.CRASHED_STAR_41224,
		ObjectID.CRASHED_STAR_41223,
		ObjectID.CRASHED_STAR_41021,
		ObjectID.CRASHED_STAR,
	};

	@Getter
	private final WorldPoint worldPoint;
	@Getter
	private final int world;
	@Getter
	@Setter
	private NPC npc;
	@Getter
	@Setter
	private GameObject object;
	@Setter
	@Getter
	private String miners = UNKNOWN_MINERS;
	@Getter
	private final Location location;
	private int health = -1;
	@Getter
	@Setter
	private int[] tierTicksEstimate;
	@Getter
	@Setter
	private String worldInfo = "";

	public Star(NPC npc, int world)
	{
		this.npc = npc;
		this.worldPoint = npc.getWorldLocation();
		this.location = Location.forLocation(worldPoint);
		this.world = world;
	}

	public Star(GameObject gameObject, int world)
	{
		this.object = gameObject;
		this.worldPoint = gameObject.getWorldLocation();
		this.location = Location.forLocation(worldPoint);
		this.world = world;
	}

	public int getTier()
	{
		if (object == null)
		{
			return -1;
		}
		return getTier(object.getId());
	}

	public static int getTier(int id)
	{
		for (int i = 0; i < TIER_IDS.length; i++)
		{
			if (id == TIER_IDS[i])
			{
				return i + 1;
			}
		}
		return -1;
	}

	/**
	 * @return Health Percentage 0-100 of current layer. -1 if not known.
	 */
	public int getHealth()
	{
		if (npc == null)
		{
			return health;
		}
		if (npc.getHealthRatio() >= 0)
		{
			health = 100 * npc.getHealthRatio() / npc.getHealthScale();
		}
		else if (npc.isDead())
		{
			health = -1;
		}
		return health;
	}

	public String getMessage()
	{
		return "Star Found: W" + world + " T" + getTier() + " " + location.getDescription() + worldInfo;
	}

	public void resetHealth()
	{
		health = 100;
	}
}