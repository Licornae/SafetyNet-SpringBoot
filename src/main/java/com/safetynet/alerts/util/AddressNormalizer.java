package com.safetynet.alerts.util;

import java.util.Locale;
import java.util.Objects;

/**
 * Normalizes street addresses by trimming and lowercasing using ROOT locale,
 * and provides normalized equality checks.
 */
public final class AddressNormalizer {

    private AddressNormalizer() {}

    /**
     * Normalize input by trim and lowercase. Returns null for null, and empty string for blank.
     *
     * @param input raw address
     * @return normalized address or null
     */
    public static String normalize(String input) {

        if (input == null) {
            return null;
        }

        String trimmed = input.trim();

        if (trimmed.isEmpty()) {
            return "";
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }

    /**
     * Compares two addresses by their normalized forms.
     */
    public static boolean equalsNormalized(String a, String b) {
        String na = normalize(a);
        String nb = normalize(b);
        return Objects.equals(na, nb);
    }
}
