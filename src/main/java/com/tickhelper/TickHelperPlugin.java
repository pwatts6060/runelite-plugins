package com.tickhelper;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.ObjectID;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.StatChanged;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Tick Manipulation Helper"
)
public class TickHelperPlugin extends Plugin
{
	private static final int PICKUP_BOX_TRAP_ANIM = 5212;
	@Inject
	private Client client;

	@Inject
	private TickHelperOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ClientThread clientThread;

	private static final int[] previous_exp = new int[Skill.values().length];

	@Getter
	private int actionTimer;

	private int lightLogTime; // Ticks since player attempted to light log

	@Getter
	private int blockTimer;

	private int prevItemId;

	private WorldPoint lastTickLocalPlayerLocation;

	private HunterTrap dismantlingTrap;

	@Getter
	private final Map<WorldPoint, HunterTrap> traps = new HashMap<>();

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);

		if (client.getGameState() == GameState.LOGGED_IN) {
			clientThread.invokeLater(() ->
			{
				int[] xps = client.getSkillExperiences();
				System.arraycopy(xps, 0, previous_exp, 0, previous_exp.length);
			});
		} else {
			Arrays.fill(previous_exp, 0);
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		traps.clear();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked e) {
//		System.out.println(e.getMenuOption() + " " + e.getMenuAction().getId() + " " + e.getParam0());
		if (e.getMenuAction().equals(MenuAction.WIDGET_TARGET_ON_WIDGET)
			&& e.getMenuOption().equals("Use")
			&& e.getWidget() != null
			&& e.getWidget().getId() == ComponentID.INVENTORY_CONTAINER) { // item used on item in inv

			int id1 = e.getItemId();

			ItemContainer inv = client.getItemContainer(InventoryID.INVENTORY);
			if (inv == null) {
				return;
			}
			Item item = inv.getItem(e.getParam0());
			if (item == null) {
				return;
			}

			int id2 = prevItemId;
			// will assume player has only 1 of the item and they have pestle and mortar
			if (itemsMatch(id1, id2, ItemID.KNIFE, ItemID.TEAK_LOGS)
				|| itemsMatch(id1, id2, ItemID.KNIFE, ItemID.MAHOGANY_LOGS)
				|| itemsMatch(id1, id2, ItemID.GUAM_LEAF, ItemID.SWAMP_TAR)
				|| itemsMatch(id1, id2, ItemID.TARROMIN, ItemID.SWAMP_TAR)
				|| itemsMatch(id1, id2, ItemID.IRIT_LEAF, ItemID.SWAMP_TAR)
				|| itemsMatch(id1, id2, ItemID.HARRALANDER, ItemID.SWAMP_TAR)
				|| itemsMatch(id1, id2, ItemID.MARRENTILL, ItemID.SWAMP_TAR)) {
				actionTimer = 3;
			} else if (itemsMatch(id1, id2, ItemID.TINDERBOX, ItemID.REDWOOD_LOGS)
				|| itemsMatch(id1, id2, ItemID.TINDERBOX, ItemID.MAGIC_LOGS)
				|| itemsMatch(id1, id2, ItemID.TINDERBOX, ItemID.YEW_LOGS)
				|| itemsMatch(id1, id2, ItemID.TINDERBOX, ItemID.MAPLE_LOGS)
				|| itemsMatch(id1, id2, ItemID.TINDERBOX, ItemID.WILLOW_LOGS)
				|| itemsMatch(id1, id2, ItemID.TINDERBOX, ItemID.OAK_LOGS)
				|| itemsMatch(id1, id2, ItemID.TINDERBOX, ItemID.LOGS)
				|| itemsMatch(id1, id2, ItemID.TINDERBOX, ItemID.ACHEY_TREE_LOGS)
				|| itemsMatch(id1, id2, ItemID.TINDERBOX, ItemID.BLISTERWOOD_LOGS)
				|| itemsMatch(id1, id2, ItemID.TINDERBOX, ItemID.TEAK_LOGS)
				|| itemsMatch(id1, id2, ItemID.TINDERBOX, ItemID.MAHOGANY_LOGS)
				|| itemsMatch(id1, id2, ItemID.TINDERBOX, ItemID.ARCTIC_PINE_LOGS)) {
				lightLogTime = 6;
			}
		} else if (e.getMenuAction().equals(MenuAction.CC_OP)) {;
			if (e.getItemId() == ItemID.BOX_TRAP && e.getMenuOption().equals("Lay")) {
				if (actionTimer == 0 && blockTimer <= 0) {
					actionTimer = 5;
				}
			} else if (e.getItemId() == ItemID.CELASTRUS_BARK && e.getMenuOption().equals("Fletch")) {
				if (actionTimer == 0) {
					actionTimer = 4;
				}
			}
		}
		/*else if (e.getMenuAction().equals(MenuAction.GAME_OBJECT_FIRST_OPTION)) {
			if (e.getMenuOption().equals("Check") || e.getMenuOption().equals("Dismantle")) {
				blockTimer = 4;
			}
		}*/
		prevItemId = e.getItemId();
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged e) {
		if (e.getActor() != client.getLocalPlayer()) {
			return;
		}

		if (e.getActor().getAnimation() == PICKUP_BOX_TRAP_ANIM) {
			blockTimer = 2;
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned e) {
		final GameObject gameObject = e.getGameObject();
		final WorldPoint trapLocation = gameObject.getWorldLocation();
		final HunterTrap myTrap = traps.get(trapLocation);
		final Player localPlayer = client.getLocalPlayer();
		switch (e.getGameObject().getId()) {
			case ObjectID.BOX_TRAP_9380:
				// If the player is on that tile, assume he is the one that placed the trap
				// Note that a player can move and set up a trap in the same tick, and this
				// event runs after the player movement has been updated, so we need to
				// compare to the trap location to the last location of the player.
				if (lastTickLocalPlayerLocation != null
					&& trapLocation.distanceTo(lastTickLocalPlayerLocation) == 0)
				{
					traps.put(trapLocation, new HunterTrap(gameObject));
					actionTimer = 5;
				}
				break;
		}
	}

	@Subscribe
	public void onGameTick(GameTick e) {
		if (actionTimer > 0) {
			--actionTimer;
		}
		if (blockTimer > 0) {
			--blockTimer;
		}
		if (lightLogTime > 0) {
			--lightLogTime;
		}
		/* NOTE: code copied form HunterPlugin for purpose of tracking box traps */

		// Check if all traps are still there, and remove the ones that are not.
		Iterator<Map.Entry<WorldPoint, HunterTrap>> it = traps.entrySet().iterator();
		Tile[][][] tiles = client.getTopLevelWorldView().getScene().getTiles();

		Instant expire = Instant.now().minus(HunterTrap.TRAP_TIME.multipliedBy(2));

		while (it.hasNext())
		{
			Map.Entry<WorldPoint, HunterTrap> entry = it.next();
			HunterTrap trap = entry.getValue();
			WorldPoint world = entry.getKey();
			LocalPoint local = LocalPoint.fromWorld(client.getTopLevelWorldView(), world);

			// Not within the client's viewport
			if (local == null)
			{
				// Cull very old traps
				if (trap.getPlacedOn().isBefore(expire))
				{
					it.remove();
					continue;
				}
				continue;
			}

			Tile tile = tiles[world.getPlane()][local.getSceneX()][local.getSceneY()];
			GameObject[] objects = tile.getGameObjects();

			boolean containsBoulder = false;
			boolean containsAnything = false;
			boolean containsYoungTree = false;
			for (GameObject object : objects)
			{
				if (object == null)
				{
					continue;
				}
				containsAnything = true;
				if (object.getId() == ObjectID.BOULDER_19215 || object.getId() == ObjectID.LARGE_BOULDER)
				{
					containsBoulder = true;
					break;
				}

				// Check for young trees (used while catching salamanders) in the tile.
				// Otherwise, hunter timers will never disappear after a trap is dismantled
				if (object.getId() == ObjectID.YOUNG_TREE_8732 || object.getId() == ObjectID.YOUNG_TREE_8990 ||
					object.getId() == ObjectID.YOUNG_TREE_9000 || object.getId() == ObjectID.YOUNG_TREE_9341 ||
					object.getId() == ObjectID.YOUNG_TREE_50721 || object.getId() == ObjectID.YOUNG_TREE_50722)
				{
					containsYoungTree = true;
				}
			}

			if (!containsAnything || containsYoungTree)
			{
				it.remove();
			}
			else if (containsBoulder) // For traps like deadfalls. This is different because when the trap is gone, there is still a GameObject (boulder)
			{
				it.remove();
			}
		}

		lastTickLocalPlayerLocation = client.getLocalPlayer().getWorldLocation();
	}

	private void onXpDrop(XpDrop xpDrop) {
		if (xpDrop.skill.equals(Skill.FIREMAKING) && lightLogTime > 0) {
			lightLogTime = 0;
			actionTimer = 4;
		}
	}

	@Subscribe
	protected void onStatChanged(StatChanged event) {
		int currentXp = event.getXp();
		int previousXp = previous_exp[event.getSkill().ordinal()];
		if (previousXp > 0 && currentXp - previousXp > 0) {
            XpDrop xpDrop = new XpDrop(event.getSkill(), currentXp - previousXp, false);
            onXpDrop(xpDrop);
		}

		previous_exp[event.getSkill().ordinal()] = event.getXp();
	}

	/**
	 * Returns if a pair of item ids matches another pair of item ids. Order within the pairs does not matter.
	 * @param id1 The item id of the first item you are checking
	 * @param id2 The item id of the second item you are checking
	 * @param searchId1 The first item id you expect to find
	 * @param searchId2 The second item id you expect to find
	 * @return If either order of id1 and id2 are the search items
	 */
	private boolean itemsMatch(int id1, int id2, int searchId1, int searchId2)
	{
		return (id1 == searchId1 && id2 == searchId2) || (id1 == searchId2 && id2 == searchId1);
	}
}
