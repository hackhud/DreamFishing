package ua.hackhud.dreamFishing.service;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import ua.hackhud.dreamFishing.Main;
import ua.hackhud.dreamFishing.entities.FishingRod;
import ua.hackhud.dreamFishing.entities.RodProgress;
import ua.hackhud.dreamFishing.util.CommandExecutor;
import ua.hackhud.dreamFishing.util.ItemStackUtils;
import ua.hackhud.dreamFishing.util.LoreParser;
import ua.hackhud.dreamFishing.util.MessageUtils;

public class FishingRodService {

    private final LoreParser loreParser;
    private final CommandExecutor commandExecutor;

    public FishingRodService() {
        this.loreParser = new LoreParser();
        this.commandExecutor = new CommandExecutor();
    }

    public FishingRod validateAndGetRod(Player player, ItemStack rod, PlayerFishEvent event) {
        if (!isValidRodItem(rod)) {
            MessageUtils.sendErrorMessage(player, "&c>> Неправильная удочка!");
            event.setCancelled(true);
            return null;
        }

        String rodName = rod.getItemMeta().getDisplayName();
        FishingRod fishingRod = Main.getPlugin().getRodConfigManager().getRod(rodName);

        if (fishingRod == null) {
            MessageUtils.sendErrorMessage(player, "&c>> Неизвестная удочка!");
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
            ItemStack transformedRod = Main.getPlugin().getRodConfigManager()
                    .getRodItemStack(fishingRod.getTransformTargetName());
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
}