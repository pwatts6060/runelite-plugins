package com.lootsplit;

import com.lootsplit.interfaces.LootList;
import com.lootsplit.interfaces.PlayerPanel;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.FriendsChatMember;
import net.runelite.api.GameState;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.ui.PluginPanel;

public class LootPanel extends PluginPanel
{
	public final LootSplitPlugin plugin;
	private final LootList lootList;
	private final JButton splitButton;
	private final Client client;
	private final ClientThread clientThread;
	public final PlayerPanel playerPanel;

	public LootPanel(LootSplitPlugin plugin, Client client, ClientThread clientThread)
	{
		this.client = client;
		this.plugin = plugin;
		this.clientThread = clientThread;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(2, 2, 2, 2));

		splitButton = new JButton("Split");
		splitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(splitButton);
		splitButton.addActionListener(e -> split());

		JButton clearButton = new JButton("Reset");
		clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(clearButton);
		clearButton.addActionListener(e -> {
			int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset?");
			if (option == JOptionPane.YES_OPTION) {
				clear();
			}
		});

		JLabel label = new JLabel("Enter new player: ");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(label);

		JTextField addPlayerTF = new JTextField(12);
		Action action = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String text = addPlayerTF.getText();
				inputPlayer(text);
			}
		};
		addPlayerTF.addActionListener(action);
		add(addPlayerTF);

		playerPanel = new PlayerPanel(this);
		add(playerPanel);

		lootList = new LootList();
		add(lootList);
	}

	private void inputPlayer(String text)
	{
		if (client.getGameState() != GameState.LOGGED_IN) {
			return;
		}

		String name = findName(text);
		if (name == null)
		{
			clientThread.invokeLater(() -> client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
				text + " not found in clan/friends chat or already present.", ""));
			return;
		}

		PlayerInfo playerInfo = new PlayerInfo(name);
		plugin.playerInfos.add(playerInfo);
		playerPanel.add(playerInfo);
	}

	private String findName(String name)
	{
		if (plugin.playerInfos.stream().anyMatch(playerInfo -> playerInfo.name.equalsIgnoreCase(name)))
		{
			return null;
		}
		if (client.getClanChannel() != null)
		{
			for (ClanChannelMember member : client.getClanChannel().getMembers())
			{
				if (Util.formatName(member.getName()).equalsIgnoreCase(name))
				{
					return Util.formatName(member.getName());
				}
			}
		}
		if (client.getFriendsChatManager() != null)
		{
			for (FriendsChatMember member : client.getFriendsChatManager().getMembers())
			{
				if (Util.formatName(member.getName()).equalsIgnoreCase(name))
				{
					return Util.formatName(member.getName());
				}
			}
		}
		return null;
	}

	private void split()
	{
		Map<PlayerInfo, Double> playerToSplit = new HashMap<>();
		Map<PlayerInfo, Integer> playerToTotal = new HashMap<>();
		for (LootEntry lootEntry : plugin.loot) {
			if (!lootEntry.inSplit) {
				continue;
			}

			List<PlayerInfo> presentPlayers = new ArrayList<>();
			for (PlayerInfo playerInfo : plugin.playerInfos) {
				for (TimePeriod timePeriod : playerInfo.timePeriods) {
					if (lootEntry.timems < timePeriod.getStartms())
						continue;
					if (timePeriod.getEndms() >= 0 && lootEntry.timems > timePeriod.getEndms())
						continue;
					presentPlayers.add(playerInfo);
					break;
				}
			}

			double split = (double) lootEntry.getValue() / presentPlayers.size();
			playerToTotal.merge(lootEntry.getPlayerInfo(), lootEntry.getValue(), Integer::sum);
			for (PlayerInfo p : presentPlayers) {
				playerToSplit.merge(p, split, Double::sum);
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Totals\n");
		playerToTotal.forEach((p, val) -> sb.append(p.name).append(": ").append(val).append("\n"));
		sb.append("\nSplit\n");
		playerToSplit.forEach((p, val) -> sb.append(p.name).append(": ").append(val).append("\n"));

		Transferable transferableText = new StringSelection(sb.toString());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(transferableText, null);

		clientThread.invokeLater(() -> client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Split information added to clipboard.", ""));
	}

	public void clear()
	{
		for (Iterator<Component> it = Arrays.stream(lootList.getComponents()).iterator(); it.hasNext(); )
		{
			Component c = it.next();
			lootList.remove(c);
		}
		plugin.loot.clear();
		plugin.playerInfos.clear();
		playerPanel.removeAll();
		lootList.revalidate();
		lootList.repaint();
	}

	public void add(LootEntry lootEntry)
	{
		SwingUtilities.invokeLater(() -> {
			lootList.add(lootEntry);
			lootList.revalidate();
			lootList.repaint();
		});
	}
}
