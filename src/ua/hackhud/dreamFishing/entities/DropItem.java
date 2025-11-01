package ua.hackhud.dreamFishing.entities;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DropItem {

    private final ItemStack itemStack;
    private final double weight;
    private final List<String> commands;

    public DropItem(ItemStack itemStack, double weight, List<String> commands) {
        this.itemStack = itemStack.clone();
        this.weight = weight;
        this.commands = commands;
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    public double getWeight() {
        return weight;
    }

    public List<String> getCommands() {
        return commands;
    }

}
