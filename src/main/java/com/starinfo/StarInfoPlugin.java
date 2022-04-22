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

package com.starinfo;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.AnimationID;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NullNpcID;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.Angle;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;

@PluginDescriptor(
	name = "Star Info",
	description = "Displays tier, number of miners, health % above shooting stars"
)
public class StarInfoPlugin extends Plugin
{

	private static final int NPC_ID = NullNpcID.NULL_10629;

	private static final Set<Integer> pickAnims = ImmutableSet.of(
		AnimationID.MINING_ADAMANT_PICKAXE,
		AnimationID.MINING_TRAILBLAZER_PICKAXE_2,
		AnimationID.MINING_TRAILBLAZER_PICKAXE,
		AnimationID.MINING_BLACK_PICKAXE,
		AnimationID.MINING_BRONZE_PICKAXE,
		AnimationID.MINING_GILDED_PICKAXE,
		AnimationID.MINING_CRYSTAL_PICKAXE,
		AnimationID.MINING_3A_PICKAXE,
		AnimationID.MINING_DRAGON_PICKAXE,
		AnimationID.MINING_DRAGON_PICKAXE_OR,
		AnimationID.MINING_DRAGON_PICKAXE_OR_TRAILBLAZER,
		AnimationID.MINING_DRAGON_PICKAXE_UPGRADED,
		AnimationID.MINING_INFERNAL_PICKAXE,
		AnimationID.MINING_IRON_PICKAXE,
		AnimationID.MINING_MITHRIL_PICKAXE,
		AnimationID.MINING_IRON_PICKAXE,
		AnimationID.MINING_RUNE_PICKAXE,
		AnimationID.MINING_STEEL_PICKAXE,
		AnimationID.MINING_TRAILBLAZER_PICKAXE_3
	);
	private static final int MINING_CACHE_TIME = 13; // count player as a miner if they have done mining anim within this many ticks ago
	private static final Map<String, Integer> playerLastMined = new HashMap<>();

	@Inject
	private StarInfoOverlay starOverlay;

	public List<Star> stars = new ArrayList<>();

	@Inject
	private InfoBoxManager infoBoxManager;

	private StarInfoBox infoBox;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	Client client;

	@Inject
	private StarInfoConfig starConfig;

