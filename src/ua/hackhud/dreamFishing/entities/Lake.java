package ua.hackhud.dreamFishing.entities;

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

    public Lake(String name, String displayName, String regionName, int updateTime, int minFishingTime, int maxFishingTime, List<LakeFishingRod> rods, List<String> baseCommands, LakeStatus lakeStatus) {
        this.name = name;
        this.displayName = displayName;
        this.regionName = regionName;
        this.updateTime = updateTime;
        this.minFishingTime = minFishingTime;
        this.maxFishingTime = maxFishingTime;
        this.rods = rods;
        this.baseCommands = baseCommands;
        this.lakeStatus = lakeStatus;
    }

    public String getRegionName() {
        return regionName;
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

    public LakeStatus getLakeStatus() {
        return lakeStatus;
    }
}
