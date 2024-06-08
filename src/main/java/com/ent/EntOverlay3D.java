package com.ent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class EntOverlay3D extends Overlay
{
	private final Client client;
	private final EntPlugin plugin;
	private final EntConfig config;

	@Inject
	private EntOverlay3D(Client client, EntPlugin plugin, EntConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		int highestPriority = 0;
		for (Ent ent : plugin.entStats.ents.values()) {
			if (!ent.perfect && ent.trimType.priority > highestPriority) {
				highestPriority = ent.trimType.priority;
			}
		}

		for (Ent ent : plugin.entStats.ents.values()) {
			boolean hidden = plugin.hideEnt(ent, highestPriority);

			String text = "";
			if (config.displayTrimOptions()) {
				text = ent.trimType == null ? "n/a" : ent.trimType.shortText;
			}
			if (config.displayTrimCount()) {
				if (!text.isEmpty()) {
					text += ": ";
				}
				text += ent.trims;
			}

			if (!text.isEmpty() && (config.displayTextOnHiddenEnts() || !hidden)) {
				OverlayUtil.renderTextLocation(graphics, ent.npc.getCanvasTextLocation(graphics, text, 0), text, config.textColor());
			}

			if (!hidden) {
				if (ent.perfect) {
					OverlayUtil.renderActorOverlay(graphics, ent.npc, "", Color.RED);
				} else if (config.highlightPriority() && highestPriority == ent.trimType.priority) {
					Shape shape = ent.npc.getConvexHull();
					OverlayUtil.renderPolygon(graphics, shape, config.priorityColor());
					OverlayUtil.renderActorOverlay(graphics, ent.npc, "", config.priorityColor());
				} else {
					OverlayUtil.renderActorOverlay(graphics, ent.npc, "", Color.GREEN);
				}
			}
		}
		return null;
	}
}
