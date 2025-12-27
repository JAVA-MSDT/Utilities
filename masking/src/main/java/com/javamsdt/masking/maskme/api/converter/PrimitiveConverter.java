/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.api.converter;

import java.util.Set;

/**
 * Converter for primitive and basic wrapper types.
 * Handles String, Character, and Boolean conversions with field-specific logic
 * for context-aware masking scenarios.
 * 
 * <p>Special behaviors:
 * - String fields: Support field placeholders and targeted field logic
 * - Email fields: Domain replacement when original contains '@'
 * - Name fields: Append custom markers for identification
 * - Character fields: Handle empty strings and primitive defaults
 * - Boolean fields: Parse various true/false representations
 * 
 * <p>Use cases:
 * - Convert "***" to masked string values
 * - Replace email domains with placeholder values
 * - Handle boolean flags from string representations
 * - Process character masks for single-char fields
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
public class PrimitiveConverter implements Converter {
    
    private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(
        String.class, Character.class, char.class, 
        Boolean.class, boolean.class
    );
    
    @Override
    public boolean canConvert(Class<?> type) {
        return SUPPORTED_TYPES.contains(type);
    }
    
    @Override
    public Object convert(String value, Class<?> targetType, Object originalValue, Object containingObject, String fieldName) {
        // Handle context placeholders first
        String processedValue = FieldAccessUtil.hasFieldPlaceholders(value) ? 
            FieldAccessUtil.replaceFieldPlaceholders(value, containingObject) : value;
        
        return switch (targetType.getName()) {
            case "java.lang.String" -> handleStringConversion(processedValue, originalValue, fieldName);
            case "java.lang.Character", "char" -> handleCharacterConversion(processedValue, targetType);
            case "java.lang.Boolean", "boolean" -> handleBooleanConversion(processedValue, targetType);
            default -> null;
        };
    }
    
    /**
     * Handles string conversion with field-specific logic and placeholder support.
     * Applies targeted behavior based on field name for context-aware masking.
     * 
     * <p>Field-specific behaviors:
     * - "email": Replaces domain part when original contains '@'
     * - "name": Appends "[][]" markers for identification
     * - Other fields: Returns processed value as-is
     * 
     * @param processedValue the maskme value after placeholder replacement
     * @param originalValue the original field value for context
     * @param fieldName the name of the field being processed
     * @return field-specific processed string value
     */
    private String handleStringConversion(String processedValue, Object originalValue, String fieldName) {
        // Handle email-specific logic for strings
        if ("email".equals(fieldName) && originalValue instanceof String originalEmail && 
            originalEmail.contains("@") && processedValue != null && !processedValue.equals(originalEmail)) {
            
            String[] parts = originalEmail.split("@");
            if (parts.length == 2) {
                // Replace domain: email@[processedValue]
                return parts[0] + "@" + processedValue + ".domain";
            }
        }
        
        // For name field, append the brackets as requested
        if ("name".equals(fieldName)) {
            return processedValue + "[][]";
        }
        
        return processedValue;
    }
    
    /**
     * Converts string to Character type with proper null handling.
     * Handles both primitive char and wrapper Character types.
     * 
     * @param value the string value to convert
     * @param targetType char.class or Character.class
     * @return first character or appropriate default
     */
    private Object handleCharacterConversion(String value, Class<?> targetType) {
        if (value.isEmpty()) {
            return targetType == char.class ? '\0' : null;
        }
        return value.charAt(0);
    }
    
    /**
     * Converts string to Boolean type with flexible parsing.
     * Supports multiple representations of true/false values.
     * 
     * <p>True values: "true" (case-insensitive), "1"
     * <p>False values: "false" (case-insensitive), "0"
     * 
     * @param value the string value to parse
     * @param targetType boolean.class or Boolean.class
     * @return parsed boolean or appropriate default
     */
    private Object handleBooleanConversion(String value, Class<?> targetType) {
        if ("true".equalsIgnoreCase(value) || "1".equals(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value) || "0".equals(value)) {
            return false;
        }
        return targetType == boolean.class ? false : null;
    }
}