package com.safetynet.alerts.util;

import com.safetynet.alerts.exception.InvalidBirthdateException;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AgeCalculator {

    private AgeCalculator() {}

    private static final DateTimeFormatter BIRTHDAY_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static Integer computeAge(String birthdateStr) throws InvalidBirthdateException {
        if (birthdateStr == null || birthdateStr.isBlank())
            throw new InvalidBirthdateException(birthdateStr, "NULL_OR_BLANK");

        try {
            LocalDate birthdate = LocalDate.parse(birthdateStr, BIRTHDAY_FORMAT);
            return Period.between(birthdate, LocalDate.now()).getYears();
        } catch (DateTimeParseException e) {
            throw new InvalidBirthdateException(birthdateStr, "BAD_FORMAT");
        }
    }

    public static boolean isChild(String birthdateStr) {
        return computeAge(birthdateStr) <= 18;
    }

    public static boolean isAdult(String birthdateStr) {
        return computeAge(birthdateStr) > 18;
    }

}
