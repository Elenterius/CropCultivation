package com.creativechasm.cropcultivation.environment.plant;

import com.creativechasm.cropcultivation.util.ModVoxelShapes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.shapes.VoxelShape;

public enum WeedType implements IStringSerializable
{
    GRASS("grass", ModVoxelShapes.BUSH),
    TALL_GRASS("tall_grass", ModVoxelShapes.BUSH),
    SOWTHISTLE("sowthistle", ModVoxelShapes.SMALL_FLOWER);

    private final String name;
    private final VoxelShape shape;

    WeedType(String name, VoxelShape shape) {
        this.name = name;
        this.shape = shape;
    }

    @Override
    public String getName() {
        return name;
    }

    public VoxelShape getShape() {
        return shape;
    }
}
