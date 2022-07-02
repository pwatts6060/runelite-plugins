package com.instantnotify;

import net.runelite.api.SoundEffectID;
import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(InstantNotifyConfig.configGroup)
public interface InstantNotifyConfig extends Config {

    String configGroup = "InstantIdleNotify";
    String soundId = "soundId";
    String volume = "volume";

    @ConfigItem(
            keyName = "toggleNotifications",
            name = "Notification Type",
            description = "Runelite notification, Sound, or Both when idle"
    )
    default NotificationType notificationType() {
        return NotificationType.RUNELITE;
    }

    @ConfigItem(
            keyName = soundId,
            name = "Sound ID",
            description = "The sound ID to play"
    )
    default int soundId() {
        return SoundEffectID.TOWN_CRIER_BELL_DING;
    }

    @Range(
            max = SoundEffectVolume.HIGH
    )
    @ConfigItem(
            keyName = volume,
            name = "Volume",
            description = "The volume of the notify sound effect (used when your sound effect setting are muted)"
    )
    default int volume() {
        return SoundEffectVolume.MEDIUM_LOW;
    }
}
