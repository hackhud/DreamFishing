package ua.hackhud.dreamFishing.util;

import org.bukkit.inventory.ItemStack;

public class ItemStackUtils {

    public static boolean hasLore(ItemStack item) {
        return item != null &&
                item.hasItemMeta() &&
                item.getItemMeta().hasLore();
    }
}