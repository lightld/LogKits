package com.l1ghtld.utility;

import org.bukkit.configuration.file.FileConfiguration;
import com.l1ghtld.LogKits;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtility {

    private static final Pattern PART = Pattern.compile("(\\d+)\\s*([dhmsDHMS])");

    private TimeUtility() {}

    public static long parseDurationToMillis(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("empty");
        }
        String s = input.trim();
        Matcher m = PART.matcher(s);
        long total = 0L;
        int matchedEnd = 0;
        while (m.find()) {
            long val = Long.parseLong(m.group(1));
            char unit = Character.toLowerCase(m.group(2).charAt(0));
            if (val < 0) throw new IllegalArgumentException("negative");
            switch (unit) {
                case 'd': total += val * 24L * 60L * 60L * 1000L; break;
                case 'h': total += val * 60L * 60L * 1000L; break;
                case 'm': total += val * 60L * 1000L; break;
                case 's': total += val * 1000L; break;
                default: throw new IllegalArgumentException("unit");
            }
            matchedEnd = m.end();
        }
        if (matchedEnd != s.length() || total <= 0) {
            throw new IllegalArgumentException("format");
        }
        return total;
    }

    public static String formatDurationRussian(long millis) {
        if (millis <= 0) {
            return getWord("zero_seconds");
        }

        long seconds = millis / 1000L;
        long days = seconds / 86400L; seconds %= 86400L;
        long hours = seconds / 3600L; seconds %= 3600L;
        long minutes = seconds / 60L; seconds %= 60L;

        Map<String, Long> parts = new LinkedHashMap<>();
        parts.put("d", days);
        parts.put("h", hours);
        parts.put("m", minutes);
        parts.put("s", seconds);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Long> e : parts.entrySet()) {
            long v = e.getValue();
            if (v <= 0) continue;
            if (sb.length() > 0) sb.append(" ");
            sb.append(v).append(" ").append(wordFor(e.getKey(), v));
        }
        if (sb.length() == 0) {
            return getWord("zero_seconds");
        }
        return sb.toString();
    }

    private static String wordFor(String unit, long n) {
        boolean few = isFew(n);
        boolean many = isMany(n);
        switch (unit) {
            case "d":
                if (many) return getWord("days5");
                if (few) return getWord("days2");
                return getWord("day");
            case "h":
                if (many) return getWord("hours5");
                if (few) return getWord("hours2");
                return getWord("hour");
            case "m":
                if (many) return getWord("minutes5");
                if (few) return getWord("minutes2");
                return getWord("minute");
            case "s":
                if (many) return getWord("seconds5");
                if (few) return getWord("seconds2");
                return getWord("second");
        }
        return "";
    }

    private static boolean isFew(long n) {
        long mod10 = n % 10;
        long mod100 = n % 100;
        return mod10 >= 2 && mod10 <= 4 && !(mod100 >= 12 && mod100 <= 14);
    }

    private static boolean isMany(long n) {
        long mod10 = n % 10;
        long mod100 = n % 100;
        return mod10 == 0 || mod10 >= 5 || (mod100 >= 11 && mod100 <= 14);
    }

    private static String getWord(String key) {
        FileConfiguration cfg = LogKits.getInstance().getConfig();
        return cfg.getString("time_words." + key, key);
    }
}
