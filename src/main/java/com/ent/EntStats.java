package com.ent;

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.NPC;
import net.runelite.api.Player;

public class EntStats
{

	public boolean eligible = false;
	Map<NPC, Ent> ents;
	boolean ttg = false;
	int ticksAlive = -1;
	int despawnTime = -1;
	Map<Player, Ent> playerEntMap;

	public EntStats()
	{
		this.playerEntMap = new HashMap<>();
		this.ents = new HashMap<>();
	}

	public void clear()
	{
		ents.clear();
		ttg = false;
		ticksAlive = -1;
		despawnTime = -1;
		playerEntMap.clear();
		eligible = false;
	}

	public void add(NPC npc)
	{
		if (ents.isEmpty())
		{
			ticksAlive = 0;
		}
		ents.put(npc, new Ent(npc));
	}

	public void remove(NPC npc)
	{
		ents.remove(npc);
		if (ents.isEmpty())
		{
			clear();
		}
	}
}
