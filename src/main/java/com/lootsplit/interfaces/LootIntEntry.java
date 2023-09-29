package com.lootsplit.interfaces;

import com.lootsplit.LootEntry;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class LootIntEntry extends JPanel
{
	protected final LootEntry lootEntry;
	private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");

	public LootIntEntry(final LootEntry lootEntry) {
		super();
		this.lootEntry = lootEntry;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		setLayout(new GridBagLayout());

		JTextArea textArea = new JTextArea(lootEntry.getName() + " " + lootEntry.getValue());
		textArea.setEditable(false);
		textArea.setRows(2);
		textArea.setForeground(lootEntry.inSplit ? Color.ORANGE : Color.GRAY);
		gbc.gridx = 0; // Column 0
		gbc.gridy = 0; // Row 0
		add(textArea, gbc);

		JButton inSplitButton = new JButton("Toggle split");
		gbc.gridx = 1; // Column 1
		gbc.gridy = 0; // Row 0
		add(inSplitButton, gbc);
		inSplitButton.addActionListener(e -> {
			lootEntry.inSplit = !lootEntry.inSplit;
			textArea.setForeground(lootEntry.inSplit ? Color.ORANGE : Color.GRAY);
		});

		JButton timeButton = new JButton();
		timeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		timeButton.setText(Instant.ofEpochMilli(lootEntry.getTimems()).atZone(ZoneId.systemDefault()).format(format));

		timeButton.addActionListener(e -> {
			String prompt = "Enter new time";
			String curTime = Instant.ofEpochMilli(lootEntry.getTimems()).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			String input = JOptionPane.showInputDialog(prompt, curTime);
			if (input == null) {
				return;
			}
			try {
				lootEntry.timems = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(input, LocalDateTime::from).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
				timeButton.setText(Instant.ofEpochMilli(lootEntry.getTimems()).atZone(ZoneId.systemDefault()).format(format));
			} catch (DateTimeException exception) {
				JOptionPane.showMessageDialog(null, "Could not parse date");
			}
		});
		gbc.gridx = 1; // Column 1
		gbc.gridy = 1; // Row 1
		add(timeButton, gbc);
	}
}
