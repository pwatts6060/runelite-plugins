/*
 * Copyright (c) 2022, Cute Rock
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.startierindicator;

import javax.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.GameState;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;

@PluginDescriptor(
	name = "Star Tier Indicator",
	description = "Displays a string of the star tier above a crashed star",
	enabledByDefault = false
)
public class StarTierIndicatorPlugin extends Plugin
{

	private static final int[] TIER_IDS = new int[]{41229, 41228, 41227, 41226, 41225, 41224, 41223, 41021, 41020};

	@Inject
	private StarTierIndicatorOverlay starOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private StarTierIndicatorConfig starConfig;

	@Provides
	StarTierIndicatorConfig
	provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StarTierIndicatorConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(starOverlay);
		starOverlay.updateConfig();
	}

	@Override
	protected void shutDown() throws Exception
	{
		starOverlay.removeStar();
		overlayManager.remove(starOverlay);
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		int tier = getTier(event.getGameObject().getId());
		if (tier != -1)
		{
			starOverlay.setStar(event.getGameObject(), "T" + tier);
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		if (getTier(event.getGameObject().getId()) != -1)
		{
			starOverlay.removeStar();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged state)
	{
		if (state.getGameState() == GameState.HOPPING || state.getGameState() == GameState.LOGGING_IN)
		{
			starOverlay.removeStar();
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (starOverlay.getStar() != null && getTier(starOverlay.getStar().starObject.getId()) != -1)
		{
			starOverlay.update();
		}
		else
		{
			starOverlay.removeStar();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("startierplugin"))
		{
			starOverlay.updateConfig();
		}
	}

	private int getTier(int id)
	{
		for (int i = 0; i < TIER_IDS.length; i++)
		{
			if (id == TIER_IDS[i])
			{
				return i + 1;
			}
		}
		return -1;
	}
}