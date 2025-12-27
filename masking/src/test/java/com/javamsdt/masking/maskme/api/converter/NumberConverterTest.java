package com.javamsdt.masking.maskme.api.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("NumberConverter Tests")
class NumberConverterTest {

    private final NumberConverter converter = new NumberConverter();

    @Nested
    @DisplayName("canConvert method")
    class CanConvertMethod {

        @Test
        @DisplayName("should return true for numeric types")
        void shouldReturnTrueForNumericTypes() {
            // Given & When & Then
            assertTrue(converter.canConvert(Integer.class));
            assertTrue(converter.canConvert(int.class));
            assertTrue(converter.canConvert(Long.class));
            assertTrue(converter.canConvert(BigDecimal.class));
            assertTrue(converter.canConvert(Double.class));
        }

        @Test
        @DisplayName("should return false for non-numeric types")
        void shouldReturnFalseForNonNumericTypes() {
            // Given & When & Then
            assertFalse(converter.canConvert(String.class));
            assertFalse(converter.canConvert(Boolean.class));
        }
    }

    @Nested
    @DisplayName("convert method")
    class ConvertMethod {

        @Test
        @DisplayName("should convert string to Integer")
        void shouldConvertStringToInteger() {
            // Given
            String value = "123";

            // When
            Object result = converter.convert(value, Integer.class, null, new Object(), "field");

            // Then
            assertEquals(123, result);
        }

        @Test
        @DisplayName("should convert string to BigDecimal")
        void shouldConvertStringToBigDecimal() {
            // Given
            String value = "123.45";

            // When
            Object result = converter.convert(value, BigDecimal.class, null, new Object(), "field");

            // Then
            assertEquals(new BigDecimal("123.45"), result);
        }

        @Test
        @DisplayName("should round BigDecimal to nearest 50 when maskme is blank")
        void shouldRoundBigDecimalToNearest50WhenMaskIsBlank() {
            // Given
            String value = "";
            BigDecimal originalValue = new BigDecimal("123.45");

            // When
            Object result = converter.convert(value, BigDecimal.class, originalValue, new Object(), "field");

            // Then
            assertEquals(new BigDecimal("100"), result);
        }

        @Test
        @DisplayName("should round BigDecimal 175 to 200")
        void shouldRoundBigDecimal175To200() {
            // Given
            String value = "";
            BigDecimal originalValue = new BigDecimal("175.30");

            // When
            Object result = converter.convert(value, BigDecimal.class, originalValue, new Object(), "field");

            // Then
            assertEquals(new BigDecimal("200"), result);
        }

        @Test
        @DisplayName("should return zero for primitive int with invalid value")
        void shouldReturnZeroForPrimitiveIntWithInvalidValue() {
            // Given
            String value = "invalid";

            // When
            Object result = converter.convert(value, int.class, null, new Object(), "field");

            // Then
            assertEquals(0, result);
        }

        @Test
        @DisplayName("should return null for Integer wrapper with invalid value")
        void shouldReturnNullForIntegerWrapperWithInvalidValue() {
            // Given
            String value = "invalid";

            // When
            Object result = converter.convert(value, Integer.class, null, new Object(), "field");

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("should convert string to BigInteger")
        void shouldConvertStringToBigInteger() {
            // Given
            String value = "12345678901234567890";

            // When
            Object result = converter.convert(value, BigInteger.class, null, new Object(), "field");

            // Then
            assertEquals(new BigInteger("12345678901234567890"), result);
        }

        @Test
        @DisplayName("should convert string to primitive types")
        void shouldConvertStringToPrimitiveTypes() {
            // Given & When & Then
            assertEquals((byte) 1, converter.convert("1", byte.class, null, new Object(), "field"));
            assertEquals((short) 123, converter.convert("123", short.class, null, new Object(), "field"));
            assertEquals(123L, converter.convert("123", long.class, null, new Object(), "field"));
            assertEquals(123.45f, converter.convert("123.45", float.class, null, new Object(), "field"));
            assertEquals(123.45d, converter.convert("123.45", double.class, null, new Object(), "field"));
        }
    }
}