package com.lootsplit;

public class Util
{
	public static String formatName(String name) {
		return name.replace("\u00a0"," ").replace(" ", " ");
	}
}
