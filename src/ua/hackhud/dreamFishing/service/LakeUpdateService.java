package ua.hackhud.dreamFishing.service;

import org.bukkit.scheduler.BukkitTask;
import ua.hackhud.dreamFishing.Main;
import ua.hackhud.dreamFishing.config.LakeConfigManager;
import ua.hackhud.dreamFishing.entities.Lake;
import ua.hackhud.dreamFishing.entities.LakeStatus;
import ua.hackhud.dreamFishing.util.TextUtils;
import ua.hackhud.dreamholohandler.HoloManager;
import ua.hackhud.dreamholohandler.Hologram;

public class LakeUpdateService {

    private static final long UPDATE_PERIOD_TICKS = 20L * 10;

    private final Main instance;
    private final LakeConfigManager configManager;
    private final HoloManager holoManager;
    private BukkitTask scheduledTask;

    public LakeUpdateService(Main instance, LakeConfigManager configManager, HoloManager holoManager) {
        this.instance = instance;
        this.configManager = configManager;
        this.holoManager = holoManager;
    }

    public void start() {
        stop();
        scheduledTask = instance.getServer().getScheduler().runTaskTimer(
                instance,
                this::tickLakes,
                0L,
                UPDATE_PERIOD_TICKS
        );
    }

    public void stop() {
        if (scheduledTask != null) {
            scheduledTask.cancel();
            scheduledTask = null;
        }
    }

    private void tickLakes() {
        for (Lake lake : configManager.getLakes().values()) {
            updateLake(lake);
        }
    }

    private void updateLake(Lake lake) {
        LakeStatus lakeStatus = lake.getLakeStatus();
        boolean isNeededToUpdate = lakeStatus.getNextUpdate() < System.currentTimeMillis();
        if (isNeededToUpdate) {
            lakeStatus.setFullness(100.0D);
            lakeStatus.setNextUpdate(System.currentTimeMillis() + lake.getUpdateTime() * 1000L);
        }

        Hologram template = lake.getHologram();
        if (template == null) {
            return;
        }

        String rendered = TextUtils.replacePlaceholders(template.text, lake);
        Hologram active = holoManager.getHolograms().get(lake.getName());
        if (active != null) {
            holoManager.updateHologram(lake.getName(), active, template.scale, rendered);
        } else {
            Hologram clone = new Hologram(template.x, template.y, template.z, template.scale, rendered, template.dimensionId, -1);
            holoManager.createHologram(lake.getName(), clone);
        }
    }

}
