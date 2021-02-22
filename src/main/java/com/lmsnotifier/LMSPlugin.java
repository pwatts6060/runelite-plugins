package com.lmsnotifier;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.Player;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectChanged;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Last Man Standing"
)
public class LMSPlugin extends Plugin
{
	static final String CONFIG_GROUP_KEY = "lmsconfig";
	private static final int LOOT_CRATE = ObjectID.CRATE_29081;
	private static final WorldArea lmsCompetitiveLobby = new WorldArea(3138, 3639, 8, 7, 0);
	private static final WorldArea lmsCasualLobby = new WorldArea(3139, 3639, 6, 6, 1);
	private static final WorldArea lmsHighStakesLobby = new WorldArea(3138, 3639, 8, 7, 2);
	private static final Set<Integer> chestIds = ImmutableSet.of(ObjectID.CHEST_29069, ObjectID.CHEST_29072);
	private static final int FEROX_REGION_ID = 12600;
	boolean inGame = false;
	Map<WorldPoint, TileObject> chests = new HashMap<>();
	Map<WorldPoint, TileObject> lootCrates = new HashMap<>();
	List<LMSPlayer> localLMSPlayers = new LinkedList<>();
	private boolean inLobby = false;
	private LMSHiscores lmsHiscores = new LMSHiscores();
	@Inject
	private Client client;

	@Inject
	private LMSConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private LMSOverlay overlay;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Lms Notifier started!");
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Lms Notifier stopped!");
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOADING)
		{
			return;
		}

		chests.clear();
		lootCrates.clear();
		if (inLobby && config.notifiesGameStart())
		{
			notifier.notify("Last Man Standing has started!");
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		inLobby = client.getLocalPlayer().getWorldLocation().distanceTo(lmsCompetitiveLobby) == 0
			|| client.getLocalPlayer().getWorldLocation().distanceTo(lmsCasualLobby) == 0
			|| client.getLocalPlayer().getWorldLocation().distanceTo(lmsHighStakesLobby) == 0;
		refreshNearbyPlayerRanks();
	}

	private void refreshNearbyPlayerRanks()
	{
		if (!inGame || client.getLocalPlayer().getWorldLocation().getRegionID() == FEROX_REGION_ID || config.rankVisual().equals(RankVisual.NONE))
		{
			localLMSPlayers.clear();
			return;
		}
		List<Player> players = new ArrayList<>(client.getPlayers());
		LocalPoint localPoint = client.getLocalPlayer().getLocalLocation();
		players.sort(Comparator.comparingInt(o -> LMSUtil.distSquared(o.getLocalLocation(), localPoint)));
		for (Player player : players)
		{
			if (client.getLocalPlayer().getName().equals(player.getName()))
			{
				continue;
			}
			lmsHiscores.fetchRank(player.getName());
		}
		localLMSPlayers.clear();
		for (Player player : players)
		{
			String name = player.getName();
			if (client.getLocalPlayer().getName().equals(name))
			{
				continue;
			}
			LMSRank lmsRank = lmsHiscores.getRankFor(name);
			if (lmsRank != null)
			{
				localLMSPlayers.add(new LMSPlayer(player, lmsRank));
			}
		}
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed ev)
	{
		if (ev.getGroupId() == WidgetInfo.LMS_KDA.getGroupId())
		{
			inGame = false;
			chests.clear();
			lootCrates.clear();
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		if (widgetLoaded.getGroupId() == WidgetInfo.LMS_KDA.getGroupId())
		{
			inGame = true;
		}
	}

	@Provides
	LMSConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LMSConfig.class);
	}

	boolean highlightChests()
	{
		switch (config.highlightChests())
		{
			case NEVER:
				return false;
			case HAS_KEY:
				return client.getItemContainer(InventoryID.INVENTORY).contains(ItemID.BLOODY_KEY) || client.getItemContainer(InventoryID.INVENTORY).contains(ItemID.BLOODIER_KEY);
			case ALWAYS:
			default:
				return true;
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		onTileObject(null, event.getGameObject());
	}

	@Subscribe
	public void onGameObjectChanged(GameObjectChanged event)
	{
		onTileObject(event.getPrevious(), event.getGameObject());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		onTileObject(event.getGameObject(), null);
	}

	@Subscribe
	public void onGroundObjectSpawned(GroundObjectSpawned event)
	{
		onTileObject(null, event.getGroundObject());
	}

	@Subscribe
	public void onGroundObjectChanged(GroundObjectChanged event)
	{
		onTileObject(event.getPrevious(), event.getGroundObject());
	}

	@Subscribe
	public void onGroundObjectDespawned(GroundObjectDespawned event)
	{
		onTileObject(event.getGroundObject(), null);
	}

	private void onTileObject(TileObject oldObject, TileObject newObject)
	{
		if (oldObject != null)
		{
			WorldPoint oldLocation = oldObject.getWorldLocation();
			chests.remove(oldLocation);
			lootCrates.remove(oldLocation);
		}

		if (newObject == null)
		{
			return;
		}

		if (chestIds.contains(newObject.getId()))
		{
			chests.put(newObject.getWorldLocation(), newObject);
			return;
		}

		if (newObject.getId() == LOOT_CRATE)
		{
			lootCrates.put(newObject.getWorldLocation(), newObject);
		}
	}

	boolean highlightLootCrates()
	{
		return lootCrates.size() > 0 && !config.lootCrateHighlightType().equals(LootCrateHightlight.NONE);
	}
}
