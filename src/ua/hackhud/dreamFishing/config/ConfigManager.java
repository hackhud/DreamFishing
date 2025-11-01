package ua.hackhud.dreamFishing.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ua.hackhud.dreamFishing.Main;

import java.io.File;

public class ConfigManager {
    private FileConfiguration config;
    private int minFishingTiming;
    private int maxFishingTiming;
    private String fishingSpeedLore;
    private String fishingProgressLore;

    public ConfigManager() {
        loadConfig();
    }

    private void loadConfig() {
        File configFile = getConfigFile();
        ensureConfigExists(configFile);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        loadFishingTimings();
        loadFishingSpeedLore();
        loadFishingProgressLore();
    }

    private File getConfigFile() {
        return new File(Main.getPlugin().getDataFolder(), "config.yml");
    }

    private void ensureConfigExists(File configFile) {
        if (!configFile.exists()) {
            Main.getPlugin().saveResource("config.yml", false);
        }
    }

    private void loadFishingTimings() {
        minFishingTiming = config.getInt("minFishingTiming");
        maxFishingTiming = config.getInt("maxFishingTiming");
    }

    private void loadFishingSpeedLore() {
        fishingSpeedLore = config.getString("fishingSpeedLore");
    }

    private void loadFishingProgressLore() {
        fishingProgressLore = config.getString("fishingProgressLore");
    }

    public int getMinFishingTiming() {
        return minFishingTiming;
    }

    public int getMaxFishingTiming() {
        return maxFishingTiming;
    }

    public String getFishingSpeedLore() {
        return fishingSpeedLore;
    }

    public String getFishingProgressLore() {
        return fishingProgressLore;
    }
}
