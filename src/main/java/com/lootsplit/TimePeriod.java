package com.lootsplit;

import com.lootsplit.interfaces.TimePeriodList;
import javax.swing.JButton;
import lombok.Getter;

public class TimePeriod
{
	@Getter
	private long startms;

	@Getter
	private long endms;
	public JButton startButton;
	public JButton endButton;

	public TimePeriod(long startms) {
		this(startms, -1L);
	}

	public TimePeriod(long startms, long endms) {
		setStartms(startms);
		setEndms(endms);
	}

	public void setStartms(long startms) {
		this.startms = startms;
		if (startButton != null) {
			startButton.setText(TimePeriodList.getButtonText(this, true));
		}
	}

	public void setEndms(long endms) {
		this.endms = endms;
		if (endButton != null) {
			endButton.setText(TimePeriodList.getButtonText(this, false));
			endButton.setVisible(endms >= 0);
		}
	}
}
