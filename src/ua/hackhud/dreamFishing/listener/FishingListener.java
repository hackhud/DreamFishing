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
import ua.hackhud.dreamFishing.service.FishingDelayService;
import ua.hackhud.dreamFishing.service.FishingRodService;
import ua.hackhud.dreamFishing.service.FishingRewardService;

public class FishingListener implements Listener {

    private final FishingRodService rodService;
    private final FishingDelayService delayService;
    private final FishingRewardService rewardService;

    public FishingListener() {
        this.rodService = new FishingRodService();
        this.delayService = new FishingDelayService();
        this.rewardService = new FishingRewardService();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack rodItemStack = player.getItemInHand();

        FishingRod fishingRod = rodService.validateAndGetRod(player, rodItemStack, event);
        if (fishingRod == null) {
            return;
        }

        switch (event.getState()) {
            case FISHING:
                handleFishingStart(event, player, rodItemStack, fishingRod);
                break;
            case CAUGHT_FISH:
                handleCaughtFish(event, player, rodItemStack, fishingRod);
                break;
            case FAILED_ATTEMPT:
            case CAUGHT_ENTITY:
            case IN_GROUND:
                delayService.cancelFishing(player);
                break;
        }
    }

    private void handleFishingStart(PlayerFishEvent event, Player player, ItemStack rod, FishingRod fishingRod) {
        Fish hook = event.getHook();
        if (hook == null) {
            return;
        }

        delayService.startCustomFishing(player, hook, rod);
    }

    private void handleCaughtFish(PlayerFishEvent event, Player player, ItemStack rod, FishingRod fishingRod) {
        if (!(event.getCaught() instanceof Item)) {
            return;
        }

        Item caughtItem = (Item) event.getCaught();
        DropItem drop = rewardService.processCatch(caughtItem, fishingRod);

        rodService.updateRodProgress(rod, fishingRod, player);
        rewardService.executeRewards(drop, fishingRod, player);
        delayService.cancelFishing(player);
    }
}