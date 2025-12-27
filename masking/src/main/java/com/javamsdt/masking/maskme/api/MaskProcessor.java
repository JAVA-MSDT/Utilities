package com.javamsdt.masking.maskme.api;

import com.javamsdt.masking.maskme.api.converter.ConverterFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Core engine for processing objects with @MaskMe annotations.
 * Handles both regular Java classes and Records with support for nested objects,
 * conditional masking, and thread-safe operation.
 * 
 * <p>Use cases:
 * - REST API response masking based on user roles
 * - Conditional data hiding in multi-tenant applications
 * - Audit log sanitization
 * - Dynamic field masking for different client types
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
@Slf4j
@Component
public final class MaskProcessor {

    private final ThreadLocal<Map<Class<?>, Object>> conditionInputs = new ThreadLocal<>();

    // Prevent infinite recursion with circular references
    private final ThreadLocal<Set<Object>> processingObjects =
            ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    public MaskProcessor() {
        log.info("MaskProcessor initialized");
    }

    /**
     * Associates runtime input with a specific maskme condition class.
     * This input will be passed to condition instances during masking evaluation.
     * 
     * <p>Use case: Pass user role to RoleBasedMaskCondition
     * <pre>{@code
     * processor.setConditionInput(RoleBasedCondition.class, UserRole.ADMIN);
     * }</pre>
     * 
     * @param conditionClass the condition class to receive the input
     * @param input the runtime input for the condition
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
     * Clears all condition inputs from current thread to prevent memory leaks.
     * Must be called after processing to ensure proper cleanup in multi-threaded environments.
     * 
     * <p>Use case: Always call in finally block
     * <pre>{@code
     * try {
     *     processor.setConditionInput(SomeCondition.class, input);
     *     return processor.process(dto);
     * } finally {
     *     processor.clearInputs();
     * }
     * }</pre>
     */
    public void clearInputs() {
        Map<Class<?>, Object> inputs = conditionInputs.get();
        if (inputs != null) {
            log.info("Conditional inputs have {} Objects.", inputs.size());
            conditionInputs.remove();
        }
        processingObjects.remove();
    }

    /**
     * Processes an object by applying @MaskMe annotations with conditional logic.
     * Creates a new instance with masked fields while preserving original object integrity.
     * Handles circular references and supports nested object processing.
     * 
     * <p>Use cases:
     * - MaskMe sensitive user data in API responses
     * - Apply role-based field visibility
     * - Sanitize objects for logging
     * 
     * @param <T> the type of object to process
     * @param object the object to maskme (can be null)
     * @return new masked instance or null if input is null
     */
    public <T> T process(T object) {
        if (object == null) {
            return null;
        }

        // Check for circular references
        if (processingObjects.get().contains(object)) {
            log.warn("Circular reference detected, returning original object");
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
        }
    }

