package com.slashswapper;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SlashSwapperTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(com.slashswapper.SlashSwapperPlugin.class);
		RuneLite.main(args);
	}
}