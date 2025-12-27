package com.javamsdt.masking.maskme.api.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ConverterFactory Tests")
class ConverterFactoryTest {

    @Nested
    @DisplayName("convertToFieldType method")
    class ConvertToFieldTypeMethod {

        @Test
        @DisplayName("should convert string maskme to String type")
        void shouldConvertStringMaskToStringType() {
            // Given
            String maskValue = "***";
            Class<?> fieldType = String.class;

            // When
            Object result = ConverterFactory.convertToFieldType(maskValue, fieldType, null, new Object(), "field");

            // Then
            assertEquals("***", result);
        }

        @Test
        @DisplayName("should convert string maskme to Integer type")
        void shouldConvertStringMaskToIntegerType() {
            // Given
            String maskValue = "123";
            Class<?> fieldType = Integer.class;

            // When
            Object result = ConverterFactory.convertToFieldType(maskValue, fieldType, null, new Object(), "field");

            // Then
            assertEquals(123, result);
        }

        @Test
        @DisplayName("should convert string maskme to LocalDate type")
        void shouldConvertStringMaskToLocalDateType() {
            // Given
            String maskValue = "1900-01-01";
            Class<?> fieldType = LocalDate.class;

            // When
            Object result = ConverterFactory.convertToFieldType(maskValue, fieldType, null, new Object(), "field");

            // Then
            assertEquals(LocalDate.of(1900, 1, 1), result);
        }

        @Test
        @DisplayName("should return default value when maskme value is null")
        void shouldReturnDefaultValueWhenMaskValueIsNull() {
            // Given
            String maskValue = null;
            Class<?> fieldType = int.class;

            // When
            Object result = ConverterFactory.convertToFieldType(maskValue, fieldType, null, new Object(), "field");

            // Then
            assertEquals(0, result);
        }

        @Test
        @DisplayName("should return null for reference types when maskme value is null")
        void shouldReturnNullForReferenceTypesWhenMaskValueIsNull() {
            // Given
            String maskValue = null;
            Class<?> fieldType = String.class;

            // When
            Object result = ConverterFactory.convertToFieldType(maskValue, fieldType, null, new Object(), "field");

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("should handle BigDecimal rounding when maskme is blank")
        void shouldHandleBigDecimalRoundingWhenMaskIsBlank() {
            // Given
            String maskValue = "";
            Class<?> fieldType = BigDecimal.class;
            BigDecimal originalValue = new BigDecimal("123.45");

            // When
            Object result = ConverterFactory.convertToFieldType(maskValue, fieldType, originalValue, new Object(), "amount");

            // Then
            assertEquals(new BigDecimal("100"), result);
        }

        @Test
        @DisplayName("should return default value for unsupported type")
        void shouldReturnDefaultValueForUnsupportedType() {
            // Given
            String maskValue = "test";
            Class<?> fieldType = CustomType.class;

            // When
            Object result = ConverterFactory.convertToFieldType(maskValue, fieldType, null, new Object(), "field");

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("should pass field name to converters")
        void shouldPassFieldNameToConverters() {
            // Given
            String maskValue = "John";
            Class<?> fieldType = String.class;
            String fieldName = "name";

            // When
            Object result = ConverterFactory.convertToFieldType(maskValue, fieldType, null, new Object(), fieldName);

            // Then
            assertEquals("John[][]", result); // PrimitiveConverter adds [][] for name field
        }
    }

    // Test helper class
    private static class CustomType {
        // Custom type for testing unsupported conversion
    }
}