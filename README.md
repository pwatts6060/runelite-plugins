# Star Info
Displays various info about shooting stars that you find/mine such as tier, number of miners, health %, star dust, time till next layer, time till death.

### Toggleable Features:
- Displays number of players actively mining the star
- Displays Tier of the star
- Displays % of the current tier left
- Displays Info Box of star information as soon as a star is in view
- Displays Hint arrow pointing towards nearby stars if in view
- Highlight stars in green if you can mine them and in red if you can't
- Add a copy option on stars to copy their information to be pasted into discord. For example, W386 T3 66% - 8 Miners - Brimhaven south dungeon entrance 3 minutes ago
- Display remaining dust in the current layer or entire star
- Estimate time to next layer.
- Estimate time till star dies
- Add found stars to game chat
- Hide health bar (enabled by default for readable text overlay)

### Time Estimates
#### Old layer time
The original time estimate was limited to estimating the time till the next layer by sampling the last 10 health bar changes.
This meant the estimate was only available after you've gone through 20% of the layer. 
This does not rely on hiscores working and can be re-enabled by turning on "Old layer time" in the config.

#### New Estimator
The new estimator uses the success rates for star tiers with mining levels pulled from the osrs hiscores. 
It keeps track of pickaxe special attacks that give +3 mining and assumes nobody uses preserve.
It knows what pickaxe people are using to get their correct mining rolls.
There is no way to know if someone is using the celestial ring but it assumes so if they are wearing at least one piece of golden prospector.
It also accounts for the fact that a maximum of 3 dust per tick can be mined from stars.
It assumes that pickaxe rolls are rolled randomly with 1/rolls probability every tick.

### Credits
Originally based on Cute Rock's [Star Tier Indicator](https://github.com/zodaz/StarTierIndicator/tree/c270a68ba8a1a4307670bdc95c8cce903a1e1744) plugin.
Some code used from Cute Rock's [Star Calling Assist](https://runelite.net/plugin-hub/show/star-calling-assist) plugin.
