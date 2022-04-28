package com.starinfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import net.runelite.api.Player;
import net.runelite.client.hiscore.HiscoreClient;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.util.Text;
import okhttp3.OkHttpClient;

public class InstantEstimator
{
	public static final long CACHE_TIME_MINUTES = 30;
	public static final int FAILED_TO_FETCH = -2;
	public static final int NOT_FETCHED = -1;

	private final StarInfoPlugin plugin;
	private final Map<String, PlayerInfo> playerInfo = new HashMap<>();
	private final Set<String> currentLookups = ConcurrentHashMap.newKeySet();

	@Inject
	private final HiscoreClient hiscoreClient = new HiscoreClient(new OkHttpClient());

	public InstantEstimator(StarInfoPlugin plugin)
	{
		this.plugin = plugin;
	}

	public String getEstimate(Star star, List<PlayerInfo> miners)
	{
		int ticks = getTicksEstimate(star, miners);
		int seconds = ticks % 100;
		int minutes = ticks / 100;
		return minutes + ":" + String.format("%02d", seconds);
	}

	private int getTicksEstimate(Star star, List<PlayerInfo> miners)
	{
		miners.forEach(this::fetchRank);
		return 0;
	}

	void fetchRank(PlayerInfo player)
	{
		final String username = player.getUsername();
		final String name = Text.sanitize(username);
		PlayerInfo info = playerInfo.get(name);
		if (info != null && !info.isTimedOut() || currentLookups.contains(username))
		{
			return;
		}
		currentLookups.add(username);
		final HiscoreEndpoint endPoint = HiscoreEndpoint.NORMAL;
		hiscoreClient.lookupAsync(name, endPoint).whenCompleteAsync(((result, ex) -> {
			if (ex != null)
			{
				currentLookups.remove(username);
				return;
			}
			if (result == null)
			{
				currentLookups.remove(username);
				player.setLevel(FAILED_TO_FETCH);
				return;
			}

			player.setLevel(result.getMining().getLevel());
			playerInfo.put(username, player);
			currentLookups.remove(username);
		}));
	}

	public void performedSpec(Player player)
	{

	}
}
