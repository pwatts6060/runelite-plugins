package com.ent;

import net.runelite.api.NPC;
import net.runelite.api.NpcID;

public class Ent
{
	public int trims;
	public TrimType trimType;
	public boolean perfect;
	public NPC npc;

	public Ent(NPC npc)
	{
		this.trims = 0;
		this.trimType = null;
		this.npc = npc;
		this.perfect = npc.getId() == NpcID.PRUNED_ENTLING;
	}
}
