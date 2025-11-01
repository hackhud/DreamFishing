package ua.hackhud.dreamFishing.util;

import org.bukkit.entity.Player;
import ua.hackhud.dreamFishing.Main;

public class MessageUtils {

    public static void sendErrorMessage(Player player, String message) {
        Main.getPlugin().sendMessage(player, message);
    }
}