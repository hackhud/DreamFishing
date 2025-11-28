package ua.hackhud.dreamFishing.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import ua.hackhud.dreamFishing.Main;
import ua.hackhud.dreamFishing.config.LakeConfigManager;
import ua.hackhud.dreamFishing.entities.FishingRod;
import ua.hackhud.dreamFishing.entities.Lake;
import ua.hackhud.dreamFishing.entities.LakeFishingRod;
import ua.hackhud.dreamFishing.util.MessageUtils;
import ua.hackhud.dreamFishing.util.RegionUtils;

import java.util.List;
import java.util.Objects;

public class FishingLakeService {

    private final LakeConfigManager lakeConfigManager;

    public FishingLakeService(LakeConfigManager lakeConfigManager) {
        this.lakeConfigManager = lakeConfigManager;
    }

    public Lake validateAndGetLake(Player player, FishingRod rod, PlayerFishEvent event) {
        List<String> regions = RegionUtils.getRegionsByLocation(event.getHook().getLocation());
        if (regions.isEmpty()) {
            MessageUtils.sendError(player, "Неизвестное озеро!");
            event.setCancelled(true);
            return null;
        }

        Lake lake = getLakeByRegionList(regions);
        if (lake == null) {
            MessageUtils.sendError(player, "Неизвестное озеро!");
            event.setCancelled(true);
            return null;
        }

        if (!isRodAllowed(lake, rod)) {
            MessageUtils.sendError(player, "Неподходящая удочка!");
            event.setCancelled(true);
            return null;
        }

        return lake;
    }

    private boolean isRodAllowed(Lake lake, FishingRod fishingRod) {
        return lake.getRods().stream()
                .anyMatch(lakeRod -> fishingRod.equals(lakeRod.getRod()));
    }

    private Lake getLakeByRegionList(List<String> regions) {
        return regions.stream()
                .map(region -> lakeConfigManager.getLakeByRegion(region))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}
