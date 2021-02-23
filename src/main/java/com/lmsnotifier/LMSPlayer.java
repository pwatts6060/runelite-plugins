package com.lmsnotifier;

import lombok.AllArgsConstructor;
import net.runelite.api.Player;

@AllArgsConstructor
class LMSPlayer
{
	final Player player;
	final LMSRank lmsRank;
}
