package com.javamsdt.masking.maskme.api.converter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("SpecialTypeConverter Tests")
class SpecialTypeConverterTest {

    private final SpecialTypeConverter converter = new SpecialTypeConverter();

    @Nested
    @DisplayName("canConvert method")
    class CanConvertMethod {

        @Test
        @DisplayName("should return true for special types")
        void shouldReturnTrueForSpecialTypes() {
            // Given & When & Then
            assertTrue(converter.canConvert(UUID.class));
            assertTrue(converter.canConvert(URL.class));
            assertTrue(converter.canConvert(Locale.class));
            assertTrue(converter.canConvert(TestEnum.class));
            assertTrue(converter.canConvert(String[].class));
        }

        @Test
        @DisplayName("should return false for unsupported types")
        void shouldReturnFalseForUnsupportedTypes() {
            // Given & When & Then
            assertFalse(converter.canConvert(String.class));
            assertFalse(converter.canConvert(Integer.class));
        }
    }

    @Nested
    @DisplayName("convert method")
    class ConvertMethod {

        @Test
        @DisplayName("should convert string to UUID")
        void shouldConvertStringToUuid() {
            // Given
            String value = "550e8400-e29b-41d4-a716-446655440000";

            // When
            Object result = converter.convert(value, UUID.class, null, new Object(), "field");

            // Then
            assertEquals(UUID.fromString(value), result);
        }

        @Test
        @DisplayName("should convert string to URL")
        void shouldConvertStringToUrl() throws Exception {
            // Given
            String value = "https://example.com";

            // When
            Object result = converter.convert(value, URL.class, null, new Object(), "field");

            // Then
            assertEquals(new URL(value), result);
        }

        @Test
        @DisplayName("should convert string to Locale")
        void shouldConvertStringToLocale() {
            // Given
            String value = "en-US";

            // When
            Object result = converter.convert(value, Locale.class, null, new Object(), "field");

            // Then
            assertEquals(Locale.forLanguageTag(value), result);
        }

        @Test
        @DisplayName("should convert string to enum with exact match")
        void shouldConvertStringToEnumWithExactMatch() {
            // Given
            String value = "ACTIVE";

            // When
            Object result = converter.convert(value, TestEnum.class, null, new Object(), "field");

            // Then
            assertEquals(TestEnum.ACTIVE, result);
        }

        @Test
        @DisplayName("should convert string to enum with case insensitive match")
        void shouldConvertStringToEnumWithCaseInsensitiveMatch() {
            // Given
            String value = "active";

            // When
            Object result = converter.convert(value, TestEnum.class, null, new Object(), "field");

            // Then
            assertEquals(TestEnum.ACTIVE, result);
        }

        @Test
        @DisplayName("should return null for invalid enum value")
        void shouldReturnNullForInvalidEnumValue() {
            // Given
            String value = "INVALID";

            // When
            Object result = converter.convert(value, TestEnum.class, null, new Object(), "field");

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("should convert string to String array")
        void shouldConvertStringToStringArray() {
            // Given
            String value = "test";

            // When
            Object result = converter.convert(value, String[].class, null, new Object(), "field");

            // Then
            assertArrayEquals(new String[]{"test"}, (String[]) result);
        }

        @Test
        @DisplayName("should convert string to int array")
        void shouldConvertStringToIntArray() {
            // Given
            String value = "123";

            // When
            Object result = converter.convert(value, int[].class, null, new Object(), "field");

            // Then
            assertArrayEquals(new int[]{123}, (int[]) result);
        }

        @Test
        @DisplayName("should return empty int array for invalid number")
        void shouldReturnEmptyIntArrayForInvalidNumber() {
            // Given
            String value = "invalid";

            // When
            Object result = converter.convert(value, int[].class, null, new Object(), "field");

            // Then
            assertArrayEquals(new int[0], (int[]) result);
        }

        @Test
        @DisplayName("should return null for invalid URL")
        void shouldReturnNullForInvalidUrl() {
            // Given
            String value = "invalid-url";

            // When
            Object result = converter.convert(value, URL.class, null, new Object(), "field");

            // Then
            assertNull(result);
        }
    }

    // Test enum
    private enum TestEnum {
        ACTIVE, INACTIVE
    }
}