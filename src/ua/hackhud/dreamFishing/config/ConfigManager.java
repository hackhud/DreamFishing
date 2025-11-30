package ua.hackhud.dreamFishing.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ua.hackhud.dreamFishing.Main;

import java.io.File;

public class ConfigManager {
    private FileConfiguration config;
    private String fishingSpeedLore;
    private String fishingProgressLore;
    private final Main instance;

    public ConfigManager(Main instance) {
        this.instance = instance;
        loadConfig();
    }

    private void loadConfig() {
        File configFile = getConfigFile();
        ensureConfigExists(configFile);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        loadFishingSpeedLore();
        loadFishingProgressLore();
    }

    private File getConfigFile() {
        return new File(instance.getDataFolder(), "config.yml");
    }

    private void ensureConfigExists(File configFile) {
        if (!configFile.exists()) {
            instance.saveResource("config.yml", false);
        }
    }

    private void loadFishingSpeedLore() {
        fishingSpeedLore = config.getString("fishingSpeedLore");
    }

    private void loadFishingProgressLore() {
        fishingProgressLore = config.getString("fishingProgressLore");
    }

    public String getFishingSpeedLore() {
        return fishingSpeedLore;
    }

    public String getFishingProgressLore() {
        return fishingProgressLore;
    }
}
