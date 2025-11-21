package ua.hackhud.dreamFishing.listener;

import org.bukkit.entity.Fish;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import ua.hackhud.dreamFishing.entities.DropItem;
import ua.hackhud.dreamFishing.entities.FishingRod;
import ua.hackhud.dreamFishing.entities.Lake;
import ua.hackhud.dreamFishing.service.FishingDelayService;
import ua.hackhud.dreamFishing.service.FishingLakeService;
import ua.hackhud.dreamFishing.service.FishingRodService;
import ua.hackhud.dreamFishing.service.FishingRewardService;

public class FishingListener implements Listener {

    private final FishingRodService fishingRodService;
    private final FishingDelayService fishingDelayService;
    private final FishingRewardService fishingRewardService;
    private final FishingLakeService fishingLakeService;

    public FishingListener(FishingRodService fishingRodService, FishingDelayService fishingDelayService, FishingRewardService fishingRewardService, FishingLakeService fishingLakeService) {
        this.fishingRodService = fishingRodService;
        this.fishingDelayService = fishingDelayService;
        this.fishingRewardService = fishingRewardService;
        this.fishingLakeService = fishingLakeService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack rodItemStack = player.getItemInHand();

        FishingRod fishingRod = fishingRodService.validateAndGetRod(player, rodItemStack, event);
        if (fishingRod == null) {
            return;
        }

        Lake lake = fishingLakeService.validateAndGetLake(player, fishingRod, event);
        if (lake == null) {
            return;
        }

        switch (event.getState()) {
            case FISHING:
                handleFishingStart(event, player, rodItemStack, lake);
                break;
            case CAUGHT_FISH:
                handleCaughtFish(event, player, rodItemStack, fishingRod, lake);
                break;
            case FAILED_ATTEMPT:
            case CAUGHT_ENTITY:
            case IN_GROUND:
                fishingDelayService.cancelFishing(player);
                break;
        }
    }

    private void handleFishingStart(PlayerFishEvent event, Player player, ItemStack rod, Lake lake) {
        Fish hook = event.getHook();
        if (hook == null) {
            return;
        }

        fishingDelayService.startCustomFishing(player, hook, rod, lake);
    }

    private void handleCaughtFish(PlayerFishEvent event, Player player, ItemStack rod, FishingRod fishingRod, Lake lake) {
        if (!(event.getCaught() instanceof Item)) {
            return;
        }

        Item caughtItem = (Item) event.getCaught();
        DropItem drop = fishingRewardService.processCatch(caughtItem, fishingRod, lake);

        fishingRodService.updateRodProgress(rod, fishingRod, player);
        fishingRewardService.executeRewards(drop, fishingRod, player, lake);
        fishingDelayService.cancelFishing(player);
    }
}