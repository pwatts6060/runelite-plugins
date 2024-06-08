package com.ent;

public enum TrimType
{
	TOP(4, "Short on top!", "Top"),
	MULLET(3, "A leafy mullet!", "Top/Sides"),
	BACK_AND_SIDES(2, "Short back and sides!", "Back/Sides"),
	BACK(1, "Breezy at the back!", "Back"),
	;

	final int priority;
	final String overheadText;
	final String shortText;


	TrimType(int priority, String overheadText, String shortText)
	{
		this.priority = priority;
		this.overheadText = overheadText;
		this.shortText = shortText;
	}

	public static final TrimType[] values = TrimType.values();
}
