# Crop Cultivation
[![Forge Version](https://img.shields.io/badge/Minecraft%20Forge-1.15.2%20--%2031.2.31-orange)](https://files.minecraftforge.net/maven/net/minecraftforge/forge/index_1.15.2.html)
[![CropCultivation Version](https://img.shields.io/badge/CropCultivation-Alpha-red)](https://github.com/Elenterius/CropCultivation)

A Minecraft Forge Mod that changes the cultivation of crops.

## Changed Crop Behavior
- growth requirements
    - soil moisture needs
    - macronutrients need
    - soil pH tolerance
    - temperature tolerance
- growth chance
    - based on nutrients availability in soil which depends on soil pH
- crop yield
    - based on nutrient concentration in soil

## Different Soils
```diff
! The Mod removes the ability to create farmland from dirt with the hoe!
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
- Rain can decrease soil pH (preventable with a roof)
- Rain can wash away nutrients (preventable with a roof)
- Overall pH doesn't change much by itself, the player is the only big influence through the application of liming or acidifying material
- Basically macronutrients and compost are the only things that should be checked before planting

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

### Fertilizer
```diff
! The Mod removes the ability to use bone meal on compatible crops
```
Fertilizers are to be applied to the soil instead of the crop.
- `bone meal`
- `Industrial Fertilizer`
- `feather meal`
- `seaweed meal`
- `fish meal`
- `soybean meal`
### Liming Material
Increases the Soil pH.
- `Lime Dust`
- `Wood Ash`
### Acidifying Material
Decreases the Soil pH.
- `Blaze Powder`
- `Gunpowder`
- `Sulphur`