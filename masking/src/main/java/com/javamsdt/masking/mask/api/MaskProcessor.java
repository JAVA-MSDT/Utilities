package com.javamsdt.masking.mask.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.logging.Logger;

public class MaskProcessor {

    private final static Logger LOGGER = Logger.getLogger(MaskProcessor.class.getName());
    private static final MaskProcessor INSTANCE = new MaskProcessor();
    private final ThreadLocal<Map<Class<?>, Object>> conditionInputs = new ThreadLocal<>();

    // Prevent infinite recursion with circular references
    private final ThreadLocal<Set<Object>> processingObjects =
            ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    private MaskProcessor() {
        LOGGER.info("MaskProcessor initialized");
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
        Map<Class<?>, Object> inputs = conditionInputs.get();
        if (inputs != null) {
            LOGGER.info("Conditional inputs have " + inputs.size() + " Objects.");
            conditionInputs.remove();
        }
        processingObjects.remove();
    }

    /**
     * Process with additional input for conditions
     */
    public <T> T process(T object) {
        if (object == null) {
            return null;
        }

        // Check for circular references
        if (processingObjects.get().contains(object)) {
            LOGGER.warning("Circular reference detected, returning original object");
            return object;
        }

        try {
            processingObjects.get().add(object);

            Class<?> clazz = object.getClass();

            if (clazz.isRecord()) {
                return processRecord(object);
            } else {
                return processRegularClass(object);
            }
        } finally {
            processingObjects.get().remove(object);
            clearInputs();
        }
    }

    /**
     * Process regular class with recursive embedded object support
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
                        // Mask this field
                        Object maskedValue = convertToFieldType(annotation.maskValue(), field.getType());
                        field.set(result, maskedValue);
                    } else {
                        // Check if a field is an embedded object that needs recursive processing
                        if (shouldProcessEmbeddedObject(field, fieldValue)) {
                            // Recursively process the embedded object
                            Object processedEmbeddedObject = process(fieldValue);
                            field.set(result, processedEmbeddedObject);
                        } else {
                            // Keep the original value
                            field.set(result, fieldValue);
                        }
                    }
                }
                currentClass = currentClass.getSuperclass();
            }

            return result;

        } catch (Exception e) {
            LOGGER.severe("Failed to process regular class: " + e.getMessage());
            return object;
        } finally {
            clearInputs();
        }
    }

    /**
     * Process record with recursive embedded object support
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
                    // Check if this is an embedded object that needs recursive processing
                    if (shouldProcessEmbeddedObject(component, originalValue)) {
                        args[i] = process(originalValue);
                    } else {
                        args[i] = originalValue;
                    }
                }
            }

            return (T) constructor.newInstance(args);

        } catch (Exception e) {
            LOGGER.severe("Failed to process record: " + e.getMessage());
            return record;
        } finally {
            clearInputs();
        }
    }

    /**
     * Determine if an embedded object should be processed recursively
     */
    private boolean shouldProcessEmbeddedObject(Object fieldOrComponent, Object fieldValue) {
        if (fieldValue == null) {
            return false;
        }

        Class<?> fieldType = getFieldType(fieldOrComponent);

        // Skip primitive types, wrappers, and common Java types
        if (fieldType.isPrimitive() ||
                fieldType.isEnum() ||
                isJavaLangType(fieldType) ||
                isJavaTimeType(fieldType) ||
                isCommonJavaType(fieldType)) {
            return false;
        }

        // Check if the embedded object has any @Mask annotations
        return hasMaskAnnotations(fieldValue.getClass());
    }

    /**
     * Get field type from Field or RecordComponent
     */
    private Class<?> getFieldType(Object fieldOrComponent) {
        if (fieldOrComponent instanceof Field) {
            return ((Field) fieldOrComponent).getType();
        } else if (fieldOrComponent instanceof RecordComponent) {
            return ((RecordComponent) fieldOrComponent).getType();
        }
        return Object.class;
    }

    /**
     * Check if a class has any @Mask annotations on its fields/components
     */
    private boolean hasMaskAnnotations(Class<?> clazz) {
        // Check regular class fields
        if (!clazz.isRecord()) {
            Class<?> currentClass = clazz;
            while (currentClass != null && currentClass != Object.class) {
                for (Field field : currentClass.getDeclaredFields()) {
                    if (field.getAnnotation(Mask.class) != null) {
                        return true;
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
        } else {
            // Check record components
            for (RecordComponent component : clazz.getRecordComponents()) {
                if (component.getAnnotation(Mask.class) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if type is from java.lang package
     */
    private boolean isJavaLangType(Class<?> type) {
        return type.getPackage() != null &&
                type.getPackage().getName().equals("java.lang") &&
                !type.isPrimitive();
    }

    /**
     * Check if the type is from the java Time package
     */
    private boolean isJavaTimeType(Class<?> type) {
        return type.getPackage() != null &&
                type.getPackage().getName().equals("java.time");
    }

    /**
     * Check if the type is a common Java type that shouldn't be recursively processed
     */
    private boolean isCommonJavaType(Class<?> type) {
        String typeName = type.getName();
        return typeName.startsWith("java.math.") ||
                typeName.startsWith("java.net.") ||
                typeName.startsWith("java.io.") ||
                typeName.startsWith("java.nio.") ||
                typeName.startsWith("java.util.") && !typeName.contains("$") ||
                type == UUID.class ||
                type == Locale.class ||
                type == Currency.class ||
                type == Class.class;
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
                LOGGER.warning("Failed to instantiate condition: " + conditionClass.getName());
            }
        }
        return false;
    }

    private Object convertToFieldType(String maskValue, Class<?> fieldType) {
        return TypeConverter.convertToFieldType(maskValue, fieldType);
    }
}