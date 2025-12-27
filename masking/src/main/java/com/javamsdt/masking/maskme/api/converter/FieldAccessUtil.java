/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.api.converter;

import com.javamsdt.masking.maskme.api.MaskingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NoArgsConstructor;

/**
 * Utility class for field access and placeholder replacement in maskme values.
 * Provides unified access to field values for both regular classes and records,
 * with support for context-aware masking using field placeholders.
 * 
 * <p>Placeholder format: [fieldName] where fieldName is any field in the containing object
 * 
 * <p>Use cases:
 * - Replace [name] with actual name field value in email masks
 * - Access field values for context-aware converter logic
 * - Support dynamic maskme patterns like "[firstName].[lastName]@company.com"
 * - Enable cross-field referencing in masking scenarios
 * 
 * <p>Example usage:
 * <pre>{@code
 * // MaskMe value: "[name]@masked.com"
 * // Object: {name: "john", email: "john@real.com"}
 * // Result: "john@masked.com"
 * }</pre>
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class FieldAccessUtil {
    
    private static final Pattern CONTEXT_PATTERN = Pattern.compile("\\[([^]]+)]");

    private static  Class<?> clazz;

    /**
     * Extracts field value from containing object by field name.
     * Handles both regular classes and records with unified access pattern.
     * 
     * <p>Use case: Get "name" field value for placeholder replacement
     * 
     * @param containingObject the object containing the field
     * @param fieldName the name of the field to access
     * @return field value or null if not found/accessible
     */
    public static Object getFieldValue(Object containingObject, String fieldName) {
        if (containingObject == null || fieldName == null) {
            return null;
        }
        
        Class<?> clazz = containingObject.getClass();
        FieldAccessUtil.clazz = clazz;
        try {
            if (clazz.isRecord()) {
                return getRecordFieldValue(containingObject, fieldName);
            } else {
                return getRegularFieldValue(containingObject, fieldName);
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Detects if a maskme value contains field placeholders in [fieldName] format.
     * Used to determine if placeholder replacement is needed.
     * 
     * <p>Examples:
     * - "[name]@company.com" → true
     * - "***" → false
     * - "user-[id]-masked" → true
     * 
     * @param value the maskme value to check
     * @return true if placeholders are present
     */
    public static boolean hasFieldPlaceholders(String value) {
        return value != null && CONTEXT_PATTERN.matcher(value).find();
    }
    
    /**
     * Replaces all field placeholders in maskme value with actual field values.
     * Processes multiple placeholders and handles missing fields gracefully.
     * 
     * <p>Replacement process:
     * 1. Find all [fieldName] patterns
     * 2. Extract field values from containing object
     * 3. Replace placeholders with string representation of values
     * 4. Return processed maskme value
     * 
     * @param maskValue the maskme value containing placeholders
     * @param containingObject the object to extract field values from
     * @return processed maskme value with placeholders replaced
     */
    public static String replaceFieldPlaceholders(String maskValue, Object containingObject) {
        if (maskValue == null || containingObject == null) {
            return maskValue;
        }
        
        Matcher matcher = CONTEXT_PATTERN.matcher(maskValue);
        if (!matcher.find()) {
            return maskValue;
        }
        
        String result = maskValue;
        matcher.reset();
        
        while (matcher.find()) {
            String placeholder = matcher.group(0); // [fieldName]
            String fieldName = matcher.group(1);   // fieldName
            
            Object fieldValue = getFieldValue(containingObject, fieldName);
            if (fieldValue != null) {
                result = result.replace(placeholder, fieldValue.toString());
            }
        }
        
        return result;
    }
    
    private static Object getRecordFieldValue(Object recordToProcess, String fieldName) throws InvocationTargetException, IllegalAccessException {
        RecordComponent[] components = recordToProcess.getClass().getRecordComponents();
        for (RecordComponent component : components) {
            if (component.getName().equals(fieldName)) {
                Method accessor = component.getAccessor();
                return accessor.invoke(recordToProcess);
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "java:S3011"})
    private static Object getRegularFieldValue(Object object, String fieldName) {
        Class<?> currentClass = object.getClass();
        while (currentClass != null && currentClass != Object.class) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }


    public static Class<?> getClazz() {
        return FieldAccessUtil.clazz;
    }

    public static Field getFieldByName(String field)  {
        try {
            return clazz.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            throw new MaskingException("No field with name= " + field + " exists in class=" + clazz.getSimpleName(), e);
        }
    }
}