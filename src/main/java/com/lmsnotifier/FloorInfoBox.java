package com.lmsnotifier;

import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.AsyncBufferedImage;

import java.awt.*;

public class FloorInfoBox extends InfoBox {

    private final Client client;
    private final LMSPlugin plugin;
    private final LMSPlugin.FloorItem floorItem;

    public FloorInfoBox(AsyncBufferedImage image, LMSPlugin lmsPlugin, Client client, LMSPlugin.FloorItem floorItem) {
        super(image, lmsPlugin);
        this.client = client;
        this.plugin = lmsPlugin;
        this.floorItem = floorItem;
    }

    @Override
    public String getText() {
        WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();
        int bearing = LMSUtil.bearing(playerLoc, floorItem.getTile().getWorldLocation());
        return bearing + "°";
    }

    @Override
    public Color getTextColor() {
        WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();
        int dist = playerLoc.distanceTo2D(floorItem.getTile().getWorldLocation());
        double proportion = Math.min(40, dist) / 40.0;
        return new Color(ColourUtil.interpolateBetweenRgbs(0x00FF00, 0xFF0000, proportion));
    }
}
