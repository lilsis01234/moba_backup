package util;

public final class FormatUtils {
    private FormatUtils() {}

    public static String formatNumber(int num) {
        if (num >= 1_000_000) return String.format("%.1fM", num / 1_000_000.0);
        if (num >= 1_000) return String.format("%.1fk", num / 1_000.0);
        return String.valueOf(num);
    }

    public static String formatGold(int gold) {
        return gold >= 1_000 ? String.format("%.1fk", gold / 1_000.0) : String.valueOf(gold);
    }

    public static String formatDuration(long ms) {
        long totalSec = ms / 1000;
        long min = totalSec / 60;
        long sec = totalSec % 60;
        return String.format("%02d:%02d", min, sec);
    }
}