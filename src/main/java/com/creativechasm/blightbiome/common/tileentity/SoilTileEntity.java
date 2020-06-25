package com.creativechasm.blightbiome.common.tileentity;

import com.creativechasm.blightbiome.registry.TileEntityRegistry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

public class SoilTileEntity extends TileEntity {
    private final byte[] nutrients = new byte[3];

    public SoilTileEntity() {
        super(TileEntityRegistry.LOAM_SOIL);
    }

    protected void setNutrientAmount(int idx, int amount, int maxAmount) {
        byte value = (byte) amount;
        if (value == nutrients[idx]) return;
        value = (byte) MathHelper.clamp(amount, 0, maxAmount);
        if (value != nutrients[idx]) {
            nutrients[idx] = value;
            markDirty();
        }
    }

    public byte getNitrogen() {
        return nutrients[0];
    }

    public void setNitrogen(int amount) {
        setNutrientAmount(0, amount, 10);
    }

    public byte getPhosphorus() {
        return nutrients[1];
    }

    public void setPhosphorus(int amount) {
        setNutrientAmount(1, amount, 10);
    }

    public byte getPotassium() {
        return nutrients[2];
    }

    public void setPotassium(int amount) {
        setNutrientAmount(2, amount, 10);
    }

    @Override
    public void read(@Nonnull CompoundNBT compound) {
        super.read(compound);
        if (compound.contains("nutrients", Constants.NBT.TAG_BYTE_ARRAY)) {
            byte[] bytes = compound.getByteArray("nutrients");
            System.arraycopy(bytes, 0, nutrients, 0, bytes.length);
        }
    }

    @Override
    @Nonnull
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        super.write(compound);
        compound.putByteArray("nutrients", nutrients);
        return compound;
    }

    //TODO: sync to client for nutrients visualization?
}
