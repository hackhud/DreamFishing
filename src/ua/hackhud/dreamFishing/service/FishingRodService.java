package ua.hackhud.dreamFishing.service;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import ua.hackhud.dreamFishing.config.RodConfigManager;
import ua.hackhud.dreamFishing.entities.FishingRod;
import ua.hackhud.dreamFishing.entities.RodProgress;
import ua.hackhud.dreamFishing.util.CommandExecutor;
import ua.hackhud.dreamFishing.util.ItemStackUtils;
import ua.hackhud.dreamFishing.util.LoreParser;
import ua.hackhud.dreamFishing.util.MessageUtils;

public class FishingRodService {

    private final LoreParser loreParser;
    private final CommandExecutor commandExecutor;
    private final RodConfigManager rodConfigManager;

    public FishingRodService(LoreParser loreParser, CommandExecutor commandExecutor, RodConfigManager rodConfigManager) {
        this.loreParser = loreParser;
        this.commandExecutor = commandExecutor;
        this.rodConfigManager = rodConfigManager;
    }

    public FishingRod validateAndGetRod(Player player, ItemStack rod, PlayerFishEvent event) {
        if (!isValidRodItem(rod)) {
            MessageUtils.sendError(player, "Неправильная удочка!");
            event.setCancelled(true);
            return null;
        }

        FishingRod fishingRod = resolveRod(rod);

        if (fishingRod == null) {
            MessageUtils.sendError(player, "Неизвестная удочка!");
            event.setCancelled(true);
            return null;
        }

        return fishingRod;
    }

    public void updateRodProgress(ItemStack rod, FishingRod fishingRod, Player player) {
        if (!ItemStackUtils.hasLore(rod)) {
            return;
        }

        RodProgress progress = loreParser.extractProgress(rod);
        RodProgress newProgress = progress.increment();
        loreParser.updateProgress(rod, newProgress);

        if (newProgress.isComplete()) {
            ItemStack transformedRod = rodConfigManager.getRodItemStack(fishingRod.getTransformTargetName());
            if (transformedRod != null) {
                rod.setItemMeta(transformedRod.getItemMeta());
                commandExecutor.executeForPlayer(fishingRod.getTransformCommands(), player);
            }
        }
    }

    public int getFishingSpeed(ItemStack rod) {
        if (!ItemStackUtils.hasLore(rod)) {
            return 0;
        }
        return loreParser.extractFishingSpeed(rod);
    }

    private boolean isValidRodItem(ItemStack rod) {
        return rod != null &&
                rod.hasItemMeta() &&
                rod.getItemMeta().hasDisplayName();
    }

    private FishingRod resolveRod(ItemStack rodItem) {
        String displayName = rodItem.getItemMeta().getDisplayName();

        FishingRod fishingRod = rodConfigManager.getRod(displayName);
        if (fishingRod != null) return fishingRod;

        fishingRod = rodConfigManager.findRodByDisplayName(displayName);
        if (fishingRod != null) return fishingRod;

        return rodConfigManager.findRodByItemStack(rodItem);
    }
}
