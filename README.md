# Potent Wounds

A Minecraft mod that introduces a wound system that limits natural regeneration.

## Features
- Wound accumulation from damage
- Regeneration cap based on wounds
- Passive wound decay over time
- Delay before decay begins after taking damage
- Healing foods:
  - Golden foods: instant wound removal
  - Pumpkin pie: strong recovery over time
  - Baked potato: weaker recovery
  - Honey bottle: Soothed effect (temporary relief)
  - Dried kelp: light recovery + minor relief
- Special food bypass: wound-related foods can be consumed even when full

## Status
Core gameplay loop complete. 

## Upcoming
Additional food items planned. 
Sleep recovery system planned.  
Background bar for rawWounds value planned. 
Display change in coloration of background bar or such when Recuperation active planned.
Heart outline overlay for appliedWounds value planned. 
Heart outline overlay for Soothed effect planned. 
Icons for Recuperation and Soothed effects planned. 
Remove unwanted particle effects (Soothed/Recuperation) planned. 
Create mod image planned. 
Rename mod planned. 
Publish mod planned. 

## Known Bugs
Wounds ignore soothed if soothed is activated prior to damage instance. Tested/observed by applying infinite soothed effect then taking damage, noticed applied wounds doesn't increase when soothed is removed. 
