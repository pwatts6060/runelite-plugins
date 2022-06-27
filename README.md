# Instant Idle Notify

A Runelite plugin that notifies you the instant you can no longer repeat a repetitive automatic action by monitoring your inventory changes.

## Conditions
This plugin will notify you under the following conditions:
- The action performed consumes items
- The action is performed at least 3 times
- The action has the same delay in between subsequent actions

## Use cases
- Herblore (Potions, cleaning herbs*, unf pots, crushing*)
- Fletching (Arrows, Bolts, Strung/Unstrung bows)
- Cooking
- Smithing (Anvil and Furnace)
- Crafting
- Prayer training at altars*
- Enchanting jewelry*

*note it will generally not work when re-triggering the action to speed it up

## Bugs
The plugin may detect other unwanted actions that also meet this conditions.
Some of them have been disabled where possible including:
- Dropping items
- Eating/Drinking items
- Depositing items

Please submit issues if there are other unexpected notifications with relevant details.