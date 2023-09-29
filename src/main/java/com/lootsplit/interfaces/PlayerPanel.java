package com.lootsplit.interfaces;

import com.lootsplit.LootPanel;
import com.lootsplit.PlayerInfo;
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class PlayerPanel extends JPanel
{
	LootPanel lootPanel;

	public PlayerPanel(LootPanel lootPanel) {
		super();
		this.lootPanel = lootPanel;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new LineBorder(Color.BLACK, 2));
	}

	public void add(PlayerInfo playerInfo)
	{
		PlayerEntry playerEntry = new PlayerEntry(this, playerInfo);
		add(playerEntry);
		revalidate();
		repaint();
	}
}
