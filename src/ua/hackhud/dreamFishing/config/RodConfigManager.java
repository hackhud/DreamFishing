package ua.hackhud.dreamFishing.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import ua.hackhud.dreamFishing.Main;
import ua.hackhud.dreamFishing.entities.FishingRod;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RodConfigManager {
    private FileConfiguration config;
    private final Map<String, FishingRod> rods = new HashMap<>();
    private final Map<String, ItemStack> rodsItemStack = new HashMap<>();

    public RodConfigManager() {
        loadConfig();
    }

    private void loadConfig() {
        File configFile = getConfigFile();
        ensureConfigExists(configFile);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        loadRods();
        loadRodItemStack();
    }

    private File getConfigFile() {
        return new File(Main.getPlugin().getDataFolder(), "fishing-rods.yml");
    }

    private void ensureConfigExists(File configFile) {
        if (!configFile.exists()) {
            Main.getPlugin().saveResource("fishing-rods.yml", false);
        }
    }

    private void loadRods() {
        rods.clear();

        ConfigurationSection rodsSection = config.getConfigurationSection("rods");
        if (rodsSection == null) return;

        for (String rodKey : rodsSection.getKeys(false)) {
            ConfigurationSection rodSection = rodsSection.getConfigurationSection(rodKey);
            if (rodSection == null) continue;

            FishingRod rod = createFishingRod(rodKey, rodSection);
            rods.put(rod.getDisplayName(), rod);
        }
    }

    private void loadRodItemStack() {
        rodsItemStack.clear();

        ConfigurationSection rodItemStackSection = config.getConfigurationSection("rodItemStack");
        if (rodItemStackSection == null) return;

        for (String rodKey : rodItemStackSection.getKeys(false)) {
            ItemStack rod = config.getItemStack("rodItemStack." + rodKey);
            rodsItemStack.put(rodKey, rod);
        }
    }

    private FishingRod createFishingRod(String name, ConfigurationSection section) {
        String displayName = section.getString("displayName");
        String dropTable = section.getString("dropTable");
        String transformTargetName = section.getString("transformTargetName");
        List<String> transformCommands = section.getStringList("transformCommands");
        List<String> baseCommands = section.getStringList("baseCommands");

        return new FishingRod(name, displayName, dropTable, transformTargetName, transformCommands, baseCommands);
    }

    public void addRodItemStack(String key, ItemStack rod) {
        if (key == null || rod == null) return;

        rodsItemStack.put(key, rod);

        if (config.getConfigurationSection("rodItemStack") == null) {
            config.createSection("rodItemStack");
        }

        config.set("rodItemStack." + key, rod);

        try {
            config.save(getConfigFile());
        } catch (Exception e) {
            e.printStackTrace();
            Main.getPlugin().getLogger().severe("Не удалось сохранить удочку в конфиг: " + key);
        }
    }


    public void reload() {
        loadConfig();
    }

    public Map<String, FishingRod> getRods() {
        return Collections.unmodifiableMap(rods);
    }

    public Map<String, ItemStack> getRodsItemStack() {
        return rodsItemStack;
    }

    public FishingRod getRod(String name) {
        return rods.get(name);
    }

    public ItemStack getRodItemStack(String name) {
        return rodsItemStack.get(name);
    }
}
