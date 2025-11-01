package ua.hackhud.dreamFishing.service;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import ua.hackhud.dreamFishing.Main;
import ua.hackhud.dreamFishing.entities.DropItem;
import ua.hackhud.dreamFishing.entities.FishingRod;
import ua.hackhud.dreamFishing.util.CommandExecutor;

import java.util.List;

public class FishingRewardService {

    private final CommandExecutor commandExecutor;

    public FishingRewardService() {
        this.commandExecutor = new CommandExecutor();
    }

    public DropItem processCatch(Item caughtItem, FishingRod rod) {
        DropItem drop = Main.getPlugin().getDropsManager().getRandomDrop(rod.getDropTable());
        if (drop != null) {
            caughtItem.setItemStack(drop.getItemStack().clone());
        }
        return drop;
    }

    public void executeRewards(DropItem drop, FishingRod rod, Player player) {
        if (drop != null) {
            executeDropCommands(drop, player);
        }
        executeRodCommands(rod.getBaseCommands(), player);
    }

    private void executeDropCommands(DropItem drop, Player player) {
        commandExecutor.executeForPlayerDrop(drop, player);
    }

    private void executeRodCommands(List<String> commands, Player player) {
        commandExecutor.executeForPlayer(commands, player);
    }
}