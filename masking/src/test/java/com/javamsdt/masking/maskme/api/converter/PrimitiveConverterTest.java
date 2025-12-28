package com.javamsdt.masking.maskme.api.converter;

import com.javamsdt.masking.maskme.implemintation.converter.PrimitiveConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PrimitiveConverter Tests")
class PrimitiveConverterTest {

    private final PrimitiveConverter converter = new PrimitiveConverter();

    @Nested
    @DisplayName("canConvert method")
    class CanConvertMethod {

        @Test
        @DisplayName("should return true for String type")
        void shouldReturnTrueForStringType() {
            // Given
            Class<?> type = String.class;

            // When
            boolean result = converter.canConvert(type);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true for Character types")
        void shouldReturnTrueForCharacterTypes() {
            // Given & When & Then
            assertTrue(converter.canConvert(Character.class));
            assertTrue(converter.canConvert(char.class));
        }

        @Test
        @DisplayName("should return true for Boolean types")
        void shouldReturnTrueForBooleanTypes() {
            // Given & When & Then
            assertTrue(converter.canConvert(Boolean.class));
            assertTrue(converter.canConvert(boolean.class));
        }

        @Test
        @DisplayName("should return false for unsupported types")
        void shouldReturnFalseForUnsupportedTypes() {
            // Given & When & Then
            assertFalse(converter.canConvert(Integer.class));
            assertFalse(converter.canConvert(Object.class));
        }
    }

    @Nested
    @DisplayName("convert method")
    class ConvertMethod {

        @Nested
        @DisplayName("String conversion")
        class StringConversion {

            @Test
            @DisplayName("should append brackets for name field")
            void shouldAppendBracketsForNameField() {
                // Given
                String value = "John";
                String fieldName = "name";

                // When
                Object result = converter.convert(value, String.class, "originalName", new Object(), fieldName);

                // Then
                assertEquals("John[][]", result);
            }

            @Test
            @DisplayName("should replace email domain for email field")
            void shouldReplaceEmailDomainForEmailField() {
                // Given
                String value = "masked.com";
                String fieldName = "email";
                String originalValue = "john@original.com";

                // When
                Object result = converter.convert(value, String.class, originalValue, new Object(), fieldName);

                // Then
                assertEquals("john@masked.com.domain", result);
            }

            @Test
            @DisplayName("should return processed value for other fields")
            void shouldReturnProcessedValueForOtherFields() {
                // Given
                String value = "***";
                String fieldName = "password";

                // When
                Object result = converter.convert(value, String.class, "original", new Object(), fieldName);

                // Then
                assertEquals("***", result);
            }
        }

        @Nested
        @DisplayName("Character conversion")
        class CharacterConversion {

            @Test
            @DisplayName("should convert string to Character")
            void shouldConvertStringToCharacter() {
                // Given
                String value = "A";

                // When
                Object result = converter.convert(value, Character.class, null, new Object(), "field");

                // Then
                assertEquals('A', result);
            }

            @Test
            @DisplayName("should return null char for empty string and Character type")
            void shouldReturnNullCharForEmptyStringAndCharacterType() {
                // Given
                String value = "";

                // When
                Object result = converter.convert(value, Character.class, null, new Object(), "field");

                // Then
                assertNull(result);
            }

            @Test
            @DisplayName("should return null char for empty string and primitive char")
            void shouldReturnNullCharForEmptyStringAndPrimitiveChar() {
                // Given
                String value = "";

                // When
                Object result = converter.convert(value, char.class, null, new Object(), "field");

                // Then
                assertEquals('\0', result);
            }
        }

        @Nested
        @DisplayName("Boolean conversion")
        class BooleanConversion {

            @Test
            @DisplayName("should convert true string to Boolean")
            void shouldConvertTrueStringToBoolean() {
                // Given & When & Then
                assertEquals(true, converter.convert("true", Boolean.class, null, new Object(), "field"));
                assertEquals(true, converter.convert("TRUE", Boolean.class, null, new Object(), "field"));
                assertEquals(true, converter.convert("1", Boolean.class, null, new Object(), "field"));
            }

            @Test
            @DisplayName("should convert false string to Boolean")
            void shouldConvertFalseStringToBoolean() {
                // Given & When & Then
                assertEquals(false, converter.convert("false", Boolean.class, null, new Object(), "field"));
                assertEquals(false, converter.convert("FALSE", Boolean.class, null, new Object(), "field"));
                assertEquals(false, converter.convert("0", Boolean.class, null, new Object(), "field"));
            }

            @Test
            @DisplayName("should return false for primitive boolean with invalid value")
            void shouldReturnFalseForPrimitiveBooleanWithInvalidValue() {
                // Given
                String value = "invalid";

                // When
                Object result = converter.convert(value, boolean.class, null, new Object(), "field");

                // Then
                assertEquals(false, result);
            }

            @Test
            @DisplayName("should return null for Boolean wrapper with invalid value")
            void shouldReturnNullForBooleanWrapperWithInvalidValue() {
                // Given
                String value = "invalid";

                // When
                Object result = converter.convert(value, Boolean.class, null, new Object(), "field");

                // Then
                assertNull(result);
            }
        }
    }
}