package com.creativechasm.cropcultivation.registry;

import com.creativechasm.cropcultivation.CropCultivationMod;
import com.creativechasm.cropcultivation.optionaldependency.OptionalCommonRegistry;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public final class CropRegistry
{

    public static final Marker LOG_MARKER = MarkerManager.getMarker("CropRegistry");
    public static final ICropEntry GENERIC_CROP = new DefaultCropEntry("generic", 0.2f, 0.1f, 0.1f, 5.5f, 7.5f, 0.5f, 0.7f, 10f, 22f);

    @SuppressWarnings("UnstableApiUsage")
    private final ListMultimap<String, ResourceLocation> commonIdToModIdMapping = MultimapBuilder.ListMultimapBuilder.hashKeys(100).arrayListValues(2).build();
    private final HashMap<String, ICropEntry> commonIdMapping = new HashMap<>(100);
    private final HashMap<String, HashMap<String, ICropEntry>> entries = new HashMap<>(3);

    public void clear() {
        commonIdMapping.clear();
        //noinspection RedundantOperationOnEmptyContainer
        commonIdMapping.clear();
        entries.clear();
    }

    public void buildRegistry(File mappings, File entries) throws Exception {
        try {
            loadMappings(mappings);
        }
        catch (Exception e) {
            CropCultivationMod.LOGGER.error(LOG_MARKER, "failed to initialize registry", e);
            throw new RuntimeException();
        }
        loadCrops(entries);
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

    public Optional<ICropEntry> findAnyBy(final String name) {
        return commonIdMapping.entrySet().parallelStream().filter(setEntry -> setEntry.getKey().contains(name)).map(Map.Entry::getValue).findAny();
    }

    private void register(ResourceLocation rl, ICropEntry iCrop) {
        entries.computeIfAbsent(rl.getNamespace(), key -> new HashMap<>()).put(rl.getPath(), iCrop);
    }

    private void loadMappings(File mappingsFile) throws Exception {
        readMappingsCSV(new FileReader(mappingsFile));
    }

    private void loadCrops(File entriesFile) throws Exception {
        readCropsCSV(new FileReader(entriesFile));
    }

    private void readMappingsCSV(InputStreamReader isr) throws Exception {
        CropCultivationMod.LOGGER.info(LOG_MARKER, "initializing common id mappings...");
        try (BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                String commonId = columns[0];
                if (commonId.isEmpty()) throw new IllegalArgumentException("invalid common id value");
                List<ResourceLocation> list = Arrays.stream(columns).skip(1).filter(s -> !s.isEmpty()).map(ResourceLocation::new).filter(rl -> rl.getNamespace().equals("minecraft") || OptionalCommonRegistry.Mods.isModLoaded(rl.getNamespace())).collect(Collectors.toList());
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

                List<ResourceLocation> availableCrops = commonIdToModIdMapping.get(commonId);
                if (availableCrops.size() > 0) { //only populate data for crops that exist, //FIXME: potential issue with player knowledge base containing unavailable crops
                    commonIdMapping.put(commonId, cropEntry);
                    availableCrops.forEach(resourceLocation -> register(resourceLocation, cropEntry));
                }
            }
        }
        CropCultivationMod.LOGGER.info(LOG_MARKER, commonIdMapping.toString());
        CropCultivationMod.LOGGER.info(LOG_MARKER, commonIdToModIdMapping.toString());
    }
}
