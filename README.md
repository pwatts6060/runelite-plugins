# Looting Bag Value
####Overlays looting bag value / free spaces on the bag in inventory.

- Right click check the bag to calibrate the current contents. 
- Updates value and free slots whenever checked or items are picked up into the bag.
- Item prices are from the osrs wiki.
- Item quantities on the ground are only accurate up to 65534. 
Picking up an item with quantity >= 65535 will show the bag value is at least the value it 
thinks by showing a > symbol.
- Manually putting items from your inventory in the bag isn't supported.
- May also track items in the following scenario:
    - You click on an item to take it
    - The item despawns by disappearance or someone else takes it
    - You're standing on the same tile as the item when it disappears

Config toggles:
- Text color
- Whether to display bag value
- Whether to use wiki price or ge price
- Whether to display number of free slots

Report bugs/feature requests in github issues with a title starting with the plugin name, "Looting Bag Value"