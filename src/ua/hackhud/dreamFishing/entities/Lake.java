package ua.hackhud.dreamFishing.entities;

import ua.hackhud.dreamholohandler.Hologram;

import java.util.List;

public class Lake {
    private final String name;
    private final String displayName;
    private final String regionName;
    private final int updateTime;
    private final int minFishingTime;
    private final int maxFishingTime;
    private final List<LakeFishingRod> rods;
    private final List<String> baseCommands;
    private final LakeStatus lakeStatus;
    private final Hologram hologram;

    public Lake(String name, String displayName, String regionName, int updateTime, int minFishingTime, int maxFishingTime, List<LakeFishingRod> rods, List<String> baseCommands, LakeStatus lakeStatus, Hologram hologram) {
        this.name = name;
        this.displayName = displayName;
        this.regionName = regionName;
        this.updateTime = updateTime;
        this.minFishingTime = minFishingTime;
        this.maxFishingTime = maxFishingTime;
        this.rods = rods;
        this.baseCommands = baseCommands;
        this.lakeStatus = lakeStatus;
        this.hologram = hologram;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRegionName() {
        return regionName;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public int getMinFishingTime() {
        return minFishingTime;
    }

    public int getMaxFishingTime() {
        return maxFishingTime;
    }

    public List<LakeFishingRod> getRods() {
        return rods;
    }

    public List<String> getBaseCommands() {
        return baseCommands;
    }

    public LakeStatus getLakeStatus() {
        return lakeStatus;
    }

    public Hologram getHologram() {
        return hologram;
    }
}
