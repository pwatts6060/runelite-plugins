package com.lmsnotifier;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.hiscore.*;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Slf4j
class LMSHiscores
{
	@Inject
	private HiscoreManager hiscoreManager;

	private final int CACHE_SIZE = 2000;
	private static final Map<String, LMSRank> cache = new HashMap<>();

	LMSRank getRankFor(String username)
	{
		final String name = Text.sanitize(username);
		log.debug("Looking up hiscores for {}", username);
		if (cache.containsKey(name)) {
			return cache.get(name);
		}
		final HiscoreEndpoint endPoint = HiscoreEndpoint.NORMAL;
		HiscoreResult result = hiscoreManager.lookupAsync(name, endPoint);
		if (result != null) {
			Skill hiscoreSkill = result.getSkill(HiscoreSkill.LAST_MAN_STANDING);
			int score = hiscoreSkill.getLevel();
			int rank = hiscoreSkill.getRank();
			log.debug("Retrieved hiscores for {} {} {}", username, rank, score);
			LMSRank lmsRank = new LMSRank(rank, score);
			cache.put(name, lmsRank);
			if (cache.size() > CACHE_SIZE) {
				cache.clear();
			}
			return lmsRank;
		}
		return null;
	}
}
