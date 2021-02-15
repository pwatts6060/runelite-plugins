package com.lmsnotifier;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.HintArrowType;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Lms Notifier"
)
public class LMSPlugin extends Plugin
{
	static final String CONFIG_GROUP_KEY = "lmsconfig";

	private static final WorldArea lmsCompetitiveLobby = new WorldArea(3138, 3639, 8, 7, 0);
	private static final WorldArea lmsCasualLobby = new WorldArea(3139, 3639, 6, 6, 1);
	private static final WorldArea lmsHighStakesLobby = new WorldArea(3138, 3639, 8, 7, 2);
	private boolean inLobby = false;
	private boolean inGame = false;
	private WorldPoint originalHintPoint;

	@Inject
	private Client client;

	@Inject
	private LMSConfig config;

	@Inject
	private Notifier notifier;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Lms Notifier started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Lms Notifier stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOADING)
		{
			return;
		}
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
		tryUpdateSafeZoneArrow();
	}

	private void tryUpdateSafeZoneArrow()
	{
		if (!inGame)
		{
			originalHintPoint = null;
			return;
		}

		if (!config.pointToSafeZone())
		{
			return;
		}

		if (!client.hasHintArrow() || !client.getHintArrowType().equals(HintArrowType.WORLD_POSITION))
		{
			originalHintPoint = null;
			return;
		}

		if (originalHintPoint == null)
		{
			originalHintPoint = client.getHintArrowPoint();
		}

		int arrowSceneX = originalHintPoint.getX() * 4 - client.getBaseX() * 4 + 2 - client.getLocalPlayer().getLocalLocation().getX() / 32;
		int arrowSceneY = originalHintPoint.getY() * 4 - client.getBaseY() * 4 + 2 - client.getLocalPlayer().getLocalLocation().getY() / 32;
		int distance = arrowSceneX * arrowSceneX + arrowSceneY * arrowSceneY;

		if (distance >= 90_000) // hint arrow won't show in minimap
		{
			// make a closer new point for the arrow that is in the same direction
			double theta = Math.atan2(arrowSceneY, arrowSceneX);
			int newX = (int) (74 * Math.cos(theta));
			int newY = (int) (74 * Math.sin(theta));
			WorldPoint newArrow = new WorldPoint(client.getLocalPlayer().getWorldLocation().getX() + newX, client.getLocalPlayer().getWorldLocation().getY() + newY, 0);
			client.setHintArrow(newArrow);
		}
		else if (!client.getHintArrowPoint().equals(originalHintPoint))
		{
			restoreOriginalHint();
		}
	}

	private void restoreOriginalHint()
	{
		if (originalHintPoint != null && client.hasHintArrow())
		{
			client.clearHintArrow();
			client.setHintArrow(originalHintPoint);
		}
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed ev)
	{
		if (ev.getGroupId() == WidgetInfo.LMS_KDA.getGroupId())
		{
			inGame = false;
			originalHintPoint = null;
			client.clearHintArrow();
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		if (widgetLoaded.getGroupId() == WidgetInfo.LMS_KDA.getGroupId())
		{
			inGame = true;
			originalHintPoint = null;
		}
	}

	@Provides
	LMSConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LMSConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(CONFIG_GROUP_KEY))
		{
			return;
		}
		if (event.getKey().equals(LMSConfig.POINT_SAFE_KEY) && Boolean.FALSE.toString().equals(event.getNewValue()))
		{
			restoreOriginalHint();
		}
	}
}
