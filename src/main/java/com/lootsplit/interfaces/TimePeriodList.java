package com.lootsplit.interfaces;

import com.lootsplit.PlayerInfo;
import com.lootsplit.TimePeriod;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TimePeriodList extends JPanel
{
	private PlayerEntry playerEntry;
	private final PlayerInfo playerInfo;
	private final ArrayList<JButton> buttons;
	private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter fullFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	GridBagConstraints startButtonC = new GridBagConstraints();
	GridBagConstraints endButtonC = new GridBagConstraints();

	public TimePeriodList(PlayerEntry playerEntry, PlayerInfo playerInfo)
	{
		super();
		this.playerEntry = playerEntry;
		this.playerInfo = playerInfo;
		buttons = new ArrayList<>();
		setLayout(new GridBagLayout());
		setAlignmentX(Component.CENTER_ALIGNMENT);


		startButtonC.gridx = 0;
		startButtonC.gridy = GridBagConstraints.RELATIVE;
		startButtonC.weightx = 0.5;
		startButtonC.fill = GridBagConstraints.BOTH;

		endButtonC.gridx = 1;
		endButtonC.gridy = GridBagConstraints.RELATIVE;
		endButtonC.weightx = 0.5;
		endButtonC.fill = GridBagConstraints.BOTH;

		for (TimePeriod timePeriod : playerInfo.timePeriods) {
			addButton(timePeriod, true);
			addButton(timePeriod, false);
		}
	}

	public JButton addButton(TimePeriod timePeriod, boolean startElseEnd) {
		JButton button = new JButton();
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setPreferredSize(new Dimension(200, 20));
		if (startElseEnd) {
			timePeriod.startButton = button;
		} else {
			timePeriod.endButton = button;
			button.setVisible(timePeriod.getEndms() >= 0);
		}
		button.setText(getButtonText(timePeriod, startElseEnd));

		button.addActionListener(e -> {
			TimePeriod last = playerInfo.timePeriods.get(playerInfo.timePeriods.size() - 1);
			boolean isLastTimeButton = last == timePeriod && (timePeriod.getEndms() < 0L && startElseEnd || !startElseEnd && timePeriod.getEndms() >= 0);
			String prompt = "Enter new " + (startElseEnd ? "start" : "end") + " time";
			if (isLastTimeButton) {
				prompt += " or enter d to delete";
			}
			String curTime = Instant.ofEpochMilli(startElseEnd ? timePeriod.getStartms() : timePeriod.getEndms()).atZone(ZoneId.systemDefault()).format(fullFormat);
			String input = JOptionPane.showInputDialog(prompt, curTime);
			if (input == null) {
				return;
			}
			if (isLastTimeButton && input.equalsIgnoreCase("d")) {
				if (startElseEnd) {
					remove(timePeriod.startButton);
					remove(timePeriod.endButton);
					revalidate();
					repaint();
					playerEntry.leaveOrJoinButton.setText("Rejoin");
					playerEntry.username.setForeground(Color.GRAY);
					this.playerInfo.timePeriods.remove(this.playerInfo.timePeriods.size() - 1);
				} else {
					playerEntry.leaveOrJoinButton.setText("Left");
					playerEntry.username.setForeground(Color.ORANGE);
					timePeriod.setEndms(-1L);
				}
				return;
			}
			try {
				long newTime = fullFormat.parse(input, LocalDateTime::from).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
				if (startElseEnd) {
					timePeriod.setStartms(newTime);
				} else {
					timePeriod.setEndms(newTime);
				}
			} catch (DateTimeException exception) {
				JOptionPane.showMessageDialog(null, "Could not parse date");
			}
		});

		buttons.add(button);
		if (startElseEnd) {
			add(button, startButtonC);
		} else {
			add(button, endButtonC);
		}
		button.revalidate();
		button.repaint();
		return button;
	}

	public static String getButtonText(TimePeriod timePeriod, boolean startElseEnd) {
		if (startElseEnd) {
			return "S:" + Instant.ofEpochMilli(timePeriod.getStartms()).atZone(ZoneId.systemDefault()).format(format);
		} else {
			if (timePeriod.getEndms() < 0) {
				return "";
			}
			return "E:" + Instant.ofEpochMilli(timePeriod.getEndms()).atZone(ZoneId.systemDefault()).format(format);
		}
	}
}
