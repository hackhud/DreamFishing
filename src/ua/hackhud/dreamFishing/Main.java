package ua.hackhud.dreamFishing;

import org.bukkit.plugin.java.JavaPlugin;
import ua.hackhud.dreamFishing.command.DreamFishingCommand;
import ua.hackhud.dreamFishing.config.ConfigManager;
import ua.hackhud.dreamFishing.config.DropsManager;
import ua.hackhud.dreamFishing.config.LakeConfigManager;
import ua.hackhud.dreamFishing.config.RodConfigManager;
import org.bukkit.event.HandlerList;
import ua.hackhud.dreamFishing.hologram.HologramInitializer;
import ua.hackhud.dreamFishing.listener.FishingListener;
import ua.hackhud.dreamFishing.service.*;
import ua.hackhud.dreamFishing.util.ServerCommandExecutor;
import ua.hackhud.dreamFishing.util.LoreParser;
import ua.hackhud.dreamholohandler.HoloManager;

public class Main extends JavaPlugin {

    private static Main instance;
    private ConfigManager configManager;
    private RodConfigManager rodConfigManager;
    private DropsManager dropsManager;
    private LakeConfigManager lakeConfigManager;

    private LoreParser loreParser;
    private ServerCommandExecutor serverCommandExecutor;
    private FishingRodService fishingRodService;
    private FishingDelayService fishingDelayService;
    private FishingRewardService fishingRewardService;
    private FishingLakeService fishingLakeService;
    private LakeUpdateService lakeUpdateService;
    private LakeFullnessService lakeFullnessService;
    private FishingListener fishingListener;

    private HologramInitializer hologramInitializer;
    private HoloManager holoManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("DreamFishing " + getDescription().getVersion() + " enabled!");
        initPlugin();
        getCommand("dreamfishing").setExecutor(new DreamFishingCommand(this));
    }

    @Override
    public void onDisable() {
        shutdownPlugin();
    }

    public void initPlugin() {
        shutdownPlugin();
        registerConfigs();
        initServices();
        initHolograms();
        initLakeUpdater();
        registerListeners();
    }

    private void shutdownPlugin() {
        if (lakeUpdateService != null) {
            lakeUpdateService.stop();
        }
        if (fishingDelayService != null) {
            fishingDelayService.cancelAll();
        }
        if (fishingListener != null) {
            HandlerList.unregisterAll(fishingListener);
            fishingListener = null;
        }
    }

    public void registerConfigs() {
        configManager = new ConfigManager(instance);
        rodConfigManager = new RodConfigManager(instance);
        dropsManager = new DropsManager(instance);
        lakeConfigManager = new LakeConfigManager(instance, rodConfigManager);
    }

    public void initServices() {
        loreParser = new LoreParser(configManager);
        serverCommandExecutor = new ServerCommandExecutor();
        fishingRodService = new FishingRodService(loreParser, serverCommandExecutor, rodConfigManager);
        fishingDelayService = new FishingDelayService(instance, fishingRodService);
        fishingRewardService = new FishingRewardService(serverCommandExecutor, dropsManager);
        fishingLakeService = new FishingLakeService(lakeConfigManager);
        lakeFullnessService = new LakeFullnessService();
    }

    public void initHolograms() {
        holoManager = ua.hackhud.dreamholohandler.Main.getPlugin().getHoloManager();
        hologramInitializer = new HologramInitializer(holoManager);
        hologramInitializer.initLakeHolograms(lakeConfigManager.getLakes());
    }

    public void initLakeUpdater() {
        lakeUpdateService = new LakeUpdateService(instance, lakeConfigManager, holoManager);
        lakeUpdateService.start();
    }

    public void registerListeners() {
        fishingListener = new FishingListener(fishingRodService, fishingDelayService, fishingRewardService, fishingLakeService, lakeFullnessService);
        getServer().getPluginManager().registerEvents(fishingListener, this);
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

    public LakeConfigManager getLakeConfigManager() {
        return lakeConfigManager;
    }

    public LoreParser getLoreParser() {
        return loreParser;
    }

    public ServerCommandExecutor getServerCommandExecutor() {
        return serverCommandExecutor;
    }

    public FishingRodService getFishingRodService() {
        return fishingRodService;
    }

    public FishingDelayService getFishingDelayService() {
        return fishingDelayService;
    }

    public FishingRewardService getFishingRewardService() {
        return fishingRewardService;
    }

    public FishingLakeService getFishingLakeService() {
        return fishingLakeService;
    }

    public LakeUpdateService getLakeUpdateService() {
        return lakeUpdateService;
    }

}
