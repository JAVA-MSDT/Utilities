/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.mask;

import java.lang.reflect.Field;
import java.util.Arrays;

public class MaskProcessor {

    public <T> T process(T object) {
        if (object == null) {
            return null;
        }

        try {
            // Create a copy to avoid modifying the original.
            T result = cloneObject(object);

            // Process all fields
            for (Field field : object.getClass().getDeclaredFields()) {
                processField(field, object, result);
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to process masking", e);
        }
    }

    private <T> void processField(Field field, T original, T result)
            throws IllegalAccessException {

        Mask annotation = field.getAnnotation(Mask.class);
        if (annotation == null) {
            return; // No annotation, skip
        }

        field.setAccessible(true);
        Object fieldValue = field.get(original);

        // Check all conditions
        boolean shouldMask = Arrays.stream(annotation.conditions())
                .anyMatch(conditionClass -> {
                    try {
                        MaskCondition condition = conditionClass.getDeclaredConstructor()
                                .newInstance();
                        return condition.shouldMask(fieldValue, original);
                    } catch (Exception e) {
                        return false; // If the condition can't be instantiated, don't mask.
                    }
                });

        if (shouldMask) {
            field.set(result, annotation.maskValue());
        } else {
            // Keep the original value.
            field.set(result, fieldValue);
        }
    }

    private <T> T cloneObject(T object) throws Exception {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();
        T clone = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            field.set(clone, field.get(object));
        }

        return clone;
    }
}