	@Provides
	StarInfoConfig
	provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StarInfoConfig.class);
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
		clear();
		refresh();
		overlayManager.remove(starOverlay);
		infoBox = null;
	}

	private void clear()
	{
		playerLastMined.clear();
		stars.clear();
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (event.getNpc().getId() != NPC_ID)
		{
			return;
		}
		for (Star star : stars)
		{
			if (star.getWorldPoint().equals(event.getNpc().getWorldLocation()))
			{
				star.setNpc(event.getNpc());
				refresh();
				return;
			}
		}
		stars.add(0, new Star(event.getNpc(), client.getWorld()));
		refresh();
	}


	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (event.getNpc().getId() != NPC_ID)
		{
			return;
		}
		for (Star star : stars)
		{
			if (star.getWorldPoint().equals(event.getNpc().getWorldLocation()))
			{
				star.setNpc(null);
				refresh();
				return;
			}
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		int tier = Star.getTier(event.getGameObject().getId());
		if (tier < 0)
		{
			return;
		}

		boolean newStar = false;
		Star star = null;
		for (Star s : stars)
		{
			if (s.getWorldPoint().equals(event.getGameObject().getWorldLocation()))
			{
				s.setObject(event.getGameObject());
				star = s;
				break;
			}
		}
		if (star == null)
		{
			star = new Star(event.getGameObject(), client.getWorld());
			stars.add(0, star);
			newStar = true;
		}

		if (newStar)
		{
			String msg = "Star Found T" + tier + " / W" + star.getWorld() + " / " + star.getLocation();
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, "");
		}
		refresh();
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		int tier = Star.getTier(event.getGameObject().getId());
		if (tier < 0)
		{
			return;
		}
		if (tier > 1)
		{
			return;
		}

		Iterator<Star> it = stars.iterator();
		while (it.hasNext())
		{
			Star star = it.next();
			if (event.getGameObject().equals(event.getGameObject()) || event.getGameObject().getWorldLocation().equals(star.getWorldPoint()))
			{
				it.remove();
				break;
			}
		}
		refresh();
	}

	void updateMiners(Star star)
	{
		WorldArea areaH = new WorldArea(star.getWorldPoint().dx(-1), 4, 2);
		WorldArea areaV = new WorldArea(star.getWorldPoint().dy(-1), 2, 4);
		int count = 0;
		int tickCount = client.getTickCount();
		for (Player p : client.getPlayers())
		{
			if (!p.getWorldLocation().isInArea2D(areaH, areaV)) // Skip players not next to the star
			{
				continue;
			}
			if (!facingObject(p.getWorldLocation(), p.getOrientation(), star.getWorldPoint()))
			{
				continue;
			}
			if (pickAnims.contains(p.getAnimation())) // count anyone that is doing mining animation
			{
				count++;
				playerLastMined.put(p.getName(), tickCount);
				continue;
			}
			if (p.getHealthRatio() < 0 || !playerLastMined.containsKey(p.getName()))
			{
				continue;
			}
			int ticksSinceMinedLast = tickCount - playerLastMined.get(p.getName());
			if (ticksSinceMinedLast < MINING_CACHE_TIME)
			{
				count++;
			}
		}
		star.setMiners(count);
	}

	private boolean facingObject(WorldPoint p1, int orientation, WorldPoint p2)
	{
		Direction dir = new Angle(orientation).getNearestDirection();
		WorldPoint dif = p2.dx(-p1.getX()).dy(-p1.getY());
		switch (dir)
		{
			case NORTH:
				return dif.getY() > 0;
			case SOUTH:
				return dif.getY() < 0;
			case EAST:
				return dif.getX() > 0;
			case WEST:
				return dif.getX() < 0;
		}
		return false;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged state)
	{
		if (state.getGameState() == GameState.HOPPING || state.getGameState() == GameState.LOGGING_IN)
		{
			clear();
			refresh();
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (stars.isEmpty())
		{
			return;
		}
		Iterator<Star> it = stars.iterator();
		boolean refresh = false;
		while (it.hasNext())
		{
			Star star = it.next();
			if (client.getLocalPlayer().getWorldLocation().distanceTo(star.getWorldPoint()) > 90)
			{
				it.remove();
				refresh = true;
			}
		}

		if (!stars.isEmpty() && starConfig.showMiners())
		{
			updateMiners(stars.get(0));
		}

		if (refresh)
		{
			refresh();
		}
	}

	public void refresh()
	{
		if (stars.isEmpty())
		{
			if (starConfig.showInfoBox())
			{
				infoBoxManager.removeInfoBox(infoBox);
			}
			if (starConfig.showHintArrow())
			{
				client.clearHintArrow();
			}
		}
		else
		{
			Star star = stars.get(0);
			if (starConfig.showInfoBox())
			{
				infoBoxManager.removeInfoBox(infoBox);
				infoBox = new StarInfoBox(itemManager.getImage(25547), this, star);
				infoBoxManager.addInfoBox(infoBox);
			}
			if (starConfig.showHintArrow())
			{
				client.setHintArrow(star.getWorldPoint());
			}
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (event.getType() != MenuAction.EXAMINE_OBJECT.getId() || !starConfig.copyToClipboard())
		{
			return;
		}

		final Tile tile = client.getScene().getTiles()[client.getPlane()][event.getActionParam0()][event.getActionParam1()];
		final TileObject tileObject = findTileObject(tile, event.getIdentifier());

		if (tileObject == null)
		{
			return;
		}

		client.createMenuEntry(-1)
			.setOption("Copy")
			.setTarget(event.getTarget())
			.setParam0(event.getActionParam0())
			.setParam1(event.getActionParam1())
			.setIdentifier(event.getIdentifier())
			.setType(MenuAction.RUNELITE)
			.onClick(this::copy);
	}

	private void copy(MenuEntry menuEntry)
	{
		if (stars.isEmpty())
		{
			return;
		}
		Star star = stars.get(0);
		String content = "W" + star.getWorld() + " T" + star.getTier() + " / " + star.getMiners() + " Miners / ";
		if (star.getHealth() >= 0)
		{
			content += star.getHealth() + "%";
		}
		content += " " + DiscordTimeStamp.relativeTimeNow();

		final StringSelection stringSelection = new StringSelection(Text.removeTags(content));
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Copied star information to clipboard.", "");
	}

	private TileObject findTileObject(Tile tile, int id)
	{
		if (tile == null)
		{
			return null;
		}
		for (GameObject object : tile.getGameObjects())
		{
			if (object != null && object.getId() == id)
			{
				return object;
			}
		}
		return null;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("starinfoplugin"))
		{
			return;
		}
		switch (event.getKey())
		{
			case StarInfoConfig.SHOW_MINERS:
				if (!stars.isEmpty() && starConfig.showMiners())
				{
					updateMiners(stars.get(0));
				}
				break;
			case StarInfoConfig.TEXT_COLOR_KEY:
				starOverlay.updateConfig();
				break;
			case StarInfoConfig.INFO_BOX_KEY:
				if (starConfig.showInfoBox())
				{
					refresh();
				}
				else
				{
					infoBoxManager.removeInfoBox(infoBox);
					infoBox = null;
				}
				break;
			case StarInfoConfig.HINT_ARROW_KEY:
				if (starConfig.showHintArrow())
				{
					refresh();
				}
				else
				{
					client.clearHintArrow();
				}
				break;
		}
	}
}