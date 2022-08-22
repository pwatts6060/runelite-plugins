package com.lmsnotifier;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.hiscore.*;
import net.runelite.client.util.Text;

import javax.inject.Inject;

@Slf4j
class LMSHiscores
{
	@Inject
	private HiscoreManager hiscoreManager;

	LMSRank getRankFor(String username)
	{
		final String name = Text.sanitize(username);
		log.debug("Looking up hiscores for {}", username);
		final HiscoreEndpoint endPoint = HiscoreEndpoint.NORMAL;
		HiscoreResult result = hiscoreManager.lookupAsync(name, endPoint);
		if (result != null) {
			Skill hiscoreSkill = result.getSkill(HiscoreSkill.LAST_MAN_STANDING);
			int score = hiscoreSkill.getLevel();
			int rank = hiscoreSkill.getRank();
			log.debug("Retrieved hiscores for {} {} {}", username, rank, score);
			return new LMSRank(rank, score);
		}
		return null;
	}
}
