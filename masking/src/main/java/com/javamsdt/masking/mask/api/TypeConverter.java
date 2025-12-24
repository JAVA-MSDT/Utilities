/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.mask.api;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

public class TypeConverter {

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

    /**
     * Convert string maskValue to field type
     */
    public static Object convertToFieldType(String maskValue, Class<?> fieldType) {
        if (maskValue == null) {
            return getDefaultValue(fieldType);
        }

        // Handle String
        if (fieldType == String.class) {
            return maskValue;
        }

        // Handle Character/char
        if (fieldType == Character.class || fieldType == char.class) {
            if (maskValue.isEmpty()) {
                return fieldType == char.class ? '\0' : null;
            }
            return maskValue.charAt(0);
        }

        // Handle Boolean/boolean
        if (fieldType == Boolean.class || fieldType == boolean.class) {
            if ("true".equalsIgnoreCase(maskValue) || "1".equals(maskValue)) {
                return true;
            }
            if ("false".equalsIgnoreCase(maskValue) || "0".equals(maskValue)) {
                return false;
            }
            return fieldType == boolean.class ? false : null;
        }

        // Handle Byte/byte
        if (fieldType == Byte.class || fieldType == byte.class) {
            try {
                return Byte.parseByte(maskValue);
            } catch (NumberFormatException e) {
                return fieldType == byte.class ? (byte) 0 : null;
            }
        }

        // Handle Short/short
        if (fieldType == Short.class || fieldType == short.class) {
            try {
                return Short.parseShort(maskValue);
            } catch (NumberFormatException e) {
                return fieldType == short.class ? (short) 0 : null;
            }
        }

        // Handle Integer/int
        if (fieldType == Integer.class || fieldType == int.class) {
            try {
                return Integer.parseInt(maskValue);
            } catch (NumberFormatException e) {
                return fieldType == int.class ? 0 : null;
            }
        }

        // Handle Long/long
        if (fieldType == Long.class || fieldType == long.class) {
            try {
                return Long.parseLong(maskValue);
            } catch (NumberFormatException e) {
                return fieldType == long.class ? 0L : null;
            }
        }

        // Handle Float/float
        if (fieldType == Float.class || fieldType == float.class) {
            try {
                return Float.parseFloat(maskValue);
            } catch (NumberFormatException e) {
                return fieldType == float.class ? 0.0f : null;
            }
        }

        // Handle Double/double
        if (fieldType == Double.class || fieldType == double.class) {
            try {
                return Double.parseDouble(maskValue);
            } catch (NumberFormatException e) {
                return fieldType == double.class ? 0.0 : null;
            }
        }

        // Handle BigInteger
        if (fieldType == BigInteger.class) {
            try {
                return new BigInteger(maskValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // Handle BigDecimal
        if (fieldType == BigDecimal.class) {
            try {
                return new BigDecimal(maskValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // Handle UUID
        if (fieldType == UUID.class) {
            try {
                return UUID.fromString(maskValue);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        // Handle Enum types
        if (fieldType.isEnum()) {
            try {
                @SuppressWarnings("unchecked")
                Class<Enum> enumClass = (Class<Enum>) fieldType;
                return Enum.valueOf(enumClass, maskValue.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Try a case-insensitive match
                for (Enum<?> constant : fieldType.asSubclass(Enum.class).getEnumConstants()) {
                    if (constant.name().equalsIgnoreCase(maskValue)) {
                        return constant;
                    }
                }
                return null;
            }
        }

        // Handle Date/Time types
        if (fieldType == LocalDate.class) {
            return parseLocalDate(maskValue);
        }

        if (fieldType == LocalDateTime.class) {
            return parseLocalDateTime(maskValue);
        }

        if (fieldType == LocalTime.class) {
            try {
                return LocalTime.parse(maskValue, DateTimeFormatter.ISO_LOCAL_TIME);
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        if (fieldType == Instant.class) {
            try {
                return Instant.parse(maskValue);
            } catch (DateTimeParseException e) {
                // Try epoch milliseconds/seconds
                try {
                    long epoch = Long.parseLong(maskValue);
                    if (maskValue.length() <= 10) { // seconds
                        return Instant.ofEpochSecond(epoch);
                    } else { // milliseconds
                        return Instant.ofEpochMilli(epoch);
                    }
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
        }

        if (fieldType == ZonedDateTime.class) {
            try {
                return ZonedDateTime.parse(maskValue, DateTimeFormatter.ISO_ZONED_DATE_TIME);
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        if (fieldType == OffsetDateTime.class) {
            try {
                return OffsetDateTime.parse(maskValue, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        if (fieldType == Year.class) {
            try {
                return Year.parse(maskValue);
            } catch (DateTimeParseException e) {
                try {
                    int year = Integer.parseInt(maskValue);
                    return Year.of(year);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
        }

        if (fieldType == YearMonth.class) {
            try {
                return YearMonth.parse(maskValue, DateTimeFormatter.ofPattern("yyyy-MM"));
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        if (fieldType == MonthDay.class) {
            try {
                return MonthDay.parse(maskValue, DateTimeFormatter.ofPattern("MM-dd"));
            } catch (DateTimeParseException e) {
                return null;
            }
        }

        // Handle java.util.Date (legacy)
        if (fieldType == java.util.Date.class) {
            try {
                // Try ISO format first
                Instant instant = Instant.parse(maskValue);
                return java.util.Date.from(instant);
            } catch (DateTimeParseException e) {
                try {
                    // Try epoch milliseconds
                    long epoch = Long.parseLong(maskValue);
                    return new java.util.Date(epoch);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
        }

        // Handle java.sql types
        if (fieldType == java.sql.Date.class) {
            try {
                LocalDate localDate = parseLocalDate(maskValue);
                return localDate != null ? java.sql.Date.valueOf(localDate) : null;
            } catch (Exception e) {
                return null;
            }
        }

        if (fieldType == java.sql.Timestamp.class) {
            try {
                Instant instant = Instant.parse(maskValue);
                return java.sql.Timestamp.from(instant);
            } catch (Exception e) {
                try {
                    long epoch = Long.parseLong(maskValue);
                    return new java.sql.Timestamp(epoch);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
        }

        if (fieldType == java.sql.Time.class) {
            try {
                LocalTime localTime = LocalTime.parse(maskValue);
                return java.sql.Time.valueOf(localTime);
            } catch (Exception e) {
                return null;
            }
        }

        // Handle URL
        if (fieldType == URL.class) {
            try {
                return new URL(maskValue);
            } catch (MalformedURLException e) {
                return null;
            }
        }

        // Handle URI
        if (fieldType == URI.class) {
            try {
                return new URI(maskValue);
            } catch (URISyntaxException e) {
                return null;
            }
        }

        // Handle File
        if (fieldType == File.class) {
            return new File(maskValue);
        }

        // Handle Path
        if (fieldType == Path.class) {
            return Path.of(maskValue);
        }

        // Handle Locale
        if (fieldType == Locale.class) {
            try {
                return Locale.forLanguageTag(maskValue);
            } catch (Exception e) {
                return null;
            }
        }

        // Handle Currency
        if (fieldType == Currency.class) {
            try {
                return Currency.getInstance(maskValue);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        // Handle Class
        if (fieldType == Class.class) {
            try {
                return Class.forName(maskValue);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        // Handle arrays of primitives
        if (fieldType.isArray()) {
            Class<?> componentType = fieldType.getComponentType();
            if (componentType == String.class) {
                return new String[]{maskValue};
            }
            if (componentType == int.class) {
                try {
                    int value = Integer.parseInt(maskValue);
                    return new int[]{value};
                } catch (NumberFormatException e) {
                    return new int[0];
                }
            }
            // We can enhance it based on our requirements.
            // Add more array types as needed...
        }

        // Try to create an instance using String constructor
        try {
            return fieldType.getConstructor(String.class).newInstance(maskValue);
        } catch (NoSuchMethodException e) {
            // No string constructor, try default constructor
            try {
                Object instance = fieldType.getDeclaredConstructor().newInstance();
                // If the object has a setter for string, try to set it
                try {
                    fieldType.getMethod("setValue", String.class).invoke(instance, maskValue);
                } catch (Exception ex) {
                    // Ignore - object doesn't have setValue method
                }
                return instance;
            } catch (Exception ex) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static LocalDate parseLocalDate(String value) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException e) {
                // Try the next formatter
            }
        }
        return null;
    }

    private static LocalDateTime parseLocalDateTime(String value) {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException e) {
                // Try the next formatter
            }
        }
        return null;
    }

    /**
     * Get default value for type
     */
    private static Object getDefaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0f;
        if (type == double.class) return 0.0d;
        if (type == char.class) return '\0';
        return null;
    }
}
