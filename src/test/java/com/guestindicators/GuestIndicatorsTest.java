package com.guestindicators;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GuestIndicatorsTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GuestIndicatorsPlugin.class);
		RuneLite.main(args);
	}
}