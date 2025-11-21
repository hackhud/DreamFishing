package ua.hackhud.dreamFishing.entities;

import java.util.List;

public class LakeFishingRod {

    private final FishingRod rod;
    private final String dropTable;
    private final List<String> commands;

    public LakeFishingRod(FishingRod rod, String dropTable, List<String> commands) {
        this.rod = rod;
        this.dropTable = dropTable;
        this.commands = commands;
    }

    public FishingRod getRod() {
        return rod;
    }

    public String getDropTable() {
        return dropTable;
    }

    public List<String> getCommands() {
        return commands;
    }
}