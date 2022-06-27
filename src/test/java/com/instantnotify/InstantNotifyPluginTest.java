package com.instantnotify;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class InstantNotifyPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(InstantNotifyPlugin.class);
		RuneLite.main(args);
	}
}