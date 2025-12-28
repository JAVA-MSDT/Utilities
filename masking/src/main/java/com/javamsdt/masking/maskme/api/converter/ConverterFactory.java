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

import java.util.List;

/**
 * Factory for orchestrating type conversion using specialized converter chain.
 * Manages the conversion of maskme values to appropriate field types
 * with support for context-aware processing and field-specific logic.
 * 
 * <p>Converter chain order (first match wins):
 * 1. PrimitiveConverter - String, Character, Boolean
 * 2. NumberConverter - Numeric types with special BigDecimal logic
 * 3. DateTimeConverter - Temporal types
 * 4. SpecialTypeConverter - UUID, URL, Enum, etc.
 * 5. FallbackConverter - Reflection-based last resort
 * 
 * <p>Use cases:
 * - Convert "***" to String for text fields
 * - Convert "1900-01-01" to LocalDate for date fields
 * - Convert "0" to Integer for numeric fields
 * - Handle blank masks for original value manipulation
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConverterFactory {

    private static final List<Converter> CONVERTERS = List.of(
        new PrimitiveConverter(),
        new NumberConverter(),
        new DateTimeConverter(),
        new SpecialTypeConverter(),
        new FallbackConverter() // Always last
    );

    /**
     * Converts string maskme value to the target field type using converter chain.
     * Iterates through specialized converters until one can handle the target type.
     * Provides full context including original value and containing object.
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
    public static Object convertToFieldType(String maskValue, Class<?> fieldType, Object originalValue, Object containingObject, String fieldName) {
        if (maskValue == null) {
            return getDefaultValue(fieldType);
        }

        for (Converter converter : CONVERTERS) {
            if (converter.canConvert(fieldType)) {
                Object result = converter.convert(maskValue, fieldType, originalValue, containingObject, fieldName);
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

    /**
     * Provides appropriate default values for primitive types when conversion fails.
     * Returns null for reference types and type-specific defaults for primitives.
     * 
     * @param type the target type needing a default value
     * @return default value for the type
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

