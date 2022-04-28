/*
 * Copyright (c) 2022, Cute Rock
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.starinfo;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("starinfoplugin")
public interface StarInfoConfig extends Config
{

	String TEXT_COLOR_KEY = "textColor";
	String THICK_OUTLINE = "thickOutline";
	String INFO_BOX_KEY = "infoBox";
	String HINT_ARROW_KEY = "hintArrow";
	String SHOW_PERCENT = "showPercent";
	String SHOW_DUST = "showDust";
	String SHOW_MINERS = "showMiners";
	String COLOR_STAR = "colorStar";
	String CLIPBOARD = "copyToClipboard";
	String ADD_TO_CHAT = "addToChat";
	String REMOVE_DISTANCE = "removeDistance";
	String ESTIMATE_TIME = "estimateTime";

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = TEXT_COLOR_KEY,
		name = "Text color",
		description = "Sets the color of the text above a star."
	)
	default Color getTextColor()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		position = 2,
		keyName = THICK_OUTLINE,
		name = "Thick Text Outline",
		description = "Use thick text outline on star info overlay"
	)
	default boolean thickOutline()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = INFO_BOX_KEY,
		name = "Show Info Box",
		description = "Whether to display star status info box"
	)
	default boolean showInfoBox()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = HINT_ARROW_KEY,
		name = "Show Hint Arrow",
		description = "Whether to display hint arrow pointing to star"
	)
	default boolean showHintArrow()
	{
		return true;
	}

	@ConfigItem(
		position = 5,
		keyName = SHOW_MINERS,
		name = "Show Miners",
		description = "Display number of active star miners, e.g. 5M = 5 Miners"
	)
	default boolean showMiners()
	{
		return true;
	}

	@ConfigItem(
		position = 6,
		keyName = SHOW_PERCENT,
		name = "Show Layer %",
		description = "Display the health percentage of the current layer"
	)
	default boolean showPercent()
	{
		return true;
	}

	@ConfigItem(
		position = 7,
		keyName = SHOW_DUST,
		name = "Show Dust Left",
		description = "Display stardust left in the current layer/star"
	)
	default DustConfig showDust()
	{
		return DustConfig.NO_DISPLAY;
	}

	@ConfigItem(
		position = 8,
		keyName = COLOR_STAR,
		name = "Highlight Star",
		description = "Highlights stars green if you can mine it, and red if you can't"
	)
	default boolean colorStar()
	{
		return true;
	}

	@ConfigItem(
		position = 9,
		keyName = CLIPBOARD,
		name = "Copy to Clipboard Option",
		description = "Allows you to right click stars and copy their information"
	)
	default boolean copyToClipboard()
	{
		return true;
	}

	@ConfigItem(
		position = 10,
		keyName = ADD_TO_CHAT,
		name = "Add Stars to Chat",
		description = "Adds a message to the game chat when stars are found with their tier/world/location"
	)
	default boolean addToChat()
	{
		return true;
	}

	@Range(
		min = 32,
		max = 90
	)
	@ConfigItem(
		position = 11,
		keyName = REMOVE_DISTANCE,
		name = "Remove distance",
		description = "The tile distance above which star info is removed"
	)
	default int removeDistance()
	{
		return 32;
	}

	@ConfigItem(
		position = 11,
		keyName = ESTIMATE_TIME,
		name = "Estimate Layer Time",
		description = "Display estimated time till the current layer finishes"
	)
	default EstimateConfig estimateTime()
	{
		return EstimateConfig.SECONDS;
	}
}
