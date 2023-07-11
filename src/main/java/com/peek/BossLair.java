package com.peek;

import lombok.AllArgsConstructor;
import net.runelite.api.ObjectID;

@AllArgsConstructor
public enum BossLair
{
	VETION(ObjectID.CREVICE_46995, "Vet'ion"),
	CALLISTO(ObjectID.CAVE_ENTRANCE_47140, "Callisto"),
	VENENATIS(ObjectID.CAVE_ENTRANCE_47077, "Venenatis"),
	CALVARION(ObjectID.MEMORIAL_46996, "Calvar'ion"),
	ARTIO(ObjectID.CAVE_ENTRANCE_47141, "Artio"),
	SPINDEL(ObjectID.CAVE_ENTRANCE_47078, "Spindel"),
	;

	final int objectId;
	final String name;

	public static final BossLair[] values = values();
}
