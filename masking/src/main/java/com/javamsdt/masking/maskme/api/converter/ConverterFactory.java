/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.api.converter;

import com.javamsdt.masking.maskme.api.masking.MaskMeException;

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
public class ConverterFactory {

    private ConverterFactory() {
        throw new MaskMeException("ConverterFactory is not to be initialized");
    }
    /**
     *  Main conversion method - delegates to registry.
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
        return ConverterRegistry.convertToFieldType(maskValue, fieldType, originalValue,
                containingObject, fieldName);
    }

}

