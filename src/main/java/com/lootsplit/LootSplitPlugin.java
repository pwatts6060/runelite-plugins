package com.lootsplit;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(
	name = "PK Loot Splitter"
)
public class LootSplitPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private LootConfig config;

	private LootPanel panel;

	private NavigationButton navButton;

	public final List<LootEntry> loot = new ArrayList<>();
	final List<PlayerInfo> playerInfos = new ArrayList<>();
	public static final String USERNAME = "([\\w\\s- ]{1,12})";
	public static final String COMMA_NUMBER = "([\\d,]+)";
	public static final Pattern lootKeyPattern = Pattern.compile("^" + USERNAME + " has opened a loot key worth " + COMMA_NUMBER + " coins!$");

	@Override
	protected void startUp() throws Exception
	{
		panel = new LootPanel(this, client, clientThread);
		navButton = NavigationButton.builder()
			.tooltip("PK Loot Split")
			.icon(ImageUtil.loadImageResource(getClass(), "/lootsplit_icon.png"))
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navButton);
		panel.clear();
//
//		PlayerInfo playerInfo = new PlayerInfo("TestName");
//		playerInfos.add(playerInfo);
//		panel.playerPanel.add(playerInfo);
//
//		LootEntry lootEntry = new LootEntry(321, playerInfo, System.currentTimeMillis());
//		loot.add(lootEntry);
//		panel.add(lootEntry);
//
//		LootEntry lootEntry2 = new LootEntry(123, playerInfo, System.currentTimeMillis());
//		loot.add(lootEntry2);
//		panel.add(lootEntry2);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (client.getClanChannel() == null) {
			return;
		}

		if (!event.getType().equals(ChatMessageType.CLAN_MESSAGE)) {
			return;
		}

		if (event.getSender() != null && !event.getSender().equals(client.getClanChannel().getName()))
		{
			return;
		}

		Matcher m = lootKeyPattern.matcher(event.getMessage());
		if (!m.matches()) {
			return;
		}

		String username = Util.formatName(m.group(1));
		String valueS = m.group(2);

		valueS = valueS.replaceAll(",", "");
		int coins;
		try {
			coins = Integer.parseInt(valueS);
		} catch (NumberFormatException e) {
			return;
		}

		PlayerInfo playerInfo = null;
		for (PlayerInfo pi : playerInfos) {
			if (pi.name.equalsIgnoreCase(username)) {
				playerInfo = pi;
				break;
			}
		}
		if (playerInfo == null) {
			return;
		}

		LootEntry lootEntry = new LootEntry(coins / 1_000, playerInfo, System.currentTimeMillis());
		lootEntry.inSplit = lootEntry.value >= config.minimumLoot();
		loot.add(lootEntry);
		panel.add(lootEntry);
	}

	@Provides
	LootConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LootConfig.class);
	}

	public void clear()
	{

	}
}
