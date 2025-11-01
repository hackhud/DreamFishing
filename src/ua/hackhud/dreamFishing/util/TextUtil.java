package ua.hackhud.dreamFishing.util;

public final class TextUtil {

    private TextUtil() {
    }

    public static String stripHexColors(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.replaceAll("#[A-Fa-f0-9]{6}", "");
    }
}