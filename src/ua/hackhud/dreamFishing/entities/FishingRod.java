package ua.hackhud.dreamFishing.entities;

import java.util.List;

public class FishingRod {
    private final String name;
    private final String displayName;
    private final String dropTable;
    private final String transformTargetName;
    private final List<String> transformCommands;
    private final List<String> baseCommands;

    public FishingRod(String name, String displayName, String dropTable, String transformTargetName, List<String> transformCommands, List<String> baseCommands) {
        this.name = name;
        this.displayName = displayName;
        this.dropTable = dropTable;
        this.transformTargetName = transformTargetName;
        this.transformCommands = transformCommands;
        this.baseCommands = baseCommands;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDropTable() {
        return dropTable;
    }

    public String getTransformTargetName() {
        return transformTargetName;
    }

    public List<String> getTransformCommands() {
        return transformCommands;
    }

    public List<String> getBaseCommands() {
        return baseCommands;
    }
}