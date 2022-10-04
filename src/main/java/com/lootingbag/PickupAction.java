package com.lootingbag;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.coords.WorldPoint;

@Data
@AllArgsConstructor
public class PickupAction
{
	int itemId;
	WorldPoint worldPoint;
}
