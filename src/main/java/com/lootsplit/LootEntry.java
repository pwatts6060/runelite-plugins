package com.lootsplit;

import lombok.Getter;

public class LootEntry
{
	@Getter
	public final int value;

	@Getter
	public long timems;

	public boolean inSplit = true;

	@Getter
	public final PlayerInfo playerInfo;

	public LootEntry(int value, PlayerInfo playerInfo, long timems) {
		this.value = value;
		this.playerInfo = playerInfo;
		this.timems = timems;
	}

	public String getName() {
		return playerInfo.name;
	}
}
