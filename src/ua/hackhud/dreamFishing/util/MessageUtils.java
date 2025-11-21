package ua.hackhud.dreamFishing.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class MessageUtils {

    private static final String PREFIX_INFO = "&e>> DreamFishing: ";
    private static final String PREFIX_ERROR = "&c>> DreamFishing: ";

    private MessageUtils() {}

    public static void sendRaw(CommandSender target, String message) {
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendInfo(CommandSender target, String message) {
        sendRaw(target, PREFIX_INFO + message);
    }

    public static void sendError(CommandSender target, String message) {
        sendRaw(target, PREFIX_ERROR + message);
    }
}