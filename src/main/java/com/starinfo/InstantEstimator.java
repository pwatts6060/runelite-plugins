package com.starinfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.WorldType;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.hiscore.HiscoreManager;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.util.Text;

public class InstantEstimator
{
	public static final int NOT_FETCHED = -1;

	private final StarInfoPlugin plugin;
	private final HiscoreManager hiscoreManager;
	private final ScheduledExecutorService scheduledExecutorService;

	private final Map<String, PlayerInfo> playerInfo = new HashMap<>();
	private final Map<String, Integer> playerSpecTicks = new HashMap<>();

	@Getter
	private double lastDustPerTick;

	@Inject
	public InstantEstimator(StarInfoPlugin plugin, HiscoreManager hiscoreManager, ScheduledExecutorService scheduledExecutorService)
	{
		this.plugin = plugin;
		this.hiscoreManager = hiscoreManager;
		this.scheduledExecutorService = scheduledExecutorService;
	}

	public void reset() {
		lastDustPerTick = 0;
		playerInfo.clear();
		playerSpecTicks.clear();
	}

	public void refreshEstimate(Star star, List<PlayerInfo> miners)
	{
		int[] ticks = getTicksEstimates(star, miners);
		star.setTierTicksEstimate(ticks);
	}

	private int[] getTicksEstimates(Star star, List<PlayerInfo> miners)
	{
		boolean members = plugin.client.getWorldType().contains(WorldType.MEMBERS);
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
		int minerCount = 0;
		while (tier > 0) {
			TierData tierData = TierData.get(tier);
			if (tierData == null) {
				return null;
			}
			double healthScale = tier == startTier ? (star.getHealth() / 100.0) : 1;
			double dustLeft = tierData.layerDust * healthScale;
			double[] dustPerTicks = new double[miners.size()];
			minerCount = 0;
			for (PlayerInfo miner : miners) {
				PlayerInfo cachedMiner = playerInfo.get(miner.getUsername());
				if (cachedMiner == null) {
					continue;
				}
				int level = cachedMiner.getLevel();
				if (level < 0) {
					continue;
				}

				if (members && miner.isRing()) {
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
			if (tier == startTier) {
				lastDustPerTick = dustPerTick;
			}
			int tierTicks = (int) (dustLeft / dustPerTick);
			totalTicks += tierTicks;
			tier--;
			ticks[tier] = totalTicks;
		}
		if (minerCount == 0) {
			return null;
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
		final HiscoreEndpoint endPoint = HiscoreEndpoint.NORMAL;
		scheduledExecutorService.execute(() -> {
			HiscoreResult result;
			try
			{
				result = hiscoreManager.lookup(Text.sanitize(username), endPoint);
			}
			catch (IOException e)
			{
				return;
			}

			player.setLevel(result.getSkill(HiscoreSkill.MINING).getLevel());
			playerInfo.put(username, player);
		});
	}

	public void performedSpec(Player player)
	{
		playerSpecTicks.put(player.getName(), plugin.client.getTickCount());
	}
}
