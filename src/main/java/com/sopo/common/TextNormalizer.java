package com.sopo.common;

import java.util.Locale;

public final class TextNormalizer {
    private TextNormalizer() {}

    public static String normalizeEmail(String raw) {
        if (raw == null) return null;
        return raw.trim().toLowerCase(Locale.ROOT);
    }

    public static String normalizePhone(String raw) {
        if (raw == null) return  null;
        return raw.replaceAll("\\D", "");
    }
}
