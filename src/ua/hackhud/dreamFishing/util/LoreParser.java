package ua.hackhud.dreamFishing.util;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ua.hackhud.dreamFishing.Main;
import ua.hackhud.dreamFishing.entities.RodProgress;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoreParser {

    private static final String CURRENT_KEY = "current";
    private static final String NEEDED_KEY = "needed";
    private static final String VALUE_KEY = "value";
    private static final String NUMBER_PATTERN = "(\\d+)";

    public RodProgress extractProgress(ItemStack rod) {
        List<String> lore = rod.getItemMeta().getLore();
        if (lore == null) {
            return RodProgress.empty();
        }

        String progressPattern = Main.getPlugin().getConfigManager().getFishingProgressLore();
        if (isEmptyPattern(progressPattern)) {
            return RodProgress.empty();
        }

        Pattern regex = createRegexPattern(progressPattern, CURRENT_KEY, NEEDED_KEY);

        for (String line : lore) {
            RodProgress progress = parseProgressLine(line, regex);
            if (progress != null) {
                return progress;
            }
        }

        return RodProgress.empty();
    }

    public int extractFishingSpeed(ItemStack rod) {
        List<String> lore = rod.getItemMeta().getLore();
        if (lore == null) {
            return 0;
        }

        String speedPattern = Main.getPlugin().getConfigManager().getFishingSpeedLore();
        if (isEmptyPattern(speedPattern)) {
            return 0;
        }

        Pattern regex = createRegexPattern(speedPattern, VALUE_KEY);

        for (String line : lore) {
            Integer speed = parseSpeedLine(line, regex);
            if (speed != null) {
                return speed;
            }
        }

        return 0;
    }

    public void updateProgress(ItemStack rod, RodProgress progress) {
        ItemMeta meta = rod.getItemMeta();
        List<String> lore = meta.getLore();

        String progressPattern = Main.getPlugin().getConfigManager().getFishingProgressLore();
        if (isEmptyPattern(progressPattern)) {
            return;
        }

        Pattern regex = createRegexPattern(progressPattern, CURRENT_KEY, NEEDED_KEY);

        for (int i = 0; i < lore.size(); i++) {
            if (regex.matcher(lore.get(i)).find()) {
                lore.set(i, formatProgressLine(progressPattern, progress));
                meta.setLore(lore);
                rod.setItemMeta(meta);
                return;
            }
        }
    }

    private RodProgress parseProgressLine(String line, Pattern regex) {
        Matcher matcher = regex.matcher(colorize(line));
        if (matcher.find()) {
            try {
                int current = Integer.parseInt(matcher.group(1));
                int needed = Integer.parseInt(matcher.group(2));
                return new RodProgress(current, needed);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private Integer parseSpeedLine(String line, Pattern regex) {
        Matcher matcher = regex.matcher(colorize(line));
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private String formatProgressLine(String pattern, RodProgress progress) {
        return colorize(pattern
                .replace("{" + CURRENT_KEY + "}", String.valueOf(progress.getCurrent()))
                .replace("{" + NEEDED_KEY + "}", String.valueOf(progress.getNeeded())));
    }

    private Pattern createRegexPattern(String pattern, String... placeholders) {
        String regex = escapeRegexSpecialChars(colorize(pattern));

        for (String placeholder : placeholders) {
            regex = regex.replace("{" + placeholder + "}", NUMBER_PATTERN);
        }

        return Pattern.compile(regex);
    }

    private String escapeRegexSpecialChars(String text) {
        String[] specialChars = {"\\", ".", "^", "$", "|", "?", "*", "+", "(", ")", "[", "]"};
        String result = text;
        for (String specialChar : specialChars) {
            result = result.replace(specialChar, "\\" + specialChar);
        }
        return result;
    }

    private String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private boolean isEmptyPattern(String pattern) {
        return pattern == null || pattern.isEmpty();
    }
}