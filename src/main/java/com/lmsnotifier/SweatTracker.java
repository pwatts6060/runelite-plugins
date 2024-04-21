package com.lmsnotifier;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.RuneLite;
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
        return usernames.contains(Text.sanitize(p.getName().toLowerCase()));
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
