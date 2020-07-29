# Crop Cultivation
[![Forge Version](https://img.shields.io/badge/Minecraft%20Forge-1.15.2%20--%2031.2.31-orange)](https://files.minecraftforge.net/maven/net/minecraftforge/forge/index_1.15.2.html)
[![CropCultivation Version](https://img.shields.io/badge/CropCultivation-Alpha-red)](https://github.com/Elenterius/CropCultivation)

A Minecraft Forge Mod that takes out the simple out of crop farming.<br>
Currently focuses on soil management, and the plant growth restrictions derived from its environment.

This mod will not add new crops to farm but instead modifies the behavior of all vanilla and compatible mod crops.

## Crops
### Changed Crop Behavior
- growth requirements
    - soil moisture need
    - macronutrients need
    - soil pH tolerance
    - temperature tolerance
- growth chance
    - based on nutrient availability in soil which depends on soil pH
- crop yield
    - based on nutrient concentration in soil throughout its growth stages

---

### Supported Crops
- Minecraft
- SimpleFarming

### Partial Support
- HarvestCraft Crops (only the crops overlapping with SimpleFarming)
- XLFoodMod (only the crops overlapping with SimpleFarming)

**Note:** HarvestCraft Crops contains over 70 crops and requires a hefty time investment to research/guesstimate the crop characteristics.
If you want full support consider helping out. 

### Compatible Crops
**Note:** Non supported mod crops will fallback to a generic behavior
- blocks extending CropsBlock
- blocks implementing IGrowable
    - should contain a Block Property for age (IntegerProperty with the identifier "age") if not crop yield will not be properly modified



## Soils (Farmland)
```diff
! The Mod removes the ability to create farmland from vanilla dirt with the hoe
```

### Soil Properties
- Soil Texture
- Moisture
- pH
- Plant Macronutrients:
`Nitrogen (N)`,
`Phosphorus (P)`,
`Potassium (K)`
- Organic Matter Content
    - increases moisture capacity/retention of soil
    - increases nutrient retention of soil

### Player Interaction
- Moisture capacity can be modified by placing sand/gravel/clay/stone below soil
- Organic Matter has a chance to decay into nutrients for the soil
- Overall pH doesn't change much by itself, the player is the only big influence through the application of liming or acidifying material
- Basically macronutrients and compost are the least things that should be checked before replanting

### Environmental Influences
- Rain can decrease soil pH (preventable with a roof)
- Rain can wash away nutrients (preventable with a roof)

## Vanilla Changes (requires Mixin)
### Composter Block
- produces now three `compost` items instead of one `bone meal` item
- increased the composting delay to 120 ticks
- spawns "heat" particles
### Sugarcane, Cactus & Nether Wart
Implemented the IGrowable Interface for these Plant Blocks
- this makes them possible to be force grown through code without scheduling a tick update
<br>**Note:** the (Forge) CropGrowthEvent will not be fired as with all other IGrowable implementations


- it's now possible to apply `bone meal` on them
<br>**Note:** this will not work when the plant has been registered in the `CropRegistry`


- retained the vanilla feature that `bone meal` cannot be used on Nether Wart


## Soil Amendments
### Compost
Applied to the soil to increase the organic matter content of soil.

_Item Tag:_ `compost`
- optional tag entry for cannycomposter

### Fertilizer
```diff
! The Mod removes the ability to use bone meal on compatible crops
```
Fertilizers are to be applied to the soil instead of the crop.

_Item Tags:_ `n_fertilizer` `p_fertilizer` `k_fertilizer`

- `Bone Meal`
- `Industrial Fertilizer`
- `Feather Meal`
- `Seaweed Meal`
- `Fish Meal`
- `Soybean Meal` `optional (requires simplefarming/harvestcraft)`
### Liming Material
Increases the Soil pH.

_Item Tag:_ `liming_material`

- `Lime Dust`
  - uncommon side product from cutting Rocks with the Stonecutter
- `Wood Ash`
  - process charcoal into dust with the mortar and pestle (unrealistic)
 <br>(make wood ash a waste product of burning/smelting wood logs in a furnace?)
### Acidifying Material
Decreases the Soil pH.

_Item Tag:_ `acidifying_material`

- `Blaze Powder`
- `Gunpowder`
- `Sulphur` `optional tag entry for simplysalty`