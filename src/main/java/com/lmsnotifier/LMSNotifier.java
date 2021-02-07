package com.lmsnotifier;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
	name = "Lms Notifier"
)
public class LMSNotifier extends Plugin
{
	private static final WorldArea lmsCompetitiveLobby = new WorldArea(3138, 3639, 8, 7, 0);
	private static final WorldArea lmsCasualLobby = new WorldArea(3139, 3639, 6, 6, 1);
	private static final WorldArea lmsHighStakesLobby = new WorldArea(3138, 3639, 8, 7, 2);
	private boolean inLobby = false;

	@Inject
	private Client client;

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
		if (inLobby)
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
	}
}
