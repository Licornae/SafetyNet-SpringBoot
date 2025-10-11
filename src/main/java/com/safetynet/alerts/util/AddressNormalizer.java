package com.safetynet.alerts.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.Objects;

/**
 * Normalizes street addresses by trimming and lowercasing using ROOT locale,
 * and provides normalized equality checks.
 * - null input returns null.
 * - Blank input returns an empty string.
 * - Otherwise: input.trim().toLowerCase(Locale.ROOT).
 */
@Slf4j
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
            log.debug("normalize: input is null -> returning null");
            return null;
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            log.debug("normalize: input is blank -> returning empty string");
            return "";
        }

        String normalized = trimmed.toLowerCase(Locale.ROOT);
        if (log.isTraceEnabled()) {
            log.trace("normalize: '{}' -> '{}'", input, normalized);
        }

        return normalized;
    }

    /**
     * Compares two addresses by their normalized forms.
     *
     * @param a first address (raw)
     * @param b second address (raw)
     * @return true if normalized values are equal, false otherwise
     */
    public static boolean equalsNormalized(String a, String b) {
        String na = normalize(a);
        String nb = normalize(b);
        boolean equals = Objects.equals(na, nb);

        log.trace("equalsNormalized: '{}' <-> '{}' -> {}", na, nb, equals);
        return equals;
    }
}
