package com.lmsnotifier;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BotIdentification {

    private final Client client;
    private final LMSConfig config;
    private final LMSPlugin plugin;

    private final Map<String, PlayerTracker> playerToTracker = new HashMap<>();
    private final Map<String, String> attackerToVictim = new HashMap<>();
    private int tick;

    @Inject
    private BotIdentification(Client client, LMSConfig config, LMSPlugin plugin) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    public void reset() {
//        dumpInfo();
        if (config.summarizeBots()) {
            displaySummary();
        }
        playerToTracker.clear();
        attackerToVictim.clear();
        tick = 0;
    }

    private void displaySummary() {
        int bots = 0;
        int humans = 0;
        int unknowns = 0;
        for (PlayerTracker tracker : playerToTracker.values()) {
            if (tracker.status == null) {
                unknowns++;
                continue;
            }
            switch (tracker.status) {
                case UNSURE:
                    unknowns++;
                    break;
                case BOT:
                    bots++;
                    break;
                case HUMAN:
                    humans++;
                    break;
            }
        }
        String message = "You saw " + ColorUtil.wrapWithColorTag(Integer.toString(humans), Color.RED) + " humans, "
                + ColorUtil.wrapWithColorTag(Integer.toString(bots), Color.RED) + " bots, and "
                + ColorUtil.wrapWithColorTag(Integer.toString(unknowns), Color.RED) + " unknowns that game.";
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, "");
    }

//    private void dumpInfo() {
//        playerToTracker.forEach((name, playerTracker) -> {
//            System.out.println(name);
//            for (PlayerSnapshot snapshot : playerTracker.snapshots) {
//                if (snapshot == null)
//                    continue;
//                System.out.println(snapshot.toString());
//            }
//        });
//    }

    public void tick() {
        if (!shouldAnalyze() || !plugin.inGame || client.getLocalPlayer().getWorldLocation().getRegionID() == LMSPlugin.FEROX_REGION_ID) {
            return;
        }
        List<Player> players = new ArrayList<>(client.getPlayers());
        for (Player player : players) {
            if (client.getLocalPlayer().getName().equals(player.getName())) {
                continue;
            }
            Player opponent = getOpponent(player);
            PlayerSnapshot snapshot = new PlayerSnapshot(player, opponent, tick);
            PlayerTracker tracker = playerToTracker.computeIfAbsent(player.getName(), name -> new PlayerTracker(player.getName()));
            tracker.addSnapshot(snapshot);
            tracker.updateStatus();
        }
        tick++;
    }

    private boolean shouldAnalyze() {
        return config.summarizeBots() || config.getBotDisplay() != BotDisplay.NONE;
    }

    private Player getOpponent(Player player) {
        String victim = attackerToVictim.get(player.getName());
        if (victim == null)
            return null;
        String victimsAttacker = attackerToVictim.get(victim);
        if (victimsAttacker == null)
            return null;
        return findPlayerByName(victim);
    }

    public Player findPlayerByName(String name) {
        for (Player player : client.getPlayers()) {
            if (player.getName().equals(name))
                return player;
        }
        return null;
    }

    public Status statusFor(String name) {
        if (!playerToTracker.containsKey(name))
            return Status.UNSURE;
        return playerToTracker.get(name).getStatus();
    }

    public void interaction(Player source, Player target) {
        attackerToVictim.put(source.getName(), target.getName());
    }

    enum Status {
        UNSURE,
        BOT,
        HUMAN,
        ;
    }
}
