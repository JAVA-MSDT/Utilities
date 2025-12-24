/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.mask;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class MaskProcessor {

    private static final MaskProcessor INSTANCE = new MaskProcessor();

    private MaskProcessor() {}

    public static MaskProcessor getInstance() {
        return INSTANCE;
    }


    /**
     * Process ANY object (class or record) and return masked COPY
     */
    public <T> T process(T object) {
        if (object == null) {
            return null;
        }

        Class<?> clazz = object.getClass();

        if (clazz.isRecord()) {
            return processRecord(object);
        } else {
            return processRegularClass(object);
        }
    }

    /**
     * Process regular class (non-record)
     */
    @SuppressWarnings("unchecked")
    private <T> T processRegularClass(T object) {
        try {
            Class<T> clazz = (Class<T>) object.getClass();

            // Create a new instance
            T result = clazz.getDeclaredConstructor().newInstance();

            // Process all fields (including parent classes)
            Class<?> currentClass = clazz;
            while (currentClass != null && currentClass != Object.class) {
                for (Field field : currentClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(object);

                    // Check for Mask annotation
                    Mask annotation = field.getAnnotation(Mask.class);
                    if (annotation != null && shouldMask(annotation, fieldValue, object)) {
                        Object maskedValue = convertToFieldType(annotation.maskValue(), field.getType());
                        field.set(result, maskedValue);
                    } else {
                        field.set(result, fieldValue);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }

            return result;

        } catch (Exception e) {
            System.err.println("Error processing record: " + e.getMessage());
            return object; // Return original if fails
        }
    }

    /**
     * Process Java record - FIXED VERSION
     */
    @SuppressWarnings("unchecked")
    private <T> T processRecord(T record) {
        try {
            Class<?> recordClass = record.getClass();
            RecordComponent[] components = recordClass.getRecordComponents();

            // Get the canonical constructor
            Class<?>[] paramTypes = Arrays.stream(components)
                    .map(RecordComponent::getType)
                    .toArray(Class[]::new);
            Constructor<?> constructor = recordClass.getDeclaredConstructor(paramTypes);

            // Build constructor arguments
            Object[] args = new Object[components.length];

            for (int i = 0; i < components.length; i++) {
                RecordComponent component = components[i];
                Method accessor = component.getAccessor();
                Object originalValue = accessor.invoke(record);

                // Annotations on record components are stored on the component
                Mask annotation = component.getAnnotation(Mask.class);

                if (annotation != null && shouldMask(annotation, originalValue, record)) {
                    args[i] = convertToFieldType(annotation.maskValue(), component.getType());
                } else {
                    args[i] = originalValue;
                }
            }

            // Create a new record instance
            return (T) constructor.newInstance(args);

        } catch (Exception e) {
            System.err.println("Error processing record: " + e.getMessage());
            return record; // Return original if fails
        }
    }

    /**
     * Check if the field should be masked
     */
    private boolean shouldMask(Mask annotation, Object fieldValue, Object containingObject) {
        for (Class<? extends MaskCondition> conditionClass : annotation.conditions()) {
            try {
                MaskCondition condition = conditionClass.getDeclaredConstructor().newInstance();
                if (condition.shouldMask(fieldValue, containingObject)) {
                    return true;
                }
            } catch (Exception e) {
                // Skip the condition if you can't instantiate
                System.err.println("Error processing record: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Convert maskValue string to field type
     */
    private Object convertToFieldType(String maskValue, Class<?> fieldType) {
        // Handle String
        if (fieldType == String.class) {
            return maskValue;
        }

        // Handle LocalDate
        if (fieldType == LocalDate.class) {
            try {
                return LocalDate.parse(maskValue, DateTimeFormatter.ISO_DATE);
            } catch (Exception e) {
                return null;
            }
        }

        // Handle primitives
        if (fieldType == int.class) return 0;
        if (fieldType == long.class) return 0L;
        if (fieldType == boolean.class) return false;
        if (fieldType == double.class) return 0.0;

        // For custom objects, return null
        return null;
    }
}