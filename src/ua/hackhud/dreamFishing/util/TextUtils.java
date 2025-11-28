package ua.hackhud.dreamFishing.util;

import ua.hackhud.dreamFishing.entities.Lake;

public final class TextUtils {

    public static String stripHexColors(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.replaceAll("#[A-Fa-f0-9]{6}", "");
    }

    public static String replacePlaceholders(String text, Lake lake) {
        if (text == null) {
            return "";
        }

        long millisLeft = lake.getLakeStatus().getNextUpdate() - System.currentTimeMillis();
        long minutesLeft = Math.max(millisLeft / 60000L, 0);

        return text.replace("{lakeName}", lake.getDisplayName()).
                replace("{fullness}", chooseFullnessColorPrefix(lake.getLakeStatus().getFullness()) + String.format("%.2f", lake.getLakeStatus().getFullness())).
                replace("{minFishingTime}", String.valueOf(lake.getMinFishingTime())).
                replace("{maxFishingTime}", String.valueOf(lake.getMaxFishingTime())).
                replace("{minBeforeUpdate}", String.valueOf(minutesLeft)).
                replaceAll("&", "§");
    }

    private static String chooseFullnessColorPrefix(double fullnessValue) {
        int group = (int) fullnessValue / 20;

        switch (group) {
            case 4: return "&a";
            case 3: return "&e";
            case 2: return "&6";
            case 1: return "&c";
            case 0: return "&4";
            default: return "&a";
        }
    }

}
