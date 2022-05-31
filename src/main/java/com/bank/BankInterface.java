package com.bank;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetType;

import javax.inject.Inject;

public class BankInterface {
    private static final String CLEAR_RECENT = "Clear Recent";

    @Getter
    private Widget parent;

    private final RecentBankPlugin plugin;
    private final Client client;
    private final RecentBankConfig config;

    @Getter
    private Widget clearButtonWidget;
    private Widget clearButtonWidgetHover;

    @Inject
    public BankInterface(RecentBankPlugin plugin, Client client, RecentBankConfig config) {
        this.plugin = plugin;
        this.client = client;
        this.config = config;
    }

    public void init() {
        if (isHidden()) {
            return;
        }
        parent = client.getWidget(WidgetInfo.BANK_CONTAINER);
        if (parent == null) {
            return;
        }
        int width = parent.getOriginalWidth() - 75;

        clearButtonWidget = createGraphic("", SpriteID.WINDOW_CLOSE_BUTTON, -1, 26, 23, width, 6, true);
        clearButtonWidget.setAction(1, CLEAR_RECENT);
        clearButtonWidget.setOnMouseOverListener((JavaScriptCallback) (event) -> clearButtonWidgetHover.setHidden(false));
        clearButtonWidget.setOnMouseLeaveListener((JavaScriptCallback) (event) -> clearButtonWidgetHover.setHidden(true));
        clearButtonWidget.setOnOpListener((JavaScriptCallback) (event) -> plugin.clearButton());

        clearButtonWidgetHover = createGraphic("", SpriteID.WINDOW_CLOSE_BUTTON_HOVERED, -1, 26, 23, width, 6, false);
        clearButtonWidgetHover.setHidden(true);
    }

    public boolean isHidden() {
        Widget widget = client.getWidget(WidgetInfo.BANK_CONTAINER);
        return !config.recentViewToggled() || widget == null || widget.isHidden();
    }

    public void destroy() {
        parent = null;
        if (clearButtonWidget != null) {
            clearButtonWidget.setHidden(true);
        }
        if (clearButtonWidgetHover != null) {
            clearButtonWidgetHover.setHidden(true);
        }
    }

    private Widget createGraphic(Widget container, String name, int spriteId, int itemId, int width, int height, int x, int y, boolean hasListener) {
        Widget widget = container.createChild(-1, WidgetType.GRAPHIC);
        widget.setOriginalWidth(width);
        widget.setOriginalHeight(height);
        widget.setOriginalX(x);
        widget.setOriginalY(y);

        widget.setSpriteId(spriteId);

        if (itemId > -1) {
            widget.setItemId(itemId);
            widget.setItemQuantity(-1);
            widget.setBorderType(1);
        }

        if (hasListener) {
            widget.setOnOpListener(ScriptID.NULL);
            widget.setHasListener(true);
        }

        widget.setName(name);
        widget.revalidate();

        return widget;
    }

    private Widget createGraphic(String name, int spriteId, int itemId, int width, int height, int x, int y, boolean hasListener) {
        return createGraphic(parent, name, spriteId, itemId, width, height, x, y, hasListener);
    }
}
