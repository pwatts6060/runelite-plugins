package com.lootsplit.interfaces;

import com.lootsplit.LootEntry;
import com.lootsplit.PlayerInfo;
import com.lootsplit.TimePeriod;
import java.awt.Color;
import java.awt.FlowLayout;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PlayerEntry extends JPanel
{
	public JLabel username;
	public JButton leaveOrJoinButton;
	private final PlayerInfo playerInfo;
	private final TimePeriodList timePeriodList;
	private PlayerPanel playerPanel;

	public PlayerEntry(PlayerPanel playerPanel, final PlayerInfo playerInfo) {
		super();
		this.playerPanel = playerPanel;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.playerInfo = playerInfo;

		username = new JLabel(playerInfo.name);
		username.setForeground(Color.ORANGE);
		add(username);

		leaveOrJoinButton = new JButton("Left");
		timePeriodList = new TimePeriodList(this, playerInfo);

		JButton addLootButton = new JButton("Add Loot");
		addLootButton.addActionListener(e -> {
			String input = JOptionPane.showInputDialog("Enter Loot value (k)");
			if (input == null) {
				return;
			}

			int value;
			try {
				value = Integer.parseInt(input);
			} catch (NumberFormatException ignored) {
				JOptionPane.showMessageDialog(null, "Could not parse number");
				return;
			}

			String curTime = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			String input2 = JOptionPane.showInputDialog("Enter time of loot", curTime);
			if (input2 == null) {
				return;
			}

			try {
				long time = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(input2, LocalDateTime::from).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
				LootEntry entry = new LootEntry(value, playerInfo, time);
				playerPanel.lootPanel.plugin.loot.add(entry);
				playerPanel.lootPanel.add(entry);
			} catch (DateTimeException exception) {
				JOptionPane.showMessageDialog(null, "Could not parse date");
			}
		});
		add(addLootButton);

		leaveOrJoinButton.addActionListener(e -> {
			if (playerInfo.timePeriods.isEmpty() || this.playerInfo.timePeriods.get(playerInfo.timePeriods.size() - 1).getEndms() >= 0)
			{
				//rejoin
				leaveOrJoinButton.setText("Left");
				username.setForeground(Color.ORANGE);
				TimePeriod timePeriod = new TimePeriod(System.currentTimeMillis());
				playerInfo.timePeriods.add(timePeriod);
				timePeriodList.addButton(timePeriod, true);
				timePeriodList.addButton(timePeriod, false);
				timePeriodList.revalidate();
				timePeriodList.repaint();
				revalidate();
				repaint();
			}
			else
			{
				//left
				leaveOrJoinButton.setText("Rejoin");
				username.setForeground(Color.GRAY);
				TimePeriod timePeriod = playerInfo.timePeriods.get(playerInfo.timePeriods.size() - 1);
				timePeriod.setEndms(System.currentTimeMillis());
			}
		});
		add(leaveOrJoinButton);
		add(timePeriodList);
	}
}
