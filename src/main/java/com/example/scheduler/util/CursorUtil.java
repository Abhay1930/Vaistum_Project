package com.example.scheduler.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class CursorUtil {
    private CursorUtil() {}

    public static String encode(long epochMillis, long id) {
        String raw = epochMillis + "," + id;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static long[] decode(String cursor) {
        if (cursor == null || cursor.isBlank()) return null;
        String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
        String[] parts = raw.split(",");
        if (parts.length != 2) throw new IllegalArgumentException("bad cursor");
        return new long[]{Long.parseLong(parts[0]), Long.parseLong(parts[1])};
    }
}
