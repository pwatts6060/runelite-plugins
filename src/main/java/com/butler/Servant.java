package com.butler;

import java.util.Optional;
import lombok.Getter;
import net.runelite.api.NpcID;

public enum Servant
{
	RICK(NpcID.RICK, 17, 20, 500, 6, 100),
	MAID(NpcID.MAID, 17, 25, 1_000, 10, 50),
	COOK(NpcID.COOK, 17, 30, 3_000, 16, 28),
	BUTLER(NpcID.BUTLER, 17, 40, 5_000, 20, 20),
	DEMON_BUTLER(NpcID.DEMON_BUTLER, 17, 50, 10_000, 26, 12),
	;

	public final int npcId;
	public final int itemId;
	public final int level;
	public final int cost;
	public final int capacity;
	public final int ticks;

	public static final Servant[] values = Servant.values();

	Servant(int npcId, int itemId, int level, int cost, int capacity, int ticks)
	{
		this.npcId = npcId;
		this.itemId = itemId;
		this.level = level;
		this.cost = cost;
		this.capacity = capacity;
		this.ticks = ticks;
	}

	public static Optional<Servant> forNpcId(int npcId)
	{
		for (Servant servant : values)
		{
			if (servant.npcId == npcId)
			{
				return Optional.of(servant);
			}
		}
		return Optional.empty();
	}
}
