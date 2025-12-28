/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.implemintation.converter;

import com.javamsdt.masking.maskme.api.converter.Converter;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Converter for special types including UUIDs, URLs, enums, and arrays.
 * Handles various Java API types that require specialized parsing logic
 * beyond basic primitives and numbers.
 * 
 * <p>Supported types:
 * - Identifiers: UUID
 * - Network: URL, URI
 * - File system: File, Path
 * - Internationalization: Locale, Currency
 * - Reflection: Class
 * - Enums: All enum types with case-insensitive matching
 * - Arrays: Basic array types (String[], int[])
 * 
 * <p>Special behaviors:
 * - Enum conversion supports case-insensitive matching
 * - UUID parsing from string representation
 * - Array conversion creates single-element arrays
 * - Locale parsing from language tags
 * 
 * <p>Use cases:
 * - Convert "550e8400-e29b-41d4-a716-446655440000" to UUID
 * - Parse "en-US" to Locale.US
 * - Convert "ACTIVE" to Status.ACTIVE enum
 * - Handle file paths and URLs in maskme values
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
public class SpecialTypeConverter implements Converter {
    
    private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(
        UUID.class, URL.class, URI.class, File.class, Path.class,
        Locale.class, Currency.class, Class.class
    );
    
    @Override
    public boolean canConvert(Class<?> type) {
        return SUPPORTED_TYPES.contains(type) || type.isEnum() || type.isArray();
    }

    @Override
    public Object convert(String value, Class<?> targetType, Object originalValue, Object containingObject, String fieldName) {
        try {
            if (targetType.isEnum()) {
                return convertEnum(value, targetType);
            }
            
            if (targetType.isArray()) {
                return convertArray(value, targetType);
            }
            
            return switch (targetType.getName()) {
                case "java.util.UUID" -> UUID.fromString(value);
                case "java.net.URL" -> new URL(value);
                case "java.net.URI" -> new URI(value);
                case "java.io.File" -> new File(value);
                case "java.nio.file.Path" -> Path.of(value);
                case "java.util.Locale" -> Locale.forLanguageTag(value);
                case "java.util.Currency" -> Currency.getInstance(value);
                case "java.lang.Class" -> Class.forName(value);
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts enum values with case-insensitive matching.
     * First tries exact match, then falls back to case-insensitive search.
     * 
     * @param value the string value to convert to enum
     * @param enumType the target enum class
     * @return matching enum constant or null if not found
     */
    @SuppressWarnings("unchecked")
    private Object convertEnum(String value, Class<?> enumType) {
        try {
            Class<Enum> enumClass = (Class<Enum>) enumType;
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try case-insensitive match
            for (Enum<?> constant : enumType.asSubclass(Enum.class).getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(value)) {
                    return constant;
                }
            }
            return null;
        }
    }
    
    /**
     * Converts string values to basic array types.
     * Creates single-element arrays for supported component types.
     * 
     * @param value the string value to convert
     * @param arrayType the target array type
     * @return single-element array or null for unsupported types
     */
    private Object convertArray(String value, Class<?> arrayType) {
        Class<?> componentType = arrayType.getComponentType();
        
        if (componentType == String.class) {
            return new String[]{value};
        }
        
        if (componentType == int.class) {
            try {
                int intValue = Integer.parseInt(value);
                return new int[]{intValue};
            } catch (NumberFormatException e) {
                return new int[0];
            }
        }
        
        // Add more array types as needed
        return null;
    }
}