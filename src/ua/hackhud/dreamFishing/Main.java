package ua.hackhud.dreamFishing;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import ua.hackhud.dreamFishing.command.DreamFishingCommand;
import ua.hackhud.dreamFishing.config.ConfigManager;
import ua.hackhud.dreamFishing.config.DropsManager;
import ua.hackhud.dreamFishing.config.RodConfigManager;
import ua.hackhud.dreamFishing.listener.FishingListener;

public class Main extends JavaPlugin {

    private static Main plugin;
    private ConfigManager configManager;
    private RodConfigManager rodConfigManager;
    private DropsManager dropsManager;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("DreamFishing " + getDescription().getVersion() + " enabled!");
        registerConfigs();
        getServer().getPluginManager().registerEvents(new FishingListener(), this);
        getCommand("dreamfishing").setExecutor(new DreamFishingCommand(this));
    }

    public static Main getPlugin() { return plugin; }

    public void registerConfigs() {
        configManager = new ConfigManager();
        rodConfigManager = new RodConfigManager();
        dropsManager = new DropsManager();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public RodConfigManager getRodConfigManager() {
        return rodConfigManager;
    }

    public DropsManager getDropsManager() {
        return dropsManager;
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
