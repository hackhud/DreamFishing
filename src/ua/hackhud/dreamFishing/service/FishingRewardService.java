package ua.hackhud.dreamFishing.service;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import ua.hackhud.dreamFishing.Main;
import ua.hackhud.dreamFishing.config.DropsManager;
import ua.hackhud.dreamFishing.entities.DropItem;
import ua.hackhud.dreamFishing.entities.FishingRod;
import ua.hackhud.dreamFishing.entities.Lake;
import ua.hackhud.dreamFishing.entities.LakeFishingRod;
import ua.hackhud.dreamFishing.util.CommandExecutor;

import java.util.List;

public class FishingRewardService {

    private final CommandExecutor commandExecutor;
    private final DropsManager dropsManger;

    public FishingRewardService(CommandExecutor commandExecutor, DropsManager dropsManager) {
        this.commandExecutor = commandExecutor;
        this.dropsManger = dropsManager;
    }

    public DropItem processCatch(Item caughtItem, FishingRod rod, Lake lake) {
        LakeFishingRod lakeFishingRod = getLakeFishingRod(rod, lake);
        DropItem drop = dropsManger.getRandomDrop(lakeFishingRod.getDropTable());
        if (drop != null) {
            caughtItem.setItemStack(drop.getItemStack().clone());
        }
        return drop;
    }

    private LakeFishingRod getLakeFishingRod(FishingRod rod, Lake lake) {
        for (LakeFishingRod lakeRod : lake.getRods()) {
            if (rod.equals(lakeRod.getRod())) {
                return lakeRod;
            }
        }
        return null;
    }

    public void executeRewards(DropItem drop, FishingRod rod, Player player, Lake lake) {
        if (drop != null) {
            executeDropCommands(drop, player);
        }

        LakeFishingRod lakeRod = getLakeFishingRod(rod, lake);
        executeRodCommands(lakeRod.getCommands(), player);
    }

    private void executeDropCommands(DropItem drop, Player player) {
        commandExecutor.executeForPlayerDrop(drop, player);
    }

    private void executeRodCommands(List<String> commands, Player player) {
        commandExecutor.executeForPlayer(commands, player);
    }
}