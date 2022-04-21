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

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("starinfoplugin")
public interface StarInfoConfig extends Config
{

	public static final String TEXT_COLOR_KEY = "textColor";
	public static final String INFO_BOX_KEY = "infoBox";
	public static final String HINT_ARROW_KEY = "hintArrow";

    @Alpha
    @ConfigItem(
	    position = 1,
	    keyName = TEXT_COLOR_KEY,
	    name = "Text color",
	    description = "Sets the color of the text above a star."
    )
    default Color getTextColor() {return Color.GREEN;}


	@ConfigItem(
		position = 2,
		keyName = INFO_BOX_KEY,
		name = "Show Info Box",
		description = "Whether to display star status info box"
	)
	default boolean showInfoBox() {return true;}

	@ConfigItem(
		position = 3,
		keyName = HINT_ARROW_KEY,
		name = "Show Hint Arrow",
		description = "Whether to display hint arrow pointing to star"
	)
	default boolean showHintArrow() {return true;}
}
