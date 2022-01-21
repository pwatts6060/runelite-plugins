package com.xpnotify;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.StatChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
        name = "XP Drop Notify"
)
public class XpNotifyPlugin extends Plugin {
    private final Set<String> filteredSkills = new HashSet<>();
    private static final int[] previous_exp = new int[Skill.values().length - 1];

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private XpNotifyConfig config;

    @Override
    protected void startUp() {
        if (client.getGameState() == GameState.LOGGED_IN) {
            clientThread.invokeLater(() ->
            {
                int[] xps = client.getSkillExperiences();
                System.arraycopy(xps, 0, previous_exp, 0, previous_exp.length);
            });
        } else {
            Arrays.fill(previous_exp, 0);
        }

        loadFilteredSkills();
    }

    @Subscribe
    protected void onConfigChanged(ConfigChanged configChanged) {
        if ("XpNotify".equals(configChanged.getGroup())) {
            if ("skillsToFilter".equals(configChanged.getKey())) {
                loadFilteredSkills();
            }
        }
    }

    private void loadFilteredSkills() {
        filteredSkills.clear();
        filteredSkills.addAll(Text.fromCSV(config.skillsToFilter()).stream().map(String::toLowerCase).collect(Collectors.toList()));
        // Since most people know this skill by runecrafting not runecraft
        if (filteredSkills.contains("runecrafting")) {
            filteredSkills.add("runecraft");
        }
    }


    @Subscribe
    protected void onStatChanged(StatChanged event) {
        int currentXp = event.getXp();
        int previousXp = previous_exp[event.getSkill().ordinal()];
        if (previousXp > 0 && currentXp - previousXp > 0 && !filteredSkills.contains(event.getSkill().getName().toLowerCase())) {
//            XpDrop xpDrop = new XpDrop(event.getSkill(), currentXp - previousXp, false);
            client.playSoundEffect(config.soundId(), config.volume());
        }

        previous_exp[event.getSkill().ordinal()] = event.getXp();
    }

    @Provides
    XpNotifyConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(XpNotifyConfig.class);
    }
}
