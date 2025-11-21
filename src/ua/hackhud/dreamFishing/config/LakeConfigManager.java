package ua.hackhud.dreamFishing.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ua.hackhud.dreamFishing.Main;
import ua.hackhud.dreamFishing.entities.FishingRod;
import ua.hackhud.dreamFishing.entities.Lake;
import ua.hackhud.dreamFishing.entities.LakeFishingRod;
import ua.hackhud.dreamFishing.entities.LakeStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LakeConfigManager {

    private FileConfiguration config;
    private final Map<String, Lake> lakes = new HashMap<>();
    private final Map<String, Lake> lakesByRegion = new HashMap<>();
    private final Main instance;
    private final RodConfigManager rodConfigManager;

    public LakeConfigManager(Main instance, RodConfigManager rodConfigManager) {
        this.instance = instance;
        this.rodConfigManager = rodConfigManager;
        loadConfig();
    }

    private void loadConfig() {
        File configFile = getConfigFile();
        ensureConfigExists(configFile);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        loadLakes();
    }

    private File getConfigFile() {
        return new File(instance.getDataFolder(), "lakes.yml");
    }

    private void ensureConfigExists(File configFile) {
        if (!configFile.exists()) {
            instance.saveResource("lakes.yml", false);
        }
    }

    private void loadLakes() {
        lakes.clear();

        ConfigurationSection lakesSection = config.getConfigurationSection("lakes");
        if (lakesSection == null) return;

        for (String lakeKey : lakesSection.getKeys(false)) {
            ConfigurationSection lakeSection = lakesSection.getConfigurationSection(lakeKey);
            if (lakeSection == null) continue;

            Lake lake = createLake(lakeKey, lakeSection);
            if (lake != null) {
                lakes.put(lakeKey, lake);
            }
        }

        loadLakesByRegion();
    }

    private Lake createLake(String name, ConfigurationSection section) {
        String displayName = section.getString("displayName");
        String regionName = section.getString("regionName");
        int updateTime = section.getInt("updateTime");
        int minFishingTime = section.getInt("minFishingTime");
        int maxFishingTime = section.getInt("maxFishingTime");

        List<LakeFishingRod> rods = loadRods(section);
        List<String> baseCommands = section.getStringList("baseCommands");

        LakeStatus lakeStatus = loadLakeStatus(section);

        return new Lake(name, displayName, regionName, updateTime, minFishingTime, maxFishingTime, rods, baseCommands, lakeStatus);
    }

    private List<LakeFishingRod> loadRods(ConfigurationSection lakeSection) {
        List<LakeFishingRod> rodsList = new ArrayList<>();
        ConfigurationSection allowedRodsSection = lakeSection.getConfigurationSection("allowedRods");
        if (allowedRodsSection == null) return rodsList;

        for (String rodKey : allowedRodsSection.getKeys(false)) {
            ConfigurationSection rodSection = allowedRodsSection.getConfigurationSection(rodKey);
            if (rodSection == null) continue;

            String dropTable = rodSection.getString("dropTable");
            List<String> commands = rodSection.getStringList("commands");

            FishingRod rod = rodConfigManager.getRod(rodKey);
            if (rod == null || dropTable == null) {
                continue;
            }

            rodsList.add(new LakeFishingRod(rod, dropTable, commands));
        }

        return rodsList;
    }

    private LakeStatus loadLakeStatus(ConfigurationSection lakeSection) {
        double fullLake = 100.0;
        long nextUpdate = System.currentTimeMillis() + lakeSection.getLong("updateTime") * 1000;
        return new LakeStatus(fullLake, nextUpdate);
    }

    private void loadLakesByRegion() {
        for (Lake lake : lakes.values()) {
            lakesByRegion.put(lake.getRegionName(), lake);
        }
    }

    public void reload() {
        loadConfig();
    }

    public Map<String, Lake> getLakes() {
        return Collections.unmodifiableMap(lakes);
    }

    public Lake getLake(String name) {
        return lakes.get(name);
    }

    public Lake getLakeByRegion(String regionName) {
        return lakesByRegion.get(regionName);
    }
}
