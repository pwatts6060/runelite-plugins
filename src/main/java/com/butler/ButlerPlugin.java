package com.butler;

import com.google.inject.Provides;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

@Slf4j
@PluginDescriptor(
	name = "Butler"
)
public class ButlerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ButlerConfig config;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private ItemManager itemManager;

	private static TickTimer tripTimerInfoBox;
	private static int ticksSinceFetchDialog = 2;
	public static boolean buildingMode = false;
	private static final int BUILDING_MODE_VARP = 780;
	private static final int BUILDING_MODE_VARBIT = 2176;
	private Widget[] dialogueOptions;

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Butler stopped!");
		infoBoxManager.removeInfoBox(tripTimerInfoBox);
		tripTimerInfoBox = null;
	}

	public void startTripTimer(Servant servant)
	{
		infoBoxManager.removeInfoBox(tripTimerInfoBox);
		tripTimerInfoBox = new TickTimer(null, this, servant.ticks + 1);
		tripTimerInfoBox.setImage(itemManager.getImage(servant.itemId));
		tripTimerInfoBox.setPriority(InfoBoxPriority.MED);
		tripTimerInfoBox.setTooltip("Servant returns in");
		infoBoxManager.addInfoBox(tripTimerInfoBox);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		checkFetchDialogue();
		updateTripTimer();
	}

	private void updateTripTimer()
	{
		if (tripTimerInfoBox != null)
		{
			tripTimerInfoBox.tick();
		}
	}

	private void checkFetchDialogue()
	{
		Widget playerDialogueOptionsWidget = client.getWidget(WidgetID.DIALOG_OPTION_GROUP_ID, 1);
		if (playerDialogueOptionsWidget != null && playerDialogueOptionsWidget.getChildren() != dialogueOptions)
		{
			dialogueOptions = playerDialogueOptionsWidget.getChildren();
			if (dialogueOptions[0].getText().equals("Repeat last task?"))
			{
				ticksSinceFetchDialog = 0;
				return;
			}
		} else if (playerDialogueOptionsWidget == null) {
			++ticksSinceFetchDialog;
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getIndex() == BUILDING_MODE_VARP)
		{
			buildingMode = client.getVarbitValue(BUILDING_MODE_VARBIT) == 1;
			if (!buildingMode)
			{
				removeTripTimer();
			}
		}
	}

	private void removeTripTimer()
	{
		infoBoxManager.removeInfoBox(tripTimerInfoBox);
		tripTimerInfoBox = null;
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (ticksSinceFetchDialog > 1 || !buildingMode)
		{
			return;
		}
		Optional<Servant> servantOp = Servant.forNpcId(event.getNpc().getId());
		servantOp.ifPresent(servant -> {
				startTripTimer(servant);
			}
		);
	}


	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (!buildingMode)
		{
			return;
		}
		Optional<Servant> servantOp = Servant.forNpcId(event.getNpc().getId());
		servantOp.ifPresent(servant -> {
				removeTripTimer();
			}
		);
	}


	@Provides
	ButlerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ButlerConfig.class);
	}
}
