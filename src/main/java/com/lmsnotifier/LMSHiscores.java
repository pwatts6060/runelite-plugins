package com.lmsnotifier;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.hiscore.*;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@Slf4j
class LMSHiscores
{
	@Inject
	private HiscoreManager hiscoreManager;

	private final Cache<String, LMSRank> cache = CacheBuilder.newBuilder().initialCapacity(30).maximumSize(2000).expireAfterAccess(60, TimeUnit.MINUTES).build();

	LMSRank getRankFor(String username)
	{
		final String name = Text.sanitize(username);
		LMSRank cachedRank = cache.getIfPresent(name);
		if (cachedRank != null) {
			return cachedRank;
		}
		log.debug("Looking up hiscores for {}", username);
		final HiscoreEndpoint endPoint = HiscoreEndpoint.NORMAL;
		HiscoreResult result = hiscoreManager.lookupAsync(name, endPoint);
		if (result != null) {
			Skill hiscoreSkill = result.getSkill(HiscoreSkill.LAST_MAN_STANDING);
			int score = hiscoreSkill.getLevel();
			int rank = hiscoreSkill.getRank();
			log.debug("Retrieved hiscores for {} {} {}", username, rank, score);
			LMSRank lmsRank = new LMSRank(rank, score);
			cache.put(name, lmsRank);
			return lmsRank;
		}
		return null;
	}
}
