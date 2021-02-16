package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void isBlank_NullString_ReturnsTrue() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            String testString = null;
            boolean result = StringUtils.isBlank(testString);
            assertTrue(result);
        });
    }

    @Test
    void isBlank_EmptyString_ReturnsTrue() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            String testString = "";
            boolean result = StringUtils.isBlank(testString);
            assertTrue(result);
        });
    }

    @Test
    void isBlank_OneWhitespaceString_ReturnsTrue() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            String testString = " ";
            boolean result = StringUtils.isBlank(testString);
            assertTrue(result);
        });
    }

    @Test
    void isBlank_MultipleWhitespaceString_ReturnsTrue() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            String testString = "  ";
            boolean result = StringUtils.isBlank(testString);
            assertTrue(result);
        });
    }

    @Test
    void isBlank_SingleCharacterString_ReturnsFalse() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            String testString = "A";
            boolean result = StringUtils.isBlank(testString);
            assertFalse(result);
        });
    }

    @Test
    void isBlank_SingleWordString_ReturnsFalse() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            String testString = "Hello";
            boolean result = StringUtils.isBlank(testString);
            assertFalse(result);
        });
    }

    @Test
    void isBlank_MultipleWordsSeparatedByWhitespaceString_ReturnsFalse() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            String testString = "Hello World";
            boolean result = StringUtils.isBlank(testString);
            assertFalse(result);
        });
    }

    @Test
    void multiplyChar_ZeroTimes_ReturnsEmptyString() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            char inputChar = 'a';
            int times = 0;
            String result = StringUtils.multiplyChar(inputChar, times);
            assertEquals("", result);
        });
    }

    @Test
    void multiplyChar_OneTime_ReturnsCharAsString() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            char inputChar = 'a';
            int times = 1;
            String result = StringUtils.multiplyChar(inputChar, times);
            assertEquals("a", result);
        });
    }

    @Test
    void multiplyChar_TwoTimes_ReturnsStringWithTwoSimilarCharacter() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            char inputChar = 'a';
            int times = 2;
            String result = StringUtils.multiplyChar(inputChar, times);
            assertEquals("aa", result);
        });
    }

    @Test
    void tokenize() {
    }

    @Test
    void isNumber_EmptyString_ReturnsFalse() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            String input = "";
            boolean result = StringUtils.isNumber(input);
            assertFalse(result);
        });
    }

    @Test
    void isNumber_NotNumber_ReturnsFalse() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            String input = "123Abc";
            boolean result = StringUtils.isNumber(input);
            assertFalse(result);
        });
    }

    @Test
    void isNumber_Zero_ReturnsTrue() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            String input = "0";
            boolean result = StringUtils.isNumber(input);
            assertTrue(result);
        });
    }

    @Test
    void isNumber_SingleDigit_ReturnsTrue() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            String input = "6";
            boolean result = StringUtils.isNumber(input);
            assertTrue(result);
        });
    }

    @Test
    void isNumber_NormalNumber_ReturnsTrue() {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            String input = "12345";
            boolean result = StringUtils.isNumber(input);
            assertTrue(result);
        });
    }
}