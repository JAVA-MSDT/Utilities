package com.javamsdt.masking.maskme.api.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FallbackConverter Tests")
class FallbackConverterTest {

    private final FallbackConverter converter = new FallbackConverter();

    @Nested
    @DisplayName("canConvert method")
    class CanConvertMethod {

        @Test
        @DisplayName("should always return true as fallback")
        void shouldAlwaysReturnTrueAsFallback() {
            // Given & When & Then
            assertTrue(converter.canConvert(String.class));
            assertTrue(converter.canConvert(Integer.class));
            assertTrue(converter.canConvert(CustomType.class));
            assertTrue(converter.canConvert(Object.class));
        }
    }

    @Nested
    @DisplayName("convert method")
    class ConvertMethod {

        @Test
        @DisplayName("should convert using string constructor")
        void shouldConvertUsingStringConstructor() {
            // Given
            String value = "123";

            // When
            Object result = converter.convert(value, StringBuilder.class, null, new Object(), "field");

            // Then
            assertNotNull(result);
            assertTrue(result instanceof StringBuilder);
            assertEquals("123", result.toString());
        }

        @Test
        @DisplayName("should convert using default constructor when string constructor fails")
        void shouldConvertUsingDefaultConstructorWhenStringConstructorFails() {
            // Given
            String value = "test";

            // When
            Object result = converter.convert(value, CustomTypeWithDefaultConstructor.class, null, new Object(), "field");

            // Then
            assertNotNull(result);
            assertTrue(result instanceof CustomTypeWithDefaultConstructor);
        }

        @Test
        @DisplayName("should return null when both constructors fail")
        void shouldReturnNullWhenBothConstructorsFail() {
            // Given
            String value = "test";

            // When
            Object result = converter.convert(value, CustomTypeWithoutConstructors.class, null, new Object(), "field");

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("should use setValue method when available")
        void shouldUseSetValueMethodWhenAvailable() {
            // Given
            String value = "test";

            // When
            Object result = converter.convert(value, CustomTypeWithSetValue.class, null, new Object(), "field");

            // Then
            assertNotNull(result);
            assertTrue(result instanceof CustomTypeWithSetValue);
            assertEquals("test", ((CustomTypeWithSetValue) result).getValue());
        }

        @Test
        @DisplayName("should return empty instance when setValue method not available")
        void shouldReturnEmptyInstanceWhenSetValueMethodNotAvailable() {
            // Given
            String value = "test";

            // When
            Object result = converter.convert(value, CustomTypeWithDefaultConstructor.class, null, new Object(), "field");

            // Then
            assertNotNull(result);
            assertTrue(result instanceof CustomTypeWithDefaultConstructor);
        }
    }

    // Test helper classes
    private static class CustomType {
        // Custom type for testing
    }

    private static class CustomTypeWithDefaultConstructor {
        public CustomTypeWithDefaultConstructor() {
            // Default constructor
        }
    }

    private static class CustomTypeWithoutConstructors {
        private CustomTypeWithoutConstructors() {
            // Private constructor to prevent instantiation
        }
    }

    private static class CustomTypeWithSetValue {
        private String value;

        public CustomTypeWithSetValue() {
            // Default constructor
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}