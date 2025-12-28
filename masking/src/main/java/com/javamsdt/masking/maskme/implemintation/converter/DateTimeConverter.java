/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.implemintation.converter;

import com.javamsdt.masking.maskme.api.converter.Converter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

/**
 * Converter for all temporal types including modern java.time and legacy java.util.Date.
 * Supports multiple date/time formats with flexible parsing strategies
 * for common date representations.
 * 
 * <p>Supported types:
 * - Modern java.time: LocalDate, LocalDateTime, LocalTime, Instant, etc.
 * - Legacy types: java.util.Date, java.sql.Date, java.sql.Timestamp
 * - Partial dates: Year, YearMonth, MonthDay
 * 
 * <p>Supported formats:
 * - ISO formats: "2023-12-25", "2023-12-25T10:30:00"
 * - Common formats: "25/12/2023", "12/25/2023"
 * - Epoch timestamps: "1703505000" (seconds), "1703505000000" (millis)
 * 
 * <p>Use cases:
 * - Convert "1900-01-01" to LocalDate for birth date masking
 * - Parse various date formats from maskme values
 * - Handle legacy database date types
 * - Support international date format variations
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
public class DateTimeConverter implements Converter {
    
    private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(
        LocalDate.class, LocalDateTime.class, LocalTime.class,
        Instant.class, ZonedDateTime.class, OffsetDateTime.class,
        Year.class, YearMonth.class, MonthDay.class,
        java.util.Date.class, java.sql.Date.class, 
        java.sql.Timestamp.class, java.sql.Time.class
    );
    
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy")
    };
    
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_INSTANT,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    };
    
    @Override
    public boolean canConvert(Class<?> type) {
        return SUPPORTED_TYPES.contains(type);
    }
    
    @Override
    public Object convert(String value, Class<?> targetType, Object originalValue, Object containingObject, String fieldName) {
        try {
            return switch (targetType.getName()) {
                case "java.time.LocalDate" -> parseLocalDate(value);
                case "java.time.LocalDateTime" -> parseLocalDateTime(value);
                case "java.time.LocalTime" -> LocalTime.parse(value, DateTimeFormatter.ISO_LOCAL_TIME);
                case "java.time.Instant" -> parseInstant(value);
                case "java.time.ZonedDateTime" -> ZonedDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME);
                case "java.time.OffsetDateTime" -> OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                case "java.time.Year" -> parseYear(value);
                case "java.time.YearMonth" -> YearMonth.parse(value, DateTimeFormatter.ofPattern("yyyy-MM"));
                case "java.time.MonthDay" -> MonthDay.parse(value, DateTimeFormatter.ofPattern("MM-dd"));
                case "java.util.Date" -> parseUtilDate(value);
                case "java.sql.Date" -> handleSqlDate(value);
                case "java.sql.Timestamp" -> parseSqlTimestamp(value);
                case "java.sql.Time" -> handleSqlTime(value);
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }
    
    private java.sql.Date handleSqlDate(String value) {
        LocalDate localDate = parseLocalDate(value);
        return localDate != null ? java.sql.Date.valueOf(localDate) : null;
    }
    
    private java.sql.Time handleSqlTime(String value) {
        LocalTime localTime = LocalTime.parse(value);
        return java.sql.Time.valueOf(localTime);
    }


    private LocalDate parseLocalDate(String value) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }
    
    private LocalDateTime parseLocalDateTime(String value) {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }
    
    private Instant parseInstant(String value) {
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException e) {
            try {
                long epoch = Long.parseLong(value);
                return value.length() <= 10 ? 
                    Instant.ofEpochSecond(epoch) : 
                    Instant.ofEpochMilli(epoch);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
    
    private Year parseYear(String value) {
        try {
            return Year.parse(value);
        } catch (DateTimeParseException e) {
            try {
                return Year.of(Integer.parseInt(value));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
    
    private java.util.Date parseUtilDate(String value) {
        try {
            Instant instant = Instant.parse(value);
            return java.util.Date.from(instant);
        } catch (DateTimeParseException e) {
            try {
                long epoch = Long.parseLong(value);
                return new java.util.Date(epoch);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
    
    private java.sql.Timestamp parseSqlTimestamp(String value) {
        try {
            Instant instant = Instant.parse(value);
            return java.sql.Timestamp.from(instant);
        } catch (Exception e) {
            try {
                long epoch = Long.parseLong(value);
                return new java.sql.Timestamp(epoch);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}