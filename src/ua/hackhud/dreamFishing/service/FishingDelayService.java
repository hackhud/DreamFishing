package ua.hackhud.dreamFishing.service;

import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ua.hackhud.dreamFishing.Main;
import ua.hackhud.dreamFishing.entities.Lake;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class FishingDelayService {

    private final Map<Player, FishingTask> activeTasks;
    private final Random random;
    private final Main instance;
    private final FishingRodService rodService;

    public FishingDelayService(Main instance, FishingRodService fishingRodService) {
        this.activeTasks = new ConcurrentHashMap<>();
        this.random = new Random();
        this.rodService = fishingRodService;
        this.instance = instance;
    }

    public void startCustomFishing(Player player, Fish hook, ItemStack rod, Lake lake) {
        cancelExistingTask(player);

        hook.setBiteChance(0.0);

        int delay = calculateDelay(rod, lake);
        FishingTask task = new FishingTask(player, hook, rod, lake);

        task.runTaskLater(instance, delay);
        activeTasks.put(player, task);
    }

    public void cancelFishing(Player player) {
        FishingTask task = activeTasks.remove(player);
        if (task != null) {
            task.cancel();
        }
    }

    private void cancelExistingTask(Player player) {
        cancelFishing(player);
    }

    private int calculateDelay(ItemStack rod, Lake lake) {
        int baseDelay = generateRandomDelay(lake);
        int fishingSpeed = rodService.getFishingSpeed(rod);
        return applySpeedModifier(baseDelay, fishingSpeed);
    }

    private int generateRandomDelay(Lake lake) {
        int minTicks = lake.getMinFishingTime() * 20;
        int maxTicks = lake.getMaxFishingTime() * 20;
        return minTicks + random.nextInt(maxTicks - minTicks + 1);
    }

    private int applySpeedModifier(int baseDelay, int speedPercentage) {
        double modifier = 1.0 - (speedPercentage / 100.0);
        return (int) (baseDelay * modifier);
    }

    private class FishingTask extends BukkitRunnable {
        private final Player player;
        private final Fish hook;
        private final ItemStack rod;
        private final Lake lake;

        public FishingTask(Player player, Fish hook, ItemStack rod, Lake lake) {
            this.player = player;
            this.hook = hook;
            this.rod = rod;
            this.lake = lake;
        }

        @Override
        public void run() {
            if (!isValidFishingState()) {
                activeTasks.remove(player);
                return;
            }

            hook.setBiteChance(1.0);

            int biteDuration = 6 + random.nextInt(5);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (isValidFishingState()) {
                        hook.setBiteChance(0.0);

                        int nextBiteDelay = 40 + random.nextInt(40);
                        FishingTask.this.runTaskLater(instance, nextBiteDelay);
                    } else {
                        activeTasks.remove(player);
                    }
                }
            }.runTaskLater(instance, biteDuration);
        }

        private boolean isValidFishingState() {
            return player.isOnline() &&
                    !hook.isDead() &&
                    !hook.isOnGround() &&
                    hook.isValid();
        }
    }
}