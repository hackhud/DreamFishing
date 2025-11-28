package ua.hackhud.dreamFishing;

import org.bukkit.plugin.java.JavaPlugin;
import ua.hackhud.dreamFishing.command.DreamFishingCommand;
import ua.hackhud.dreamFishing.config.ConfigManager;
import ua.hackhud.dreamFishing.config.DropsManager;
import ua.hackhud.dreamFishing.config.LakeConfigManager;
import ua.hackhud.dreamFishing.config.RodConfigManager;
import ua.hackhud.dreamFishing.hologram.HologramInitializer;
import ua.hackhud.dreamFishing.listener.FishingListener;
import ua.hackhud.dreamFishing.service.FishingDelayService;
import ua.hackhud.dreamFishing.service.FishingLakeService;
import ua.hackhud.dreamFishing.service.FishingRewardService;
import ua.hackhud.dreamFishing.service.FishingRodService;
import ua.hackhud.dreamFishing.service.LakeUpdateService;
import ua.hackhud.dreamFishing.util.CommandExecutor;
import ua.hackhud.dreamFishing.util.LoreParser;
import ua.hackhud.dreamholohandler.HoloManager;

public class Main extends JavaPlugin {

    private static Main instance;
    private ConfigManager configManager;
    private RodConfigManager rodConfigManager;
    private DropsManager dropsManager;
    private LakeConfigManager lakeConfigManager;

    private LoreParser loreParser;
    private CommandExecutor commandExecutor;
    private FishingRodService fishingRodService;
    private FishingDelayService fishingDelayService;
    private FishingRewardService fishingRewardService;
    private FishingLakeService fishingLakeService;
    private LakeUpdateService lakeUpdateService;

    private HologramInitializer hologramInitializer;
    private HoloManager holoManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("DreamFishing " + getDescription().getVersion() + " enabled!");
        registerConfigs();
        initServices();
        initHolograms();
        initLakeUpdater();
        getServer().getPluginManager().registerEvents(new FishingListener(fishingRodService, fishingDelayService, fishingRewardService, fishingLakeService), this);
        getCommand("dreamfishing").setExecutor(new DreamFishingCommand(this));
    }

    @Override
    public void onDisable() {
        if (lakeUpdateService != null) {
            lakeUpdateService.stop();
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
        commandExecutor = new CommandExecutor();
        fishingRodService = new FishingRodService(loreParser, commandExecutor, rodConfigManager);
        fishingDelayService = new FishingDelayService(instance, fishingRodService);
        fishingRewardService = new FishingRewardService(commandExecutor, dropsManager);
        fishingLakeService = new FishingLakeService(lakeConfigManager);
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

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
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
