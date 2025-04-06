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
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;
import net.runelite.client.config.Range;

@ConfigGroup("starinfoplugin")
public interface StarInfoConfig extends Config
{

	String TEXT_COLOR_KEY = "textColor";
	String THICK_OUTLINE = "thickOutline";
	String INFO_BOX_KEY = "infoBox";
	String BONUS_INFO_BOX_KEY = "showStarDiscovery";
	String HINT_ARROW_KEY = "hintArrow";
	String SHOW_PERCENT = "showPercent";
	String SHOW_DUST = "showDust";
	String SHOW_MINERS = "showMiners";
	String COLOR_STAR = "colorStar";
	String CLIPBOARD = "copyToClipboard";
	String T0_IN_COPY = "t0inCopy";
	String ADD_TO_CHAT = "addToChat";
	String NOTIFY_TIER_CHANGE = "notifyTierChange";
	String REMOVE_DISTANCE = "removeDistance";
	String ESTIMATE_LAYER = "estimateLayerTime";
	String ESTIMATE_DEPLETION_TIME = "estimateFullTime";

	@ConfigSection(
		name = "Text Overlay",
		description = "Settings for the text overlaid on stars",
		position = 0
	)
	String textOverlaySection = "textOverlaySection";

	@Alpha
	@ConfigItem(
		position = 1,
		keyName = TEXT_COLOR_KEY,
		name = "Text color",
		section = textOverlaySection,
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
		section = textOverlaySection,
		description = "Use thick text outline on star info overlay"
	)
	default boolean thickOutline()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = SHOW_MINERS,
		name = "Show Miners",
		section = textOverlaySection,
		description = "Display number of active star miners, e.g. 5M = 5 Miners"
	)
	default boolean showMiners()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = SHOW_PERCENT,
		name = "Show Layer %",
		section = textOverlaySection,
		description = "Display the health percentage of the current layer"
	)
	default boolean showPercent()
	{
		return true;
	}

	@ConfigItem(
		position = 6,
		keyName = ESTIMATE_LAYER,
		name = "Estimate Layer Time",
		section = textOverlaySection,
		description = "Display estimated time till the current layer finishes"
	)
	default EstimateConfig estimateLayerTime()
	{
		return EstimateConfig.NONE;
	}

	@ConfigItem(
		position = 8,
		keyName = ESTIMATE_DEPLETION_TIME,
		name = "Estimate Depletion Time",
		section = textOverlaySection,
		description = "Display estimated time till the star depletes"
	)
	default EstimateConfig estimateDeathTime()
	{
		return EstimateConfig.NONE;
	}

	@ConfigItem(
		position = 9,
		keyName = "xpPerHour",
		name = "Layer Xp/Hr",
		section = textOverlaySection,
		description = "Display Xp/Hr for current layer"
	)
	default boolean xpPerHour()
	{
		return false;
	}

	@ConfigItem(
		position = 10,
		keyName = "dustPerHour",
		name = "Dust/Hr",
		section = textOverlaySection,
		description = "Display how much Dust per Hour you mine for current layer"
	)
	default boolean dustPerHour()
	{
		return false;
	}

	@ConfigItem(
		position = 12,
		keyName = "compactText",
		name = "Compact text",
		section = textOverlaySection,
		description = "Use abbreviations and less text"
	)
	default boolean compact()
	{
		return false;
	}

	@ConfigItem(
		position = 13,
		keyName = INFO_BOX_KEY,
		name = "Show Info Box",
		description = "Whether to display star status info box"
	)
	default boolean showInfoBox()
	{
		return true;
	}

	@ConfigItem(
		position = 14,
		keyName = BONUS_INFO_BOX_KEY,
		name = "Show Star Discovery",
		description = "Whether to display star discovery boost info box"
	)
	default boolean showStarDiscovery()
	{
		return true;
	}

	@ConfigItem(
		position = 15,
		keyName = HINT_ARROW_KEY,
		name = "Show Hint Arrow",
		description = "Whether to display hint arrow pointing to star"
	)
	default boolean showHintArrow()
	{
		return true;
	}

	@ConfigItem(
		position = 16,
		keyName = COLOR_STAR,
		name = "Highlight Star",
		description = "Highlights stars green if you can mine it, and red if you can't"
	)
	default boolean colorStar()
	{
		return true;
	}

	@ConfigItem(
		position = 17,
		keyName = CLIPBOARD,
		name = "Copy to Clipboard Option",
		description = "Allows you to right click stars and copy their information"
	)
	default boolean copyToClipboard()
	{
		return true;
	}

	@ConfigItem(
		position = 18,
		keyName = T0_IN_COPY,
		name = "T0 Clipboard estimate",
		description = "Adds time estimate till star is dead to copy option message"
	)
	default boolean addT0Estimate()
	{
		return false;
	}

	@ConfigItem(
		position = 19,
		keyName = ADD_TO_CHAT,
		name = "Add Stars to Chat",
		description = "Adds a message to the game chat when stars are found with their tier/world/location"
	)
	default boolean addToChat()
	{
		return true;
	}

	@ConfigItem(
		position = 20,
		keyName = NOTIFY_TIER_CHANGE,
		name = "Notify Tier Change",
		description = "Notifies you when a star changes tier"
	)
	default Notification notifyTierChange()
	{
		return Notification.ON;
	}

	@ConfigSection(
		name = "Health Bar",
		description = "Settings for the health bar on stars",
		position = 21,
		closedByDefault = true
	)
	String healthBarSection = "healthBarSection";

	@ConfigItem(
		position = 22,
		keyName = "hideHealthBar",
		name = "Hide health bar",
		section = healthBarSection,
		description = "Hides the health bar of the star"
	)
	default boolean hideHealthBar()
	{
		return false;
	}

	@Range(
		min = 1,
		max = 40
	)
	@ConfigItem(
		position = 23,
		keyName = "healthBarHeight",
		name = "Health bar height",
		section = healthBarSection,
		description = "The height of the health bar (5 default)"
	)
	default int healthBarHeight()
	{
		return 5;
	}

	@Range(
		min = 10,
		max = 400
	)
	@ConfigItem(
		position = 24,
		keyName = "healthBarWidth",
		name = "Health bar width",
		section = healthBarSection,
		description = "The width of the health bar (50 default)"
	)
	default int healthBarWidth()
	{
		return 50;
	}

	@Alpha
	@ConfigItem(
		position = 25,
		keyName = "hpColorFore",
		name = "Hp left color",
		section = healthBarSection,
		description = "Sets the hp bar's hp left color"
	)
	default Color getHpColorFore()
	{
		return new Color(0,169,255, 180);
	}

	@Alpha
	@ConfigItem(
		position = 26,
		keyName = "hpColorBack",
		name = "Hp background color",
		section = healthBarSection,
		description = "Sets the hp bar's background color"
	)
	default Color getHpColorBack()
	{
		return new Color(0,22,33, 180);
	}

	@Range(
		min = 32,
		max = 90
	)
	@ConfigItem(
		position = 27,
		keyName = REMOVE_DISTANCE,
		name = "Remove distance",
		description = "The tile distance above which star info is removed"
	)
	default int removeDistance()
	{
		return 32;
	}
}
