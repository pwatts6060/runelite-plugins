package com.lmsnotifier;

import net.runelite.api.HeadIcon;
import net.runelite.api.Player;
import net.runelite.api.kit.KitType;

import java.util.Arrays;

public class PlayerSnapshot {
    final int[] equipment;
    final int animation;
    HeadIcon headIcon;
    HeadIcon opponentIcon;
    int opponentWeapon;
    final int tick;

    public PlayerSnapshot(Player player, Player opponent, int tick) {
        this.equipment = player.getPlayerComposition().getEquipmentIds();
        this.animation = player.getAnimation();
        this.headIcon = player.getOverheadIcon();
        this.tick = tick;
        if (opponent != null) {
            opponentIcon = opponent.getOverheadIcon();
            opponentWeapon = opponent.getPlayerComposition().getEquipmentId(KitType.WEAPON);
        }
    }

    public String toString() {
        return (headIcon == null ? "none" : headIcon.name()) + " a: " + animation + " e: " + Arrays.toString(equipment) + " " + (opponentIcon == null ? "none" : opponentIcon.name()) + " " + opponentWeapon;
    }
}
