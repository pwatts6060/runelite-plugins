package com.xpnotify;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class XpNotifyTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(XpNotifyPlugin.class);
        RuneLite.main(args);
    }
}