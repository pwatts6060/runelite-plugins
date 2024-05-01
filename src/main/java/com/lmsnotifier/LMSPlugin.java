package com.lmsnotifier;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@PluginDescriptor(
        name = "Last Man Standing"
)
public class LMSPlugin extends Plugin {
    private static final String MARK = "Mark";
    static final String CONFIG_GROUP_KEY = "lmsconfig";
    private static final int LOOT_CRATE = ObjectID.CRATE_29081;
    private static final WorldArea inFrontCompLobby = new WorldArea(3141, 3638, 2, 1, 0);
    static final WorldArea lmsCompetitiveLobby = new WorldArea(3138, 3639, 8, 7, 0);
    private static final WorldArea lmsCasualLobby = new WorldArea(3139, 3639, 6, 6, 1);
    private static final WorldArea lmsHighStakesLobby = new WorldArea(3138, 3639, 8, 7, 2);
    private static final Set<Integer> chestIds = ImmutableSet.of(ObjectID.CHEST_29069, ObjectID.CHEST_29072);
    static final int FEROX_REGION_ID = 12600;
    boolean inGame = false;
    Map<WorldPoint, TileObject> chests = new HashMap<>();
    Map<WorldPoint, TileObject> lootCrates = new HashMap<>();
    List<LMSPlayer> localLMSPlayers = new LinkedList<>();
    boolean inLobby = false;
    boolean preLobby = false;

    @Inject
    private LMSHiscores lmsHiscores;

    @Inject
    @Getter
    private Client client;

    @Inject
    @Getter
    private LMSConfig config;

    @Inject
    private Notifier notifier;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private LMSOverlay overlay;

    @Inject
    private LMSOverlay2D overlay2d;

    @Inject
	DeathTracker deathTracker;

    @Inject
    SweatTracker sweatTracker;

    @Inject
    BotIdentification botIdentification;

    @Inject
    private Provider<MenuManager> menuManager;

    @Override
    protected void startUp() throws Exception {
        log.info("Lms Notifier started!");
        overlayManager.add(overlay);
        overlayManager.add(overlay2d);
        deathTracker.load();
        sweatTracker.load();
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Lms Notifier stopped!");
        overlayManager.remove(overlay);
        overlayManager.remove(overlay2d);
        deathTracker.save();
        sweatTracker.save();

        if (client != null)
        {
            menuManager.get().removePlayerMenuItem(MARK);
        }
    }

