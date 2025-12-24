/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.mask;

import java.lang.reflect.*;
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
     * Does NOT modify original object
     */
    public <T> T process(T object) {
        if (object == null) {
            return null;
        }

        Class<?> clazz = object.getClass();
        System.out.println(clazz.getSimpleName());
        // Handle records differently
        if (clazz.isRecord()) {
            return processRecord(object);
        } else {
            return processRegularClass(object);
        }
    }

    /**
     * Process regular class (non-record) - FIXED: Process called only once
     */
    @SuppressWarnings("unchecked")
    private <T> T processRegularClass(T object) {
        try {
            Class<T> clazz = (Class<T>) object.getClass();

            // Create new instance
            T result = createInstance(clazz);

            // Copy ALL fields at once (including inherited)
            copyAllFields(object, result);

            return result;

        } catch (Exception e) {
            System.err.println("Failed to process class: " + e.getMessage());
            return object; // Return original if fails
        }
    }

    /**
     * Copy ALL fields at once - prevents recursive processing
     */
    private void copyAllFields(Object source, Object target)
            throws IllegalAccessException {

        Class<?> currentClass = source.getClass();

        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                field.setAccessible(true);

                Object fieldValue = field.get(source);
                Mask annotation = field.getAnnotation(Mask.class);

                if (annotation != null && shouldMask(annotation, fieldValue, source)) {
                    field.set(target, convertToFieldType(
                            annotation.maskValue(),
                            field.getType()
                    ));
                } else {
                    field.set(target, fieldValue);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    /**
     * Process Java record - FIXED: Detect annotations on record components
     */
    @SuppressWarnings("unchecked")
    private <T> T processRecord(T record) {
        try {
            Class<?> recordClass = record.getClass();
            RecordComponent[] components = recordClass.getRecordComponents();

            // Build arguments for canonical constructor
            Object[] constructorArgs = new Object[components.length];

            for (int i = 0; i < components.length; i++) {
                RecordComponent component = components[i];
                Method accessor = component.getAccessor();
                Object originalValue = accessor.invoke(record);

                // FIX: Check for annotation on the ACCESSOR METHOD, not component
                Mask annotation = accessor.getAnnotation(Mask.class);

                // If not on method, check on component
                if (annotation == null) {
                    annotation = component.getAnnotation(Mask.class);
                }

                // If not on component, check on the parameter (for constructor)
                if (annotation == null) {
                    // Get constructor parameters
                    Constructor<?> constructor = recordClass.getDeclaredConstructors()[0];
                    Parameter[] parameters = constructor.getParameters();
                    if (i < parameters.length) {
                        annotation = parameters[i].getAnnotation(Mask.class);
                    }
                }

                if (annotation != null && shouldMask(annotation, originalValue, record)) {
                    constructorArgs[i] = convertToFieldType(
                            annotation.maskValue(),
                            component.getType()
                    );
                } else {
                    constructorArgs[i] = originalValue;
                }
            }

            // Create new record instance with (possibly) masked values
            Constructor<?> canonicalConstructor = recordClass.getDeclaredConstructor(
                    Arrays.stream(components)
                            .map(RecordComponent::getType)
                            .toArray(Class[]::new)
            );

            return (T) canonicalConstructor.newInstance(constructorArgs);

        } catch (Exception e) {
            System.err.println("Failed to process record: " + e.getMessage());
            return record; // Return original if fails
        }
    }

    /**
     * Check if field should be masked
     */
    private boolean shouldMask(Mask annotation, Object fieldValue, Object containingObject) {
        for (Class<? extends MaskCondition> conditionClass : annotation.conditions()) {
            try {
                MaskCondition condition = conditionClass.getDeclaredConstructor().newInstance();
                if (condition.shouldMask(fieldValue, containingObject)) {
                    return true;
                }
            } catch (Exception e) {
                // Skip condition if can't instantiate
                continue;
            }
        }
        return false;
    }

    /**
     * Create instance of class (handles various constructors)
     */
    @SuppressWarnings("unchecked")
    private <T> T createInstance(Class<T> clazz) throws Exception {
        try {
            // Try default constructor first
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            // Try to find any constructor and pass null/default values
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if (constructors.length > 0) {
                Constructor<?> constructor = constructors[0];
                constructor.setAccessible(true);

                // Create default arguments
                Object[] args = new Object[constructor.getParameterCount()];
                Class<?>[] paramTypes = constructor.getParameterTypes();

                for (int i = 0; i < args.length; i++) {
                    args[i] = getDefaultValue(paramTypes[i]);
                }

                return (T) constructor.newInstance(args);
            }
            throw e;
        }
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

        // Handle primitives and wrappers
        if (fieldType == int.class || fieldType == Integer.class) {
            try {
                return Integer.parseInt(maskValue);
            } catch (NumberFormatException e) {
                return fieldType == int.class ? 0 : null;
            }
        }

        if (fieldType == long.class || fieldType == Long.class) {
            try {
                return Long.parseLong(maskValue);
            } catch (NumberFormatException e) {
                return fieldType == long.class ? 0L : null;
            }
        }

        if (fieldType == boolean.class || fieldType == Boolean.class) {
            return Boolean.parseBoolean(maskValue);
        }

        if (fieldType == double.class || fieldType == Double.class) {
            try {
                return Double.parseDouble(maskValue);
            } catch (NumberFormatException e) {
                return fieldType == double.class ? 0.0 : null;
            }
        }

        // For custom objects, try string constructor, then default constructor
        try {
            // Try constructor with String parameter
            Constructor<?> stringConstructor = fieldType.getConstructor(String.class);
            return stringConstructor.newInstance(maskValue);
        } catch (NoSuchMethodException e) {
            try {
                // Try default constructor
                return fieldType.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get default value for type (used for constructor args)
     */
    private Object getDefaultValue(Class<?> type) {
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