package com.safetynet.alerts.util;

import com.safetynet.alerts.exception.InvalidBirthdateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AgeCalculatorTest {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @InjectMocks
    AgeCalculator ageCalculator;

    @Test
    public void computeAge_Valid_ComputesExpectedAge() {
        LocalDate now = LocalDate.now();

        //20 years
        LocalDate twentyYearsMinus1Day = now.minusYears(20).minusDays(1);
        int age1 = AgeCalculator.computeAge(twentyYearsMinus1Day.format(FORMAT));
        assertEquals(20, age1);

        //Birth today
        int age2 = AgeCalculator.computeAge(now.format(FORMAT));
        assertEquals(0, age2);

        //Exactly 18
        LocalDate exactly18 = now.minusYears(18);
        int age3 = AgeCalculator.computeAge(exactly18.format(FORMAT));
        assertEquals(18, age3);
    }

    @Test
    public void computeAge_Null_ThrowsInvalidBirthdateException() {
        InvalidBirthdateException exception = assertThrows(InvalidBirthdateException.class,
                () -> AgeCalculator.computeAge(null));
    }

    @Test
    public void computeAge_Blank_ThrowsInvalidBirthdateException() {
        InvalidBirthdateException exception = assertThrows(InvalidBirthdateException.class,
                () -> AgeCalculator.computeAge("   "));
    }

    @Test
    public void computeAge_BadFormat_ThrowsInvalidBirthdateException() {
        assertThrows(InvalidBirthdateException.class, () -> AgeCalculator.computeAge("2000-12-31"));
        assertThrows(InvalidBirthdateException.class, () -> AgeCalculator.computeAge("31/12/2000"));
        assertThrows(InvalidBirthdateException.class, () -> AgeCalculator.computeAge("13/40/2000"));
        assertThrows(InvalidBirthdateException.class, () -> AgeCalculator.computeAge("not a date"));
    }

    // Tests isChild
    @Test
    public void isChild_True_WhenAgeLessThan18() {
        LocalDate birth = LocalDate.now().minusYears(10);
        assertTrue(AgeCalculator.isChild(birth.format(FORMAT)));
    }

    @Test
    public void isChild_True_WhenAgeEquals18() {
        LocalDate birth = LocalDate.now().minusYears(18); // exactement 18
        assertTrue(AgeCalculator.isChild(birth.format(FORMAT)));
    }

    @Test
    public void isChild_False_WhenAgeGreaterThan18() {
        LocalDate birth = LocalDate.now().minusYears(19);
        assertFalse(AgeCalculator.isChild(birth.format(FORMAT)));
    }

    // Tests isAdult
    @Test
    public void isAdult_True_WhenAgeGreaterThan18() {
        LocalDate birth = LocalDate.now().minusYears(19);
        assertTrue(AgeCalculator.isAdult(birth.format(FORMAT)));
    }

    @Test
    public void isAdult_False_WhenAgeEquals18() {
        LocalDate birth = LocalDate.now().minusYears(18);
        assertFalse(AgeCalculator.isAdult(birth.format(FORMAT)));
    }

    @Test
    public void isAdult_False_WhenAgeLessThan18() {
        LocalDate birth = LocalDate.now().minusYears(5);
        assertFalse(AgeCalculator.isAdult(birth.format(FORMAT)));
    }
}