    /**
     * Processes regular Java classes using reflection to access fields.
     * Creates new instance via default constructor and copies/masks fields based on annotations.
     * Supports inheritance hierarchy traversal and embedded object processing.
     * 
     * @param <T> the type of regular class
     * @param object the class instance to process
     * @return new masked instance or original if processing fails
     */
    @SuppressWarnings({"unchecked", "java:S3011"})
    private <T> T processRegularClass(T object) {
        try {
            Class<T> clazz = (Class<T>) object.getClass();
            T result = clazz.getDeclaredConstructor().newInstance();

            Class<?> currentClass = clazz;
            while (currentClass != null && currentClass != Object.class) {
                for (Field field : currentClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(object);

                    MaskMe annotation = field.getAnnotation(MaskMe.class);

                    if (annotation != null && shouldMask(annotation, fieldValue, object)) {
                        // MaskMe this field
                        Object maskedValue = convertToFieldType(annotation.maskValue(), field.getType(), fieldValue, object, field.getName());
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
            log.warn("Failed to process regular class: {}", e.getMessage());
            return object;
        }
    }

    /**
     * Processes Java Records using record components and canonical constructor.
     * Builds new record instance with masked component values based on annotations.
     * Leverages record accessor methods for field value extraction.
     * 
     * @param <T> the record type
     * @param recordToProcess the record instance to process
     * @return new masked record instance or original if processing fails
     */
    @SuppressWarnings("unchecked")
    private <T> T processRecord(T recordToProcess) {
        try {
            Class<?> recordClass = recordToProcess.getClass();
            RecordComponent[] components = recordClass.getRecordComponents();

            Class<?>[] paramTypes = Arrays.stream(components)
                    .map(RecordComponent::getType)
                    .toArray(Class[]::new);
            Constructor<?> constructor = recordClass.getDeclaredConstructor(paramTypes);

            Object[] args = new Object[components.length];

            for (int i = 0; i < components.length; i++) {
                RecordComponent component = components[i];
                Method accessor = component.getAccessor();
                Object originalValue = accessor.invoke(recordToProcess);

                MaskMe annotation = component.getAnnotation(MaskMe.class);
                if (annotation != null && shouldMask(annotation, originalValue, recordToProcess)) {
                    args[i] = convertToFieldType(annotation.maskValue(), component.getType(), originalValue, recordToProcess, component.getName());
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
            log.warn("Failed to process recordToProcess: {}", e.getMessage());
            return recordToProcess;
        }
    }

    /**
     * Determines whether a field value should be recursively processed for masking.
     * Skips primitive types, Java standard types, and objects without @MaskMe annotations.
     * Prevents unnecessary processing of simple types and circular references.
     * 
     * @param fieldOrComponent Field or RecordComponent being evaluated
     * @param fieldValue the actual field value
     * @return true if field should be recursively processed
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

        // Check if the embedded object has any @MaskMe annotations
        return hasMaskAnnotations(fieldValue.getClass());
    }

    /**
     * Extracts the declared type from either a Field or RecordComponent.
     * Provides unified type access for both regular classes and records.
     * 
     * @param fieldOrComponent Field or RecordComponent instance
     * @return the declared type of the field/component
     */
    private Class<?> getFieldType(Object fieldOrComponent) {
        if (fieldOrComponent instanceof Field field) {
            return  field.getType();
        } else if (fieldOrComponent instanceof RecordComponent recordField) {
            return recordField.getType();
        }
        return Object.class;
    }

    /**
     * Scans a class to determine if it contains any @MaskMe annotations.
     * Used to optimize processing by skipping classes without masking requirements.
     * Handles both regular classes and records appropriately.
     * 
     * @param clazz the class to scan
     * @return true if class has @MaskMe annotations
     */
    private boolean hasMaskAnnotations(Class<?> clazz) {
        if (!clazz.isRecord()) {
            return checkRegularClassFields(clazz);
        } else {
            return checkRecordComponents(clazz);
        }
    }

    private boolean checkRegularClassFields(Class<?> clazz) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (field.getAnnotation(MaskMe.class) != null) {
                    return true;
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return false;
    }

    private boolean checkRecordComponents(Class<?> clazz) {
        for (RecordComponent component : clazz.getRecordComponents()) {
            if (component.getAnnotation(MaskMe.class) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Identifies types from java.lang package (excluding primitives).
     * Used to skip recursive processing of standard Java types like String, Integer.
     * 
     * @param type the class type to check
     * @return true if type is from java.lang package
     */
    private boolean isJavaLangType(Class<?> type) {
        return type.getPackage() != null &&
                type.getPackage().getName().equals("java.lang") &&
                !type.isPrimitive();
    }

    /**
     * Identifies temporal types from java.time package.
     * Used to skip recursive processing of date/time types like LocalDate, Instant.
     * 
     * @param type the class type to check
     * @return true if type is from java.time package
     */
    private boolean isJavaTimeType(Class<?> type) {
        return type.getPackage() != null &&
                type.getPackage().getName().equals("java.time");
    }

    /**
     * Identifies common Java API types that should not be recursively processed.
     * Includes math, networking, I/O, collections, and other standard library types.
     * Prevents unnecessary deep traversal of Java framework objects.
     * 
     * @param type the class type to check
     * @return true if type is a common Java API type
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
     * Evaluates all maskme conditions for a field to determine if masking should occur.
     * Creates condition instances, applies runtime inputs, and checks masking criteria.
     * Returns true if any condition indicates the field should be masked.
     * 
     * <p>Use case: Field masked when user lacks admin role
     * 
     * @param annotation the @MaskMe annotation containing conditions
     * @param fieldValue the current field value
     * @param containingObject the object containing this field
     * @return true if field should be masked
     */
    private boolean shouldMask(MaskMe annotation, Object fieldValue, Object containingObject) {
        for (Class<? extends MaskCondition> conditionClass : annotation.conditions()) {
            try {
                MaskCondition condition = MaskConditionFactory.createCondition(conditionClass);

                // Apply input if available
                Map<Class<?>, Object> inputs = conditionInputs.get();
                if (inputs != null && inputs.containsKey(conditionClass)) {
                    condition.setInput(inputs.get(conditionClass));
                }

                if (condition.shouldMask(fieldValue, containingObject)) {
                    return true;
                }
            } catch (Exception e) {
                log.warn("Failed to instantiate condition: {}", conditionClass.getName());
            }
        }
        return false;
    }
    
    private Object convertToFieldType(String maskValue, Class<?> fieldType, Object originalValue, Object containingObject, String fieldName) {
        return ConverterFactory.convertToFieldType(maskValue, fieldType, originalValue, containingObject, fieldName);
    }
}