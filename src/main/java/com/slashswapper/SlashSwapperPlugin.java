package com.slashswapper;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.clan.ClanID;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
@Slf4j
@PluginDescriptor(
		name = "Slash Swapper"
)
public class SlashSwapperPlugin extends Plugin
{
	@Inject
	private Client client;

	private static final int GROUP_CHANNEL = 6;
	// if a message starts with any of these strings, do nothing. the player is intentionally sending a message to a channel.
	private static final String[] excludedStrings = {"/@p ","/@c ","/@gc "};

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		// if the current event is not a chat being sent, (4394)?,  the player is not chatting in a clan (13805), or the player not part of a clan, do nothing.
		if (event.getScriptId() != ScriptID.CHAT_SEND || client.getVarbitValue(4394) == 1 || client.getVarbitValue(13805) == 1 || client.getClanChannel(ClanID.CLAN) == null) {
			return;
		}

		final String[] stringStack = client.getStringStack();
		final int[] intStack = client.getIntStack();
		int stringStackCount = client.getStringStackSize();
		int intStackCount = client.getIntStackSize();

		int targetIndex = intStackCount - 4;
		// the chat channel the game wants to send the message to by default, 0: public, 2: friends channel, 3: clan channel OR group channel (because jagex), 4: guest clan channel.
		int target = intStack[targetIndex];

		// set to determine if messages are sent to group channel, 0: send to clan, 1: send to group, -1: ignore.
		int groupChatIndex = intStackCount - 3;

		// selected channel button, 0: all, 1: game, 2: public, 3: private, 4: friends channel, 5: clan channel, 6: trade OR group channel.
		int channelSelected = client.getVarcIntValue(41);

		String message = stringStack[stringStackCount - 1];
		String messagePrefix = stringStack[1];

		if (message.isEmpty()) {
			return;
		}

		// if the chat begins with a special character sequence, do nothing.
		if (StringUtils.equalsAny(messagePrefix, excludedStrings)) {
			return;
		}

		// if the channel selected is group channel / trade channel, do nothing (jagex's code is already really broken for this case).
		if (channelSelected == GROUP_CHANNEL) {
			return;
		}

		// if someone uses "/f " or "/@f " (again, jagex spaghetti).
		if (("/" + stringStack[1]).equals(stringStack[0])) {
			return;
		}

		// these two checks make "/" go to clan channel and "//" go to friends channel.
		// default behavior: send to friends channel, slashswapper behavior: send to clan channel, instead.
		if ((target == 0 || target == 2) && message.startsWith("/")) {
			// set the target to clan channel / group channel.
			intStack[targetIndex] = 3;
			// set the groupChatIndicator to clan.
			intStack[groupChatIndex] = 0;
			// trim forward slash from message.
			stringStack[stringStackCount - 1] = message.substring(1);
		}

		// default behavior: send to clan channel, slashswapper behavior: send to friends channel, instead.
		else if (target == 3 && messagePrefix.equals("//@")) {
			// if player is not in a friends channel, do nothing.
			if (client.getFriendsChatManager() == null) {
				return;
			}
			// set the target to friends channel.
			intStack[targetIndex] = 2;
			// set the groupChatIndicator to be ignored.
			intStack[groupChatIndex] = -1;
			// append "/" to the beginning of the message (jagex expects this prefix for friends channel messages).
			stringStack[stringStackCount - 1] = "/" + message;
		}
	}
}
