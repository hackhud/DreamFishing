package ua.hackhud.dreamFishing.util;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegionUtils {

    public static List<String> getRegionsByLocation(Location location) {
        WorldGuardPlugin worldGuardPlugin = WorldGuardPlugin.inst();
        if (worldGuardPlugin == null || !worldGuardPlugin.isEnabled()) {
            return Collections.emptyList();
        }

        if (location == null || location.getWorld() == null) {
            return Collections.emptyList();
        }

        try {
            RegionManager regionManager = worldGuardPlugin.getRegionManager(location.getWorld());
            if (regionManager == null) {
                return Collections.emptyList();
            }

            ApplicableRegionSet applicableRegions = regionManager.getApplicableRegions(location);

            List<String> regionList = new ArrayList<>();
            for (ProtectedRegion region : applicableRegions) {
                regionList.add(region.getId());
            }

            return regionList;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
