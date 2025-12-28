/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.api.converter;


import com.javamsdt.masking.maskme.implemintation.converter.DateTimeConverter;
import com.javamsdt.masking.maskme.implemintation.converter.NumberConverter;
import com.javamsdt.masking.maskme.implemintation.converter.PrimitiveConverter;
import com.javamsdt.masking.maskme.implemintation.converter.SpecialTypeConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry that manages all converters with user customization support.
 * Users can add their custom converters with priority over default ones.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConverterRegistry {

    // Thread-safe list for user custom converters
    private static final List<Converter> USER_CONVERTERS = new CopyOnWriteArrayList<>();

    // Default converters (lower priority)
    private static final List<Converter> DEFAULT_CONVERTERS = List.of(
            new PrimitiveConverter(),
            new NumberConverter(),
            new DateTimeConverter(),
            new SpecialTypeConverter(),
            new FallbackConverter()
    );

    /**
     * Add user's custom converter (higher priority than defaults)
     */
    public static void registerConverter(Converter converter) {
        if (converter != null) {
            USER_CONVERTERS.add(converter);
        }
    }

    /**
     * Remove user's custom converter
     */
    public static void unregisterConverter(Converter converter) {
        USER_CONVERTERS.remove(converter);
    }

    /**
     * Clear all user converters
     */
    public static void clearUserConverters() {
        USER_CONVERTERS.clear();
    }

    /**
     * Get all converters in priority order
     */
    private static List<Converter> getAllConverters() {
        // Combine user converters (higher priority) with defaults
        List<Converter> allConverters = new ArrayList<>();
        allConverters.addAll(USER_CONVERTERS);
        allConverters.addAll(DEFAULT_CONVERTERS);

        // Sort by priority (descending) - higher priority first
        allConverters.sort((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority()));

        return allConverters;
    }

    /**
     * Converts string maskme value to the target field type using a converter chain.
     * Iterates through specialized converters until one can handle the target type.
     * Provides full context including original value and containing an object.
     *
     * <p>Conversion examples:
     * - maskValue="***", fieldType=String.class → "***"
     * - maskValue="1900-01-01", fieldType=LocalDate.class → LocalDate.of(1900,1,1)
     * - maskValue="", fieldType=BigDecimal.class → rounded original value
     *
     * @param maskValue the string value to convert (can be null)
     * @param fieldType the target type for conversion
     * @param originalValue the original field value for context
     * @param containingObject the object containing this field
     * @param fieldName the name of the field being processed
     * @return converted value or appropriate default
     */
    public static Object convertToFieldType(String maskValue, Class<?> fieldType,
                                            Object originalValue, Object containingObject,
                                            String fieldName) {

        if (maskValue == null) {
            return getDefaultValue(fieldType);
        }

        for (Converter converter : getAllConverters()) {
            if (converter.canConvert(fieldType)) {
                Object result = converter.convert(maskValue, fieldType, originalValue,
                        containingObject, fieldName);
                if (result != null || !shouldTryNextConverter(converter)) {
                    return result;
                }
            }
        }

        return getDefaultValue(fieldType);
    }

    /**
     * Determines if the converter chain should continue after a null result.
     * Stops chain progression when FallbackConverter is reached as it's the final option.
     *
     * @param converter the converter that returned null
     * @return true if the next converter should be tried
     */
    private static boolean shouldTryNextConverter(Converter converter) {
        // Don't try the next converter if this is the fallback converter
        return !(converter instanceof FallbackConverter);
    }

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
