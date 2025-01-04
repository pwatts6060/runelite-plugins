package com.lmsnotifier;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.RuneLite;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.Set;

public class SweatTracker {
    @Inject
    private Client client;

    private static final File SWEATS_FILE = new File(RuneLite.RUNELITE_DIR, "lms-sweats.txt");
    public Set<String> usernames = new LinkedHashSet<>();
    boolean requiresSave = false;

    public boolean isSweat(Player p) {
        if (p.getName() == null)
            return false;
        return isSweat(p.getName());
    }

    public boolean isSweat(String name) {
        return usernames.contains(Text.sanitize(name.toLowerCase()));
    }

    public void markPlayer(String name) {
        if (usernames.contains(name)) {
            usernames.remove(name);
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", name + " unmarked as sweat for Last Man Standing.", "");
        } else {
            usernames.add(name);
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", name + " marked as sweat for Last Man Standing.", "");
        }
        requiresSave = true;
    }


    /*
    Outdated
    Range 1
    Construction 16
    Herblore 52
    Crafting 43
    Fletching 60
    Hunter 1
    Mining 18 or 41
    Smithing 39
    Fishing 24
    Farming 9
    total level 750-760
     */
    public int botCriteriaCount(HiscoreResult r) {
        int criteriaMet = 0;
        /*
        Outdated
        Att/Str 40
        Construction 20
        Herblore 23-24
        Crafting 37-38
        Fletching 95+
        Hunter 9/11
        Mining 40-46
        Smithing 40-41
        Fishing 24
        Farming 9
        RC 9
        FM 40
         */
        if (r.getSkill(HiscoreSkill.ATTACK).getLevel() == 40 &&
                r.getSkill(HiscoreSkill.STRENGTH).getLevel() == 40) {
            criteriaMet++;
        }
        if (r.getSkill(HiscoreSkill.CONSTRUCTION).getLevel() == 20) {
            criteriaMet++;
        }
        if (r.getSkill(HiscoreSkill.HERBLORE).getLevel() >= 23 && r.getSkill(HiscoreSkill.HERBLORE).getLevel() <= 24) {
            criteriaMet++;
        }
        if (r.getSkill(HiscoreSkill.CRAFTING).getLevel() >= 37 && r.getSkill(HiscoreSkill.CRAFTING).getLevel() <= 38) {
            criteriaMet++;
        }
        if (r.getSkill(HiscoreSkill.FLETCHING).getLevel() >= 95) {
            criteriaMet++;
        }
        if (r.getSkill(HiscoreSkill.HUNTER).getLevel() == 9 || r.getSkill(HiscoreSkill.HUNTER).getLevel() == 13) {
            criteriaMet++;
        }
        if (r.getSkill(HiscoreSkill.MINING).getLevel() >= 40 && r.getSkill(HiscoreSkill.MINING).getLevel() <= 46) {
            criteriaMet++;
        }
        if (r.getSkill(HiscoreSkill.SMITHING).getLevel() >= 40 && r.getSkill(HiscoreSkill.SMITHING).getLevel() <= 41) {
            criteriaMet++;
        }
        if (r.getSkill(HiscoreSkill.FISHING).getLevel() == 24) {
            criteriaMet++;
        }
        if (r.getSkill(HiscoreSkill.FARMING).getLevel() == 9) {
            criteriaMet++;
        }
        if (r.getSkill(HiscoreSkill.FIREMAKING).getLevel() == 40) {
            criteriaMet++;
        }
        if (r.getSkill(HiscoreSkill.COOKING).getLevel() == 16) {
            criteriaMet++;
        }
        if (r.getSkill(HiscoreSkill.RUNECRAFT).getLevel() == 9) {
            criteriaMet++;
        }
        return criteriaMet;
    }

    public void load() {
        if (!SWEATS_FILE.exists()) {
            return;
        }

        try {
            for (String line : Files.readAllLines(SWEATS_FILE.toPath())) {
                if (line.isEmpty())
                    continue;
                line = line.trim();
                usernames.add(Text.sanitize(line.toLowerCase()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (!requiresSave)
            return;
        requiresSave = false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SWEATS_FILE))) {
            for (String name : usernames) {
                writer.write(name);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
