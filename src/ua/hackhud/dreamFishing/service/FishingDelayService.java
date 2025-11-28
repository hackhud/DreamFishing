package ua.hackhud.dreamFishing.service;

import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import ua.hackhud.dreamFishing.Main;
import ua.hackhud.dreamFishing.entities.Lake;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class FishingDelayService {

    private final Map<Player, FishingTask> activeTasks = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final Main plugin;
    private final FishingRodService rodService;

    public FishingDelayService(Main plugin, FishingRodService rodService) {
        this.plugin = plugin;
        this.rodService = rodService;
    }

    public void startCustomFishing(Player player, Fish hook, ItemStack rod, Lake lake) {
        cancelFishing(player);
        if (hook == null) {
            return;
        }
        hook.setBiteChance(0.0);
        int delay = calculateDelay(rod, lake);
        FishingTask task = new FishingTask(player, hook, rod, lake);
        task.schedule(delay);
        activeTasks.put(player, task);
    }

    public void cancelFishing(Player player) {
        FishingTask task = activeTasks.remove(player);
        if (task != null) {
            task.cancelTask();
        }
    }

    private int calculateDelay(ItemStack rod, Lake lake) {
        int minTicks = lake.getMinFishingTime() * 20;
        int maxTicks = lake.getMaxFishingTime() * 20;
        int baseDelay = minTicks + random.nextInt(maxTicks - minTicks + 1);
        int fishingSpeed = rodService.getFishingSpeed(rod);
        double modifier = 1.0 - (fishingSpeed / 100.0);
        int result = (int) (baseDelay * modifier);
        return Math.max(result, 1);
    }

    private final class FishingTask {

        private final Player player;
        private final Fish hook;
        private final ItemStack rod;
        private final Lake lake;
        private State state = State.WAIT_FOR_BITE;
        private BukkitTask scheduledTask;
        private boolean cancelled;

        private FishingTask(Player player, Fish hook, ItemStack rod, Lake lake) {
            this.player = player;
            this.hook = hook;
            this.rod = rod;
            this.lake = lake;
        }

        private void schedule(long delayTicks) {
            if (cancelled) return;
            scheduledTask = plugin.getServer().getScheduler().runTaskLater(plugin, this::tick, delayTicks);
        }

        private void tick() {
            if (cancelled || !isValidFishingState()) {
                cleanup();
                return;
            }

            switch (state) {
                case WAIT_FOR_BITE:
                    hook.setBiteChance(1.0);
                    state = State.BITE_WINDOW;
                    schedule(biteWindowTicks());
                    break;
                case BITE_WINDOW:
                    hook.setBiteChance(0.0);
                    state = State.WAIT_FOR_BITE;
                    schedule(nextBiteDelayTicks());
                    break;
            }
        }

        private void cancelTask() {
            cancelled = true;
            if (scheduledTask != null) {
                scheduledTask.cancel();
            }
            if (hook != null && hook.isValid()) {
                hook.setBiteChance(0.0);
            }
        }

        private void cleanup() {
            cancelTask();
            activeTasks.remove(player);
        }

        private boolean isValidFishingState() {
            return player.isOnline() && hook.isValid() && !hook.isDead() && !hook.isOnGround();
        }

        private long biteWindowTicks() {
            return 6 + random.nextInt(5);
        }

        private long nextBiteDelayTicks() {
            return 40 + random.nextInt(40);
        }
    }

    private enum State {
        WAIT_FOR_BITE,
        BITE_WINDOW
    }
}
