package com.creativechasm.cropcultivation.registry;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public final class CropRegistry {

    public static final Marker LOG_MARKER = MarkerManager.getMarker("CropRegistry");

    @SuppressWarnings("UnstableApiUsage")
    private final ListMultimap<String, ResourceLocation> commonIdToModIdMapping = MultimapBuilder.ListMultimapBuilder.hashKeys(100).arrayListValues(2).build();
    private final HashMap<String, ICropEntry> commonIdMapping = new HashMap<>(100);
    private final HashMap<String, HashMap<String, ICropEntry>> entries = new HashMap<>(3);

    private final String mappings_resource;
    private final String entries_resource;

    public CropRegistry(String mappingsCSV, String entriesCSV) {
        this.mappings_resource = mappingsCSV;
        this.entries_resource = entriesCSV;

        try {
            boolean internal = true;
            loadMappings(internal);
        }
        catch (Exception e) {
            CropCultivationMod.LOGGER.error(LOG_MARKER, "failed to initialize registry", e);
            throw new RuntimeException();
        }
    }

    public void buildRegistry() throws Exception {
        boolean internal = true;
        loadCrops(internal);
    }

    public Optional<ICropEntry> get(@Nullable String commonId) {
        if (commonId != null && !commonId.isEmpty()) {
            return Optional.ofNullable(commonIdMapping.get(commonId));
        }
        return Optional.empty();
    }

    public Optional<ICropEntry> get(@Nullable ResourceLocation rl) {
        if (rl != null) {
            String namespace = rl.getNamespace();
            String cropName = rl.getPath();
            if (entries.containsKey(namespace)) {
                HashMap<String, ICropEntry> cropMap = entries.get(namespace);
                return Optional.ofNullable(cropMap.get(cropName));
            }
        }
        return Optional.empty();
    }

    @Nonnull
    public List<ResourceLocation> getModsFor(@Nullable String commonId) {
        if (commonId != null && !commonId.isEmpty()) {
            return commonIdToModIdMapping.get(commonId);
        }
        return new ArrayList<>();
    }

    public Optional<String> getCommonId(@Nullable ICropEntry cropEntry) {
        if (cropEntry != null) {
            if (cropEntry instanceof DefaultCropEntry) return Optional.of(((DefaultCropEntry) cropEntry).commonId);
            return commonIdMapping.entrySet().stream().filter(stringICropEntryEntry -> stringICropEntryEntry.getValue() == cropEntry).findFirst().map(Map.Entry::getKey);
        }
        return Optional.empty();
    }

    private void register(ResourceLocation rl, ICropEntry iCrop) {
        entries.computeIfAbsent(rl.getNamespace(), key -> new HashMap<>()).put(rl.getPath(), iCrop);
    }

    private void loadMappings(boolean internalResource) throws Exception {
        if (internalResource) {
            readMappingsCSV(new InputStreamReader(getClass().getResourceAsStream(mappings_resource)));
        }
        else {
            readMappingsCSV(new FileReader(mappings_resource.replace("/data", ".")));
        }
    }

    private void loadCrops(boolean internalResource) throws Exception {
        if (internalResource) {
            readCropsCSV(new InputStreamReader(getClass().getResourceAsStream(entries_resource)));
        }
        else {
            readCropsCSV(new FileReader(entries_resource.replace("/data", ".")));
        }
    }

    private void readMappingsCSV(InputStreamReader isr) throws Exception {
        CropCultivationMod.LOGGER.info(LOG_MARKER, "initializing common id mappings...");
        try (BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                String commonId = columns[0];
                if (commonId.isEmpty()) throw new IllegalArgumentException("invalid common id value");

                List<ResourceLocation> list = Arrays.stream(columns).skip(1).filter(s -> !s.isEmpty()).map(ResourceLocation::new).collect(Collectors.toList());
                commonIdToModIdMapping.putAll(commonId, list);
            }
        }
        CropCultivationMod.LOGGER.info(LOG_MARKER, commonIdToModIdMapping.toString());
    }

    private void readCropsCSV(InputStreamReader streamReader) throws Exception {
        CropCultivationMod.LOGGER.info(LOG_MARKER, "registering crop entries...");
        try (BufferedReader br = new BufferedReader(streamReader)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length != 10) throw new Exception("csv contains illegal amount of columns");
                String commonId = columns[0];
                if (commonId.isEmpty()) throw new IllegalArgumentException("invalid common id value");

                ICropEntry cropEntry = new DefaultCropEntry(
                        commonId,
                        Float.parseFloat(columns[1]), Float.parseFloat(columns[2]), Float.parseFloat(columns[3]),
                        Float.parseFloat(columns[4]), Float.parseFloat(columns[5]),
                        Float.parseFloat(columns[6]), Float.parseFloat(columns[7]),
                        Float.parseFloat(columns[8]), Float.parseFloat(columns[9])
                );

                commonIdMapping.put(commonId, cropEntry);
                commonIdToModIdMapping.get(commonId).forEach(resourceLocation -> register(resourceLocation, cropEntry));
            }
        }
        CropCultivationMod.LOGGER.info(LOG_MARKER, commonIdMapping.toString());
        CropCultivationMod.LOGGER.info(LOG_MARKER, commonIdToModIdMapping.toString());
    }
}
