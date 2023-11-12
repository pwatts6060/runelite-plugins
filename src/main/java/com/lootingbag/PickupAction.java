package com.lootingbag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemDespawned;

@AllArgsConstructor
public class PickupAction
{
	@Getter
	private int itemId;

	@NonNull
	@Getter
	private WorldPoint worldPoint;

	@Getter
	private int ticksSincePickup;

	@Getter
	@Setter
	private int quantity;

	public PickupAction(int itemId, @NonNull WorldPoint worldPoint) {
		this.itemId = itemId;
		this.worldPoint = worldPoint;
	}

	public void incrementTicksSincePickup() {
		ticksSincePickup++;
	}

	public boolean matchesItemDespawnEvent(ItemDespawned event) {
		return event.getTile().getWorldLocation().equals(worldPoint)
			&& event.getItem().getId() == itemId;
	}
}
