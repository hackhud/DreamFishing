package ua.hackhud.dreamFishing.config;

import org.bukkit.ChatColor;
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
    private final Map<String, ItemStack> rodItemStacks = new HashMap<>();
    private final Main instance;

    public RodConfigManager(Main instance) {
        this.instance = instance;
        loadConfig();
    }

    private void loadConfig() {
        File configFile = getConfigFile();
        ensureConfigExists(configFile);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        loadRods();
        loadRodItemStacks();
    }

    private File getConfigFile() {
        return new File(instance.getDataFolder(), "fishing-rods.yml");
    }

    private void ensureConfigExists(File configFile) {
        if (!configFile.exists()) {
            instance.saveResource("fishing-rods.yml", false);
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
            rods.put(rodKey, rod);
        }
    }

    private void loadRodItemStacks() {
        rodItemStacks.clear();
        ConfigurationSection rodItemStackSection = config.getConfigurationSection("rodItemStack");
        if (rodItemStackSection == null) return;

        for (String rodKey : rodItemStackSection.getKeys(false)) {
            ItemStack rod = config.getItemStack("rodItemStack." + rodKey);
            rodItemStacks.put(rodKey, rod);
        }
    }

    private FishingRod createFishingRod(String rodKey, ConfigurationSection section) {
        String displayName = section.getString("displayName");
        String transformTargetName = section.getString("transformTargetName");
        List<String> transformCommands = section.getStringList("transformCommands");
        return new FishingRod(rodKey, displayName, transformTargetName, transformCommands);
    }

    public void addRodItemStack(String key, ItemStack rod) {
        if (key == null || rod == null) return;

        rodItemStacks.put(key, rod);

        if (config.getConfigurationSection("rodItemStack") == null) {
            config.createSection("rodItemStack");
        }

        config.set("rodItemStack." + key, rod);

        try {
            config.save(getConfigFile());
        } catch (Exception e) {
            e.printStackTrace();
            instance.getLogger().severe("Ошибка при сохранении ItemStack для удочки: " + key);
        }
    }

    public void reload() {
        loadConfig();
    }

    public Map<String, FishingRod> getRods() {
        return Collections.unmodifiableMap(rods);
    }

    public Map<String, ItemStack> getRodItemStacks() {
        return rodItemStacks;
    }

    public FishingRod getRod(String rodKey) {
        return rods.get(rodKey);
    }

    public ItemStack getRodItemStack(String rodKey) {
        return rodItemStacks.get(rodKey);
    }

    public FishingRod findRodByDisplayName(String displayName) {
        if (displayName == null) return null;
        String normalized = ChatColor.stripColor(displayName);
        return rods.values().stream()
                .filter(rod -> normalized.equals(ChatColor.stripColor(rod.getDisplayName())))
                .findFirst()
                .orElse(null);
    }

    public FishingRod findRodByItemStack(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) {
            return null;
        }
        return findRodByDisplayName(itemStack.getItemMeta().getDisplayName());
    }
}
