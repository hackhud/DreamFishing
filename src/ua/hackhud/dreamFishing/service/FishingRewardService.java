package ua.hackhud.dreamFishing.service;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import ua.hackhud.dreamFishing.config.DropsManager;
import ua.hackhud.dreamFishing.entities.DropItem;
import ua.hackhud.dreamFishing.entities.FishingRod;
import ua.hackhud.dreamFishing.entities.Lake;
import ua.hackhud.dreamFishing.entities.LakeFishingRod;
import ua.hackhud.dreamFishing.util.ServerCommandExecutor;

import java.util.List;

public class FishingRewardService {

    private final ServerCommandExecutor serverCommandExecutor;
    private final DropsManager dropsManager;

    public FishingRewardService(ServerCommandExecutor serverCommandExecutor, DropsManager dropsManager) {
        this.serverCommandExecutor = serverCommandExecutor;
        this.dropsManager = dropsManager;
    }

    public DropItem processCatch(Item caughtItem, FishingRod rod, Lake lake) {
        LakeFishingRod lakeFishingRod = getLakeFishingRod(rod, lake);
        if (lakeFishingRod == null) {
            return null;
        }

        DropItem drop = dropsManager.getRandomDrop(
                lakeFishingRod.getDropTable(),
                lake.getLakeStatus().getFullness()
        );
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
        serverCommandExecutor.executeForPlayerDrop(drop, player);
    }

    private void executeRodCommands(List<String> commands, Player player) {
        serverCommandExecutor.executeForPlayer(commands, player);
    }
}
