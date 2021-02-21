package com.lmsnotifier;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.Text;
import net.runelite.http.api.hiscore.HiscoreClient;
import net.runelite.http.api.hiscore.HiscoreEndpoint;
import net.runelite.http.api.hiscore.Skill;
import okhttp3.OkHttpClient;

@Slf4j
class LMSHiscores
{
	static final long CACHE_TIME_MINUTES = 30;
	private final Set<String> currentLookups = ConcurrentHashMap.newKeySet();
	private final Map<String, LMSRank> usernameToRank = new ConcurrentHashMap<>();
	@Inject
	private HiscoreClient hiscoreClient = new HiscoreClient(new OkHttpClient());;

	void fetchRank(String username)
	{
		final String name = Text.sanitize(username);
		LMSRank lmsRank = usernameToRank.get(name);
		if (lmsRank != null && !lmsRank.isTimedOut() || currentLookups.contains(username))
		{
			return;
		}
//		log.info("Looking up hiscores for {}", username);
		currentLookups.add(username);
		final HiscoreEndpoint endPoint = HiscoreEndpoint.NORMAL;
		hiscoreClient.lookupAsync(name, endPoint).whenCompleteAsync(((result, ex) -> {
			if (ex != null)
			{
//				log.warn("error looking up {}", HiscoreSkill.LAST_MAN_STANDING.getName().toLowerCase(), ex);
				currentLookups.remove(username);
				return;
			}
			if (result == null)
			{
//				log.warn("error looking up {} score: not found", HiscoreSkill.LAST_MAN_STANDING.getName().toLowerCase());
				currentLookups.remove(username);
				usernameToRank.put(username, new LMSRank(-1, -1, Instant.now()));
				return;
			}

			Skill hiscoreSkill = result.getLastManStanding();
			int score = hiscoreSkill.getLevel();
			int rank = hiscoreSkill.getRank();
			usernameToRank.put(username, new LMSRank(rank, score, Instant.now()));
			currentLookups.remove(username);
//			log.info("Retrieved hiscores for {} {} {}", username, rank, score);
		}));
	}

	LMSRank getRankFor(String username)
	{
		return usernameToRank.get(username);
	}
}
