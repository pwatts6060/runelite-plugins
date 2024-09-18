package com.entrana;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Entrana"
)
public class EntranaPlugin extends Plugin
{
	private static final int BALLOON_TRANSPORT_ID = 469;

	@Inject
	private Client client;

	@Inject
	private EntranaConfig config;

	@Inject
	private ContainerHighlight containerHighlight;

	@Inject
	private OverlayManager overlayManager;

	@Provides
	EntranaConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EntranaConfig.class);
	}

	private static final WorldArea entranaDock = new WorldArea(new WorldPoint(3043, 3234, 0), 8, 4);

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}
		if (!entranaDock.contains2D(player.getWorldLocation()) && client.getWidget(BALLOON_TRANSPORT_ID, 0) == null)
		{
			overlayManager.remove(containerHighlight);
			return;
		}
		overlayManager.add(containerHighlight);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(containerHighlight);
	}
}
