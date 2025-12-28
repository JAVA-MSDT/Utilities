package com.javamsdt.masking.maskme.api.converter;

import com.javamsdt.masking.maskme.implemintation.converter.DateTimeConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DateTimeConverter Tests")
class DateTimeConverterTest {

    private final DateTimeConverter converter = new DateTimeConverter();

    @Nested
    @DisplayName("canConvert method")
    class CanConvertMethod {

        @Test
        @DisplayName("should return true for temporal types")
        void shouldReturnTrueForTemporalTypes() {
            // Given & When & Then
            assertTrue(converter.canConvert(LocalDate.class));
            assertTrue(converter.canConvert(LocalDateTime.class));
            assertTrue(converter.canConvert(Year.class));
            assertTrue(converter.canConvert(java.util.Date.class));
        }

        @Test
        @DisplayName("should return false for non-temporal types")
        void shouldReturnFalseForNonTemporalTypes() {
            // Given & When & Then
            assertFalse(converter.canConvert(String.class));
            assertFalse(converter.canConvert(Integer.class));
        }
    }

    @Nested
    @DisplayName("convert method")
    class ConvertMethod {

        @Test
        @DisplayName("should convert ISO date string to LocalDate")
        void shouldConvertIsoDateStringToLocalDate() {
            // Given
            String value = "2023-12-25";

            // When
            Object result = converter.convert(value, LocalDate.class, null, new Object(), "field");

            // Then
            assertEquals(LocalDate.of(2023, 12, 25), result);
        }

        @Test
        @DisplayName("should convert various date formats to LocalDate")
        void shouldConvertVariousDateFormatsToLocalDate() {
            // Given & When & Then
            assertEquals(LocalDate.of(2023, 12, 25), 
                converter.convert("25/12/2023", LocalDate.class, null, new Object(), "field"));
            assertEquals(LocalDate.of(2023, 12, 25), 
                converter.convert("12/25/2023", LocalDate.class, null, new Object(), "field"));
        }

        @Test
        @DisplayName("should convert ISO datetime string to LocalDateTime")
        void shouldConvertIsoDatetimeStringToLocalDateTime() {
            // Given
            String value = "2023-12-25T10:30:00";

            // When
            Object result = converter.convert(value, LocalDateTime.class, null, new Object(), "field");

            // Then
            assertEquals(LocalDateTime.of(2023, 12, 25, 10, 30, 0), result);
        }

        @Test
        @DisplayName("should convert year string to Year")
        void shouldConvertYearStringToYear() {
            // Given
            String value = "2023";

            // When
            Object result = converter.convert(value, Year.class, null, new Object(), "field");

            // Then
            assertEquals(Year.of(2023), result);
        }

        @Test
        @DisplayName("should return null for invalid date format")
        void shouldReturnNullForInvalidDateFormat() {
            // Given
            String value = "invalid-date";

            // When
            Object result = converter.convert(value, LocalDate.class, null, new Object(), "field");

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("should convert epoch timestamp to util Date")
        void shouldConvertEpochTimestampToUtilDate() {
            // Given
            String value = "1703505000000"; // milliseconds

            // When
            Object result = converter.convert(value, java.util.Date.class, null, new Object(), "field");

            // Then
            assertNotNull(result);
            assertTrue(result instanceof java.util.Date);
        }

        @Test
        @DisplayName("should handle sql Date conversion")
        void shouldHandleSqlDateConversion() {
            // Given
            String value = "2023-12-25";

            // When
            Object result = converter.convert(value, java.sql.Date.class, null, new Object(), "field");

            // Then
            assertNotNull(result);
            assertTrue(result instanceof java.sql.Date);
        }
    }
}