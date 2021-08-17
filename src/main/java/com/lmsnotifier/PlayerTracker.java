package com.lmsnotifier;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.HeadIcon;
import net.runelite.client.util.ColorUtil;

import java.awt.*;

@Slf4j
public class PlayerTracker {

    private static final int TOTAL = 500;

    private final LMSPlugin plugin;
    String name;
    PlayerSnapshot[] snapshots;
    int snapshotIndex;
    BotIdentification.Status status;
    String statusReason;

    PlayerTracker(String name, LMSPlugin plugin) {
        this.name = name;
        this.plugin = plugin;
        snapshots = new PlayerSnapshot[TOTAL];
        snapshotIndex = 0;
        status = null;
    }

    void addSnapshot(PlayerSnapshot snapshot) {
        if (snapshotIndex >= TOTAL)
            return;
        snapshots[snapshotIndex++] = snapshot;
    }

    public BotIdentification.Status getStatus() {
        if (status == null)
            return BotIdentification.Status.UNSURE;
        return status;
    }

    public void setStatus(BotIdentification.Status status, String reason) {
        this.status = status;
        this.statusReason = reason;
        log.debug("{} is a {} reason: {}", name, status, statusReason);
        if (plugin.getConfig().putNamesInChat() && status == BotIdentification.Status.BOT) {
            plugin.getClient().addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Bot: " + ColorUtil.wrapWithColorTag(name, Color.RED), "");
        }
    }

    public void updateStatus() {
        if (status != null)
            return;
        int total = 0;
        int exact2t = 0;
        int exact1T = 0;
//        boolean whipSinceStart = true; // whether they haven't changed their weapon from whip when first seen
        for (int i = 1, n = snapshotIndex - 2; i < n; i++) {
            PlayerSnapshot prev = snapshots[i-1];
            PlayerSnapshot cur = snapshots[i];
            PlayerSnapshot next = snapshots[i+1];
            PlayerSnapshot next2 = snapshots[i+2];
//            if (cur.equipment[3] != 20917) {
//                whipSinceStart = false;
//            }
//            if (!whipSinceStart && cur.animation == 1658 && cur.equipment[3] == 20917) { // make sure they have whip on still cause anim plays for 2t
//                // bots never attack with whip except at start of the game sometimes
//                setStatus(BotIdentification.Status.HUMAN, "Attacked with whip");
//                return;
//            }
            if (prev.headIcon == null)
                continue;
            if (next2.tick - next.tick > 1) { // skip gaps where the player went out of tracking range
                i += 4;
                continue;
            }

            HeadIcon prevStyle = getStyleFor(prev.opponentWeapon);
            HeadIcon curStyle = getStyleFor(cur.opponentWeapon);
            HeadIcon nextStyle = getStyleFor(next.opponentWeapon);
            if (prevStyle != null && curStyle != null && nextStyle != null) {
                /*
                other player changed weapons
                bots see what is worn and then switch, this is guessing what is going to be switched to
                2nd check is to make sure they aren't acting on the switch 2 ticks ago,
                e.g. player does whip, bow, whip and it changes to pray melee on the 3rd tick because it saw whip on 1st
                */
                if (curStyle != nextStyle && next.headIcon == nextStyle && cur.headIcon != nextStyle && prevStyle != next.headIcon) {
                    setStatus(BotIdentification.Status.HUMAN, "Same tick switch");
                    return;
                }

                if (curStyle != prevStyle) { // other player changed weapons
                    if (next.headIcon != curStyle && next2.headIcon == curStyle) {
                        exact2t++;
                    } else if (next.headIcon == curStyle) {
                        exact1T++;
                    }
                    total++;
                }
            }
        }
        double proportion = (double) (exact1T + exact2t) / total;
        if (total > 3) {
            log.debug("{} 1t:{} 2t:{} total:{} {}%", name, exact1T, exact2t, total, String.format("%.2f", 100 * proportion));
        }
        if (total > 6) {
            if (proportion >= 0.85) {
                setStatus(BotIdentification.Status.BOT, "85%+ 2t switches"); // bots always do 1-2t prayer changes
            } else if (proportion < 0.5) {
                setStatus(BotIdentification.Status.HUMAN, "50%- 2t switches"); // bots always do 1-2t prayer changes
            }
        }
    }

    private HeadIcon getStyleFor(int weaponItemId) {
        switch (weaponItemId) {
            case 25517: // Volatile nightmare staff
            case 23626: // Kodai wand
            case 23617: // Zuriel's staff
            case 23653: // Ahrim's staff
            case 23613: // Staff of the dead
                return HeadIcon.MAGIC;
            case 20408: // Dark bow
            case 23630: // Heavy ballista
            case 23619: // Morrigan's javelin
            case 23611: // Armadyl crossbow
            case 23601: // Rune crossbow
                return HeadIcon.RANGED;
            case 23620: // Statius's warhammer
            case 23615: // Vesta's longsword
            case 20407: // Dragon dagger
            case 20405: // Abyssal whip
            default:
                return HeadIcon.MELEE;
            case 25516: // Dharok's greataxe
            case 23628: // Ghrazi rapier
                return null; // null style is for when the bot can't detect the weapon correctly
        }
    }
}
