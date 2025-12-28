/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.api.converter;

/**
 * Base interface for type conversion in the masking system.
 * Defines the contract for converting string maskme values to specific field types
 * with full context awareness including original values and containing objects.
 * 
 * <p>Implementations should handle specific type categories:
 * - PrimitiveConverter: String, Character, Boolean
 * - NumberConverter: All numeric types with special logic
 * - DateTimeConverter: Temporal types
 * - SpecialTypeConverter: UUID, URL, Enum, etc.
 * - FallbackConverter: Reflection-based last resort
 * 
 * <p>Use cases:
 * - Convert maskme strings to appropriate field types
 * - Handle context-aware masking with field placeholders
 * - Manipulate original values when maskme is blank
 * - Provide field-specific conversion logic
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
public interface Converter {
    
    /**
     * Determines if this converter can handle the specified type.
     * Called by ConverterFactory to find the appropriate converter for a field type.
     * 
     * @param type the target type to check
     * @return true if this converter supports the type
     */
    boolean canConvert(Class<?> type);
    
    /**
     * Converts string maskme value to the target type with full context.
     * Implementations should handle type-specific conversion logic,
     * placeholder replacement, and original value manipulation.
     * 
     * <p>Context usage examples:
     * - fieldName="email" for email-specific domain replacement
     * - originalValue for blank maskme manipulation
     * - containingObject for field placeholder resolution
     * 
     * @param value the string maskme value to convert
     * @param targetType the target field type
     * @param originalValue the original field value for context
     * @param containingObject the object containing this field
     * @param fieldName the name of the field being processed
     * @return converted value or null if conversion not possible
     */
    Object convert(String value, Class<?> targetType, Object originalValue, Object containingObject, String fieldName);

    /**
     * Gets the priority of this converter (higher = earlier in a chain).
     * The default is 0. User custom converters should use > 0 for higher priority.
     */
    default int getPriority() {
        return 0;
    }
}