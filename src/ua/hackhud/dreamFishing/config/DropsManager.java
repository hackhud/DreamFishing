package ua.hackhud.dreamFishing.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import ua.hackhud.dreamFishing.entities.DropItem;
import ua.hackhud.dreamFishing.Main;
import ua.hackhud.dreamFishing.entities.WeightedDropTable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DropsManager {
    private final Map<String, WeightedDropTable> weightedTables = new HashMap<>();
    private final Main instance;

    public DropsManager(Main instance) {
        this.instance = instance;
        loadDrops();
    }

    private void loadDrops() {
        weightedTables.clear();
        File dropsFolder = getDropsFolder();
        ensureDropsFolderExists(dropsFolder);

        File[] files = dropsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            loadDropFile(file);
        }
    }

    private void loadDropFile(File file) {
        String dropTableName = file.getName().replace(".yml", "");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection dropsSection = config.getConfigurationSection("drops");
        if (dropsSection == null) return;

        List<DropItem> dropItems = new ArrayList<>();
        for (String key : dropsSection.getKeys(false)) {
            ConfigurationSection dropSection = dropsSection.getConfigurationSection(key);
            if (dropSection == null) continue;

            DropItem dropItem = createDropItem(dropSection);
            if (dropItem != null) {
                dropItems.add(dropItem);
            }
        }

        weightedTables.put(dropTableName, new WeightedDropTable(dropItems));
    }

    private DropItem createDropItem(ConfigurationSection section) {
        ItemStack itemStack = section.getItemStack("itemStack");
        if (itemStack == null) return null;

        double weight = section.getDouble("weight");
        List<String> commands = section.getStringList("commands");

        return new DropItem(itemStack, weight, commands);
    }

    private File getDropsFolder() {
        return new File(instance.getDataFolder(), "drops");
    }

    private void ensureDropsFolderExists(File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public DropItem getRandomDrop(String tableName) {
        WeightedDropTable table = weightedTables.get(tableName);
        if (table == null) return null;
        return table.getRandomDrop();
    }

    public boolean addItemToDropTable(String tableName, ItemStack itemStack, double weight, List<String> commands) {
        if (itemStack == null || tableName == null || tableName.isEmpty()) {
            return false;
        }

        WeightedDropTable table = weightedTables.get(tableName);
        if (table == null) {
            table = new WeightedDropTable(new ArrayList<>());
            weightedTables.put(tableName, table);
        }

        DropItem dropItem = new DropItem(itemStack, weight, commands);
        table.addDrop(dropItem);

        return saveDropTable(tableName);
    }

    public boolean addItemToDropTable(String tableName, ItemStack itemStack, double weight) {
        return addItemToDropTable(tableName, itemStack, weight, new ArrayList<>());
    }

    private boolean saveDropTable(String tableName) {
        WeightedDropTable table = weightedTables.get(tableName);
        if (table == null) return false;

        File dropsFolder = getDropsFolder();
        ensureDropsFolderExists(dropsFolder);

        File file = new File(dropsFolder, tableName + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("drops", null);
        ConfigurationSection dropsSection = config.createSection("drops");

        List<DropItem> drops = table.getItems();
        for (int i = 0; i < drops.size(); i++) {
            DropItem drop = drops.get(i);
            ConfigurationSection dropSection = dropsSection.createSection("drop_" + (i + 1));
            dropSection.set("itemStack", drop.getItemStack());
            dropSection.set("weight", drop.getWeight());
            dropSection.set("commands", drop.getCommands());
        }

        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void reloadDropTables() {
        loadDrops();
    }

    public Set<String> getDropTableNames() {
        return new HashSet<>(weightedTables.keySet());
    }
}
