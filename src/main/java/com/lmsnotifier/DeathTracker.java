package com.lmsnotifier;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Value;
import net.runelite.api.*;
import net.runelite.client.RuneLite;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class DeathTracker {

    private static final File SAVE_FILE = new File(RuneLite.RUNELITE_DIR, "lms-kd.json");

    @Inject
    private Client client;

    @Inject
    private LMSConfig config;

    private int lastAttackTick;
    private Player opponent;
    private Player potentialOpponent;

    // maps player names to deaths/kills
    private final Map<String, KD> deathMap = new HashMap<>();

    public void interactChanged(Player source, Player target) {
        if (source.equals(client.getLocalPlayer())) {
            potentialOpponent = target;
        } else if (target.equals(client.getLocalPlayer())) {
            potentialOpponent = source;
        }
    }

    public void animationChanged(Actor actor) {
        if (!(actor instanceof Player)) {
            return;
        }
        checkDeath(actor);
        if (!AttackAnimation.contains(actor.getAnimation())) {
            return;
        }
        Player player = (Player) actor;
        if (player.getName() == null) {
            return;
        }
        if (opponent != null && player.getName().equals(opponent.getName())) {
            lastAttackTick = client.getTickCount();
            return;
        }
        if (potentialOpponent != null && client.getTickCount() - lastAttackTick > 5
                && player.getName().equals(potentialOpponent.getName())) {
            lastAttackTick = client.getTickCount();
            opponent = potentialOpponent;
        }
    }

    private void checkDeath(Actor actor) {
        if (actor.getAnimation() != AnimationID.DEATH || actor.getName() == null || opponent == null) {
            return;
        }
        KD kd;
        if (actor.getName().equals(opponent.getName())) {
            kd = new KD(1, 0);
            if (config.fightResultInChat()) {
                client.addChatMessage(ChatMessageType.CONSOLE, "", "You killed " + opponent.getName(), "");
            }
        } else if (actor.getName().equals(client.getLocalPlayer().getName())) {
            kd = new KD(0, 1);
            if (config.fightResultInChat()) {
                client.addChatMessage(ChatMessageType.CONSOLE, "", "You died to " + opponent.getName(), "");
            }
        } else {
            return;
        }
        deathMap.merge(opponent.getName(), kd, KD::sum);
        opponent = null;
        lastAttackTick = 0;
    }

    public void load() {
        if (!SAVE_FILE.exists()) {
            return;
        }
        try {
            String json = new String(Files.readAllBytes(SAVE_FILE.toPath()));
            deathMap.putAll(new Gson().fromJson(json, new TypeToken<Map<String, KD>>() {}.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        String json = new Gson().toJson(deathMap);
        try {
            Files.write(SAVE_FILE.toPath(), json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public KD getKD(String name) {
        return deathMap.getOrDefault(name, new KD(0, 0));
    }

    @AllArgsConstructor
    @Value
    public static class KD {
        public int kills;
        public int deaths;

        public static KD sum(KD kd, KD kd1) {
            return new KD(kd.kills + kd1.kills, kd.deaths + kd1.deaths);
        }
    }
}
