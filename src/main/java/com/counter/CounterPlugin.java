package com.counter;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import net.runelite.client.input.KeyManager;
import net.runelite.client.util.HotkeyListener;

@PluginDescriptor(
        name = "Counter",
        description = "Shows a visual counter that you can manually increment, decrement, reset, and/or automatically rollover",
        tags = {"overlay", "count", "manual", "counter"}
)
public class CounterPlugin extends Plugin
{
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ConfigManager configManager;

    @Inject
    private CounterTileOverlay tileOverlay;

    @Inject
    private CounterNumberOverlay numberOverlay;

    @Inject
    private FullResizeableCounterOverlay overlay;

    @Inject
    private CounterConfig config;

    @Inject
    private KeyManager keyManager;

    protected int currentColorIndex = 0;
    protected int counter = 0;
    protected Color currentColor = Color.WHITE;

    protected Dimension DEFAULT_SIZE = new Dimension(25, 25);

    @Provides
	CounterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(CounterConfig.class);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!event.getGroup().equals("counter"))
        {
            return;
        }

        if (currentColorIndex > config.colorCycle())
        {
            currentColorIndex = 0;
        }

        if (counter > config.resetNumber())
        {
            counter = 0;
        }

        DEFAULT_SIZE = new Dimension(config.boxWidth(), config.boxWidth());
    }

	private final HotkeyListener incrHotkeyListener = new HotkeyListener(() -> config.incrementCounterHotKey())
	{
		@Override
		public void hotkeyPressed()
		{
			if (counter % config.resetNumber() == 0)
			{
				counter = 0;
				if (currentColorIndex == config.colorCycle())
				{
					currentColorIndex = 0;
				}
				switch (++currentColorIndex)
				{
					case 1:
						currentColor = config.getTickColor();
						break;
					case 2:
						currentColor = config.getTockColor();
						break;
					case 3:
						currentColor = config.getTick3Color();
						break;
					case 4:
						currentColor = config.getTick4Color();
						break;
					case 5:
						currentColor = config.getTick5Color();
						break;
					case 6:
						currentColor = config.getTick6Color();
						break;
					case 7:
						currentColor = config.getTick7Color();
						break;
					case 8:
						currentColor = config.getTick8Color();
						break;
					case 9:
						currentColor = config.getTick9Color();
						break;
					case 10:
						currentColor = config.getTick10Color();
				}
			}
			counter++;
		}
	};

	private final HotkeyListener resetHotkeyListener = new HotkeyListener(() -> config.tickResetHotkey())
	{
		@Override
		public void hotkeyPressed()
		{
			counter = 0;
			currentColorIndex = 0;
		}
	};

    @Override
    protected void startUp() throws Exception
    {
        DEFAULT_SIZE = new Dimension(config.boxWidth(), config.boxWidth());
        overlay.setPreferredSize(DEFAULT_SIZE);
        overlayManager.add(overlay);
        overlayManager.add(tileOverlay);
        overlayManager.add(numberOverlay);
        keyManager.registerKeyListener(resetHotkeyListener);
		keyManager.registerKeyListener(incrHotkeyListener);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        overlayManager.remove(tileOverlay);
        overlayManager.remove(numberOverlay);
        counter = 0;
        currentColorIndex = 0;
        currentColor = config.getTickColor();
        keyManager.unregisterKeyListener(resetHotkeyListener);
		keyManager.unregisterKeyListener(incrHotkeyListener);
    }
}
