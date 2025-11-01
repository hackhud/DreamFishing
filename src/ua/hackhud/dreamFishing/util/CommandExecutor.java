package ua.hackhud.dreamFishing.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ua.hackhud.dreamFishing.entities.DropItem;

import java.util.List;

public class CommandExecutor {

    private static final String PLAYER_PLACEHOLDER = "%player%";
    private static final String ITEMID_PLACEHOLDER = "%itemid%";
    private static final String ITEMNAME_PLACEHOLDER = "%itemname%";
    private static final String ITEMCOUNT_PLACEHOLDER = "%itemcount%";

    public void executeForPlayerDrop(DropItem dropItem, Player player) {
        if (dropItem.getCommands() == null || dropItem.getCommands().isEmpty()) {
            return;
        }

        for (String command : dropItem.getCommands()) {
            executeConsoleCommand(replacePlaceholders(command, player, dropItem));
        }
    }

    public void executeForPlayer(List<String> commands, Player player) {
        if (commands == null || commands.isEmpty()) {
            return;
        }

        for (String command : commands) {
            executeConsoleCommand(command.replace(PLAYER_PLACEHOLDER, player.getName()));
        }
    }

    private void executeConsoleCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    private String replacePlaceholders(String command, Player player, DropItem dropItem) {
        return command.replace(PLAYER_PLACEHOLDER, player.getName())
                .replace(ITEMID_PLACEHOLDER, String.valueOf(dropItem.getItemStack().getTypeId()))
                .replace(ITEMNAME_PLACEHOLDER, TextUtil.stripHexColors(dropItem.getItemStack().getItemMeta().getDisplayName()))
                .replace(ITEMCOUNT_PLACEHOLDER, String.valueOf(dropItem.getItemStack().getAmount()));
    }
}