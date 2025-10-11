package com.safetynet.alerts.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AddressNormalizerTest {

    @InjectMocks
    AddressNormalizer addressNormalizer;

    @Test
    public void normalize_Null_ReturnsNull() {
        assertNull(AddressNormalizer.normalize(null));
    }

    @Test
    public void normalize_Blank_ReturnsEmptyString() {
        assertEquals("", AddressNormalizer.normalize("   "));
        assertEquals("", AddressNormalizer.normalize("\t\n"));
    }

    @Test
    public void normalize_TrimsAndLowercases() {
        assertEquals("1509 culver st", AddressNormalizer.normalize("  1509 CulVer St "));
        assertEquals("rue des lilas", AddressNormalizer.normalize("Rue DES Lilas"));
    }

    @Test
    public void normalize_KeepsNonAlphaCharacters() {
        assertEquals("apt. b-2", AddressNormalizer.normalize(" Apt. B-2 "));
    }

    @Test
    public void equalsNormalized_True_WhenSameIgnoringCaseAndSpaces() {
        assertTrue(AddressNormalizer.equalsNormalized(" 1509 Culver St ", "1509 CULVER st"));
    }

    @Test
    public void equalsNormalized_false_whenDifferent() {
        assertFalse(AddressNormalizer.equalsNormalized("1509 Culver St", "1510 Culver St"));
    }
}
