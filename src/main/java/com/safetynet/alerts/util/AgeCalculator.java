package com.safetynet.alerts.util;

import com.safetynet.alerts.exception.InvalidBirthdateException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * Computes age from a birthdate string.
 * This utility parses birthdates in the MM/dd/yyyy format and calculates the age.
 * It also has methods to check whether a person is considered a child or an adult.
 * - Null or blank input throws InvalidBirthdateException with reason "NULL_OR_BLANK".
 * - Invalid format throws InvalidBirthdateException with reason "BAD_FORMAT".
 */
@Slf4j
public class AgeCalculator {

    private AgeCalculator() {}

    private static final DateTimeFormatter BIRTHDAY_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Compute age in years from a birthdate string.
     *
     * @param birthdateStr birthdate in MM/dd/yyyy format
     * @return age in years
     */
    public static Integer computeAge(String birthdateStr) throws InvalidBirthdateException {

        if (birthdateStr == null || birthdateStr.isBlank()){
            log.warn("computeAge: input birthdate is null or blank");
            throw new InvalidBirthdateException(birthdateStr, "NULL_OR_BLANK");
        }

        try {
            LocalDate birthdate = LocalDate.parse(birthdateStr, BIRTHDAY_FORMAT);
            int years = Period.between(birthdate, LocalDate.now()).getYears();
            log.debug("computeAge: birthdate='{}' -> age={} year(s)", birthdateStr, years);

            return years;

        } catch (DateTimeParseException e) {
            log.warn("computeAge: invalid format for birthdate='{}' (expected MM/dd/yyyy)", birthdateStr);
            throw new InvalidBirthdateException(birthdateStr, "BAD_FORMAT");
        }
    }

    /**
     * Returns true if the age computed from birthdateStr is less than or equal to 18 years.
     *
     * @param birthdateStr birthdate in MM/dd/yyyy format
     * @return true if age ≤ 18, false otherwise
     */
    public static boolean isChild(String birthdateStr) {
        boolean child = computeAge(birthdateStr) <= 18;
        log.trace("isChild: birthdate='{}' -> {}", birthdateStr, child);

        return child;
    }

    /**
     * Returns true if the age computed from birthdateStr is strictly greater than 18 years.
     *
     * @param birthdateStr birthdate in MM/dd/yyyy format
     * @return true if age > 18, false otherwise
     */
    public static boolean isAdult(String birthdateStr) {
        boolean adult = computeAge(birthdateStr) > 18;
        log.trace("isAdult: birthdate='{}' -> {}", birthdateStr, adult);

        return adult;
    }
}
