package ua.hackhud.dreamFishing.service;

import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ua.hackhud.dreamFishing.Main;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class FishingDelayService {

    private final Map<Player, FishingTask> activeTasks;
    private final Random random;
    private final FishingRodService rodService;

    public FishingDelayService() {
        this.activeTasks = new ConcurrentHashMap<>();
        this.random = new Random();
        this.rodService = new FishingRodService();
    }

    public void startCustomFishing(Player player, Fish hook, ItemStack rod) {
        cancelExistingTask(player);

        hook.setBiteChance(0.0);

        int delay = calculateDelay(rod);
        FishingTask task = new FishingTask(player, hook);

        task.runTaskLater(Main.getPlugin(), delay);
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

    private int calculateDelay(ItemStack rod) {
        int baseDelay = generateRandomDelay();
        int fishingSpeed = rodService.getFishingSpeed(rod);
        return applySpeedModifier(baseDelay, fishingSpeed);
    }

    private int generateRandomDelay() {
        int minTicks = Main.getPlugin().getConfigManager().getMinFishingTiming() * 20;
        int maxTicks = Main.getPlugin().getConfigManager().getMaxFishingTiming() * 20;
        return minTicks + random.nextInt(maxTicks - minTicks + 1);
    }

    private int applySpeedModifier(int baseDelay, int speedPercentage) {
        double modifier = 1.0 - (speedPercentage / 100.0);
        return (int) (baseDelay * modifier);
    }

    private class FishingTask extends BukkitRunnable {
        private final Player player;
        private final Fish hook;

        public FishingTask(Player player, Fish hook) {
            this.player = player;
            this.hook = hook;
        }

        @Override
        public void run() {
            if (!isValidFishingState()) {
                activeTasks.remove(player);
                return;
            }

            hook.setBiteChance(1.0);
            activeTasks.remove(player);
        }

        private boolean isValidFishingState() {
            return player.isOnline() &&
                    !hook.isDead() &&
                    !hook.isOnGround();
        }
    }
}