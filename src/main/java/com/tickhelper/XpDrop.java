package com.tickhelper;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.Skill;

@Data
@AllArgsConstructor
public class XpDrop {
	Skill skill;
	int experience;
	boolean fake;
}
