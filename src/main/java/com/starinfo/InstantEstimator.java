package com.starinfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.client.hiscore.HiscoreClient;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.util.Text;
import okhttp3.OkHttpClient;

public class InstantEstimator
{
	public static final long CACHE_TIME_MINUTES = 60;
	public static final int FAILED_TO_FETCH = -2;
	public static final int NOT_FETCHED = -1;

	private final StarInfoPlugin plugin;
	private final Map<String, PlayerInfo> playerInfo = new HashMap<>();
	private final Set<String> currentLookups = ConcurrentHashMap.newKeySet();
	private final Map<String, Integer> playerSpecTicks = new HashMap<>();

	@Inject
	private final HiscoreClient hiscoreClient = new HiscoreClient(new OkHttpClient());

	public InstantEstimator(StarInfoPlugin plugin)
	{
		this.plugin = plugin;
	}

	public void refreshEstimate(Star star, List<PlayerInfo> miners)
	{
		long start = System.nanoTime();
		int[] ticks = getTicksEstimates(star, miners);
		star.setTierTicksEstimate(ticks);
		long end = System.nanoTime();
		System.out.println((end - start) / 1_000_000 + " ms");
	}

	private int[] getTicksEstimates(Star star, List<PlayerInfo> miners)
	{
		miners.forEach(this::fetchRank);
		if (star.getHealth() < 0 || miners.isEmpty()) {
			return null;
		}
		int startTier = star.getTier();
		if (startTier < 0) {
			return null;
		}
		int tier = startTier;
		int[] ticks = new int[startTier];
		int totalTicks = 0;
		int tickCount = plugin.client.getTickCount();
		while (tier > 0) {
			TierData tierData = TierData.get(tier);
			if (tierData == null) {
				return null;
			}
			double healthScale = tier == startTier ? (star.getHealth() / 100.0) : 1;
			double dustLeft = tierData.layerDust * healthScale;
			double[] dustPerTicks = new double[miners.size()];
			int minerCount = 0;
			for (PlayerInfo miner : miners) {
				PlayerInfo cachedMiner = playerInfo.get(miner.getUsername());
				if (cachedMiner == null) {
					continue;
				}
				int level = cachedMiner.getLevel();
				if (level < 0) {
					continue;
				}

				if (miner.isRing()) {
					level += 4;
				}

				int ticksSinceSpec = playerSpecTicks.getOrDefault(miner.getUsername(), -1);
				int dif = tickCount - ticksSinceSpec;
				boolean usesBoost = ticksSinceSpec > 0 && dif < 550;

				double avgChance;
				if (usesBoost) {
					// this gets the average chance across each mining level reached with dpick specs every 5 mins
					avgChance = 2 * tierData.getChance(level);
					for (int i = 1; i <= 3; i++) {
						avgChance += tierData.getChance(level + i);
					}
					avgChance /= 5;
				} else {
					avgChance = tierData.getChance(level);
				}
				dustPerTicks[minerCount++] = avgChance / miner.pickTicks;
			}

			double dustPerTick = 0;
			if (minerCount <= 3) {
				for (int i = 0; i < minerCount; i++) {
					dustPerTick += dustPerTicks[i];
				}
			} else {
				dustPerTick += avgDust3MinersPlus(dustPerTicks, minerCount);
			}
			int tierTicks = (int) (dustLeft / dustPerTick);
			totalTicks += tierTicks;
//			System.out.println(tier + " " + dustLeft / dustPerTick);
			tier--;
			ticks[tier] = totalTicks;
		}
		return ticks;
	}

	private double avgDust3MinersPlus(double[] d, int n) {
		double p0 = 1;
		double[] md = new double[n];
		double mdProduct = 1;

		for (int i = 0; i < n; i++) {
			double minusD = 1 - d[i];
			md[i] = minusD;
			p0 *= minusD;
			mdProduct *= minusD;
		}

		double p1 = 0;
		for (int i = 0; i < n; i++) {
			p1 += d[i] * mdProduct / md[i];
		}

		double p2 = 0;
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				p2 += d[i] * d[j] * mdProduct / md[i] / md[j];
			}
		}

		double pge3 = 1 - p0 - p1 - p2; // P >= 3

		return 1 * p1 + 2 * p2 + 3 * pge3; // expectation
	}

	void fetchRank(PlayerInfo player)
	{
		final String username = player.getUsername();
		if (username.equals(plugin.client.getLocalPlayer().getName())) {
			player.setLevel(plugin.client.getRealSkillLevel(Skill.MINING));
			playerInfo.put(username, player);
			return;
		}
		PlayerInfo info = playerInfo.get(username);
		if (info != null && info.isTimedOut() || currentLookups.contains(username))
		{
			return;
		}
		currentLookups.add(username);
		final HiscoreEndpoint endPoint = HiscoreEndpoint.NORMAL;
		hiscoreClient.lookupAsync(Text.sanitize(username), endPoint).whenCompleteAsync(((result, ex) -> {
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
		playerSpecTicks.put(player.getName(), plugin.client.getTickCount());
	}
}