    @Subscribe
    public void onClientShutdown(ClientShutdown event) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(deathTracker::save);
        event.waitFor(future);
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(sweatTracker::save);
        event.waitFor(future2);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() != GameState.LOADING) {
            return;
        }

        chests.clear();
        lootCrates.clear();
        if (inLobby && config.notifiesGameStart()) {
            notifier.notify("Last Man Standing has started!");
        }
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        if (!inGame || client.getLocalPlayer().getWorldLocation().getRegionID() == FEROX_REGION_ID) {
            return;
        }
        if (event.getSource() instanceof Player && event.getTarget() instanceof Player) {
            Player s = (Player) event.getSource();
            Player t = (Player) event.getTarget();
            botIdentification.interaction(s, t);
			deathTracker.interactChanged(s, t);
        }
    }

	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		if (!inGame || client.getLocalPlayer().getWorldLocation().getRegionID() == FEROX_REGION_ID) {
			return;
		}
		deathTracker.animationChanged(event.getActor());
	}

    @Subscribe
    public void onGameTick(GameTick event) {
        boolean inLmsArea = inLobby || preLobby;
        inLobby = client.getLocalPlayer().getWorldLocation().distanceTo(lmsCompetitiveLobby) == 0
                || client.getLocalPlayer().getWorldLocation().distanceTo(lmsCasualLobby) == 0
                || client.getLocalPlayer().getWorldLocation().distanceTo(lmsHighStakesLobby) == 0;
        preLobby = client.getLocalPlayer().getWorldLocation().distanceTo(inFrontCompLobby) == 0;
        refreshNearbyPlayerRanks();
        botIdentification.tick();

        if (config.getSweatDisplay() && inLmsArea && !(inLobby || preLobby)) {
            // Was in lms, now left. Remove mark option
            menuManager.get().removePlayerMenuItem(MARK);
        } else if (config.getSweatDisplay() && !inLmsArea && (inLobby || preLobby)) {
            // Was not in lms, now in lms. Add mark option
            menuManager.get().removePlayerMenuItem(MARK);
            menuManager.get().addPlayerMenuItem(MARK);
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(CONFIG_GROUP_KEY)) {
            return;
        }

        if (client != null)
        {
            menuManager.get().removePlayerMenuItem(MARK);
            if (config.getSweatDisplay())
            {
                menuManager.get().addPlayerMenuItem(MARK);
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        if ((event.getType() != MenuAction.CC_OP.getId() && event.getType() != MenuAction.CC_OP_LOW_PRIORITY.getId()) || !config.getSweatDisplay())
        {
            return;
        }

        final String option = event.getOption();
        final int componentId = event.getActionParam1();
        final int groupId = WidgetUtil.componentToInterface(componentId);

        if (groupId == InterfaceID.FRIEND_LIST && option.equals("Delete")
                || groupId == InterfaceID.FRIENDS_CHAT && (option.equals("Add ignore") || option.equals("Remove friend"))
                || groupId == InterfaceID.CHATBOX && (option.equals("Add ignore") || option.equals("Message"))
                || groupId == InterfaceID.IGNORE_LIST && option.equals("Delete")
        )
        {
            client.createMenuEntry(-2)
                    .setOption(MARK)
                    .setTarget(event.getTarget())
                    .setType(MenuAction.RUNELITE)
                    .setIdentifier(event.getIdentifier())
                    .onClick(e ->
                    {
                        String target = Text.removeTags(e.getTarget());
                        sweatTracker.markPlayer(Text.sanitize(target.toLowerCase()));
                    });
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (event.getMenuAction() == MenuAction.RUNELITE_PLAYER && event.getMenuOption().equals(MARK))
        {
            Player player = event.getMenuEntry().getPlayer();
            if (player == null)
            {
                return;
            }

            String target = player.getName();
            if (target != null) {
                sweatTracker.markPlayer(Text.sanitize(target.toLowerCase()));
            }
        }
    }

    private void refreshNearbyPlayerRanks() {
        if (!preLobby && !inLobby && !inGame || config.rankVisual().equals(RankVisual.NONE)) {
            localLMSPlayers.clear();
            return;
        }
        List<Player> players = new ArrayList<>(client.getPlayers());
        LocalPoint localPoint = client.getLocalPlayer().getLocalLocation();
        players.sort(Comparator.comparingInt(o -> LMSUtil.distSquared(o.getLocalLocation(), localPoint)));
        localLMSPlayers.clear();
        for (Player player : players) {
            if ((preLobby || inLobby) && player.getWorldLocation().distanceTo(lmsCompetitiveLobby) != 0) {
                continue;
            }
            String name = player.getName();
            if (client.getLocalPlayer().getName().equals(name)) {
                continue;
            }
            LMSRank lmsRank = lmsHiscores.getRankFor(name, sweatTracker);
            if (lmsRank != null) {
                localLMSPlayers.add(new LMSPlayer(player, lmsRank));
            }
        }
    }

    @Subscribe
    public void onWidgetClosed(WidgetClosed ev) {
        if (ev.getGroupId() == InterfaceID.LMS_INGAME) {
            inGame = false;
            chests.clear();
            lootCrates.clear();
            botIdentification.reset();
            if (config.getSweatDisplay()) {
                menuManager.get().removePlayerMenuItem(MARK);
            }
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
        if (widgetLoaded.getGroupId() == InterfaceID.LMS_INGAME) {
            inGame = true;
            if (config.getSweatDisplay()) {
                menuManager.get().addPlayerMenuItem(MARK);
            }
        }
    }

    @Provides
    LMSConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(LMSConfig.class);
    }

    boolean highlightChests() {
        switch (config.highlightChests()) {
            case NEVER:
                return false;
            case HAS_KEY:
                ItemContainer inv = client.getItemContainer(InventoryID.INVENTORY);
                if (inv == null) {
                    return false;
                }
                return inv.contains(ItemID.BLOODY_KEY) || inv.contains(ItemID.BLOODIER_KEY);
            case ALWAYS:
            default:
                return true;
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        onTileObject(null, event.getGameObject());
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        onTileObject(event.getGameObject(), null);
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        onTileObject(null, event.getGroundObject());
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event) {
        onTileObject(event.getGroundObject(), null);
    }

    private void onTileObject(TileObject oldObject, TileObject newObject) {
        if (oldObject != null) {
            WorldPoint oldLocation = oldObject.getWorldLocation();
            chests.remove(oldLocation);
            lootCrates.remove(oldLocation);
        }

        if (newObject == null) {
            return;
        }

        if (chestIds.contains(newObject.getId())) {
            chests.put(newObject.getWorldLocation(), newObject);
            return;
        }

        if (newObject.getId() == LOOT_CRATE) {
            lootCrates.put(newObject.getWorldLocation(), newObject);
        }
    }

    boolean highlightLootCrates() {
        return lootCrates.size() > 0 && !config.lootCrateHighlightType().equals(LootCrateHightlight.NONE);
    }
}
