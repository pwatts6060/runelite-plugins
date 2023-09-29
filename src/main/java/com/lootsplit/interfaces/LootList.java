package com.lootsplit.interfaces;

import com.lootsplit.LootEntry;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class LootList extends JPanel
{
	protected List<LootIntEntry> entries = new ArrayList<>();
	final GridBagConstraints c = new GridBagConstraints();

	public LootList() {
		super();
		setLayout(new GridBagLayout());
		setBorder(new LineBorder(Color.BLACK, 2));
		c.gridy = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
	}

	public void add(LootEntry lootEntry) {
		LootIntEntry lootIntEntry = new LootIntEntry(lootEntry);
		entries.add(lootIntEntry);
		add(lootIntEntry, c);
	}
}
