package com.creativechasm.cropcultivation.api.block;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.api.soil.SoilPH;
import com.google.common.primitives.UnsignedBytes;
import jdk.jfr.Unsigned;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;

public class SoilStateTileEntity extends TileEntity {
    @Unsigned
    private final byte[] nutrients = new byte[]{4, 3, 3};

    @Unsigned
    private byte pH = (SoilPH.MAX_VALUE * 10) / 2; // init as perfect neutral pH value (fallback)

    private float cropYield = 0f; //TODO: store as unsigned byte

    private SoilPH cachedSoilPH = null;

    public SoilStateTileEntity(TileEntityType<?> type) {
        super(type);

        //validate max values are in unsigned byte range (0-255)
        UnsignedBytes.checkedCast(getMaxNutrientAmount());
        UnsignedBytes.checkedCast(getMaxPHValueInInt());
    }

    protected void setNutrientAmount(int idx, int amount, int maxAmount) {
        int ni = MathHelper.clamp(amount, 0, maxAmount);
        byte value = (byte) ni;
        if (value == nutrients[idx]) return;
        nutrients[idx] = value;
        markDirty();
    }

    public void setPH(float pHf) {
        int pHi = Math.round(pHf * 10f);
        pHi = MathHelper.clamp(pHi, 0, getMaxPHValueInInt());
        byte value = (byte) pHi;
        if (value == pH) return;
        pH = value;
        cachedSoilPH = SoilPH.fromPH(pHf);
        markDirty();
    }

    public float getPH() {
        return (pH & 0xFF) / 10f;
    }

    protected int getMaxPHValueInInt() {
        return SoilPH.MAX_VALUE * 10;
    }

    public SoilPH getSoilPHType() {
        if (cachedSoilPH == null) cachedSoilPH = SoilPH.fromPH(getPH());
        return cachedSoilPH;
    }

//    public byte getNutrient(PlantNutrient nutrientType) {
//        switch (nutrientType) {
//            case NITROGEN:
//                return getNitrogen();
//            case PHOSPHORUS:
//                return getPhosphorus();
//            case POTASSIUM:
//                return getPotassium();
//            default: return -1;
//        }
//    }

    public int getNitrogen() {
        return nutrients[0] & 0xFF;
    }

    public void setNitrogen(int amount) {
        setNutrientAmount(0, amount, getMaxNutrientAmount());
    }

    public void addNitrogen(int amount) {
        setNitrogen(getNitrogen() + amount);
    }

    public int getMaxNutrientAmount() {
        return 10;
    }

    public int getPhosphorus() {
        return nutrients[1] & 0xFF;
    }

    public void setPhosphorus(int amount) {
        setNutrientAmount(1, amount, getMaxNutrientAmount());
    }

    public void addPhosphorus(int amount) {
        setPhosphorus(getPhosphorus() + amount);
    }

    public int getPotassium() {
        return nutrients[2] & 0xFF;
    }

    public void setPotassium(int amount) {
        setNutrientAmount(2, amount, getMaxNutrientAmount());
    }

    public void addPotassium(int amount) {
        setPotassium(getPotassium() + amount);
    }

    public void addCropYield(float value) {
        CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("ICrop"), "increasing crop yield...");
        markDirty();
        cropYield += value;
    }

    public void resetCropYield() {
        CropCultivationMod.LOGGER.debug(MarkerManager.getMarker("ICrop"), "resetting crop yield...");
        markDirty();
        cropYield = 0f;
    }

    public float getCropYieldSum() {
        return cropYield;
    }

    public float getCropYieldAveraged(int cropAge) {
        if (cropYield == 0) return 1f;
        return cropAge > 0 ? cropYield / cropAge : 1f;
    }

    @Override
    public void read(@Nonnull CompoundNBT compound) {
        super.read(compound);
        if (compound.contains("nutrients", Constants.NBT.TAG_BYTE_ARRAY)) {
            byte[] bytes = compound.getByteArray("nutrients");
            System.arraycopy(bytes, 0, nutrients, 0, bytes.length);
        }
        if (compound.contains("pH", Constants.NBT.TAG_BYTE)) {
            pH = compound.getByte("pH");
        }
        if (compound.contains("cropYield", Constants.NBT.TAG_FLOAT)) {
            cropYield = compound.getFloat("cropYield");
        }
    }

    @Override
    @Nonnull
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        super.write(compound);
        compound.putByteArray("nutrients", nutrients);
        compound.putByte("pH", pH);
        compound.putFloat("cropYield", cropYield);
        return compound;
    }

    //TODO: sync to client for nutrients visualization?
}
