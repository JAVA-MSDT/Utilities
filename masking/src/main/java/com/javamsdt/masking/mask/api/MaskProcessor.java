/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.mask.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MaskProcessor {

    private static final MaskProcessor INSTANCE = new MaskProcessor();
    private final ThreadLocal<Map<Class<?>, Object>> conditionInputs = new ThreadLocal<>();

    private MaskProcessor() {
    }

    public static MaskProcessor getInstance() {
        return INSTANCE;
    }

    /**
     * Set input for a specific condition class
     */
    public void setConditionInput(Class<? extends MaskCondition> conditionClass, Object input) {
        Map<Class<?>, Object> inputs = conditionInputs.get();
        if (inputs == null) {
            inputs = new HashMap<>();
            conditionInputs.set(inputs);
        }
        inputs.put(conditionClass, input);
    }

    /**
     * Clear thread-local inputs
     */
    public void clearInputs() {
        System.out.println("Conditional inputs has #" + conditionInputs.get().size() + " Objects.");
        conditionInputs.remove();
        System.out.println("Conditional inputs cleared now is=" + conditionInputs.get());
    }

    /**
     * Process with additional input for conditions
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
     * Process regular class
     */
    @SuppressWarnings("unchecked")
    private <T> T processRegularClass(T object) {
        try {
            Class<T> clazz = (Class<T>) object.getClass();
            T result = clazz.getDeclaredConstructor().newInstance();

            Class<?> currentClass = clazz;
            while (currentClass != null && currentClass != Object.class) {
                for (Field field : currentClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(object);

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
            return object;
        } finally {
            clearInputs();
        }
    }

    /**
     * Process record
     */
    @SuppressWarnings("unchecked")
    private <T> T processRecord(T record) {
        try {
            Class<?> recordClass = record.getClass();
            RecordComponent[] components = recordClass.getRecordComponents();

            Class<?>[] paramTypes = Arrays.stream(components)
                    .map(RecordComponent::getType)
                    .toArray(Class[]::new);
            Constructor<?> constructor = recordClass.getDeclaredConstructor(paramTypes);

            Object[] args = new Object[components.length];

            for (int i = 0; i < components.length; i++) {
                RecordComponent component = components[i];
                Method accessor = component.getAccessor();
                Object originalValue = accessor.invoke(record);

                Mask annotation = component.getAnnotation(Mask.class);
                if (annotation != null && shouldMask(annotation, originalValue, record)) {
                    args[i] = convertToFieldType(annotation.maskValue(), component.getType());
                } else {
                    args[i] = originalValue;
                }
            }

            return (T) constructor.newInstance(args);

        } catch (Exception e) {
            return record;
        } finally {
            clearInputs();
        }
    }

    /**
     * Check if the field should be masked with input support
     */
    private boolean shouldMask(Mask annotation, Object fieldValue, Object containingObject) {
        for (Class<? extends MaskCondition> conditionClass : annotation.conditions()) {
            try {
                MaskCondition condition = conditionClass.getDeclaredConstructor().newInstance();

                // Apply input if available
                Map<Class<?>, Object> inputs = conditionInputs.get();
                if (inputs != null && inputs.containsKey(conditionClass)) {
                    condition.setInput(inputs.get(conditionClass));
                }

                if (condition.shouldMask(fieldValue, containingObject)) {
                    return true;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return false;
    }

    private Object convertToFieldType(String maskValue, Class<?> fieldType) {
        if (fieldType == String.class) {
            return maskValue;
        }
        if (fieldType == LocalDate.class) {
            try {
                return LocalDate.parse(maskValue, DateTimeFormatter.ISO_DATE);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}