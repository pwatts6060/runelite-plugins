package com.lmsnotifier;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;

class LMSOverlay extends Overlay
{
	private static final int MIN_SCORE = 500;
	private static final int MAX_SCORE = 3_000;
	private final Client client;
	private final LMSConfig config;
	private final LMSPlugin plugin;

	@Inject
	private LMSOverlay(Client client, LMSConfig config, LMSPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.inGame)
		{
			return null;
		}

		if (plugin.highlightChests())
		{
			renderChests(graphics);
		}

		if (plugin.highlightLootCrates())
		{
			renderLootCrates(graphics);
		}

		if (!config.rankVisual().equals(RankVisual.NONE))
		{
			renderRanks(graphics);
		}

		if (config.getBotDisplay() != BotDisplay.NONE)
		{
			renderBots(graphics);
		}

		if (config.overlayKillDeaths())
		{
			renderKillDeaths(graphics);
		}

		return null;
	}

    private void renderKillDeaths(Graphics2D graphics) {
        for (LMSPlayer lmsPlayer : plugin.localLMSPlayers) {
            DeathTracker.KD kd = plugin.deathTracker.getKD(lmsPlayer.player.getName());
            if (kd.kills <= 0 && kd.deaths <= 0) {
                continue;
            }
            String text = kd.kills + "-" + kd.deaths;
            Point textLocation = lmsPlayer.player.getCanvasTextLocation(graphics, text, 0);
            if (textLocation != null)
            {
                OverlayUtil.renderTextLocation(graphics, new Point(textLocation.getX(), textLocation.getY() - 12), text, Color.WHITE);
            }
        }
    }

	private void renderBots(Graphics2D graphics) {
		for (LMSPlayer lmsPlayer : plugin.localLMSPlayers)
		{
			BotIdentification.Status status = plugin.botIdentification.statusFor(lmsPlayer.player.getName());
			if (config.getBotDisplay() == BotDisplay.BOTS_ONLY && status != BotIdentification.Status.BOT) {
				continue;
			}
			Color color;
			String text;
			switch (status) {
				case BOT:
					color = Color.RED;
					text = "Bot";
					break;
				case HUMAN:
					color = Color.GREEN;
					text = "Human";
					break;
				case UNSURE:
				default:
					color = Color.WHITE;
					text = "?";
					break;
			}
			Point textLocation = lmsPlayer.player.getCanvasTextLocation(graphics, text, 0);
			if (textLocation != null)
			{
				OverlayUtil.renderTextLocation(graphics, new Point(textLocation.getX(), textLocation.getY() + 12), text, color);
			}
		}
	}

	private void renderRanks(Graphics2D graphics)
	{
		for (LMSPlayer lmsPlayer : plugin.localLMSPlayers)
		{
			Color colour;
			if (config.metricHeatmap())
			{
				int clampedScore = Math.max(MIN_SCORE, Math.min(lmsPlayer.lmsRank.score, MAX_SCORE));
				double proportion = (double) (clampedScore - MIN_SCORE) / (MAX_SCORE - MIN_SCORE);
				colour = new Color(ColourUtil.interpolateBetweenRgbs(0x00FF00, 0xFF0000, proportion));
			}
			else
			{
				colour = config.metricColour();
			}
			String text = "";
			if (config.rankMetric().equals(RankMetric.RANK))
			{
				text = lmsPlayer.lmsRank.rank < 0 ? "n/a" : Integer.toString(lmsPlayer.lmsRank.rank);
			}
			else if (config.rankMetric().equals(RankMetric.SCORE))
			{
				text = lmsPlayer.lmsRank.score < 0 ? "n/a" : Integer.toString(lmsPlayer.lmsRank.score);
			}
			Point textLocation = lmsPlayer.player.getCanvasTextLocation(graphics, text, 0);
			if (textLocation != null)
			{
				OverlayUtil.renderTextLocation(graphics, textLocation, text, colour);
			}
		}
	}

	private void renderLootCrates(Graphics2D graphics)
	{
		int max = config.lootCrateRadius() * config.lootCrateRadius() * 128 * 128;
		LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
		for (TileObject object : plugin.lootCrates.values())
		{
			if (LMSUtil.distSquared(object.getLocalLocation(), playerLocation) >= max)
			{
				continue;
			}

			if (config.lootCrateHighlightType().equals(LootCrateHightlight.CLICK_BOX))
			{
				Shape shape = object.getClickbox();
				if (shape != null)
				{
					Color color = config.lootCrateColour();
					Color clickBoxColor = ColorUtil.colorWithAlpha(color, color.getAlpha() / 12);

					graphics.setColor(color);
					graphics.draw(shape);
					graphics.setColor(clickBoxColor);
					graphics.fill(shape);
				}
			}
			else if (config.lootCrateHighlightType().equals(LootCrateHightlight.TILE))
			{
				Shape shape = object.getCanvasTilePoly();
				if (shape != null)
				{
					OverlayUtil.renderPolygon(graphics, shape, config.lootCrateColour());
				}
			}
			else if (config.lootCrateHighlightType().equals(LootCrateHightlight.HULL))
			{
				Shape shape = ((GameObject) object).getConvexHull();
				if (shape != null)
				{
					OverlayUtil.renderPolygon(graphics, shape, config.lootCrateColour());
				}
			}
		}
	}

	private void renderChests(Graphics2D graphics)
	{
		int max = config.chestRadius() * config.chestRadius() * 128 * 128;
		LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
		for (TileObject object : plugin.chests.values())
		{
			if (object.getPlane() != client.getPlane())
			{
				continue;
			}
			if (LMSUtil.distSquared(object.getLocalLocation(), playerLocation) >= max)
			{
				continue;
			}

			if (config.highlightChestType().equals(ChestHightlightType.CLICK_BOX))
			{
				Shape shape = object.getClickbox();
				if (shape != null)
				{
					Color color = config.chestColour();
					Color clickBoxColor = ColorUtil.colorWithAlpha(color, color.getAlpha() / 12);

					graphics.setColor(color);
					graphics.draw(shape);
					graphics.setColor(clickBoxColor);
					graphics.fill(shape);
				}
			}
			else if (config.highlightChestType().equals(ChestHightlightType.TILE))
			{
				Shape shape = object.getCanvasTilePoly();
				if (shape != null)
				{
					OverlayUtil.renderPolygon(graphics, shape, config.chestColour());
				}
			}
			else if (config.highlightChestType().equals(ChestHightlightType.HULL))
			{
				Shape shape = ((GameObject) object).getConvexHull();
				if (shape != null)
				{
					OverlayUtil.renderPolygon(graphics, shape, config.chestColour());
				}
			}
		}
	}
}
