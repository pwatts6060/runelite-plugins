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

	@Inject
	private LMSConfig config;

	private final Cache<String, LMSRank> cache = CacheBuilder.newBuilder().initialCapacity(30).maximumSize(2000).expireAfterAccess(60, TimeUnit.MINUTES).build();

	LMSRank getRankFor(String username, SweatTracker sweatTracker)
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
			if (config.autoMarkSweats() && !sweatTracker.isSweat(name) && sweatTracker.botCriteriaCount(result) >= 10) {
				sweatTracker.markPlayer(name);
			}
			log.debug("Retrieved hiscores for {} {} {}", username, rank, score);
			LMSRank lmsRank = new LMSRank(rank, score);
			cache.put(name, lmsRank);
			return lmsRank;
		}
		return null;
	}

//	private boolean botCheck(String name, HiscoreResult result) {
//		return botCriteriaCount(result) >= 9;
//
//		System.out.println(name + ": " + criteriaMet);
//		try {
//			BufferedWriter out = new BufferedWriter(new FileWriter("./bots.txt", true));
//			if (criteriaMet >= 10) {
//				out.write(name + ":BOT:" + getSkills(result) + "\n");
//			} else {
//				out.write(name + ":pBOT:" + getSkills(result) + "\n");
//			}
//			out.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static String getSkills(HiscoreResult r) {
//		StringBuilder sb = new StringBuilder();
//		for (Map.Entry<HiscoreSkill, Skill> entry : r.getSkills().entrySet()) {
//			HiscoreSkill hiscoreSkill = entry.getKey();
//			Skill skill = entry.getValue();
//			if (skill.getLevel() < 0)
//				continue;
//			sb.append(hiscoreSkill.getName()).append(":").append(skill.getLevel()).append("\n");
//		}
//		return sb.toString();
//	}
}
