package com.lmsnotifier;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class LMSOverlay2D extends OverlayPanel {
    private final Client client;
    private final LMSConfig config;
    private final LMSPlugin plugin;

    @Inject
    private LMSOverlay2D(Client client, LMSConfig config, LMSPlugin plugin)
    {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.BOTTOM_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.preLobby && !plugin.inLobby || !config.getLobbySummary()) {
            return null;
        }

        int botCbRangeCnt = 0;
        int historicLosses = 0;
        int highRankCnt = 0;
        int highCbLvl = 0;
        int sweats = 0;

        final int RANK_CAP = 6000;
        final int MIN_BOT_CB = 40;
        final int MAX_BOT_CB = 59;
        final int HIGH_CB = 90;

        for (Player p : client.getPlayers()) {
            if (p.getWorldLocation().distanceTo(LMSPlugin.lmsCompetitiveLobby) != 0) {
                continue;
            }
            String name = p.getName();
            if (client.getLocalPlayer().getName() != null && client.getLocalPlayer().getName().equals(name)) {
                continue;
            }

            if (p.getCombatLevel() >= HIGH_CB) {
                highCbLvl++;
            }

            DeathTracker.KD kd = plugin.deathTracker.getKD(p.getName());
            if (kd.deaths != 0 && kd.deaths >= kd.kills) {
                historicLosses++;
            }

            if (p.getCombatLevel() >= MIN_BOT_CB && p.getCombatLevel() <= MAX_BOT_CB) {
                botCbRangeCnt++;
            }

            if (plugin.sweatTracker.isSweat(p)) {
                sweats++;
            }
        }

        for (LMSPlayer p : plugin.localLMSPlayers) {
            if (p.player.getCombatLevel() >= MIN_BOT_CB && p.player.getCombatLevel() <= MAX_BOT_CB) {
//                botCbRangeCnt++;
            } else if (p.lmsRank.score > RANK_CAP) {
                highRankCnt++;
            }
        }

        panelComponent.getChildren().add(TitleComponent.builder().text("Lobby Summary").build());

        panelComponent.getChildren().add(LineComponent.builder().left("Bot Cb Range ("+MIN_BOT_CB+"-"+MAX_BOT_CB+"):").right(Integer.toString(botCbRangeCnt)).build());
        panelComponent.getChildren().add(LineComponent.builder().left("Prev deaths:").right(Integer.toString(historicLosses)).build());
        panelComponent.getChildren().add(LineComponent.builder().left("High Rank ("+RANK_CAP+"+):").right(Integer.toString(highRankCnt)).build());
        panelComponent.getChildren().add(LineComponent.builder().left("High Cb ("+HIGH_CB+"+):").right(Integer.toString(highCbLvl)).build());
        if (config.getSweatDisplay()) {
            panelComponent.getChildren().add(LineComponent.builder().left("Sweats:").right(Integer.toString(sweats)).build());
        }

        return super.render(graphics);
    }
}
