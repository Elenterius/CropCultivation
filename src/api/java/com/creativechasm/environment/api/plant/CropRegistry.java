package com.creativechasm.environment.api.plant;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Optional;

@ParametersAreNonnullByDefault
public final class CropRegistry {

    private static final CropRegistry INSTANCE = new CropRegistry();

    public static CropRegistry getInstance() {
        return INSTANCE;
    }

    private final HashMap<String, HashMap<String, ICrop>> registryMap = new HashMap<>();

    private CropRegistry() {

    }

    public Optional<ICrop> get(@Nullable ResourceLocation rl) {
        if (rl != null) {
            String namespace = rl.getNamespace();
            String cropName = rl.getPath();
            if (registryMap.containsKey(namespace)) {
                HashMap<String, ICrop> cropMap = registryMap.get(namespace);
                return Optional.ofNullable(cropMap.get(cropName));
            }
        }
        return Optional.empty();
    }

    public void put(ResourceLocation rl, ICrop iCrop) {
        registryMap.computeIfAbsent(rl.getNamespace(), key -> new HashMap<>()).put(rl.getPath(), iCrop);
    }
}
