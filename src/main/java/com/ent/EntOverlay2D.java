package com.ent;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class EntOverlay2D extends OverlayPanel
{
	private final Client client;
	private final EntPlugin plugin;
	private final EntConfig config;

	@Inject
	private EntOverlay2D(Client client, EntPlugin plugin, EntConfig config)
	{
		this.setPosition(OverlayPosition.BOTTOM_LEFT);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.entStats.ents.isEmpty()) {
			return null;
		}

		panelComponent.getChildren().add(TitleComponent.builder().text("Ent Trimmer").build());
		long perfectCuts = plugin.entStats.getPerfectCutCount();
		panelComponent.getChildren().add(
			LineComponent.builder().left("Perfectly Cut: ").right(perfectCuts + "/5").build()
		);
		panelComponent.getChildren().add(
			LineComponent.builder().left("Ticks seen: ").right(Integer.toString(plugin.entStats.ticksAlive)).build()
		);
		if (plugin.entStats.ttg) {
			panelComponent.getChildren().add(
				LineComponent.builder().left("Almost time to go!").build()
			);
			panelComponent.getChildren().add(
				LineComponent.builder().left("Despawn in: ").right(Integer.toString(plugin.entStats.despawnTime)).build()
			);
		}
		return super.render(graphics);
	}
}
