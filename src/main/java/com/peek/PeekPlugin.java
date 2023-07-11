package com.peek;

import com.google.inject.Provides;
import java.time.Instant;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Wilderness Boss Peek"
)
public class PeekPlugin extends Plugin
{

	public static final String ACTIVITY_MSG = "You peek into the darkness and can make out some movement. There is activity inside.";
	public static final String EMPTY_MSG = "You peek into the darkness and everything seems quiet. The cave is empty.";

	@Inject
	private Client client;

	@Inject
	private PeekConfig config;

	@Inject
	private PeekOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Getter
	private BossLair currentBoss;

	@Getter
	private Map<BossLair, Map<Integer, PeekInfo>> bossToWorlds;

	@Getter
	private final Map<BossLair, GameObject> lairToObject = new EnumMap<>(BossLair.class);

	@Override
	protected void startUp() throws Exception
	{
		bossToWorlds = new EnumMap<>(BossLair.class);
		for (BossLair bossLair : BossLair.values)
		{
			bossToWorlds.put(bossLair, new HashMap<>());
		}
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		for (BossLair bossLair : BossLair.values)
		{
			bossToWorlds.get(bossLair).clear();
		}
		bossToWorlds.clear();
		bossToWorlds = null;
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (!event.getType().equals(ChatMessageType.GAMEMESSAGE))
		{
			return;
		}
		if (ACTIVITY_MSG.equals(event.getMessage()))
		{
			PeekInfo peekInfo = new PeekInfo(Instant.now().toEpochMilli(), true);
			bossToWorlds.get(currentBoss).put(client.getWorld(), peekInfo);
			if (config.addActiveScoutsToChat())
			{
				String msg = "W" + client.getWorld() + " " + currentBoss.name + " Active";
				client.addChatMessage(ChatMessageType.CONSOLE, "", msg, "");
			}
		}
		else if (EMPTY_MSG.equals(event.getMessage()))
		{
			PeekInfo peekInfo = new PeekInfo(Instant.now().toEpochMilli(), false);
			bossToWorlds.get(currentBoss).put(client.getWorld(), peekInfo);
			if (config.addEmptyScoutsToChat())
			{
				String msg = "W" + client.getWorld() + " " + currentBoss.name + " Empty";
				client.addChatMessage(ChatMessageType.CONSOLE, "", msg, "");
			}
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (!event.getMenuAction().equals(MenuAction.GAME_OBJECT_SECOND_OPTION))
		{
			return;
		}
		int objectId = event.getId();
		for (BossLair bossLair : BossLair.values)
		{
			if (bossLair.objectId == objectId)
			{
				currentBoss = bossLair;
				return;
			}
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		int objId = event.getGameObject().getId();
		for (BossLair lair : BossLair.values)
		{
			if (objId == lair.objectId)
			{
				lairToObject.put(lair, event.getGameObject());
				return;
			}
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		int objId = event.getGameObject().getId();
		for (BossLair lair : BossLair.values)
		{
			if (objId == lair.objectId)
			{
				lairToObject.remove(lair);
				return;
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		GameState gameState = gameStateChanged.getGameState();
		if (gameState == GameState.LOADING)
		{
			lairToObject.clear();
		}
	}

	@Provides
	PeekConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PeekConfig.class);
	}
}
