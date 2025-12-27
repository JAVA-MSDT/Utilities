/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.api.converter;

import org.jspecify.annotations.NonNull;
import java.lang.reflect.InvocationTargetException;

/**
 * Last-resort converter using reflection for unknown types.
 * Attempts conversion using string constructors or default constructors
 * when specialized converters cannot handle the target type.
 * 
 * <p>Conversion strategies:
 * 1. Try constructor that accepts String parameter
 * 2. Try default constructor with setValue(String) method
 * 3. Return empty instance from default constructor
 * 4. Return null if all strategies fail
 * 
 * <p>Use cases:
 * - Handle custom domain objects with string constructors
 * - Support third-party types not covered by other converters
 * - Provide graceful degradation for unknown types
 * - Enable extensibility without modifying core converters
 * 
 * <p>Note: This converter always returns true for canConvert()
 * as it serves as the final fallback in the converter chain.
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
public class FallbackConverter implements Converter {
    
    @Override
    public boolean canConvert(Class<?> type) {
        return true; // Always can attempt conversion as fallback
    }
    
    @Override
    public Object convert(String value, Class<?> targetType, Object originalValue, Object containingObject, String fieldName) {
        // Try string constructor
        try {
            return targetType.getConstructor(String.class).newInstance(value);
        } catch (Exception e) {
            // Try default constructor with setValue method
            try {
                return getInstanceFromDefaultConstructor(value, targetType);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    /**
     * Creates instance using default constructor and attempts to set value.
     * Tries to call setValue(String) method if available, otherwise returns empty instance.
     * 
     * @param value the string value to set
     * @param targetType the target class type
     * @return configured instance or empty instance
     * @throws InstantiationException if constructor fails
     * @throws IllegalAccessException if constructor not accessible
     * @throws InvocationTargetException if constructor throws exception
     * @throws NoSuchMethodException if default constructor not found
     */
    private static @NonNull Object getInstanceFromDefaultConstructor(String value, Class<?> targetType) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object instance = targetType.getDeclaredConstructor().newInstance();
        try {
            targetType.getMethod("setValue", String.class).invoke(instance, value);
            return instance;
        } catch (Exception ex) {
            // Return the empty instance
            return instance;
        }
    }
}