package ua.hackhud.dreamFishing.entities;

import java.util.List;

public class FishingRod {

    private final String name;
    private final String displayName;
    private final String transformTargetName;
    private final List<String> transformCommands;

    public FishingRod(String name, String displayName, String transformTargetName, List<String> transformCommands) {
        this.name = name;
        this.displayName = displayName;
        this.transformTargetName = transformTargetName;
        this.transformCommands = transformCommands;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTransformTargetName() {
        return transformTargetName;
    }

    public List<String> getTransformCommands() {
        return transformCommands;
    }
}
