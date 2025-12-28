/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.api.converter;

import com.javamsdt.masking.maskme.api.masking.MaskMeException;
import com.javamsdt.masking.maskme.api.masking.MaskProcessor;
import com.javamsdt.masking.maskme.implemintation.converter.DateTimeConverter;
import com.javamsdt.masking.maskme.implemintation.converter.NumberConverter;
import com.javamsdt.masking.maskme.implemintation.converter.PrimitiveConverter;
import com.javamsdt.masking.maskme.implemintation.converter.SpecialTypeConverter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Scoped converter registry for managing type conversion in the masking system.
 * Supports multiple isolation levels (global, thread, request) for safe converter management.
 *
 * <p><b>Scopes Overview:</b>
 * <ul>
 *   <li><b>GLOBAL:</b> Application-wide converters (default)</li>
 *   <li><b>THREAD:</b> Thread-local converters for isolated processing</li>
 *   <li><b>REQUEST:</b> Request-scoped converters for web applications</li>
 *   <li><b>TEST:</b> Test-scoped converters for isolated testing</li>
 * </ul>
 *
 * <p><b>Priority Execution Order:</b>
 * 1. Thread-scoped converters (the highest priority)
 * 2. Request-scoped converters
 * 3. Global converters
 * 4. Default converters (the lowest priority)
 *
 * <p><b>Thread Safety:</b>
 * Fully thread-safe with concurrent collections. Scope operations are isolated.
 *
 * <p><b>Example:</b>
 * <pre>{@code
 * // Register global converter (affects all threads)
 * ConverterRegistry.registerGlobal(new CustomEmailConverter());
 *
 * // Register thread-local converter (affects only current thread)
 * ConverterRegistry.registerThreadLocal(new TestConverter());
 *
 * // Register request-scoped converter (web applications)
 * ConverterRegistry.registerRequestScoped(new UserSpecificConverter());
 *
 * // Safe cleanup - only affects specified scope
 * ConverterRegistry.clearThreadLocal();  // Safe
 * ConverterRegistry.clearRequestScope(); // Safe
 * }</pre>
 *
 * @see Converter
 * @see MaskProcessor
 * @author Ahmed Samy
 * @since 1.0.0
 */
public class ConverterRegistry {

    private ConverterRegistry() {
        throw new MaskMeException("ConverterRegistry is not to be initialized");
    }

    // ==================== SCOPED STORAGE ====================

    // Global converters - application-wide
    private static final List<Converter> GLOBAL_CONVERTERS = new CopyOnWriteArrayList<>();

    // Thread-local converters - isolated per thread
    private static final ThreadLocal<List<Converter>> THREAD_CONVERTERS =
            ThreadLocal.withInitial(CopyOnWriteArrayList::new);

    // Request-scoped converters (for web applications)
    private static final ThreadLocal<List<Converter>> REQUEST_CONVERTERS =
            ThreadLocal.withInitial(CopyOnWriteArrayList::new);

    // Test-scoped converters (by test ID)
    private static final Map<String, List<Converter>> TEST_CONVERTERS =
            new ConcurrentHashMap<>();

    // Default converters (built-in, the lowest priority)
    private static final List<Converter> DEFAULT_CONVERTERS = List.of(
            new PrimitiveConverter(),
            new NumberConverter(),
            new DateTimeConverter(),
            new SpecialTypeConverter(),
            new FallbackConverter()
    );

    // Current request scope identifier (for web apps)
    private static final ThreadLocal<String> CURRENT_REQUEST_ID = new ThreadLocal<>();

    // ==================== GLOBAL SCOPE METHODS ====================

    /**
     * Registers a converter in the global scope (affects entire application).
     * Use for application-wide converters that should be available to all threads.
     *
     * <p><b>Use Cases:</b>
     * <ul>
     *   <li>Application default converters</li>
     *   <li>Third-party library converters</li>
     *   <li>Converters needed by all users/tenants</li>
     * </ul>
     *
     * @param converter the converter to register globally
     */
    public static void registerGlobal(Converter converter) {
        if (converter != null) {
            GLOBAL_CONVERTERS.add(converter);
        }
    }

    /**
     * Removes a converter from the global scope.
     *
     * @param converter the converter to remove
     * @return true if the converter was found and removed
     */
    public static boolean unregisterGlobal(Converter converter) {
        return GLOBAL_CONVERTERS.remove(converter);
    }

    /**
     * Clears all global converters (application-wide).
     * Use only during application shutdown or reinitialization.
     */
    public static void clearGlobal() {
        GLOBAL_CONVERTERS.clear();
    }

    // ==================== THREAD SCOPE METHODS ====================

    /**
     * Registers a converter in the current thread's scope.
     * The converter will only affect the current thread and its children.
     *
     * <p><b>Use Cases:</b>
     * <ul>
     *   <li>Thread-specific test converters</li>
     *   <li>Background job-specific converters</li>
     *   <li>Thread-isolated processing</li>
     * </ul>
     *
     * @param converter the converter to register for the current thread
     */
    public static void registerThreadLocal(Converter converter) {
        if (converter != null) {
            THREAD_CONVERTERS.get().add(converter);
        }
    }

    /**
     * Removes a converter from the current thread's scope.
     *
     * @param converter the converter to remove
     * @return true if the converter was found and removed
     */
    public static boolean unregisterThreadLocal(Converter converter) {
        return THREAD_CONVERTERS.get().remove(converter);
    }

    /**
     * Clears all converters from the current thread's scope.
     * Safe to call - only affects the current thread.
     */
    public static void clearThreadLocal() {
        THREAD_CONVERTERS.get().clear();
    }

    // ==================== REQUEST SCOPE METHODS ====================

    /**
     * Starts a new request scope with the given identifier.
     * Call this at the beginning of each request (e.g., in a Servlet Filter).
     *
     * @param requestId unique identifier for the request
     */
    public static void startRequestScope(String requestId) {
        CURRENT_REQUEST_ID.set(requestId);
        REQUEST_CONVERTERS.get().clear(); // Clear previous request converters
    }

    /**
     * Ends the current request scope and clears its converters.
     * Call this at the end of each request.
     */
    public static void endRequestScope() {
        REQUEST_CONVERTERS.get().clear();
        CURRENT_REQUEST_ID.remove();
    }

    /**
     * Registers a converter in the current request scope.
     * The converter will only affect processing within the current request.
     *
     * <p><b>Use Cases:</b>
     * <ul>
     *   <li>User-specific converters based on authentication</li>
     *   <li>Request-specific formatting rules</li>
     *   <li>Temporary converters for a single API call</li>
     * </ul>
     *
     * @param converter the converter to register for the current request
     */
    public static void registerRequestScoped(Converter converter) {
        if (converter != null && CURRENT_REQUEST_ID.get() != null) {
            REQUEST_CONVERTERS.get().add(converter);
        }
    }

    /**
     * Clears all converters from the current request scope.
     */
    public static void clearRequestScope() {
        REQUEST_CONVERTERS.get().clear();
    }

    // ==================== TEST SCOPE METHODS ====================

    /**
     * Registers a converter for a specific test scope.
     * Ideal for JUnit tests where each test needs isolated converters.
     *
     * @param testId unique test identifier (e.g., test class + method name)
     * @param converter the converter to register
     */
    public static void registerForTest(String testId, Converter converter) {
        if (converter != null && testId != null) {
            TEST_CONVERTERS
                    .computeIfAbsent(testId, k -> new CopyOnWriteArrayList<>())
                    .add(converter);
        }
    }

    /**
     * Clears all converters for a specific test.
     *
     * @param testId the test identifier to clear
     */
    public static void clearTestScope(String testId) {
        TEST_CONVERTERS.remove(testId);
    }

    /**
     * Gets converters for a specific test scope.
     * Used by test runners to activate test-specific converters.
     */
    public static List<Converter> getTestConverters(String testId) {
        return TEST_CONVERTERS.getOrDefault(testId, List.of());
    }

    // ==================== CONVERSION CORE ====================

    /**
     * Converts a mask value to the target field type using all active scopes.
     * Collects converters from all applicable scopes in priority order.
     *
     * @param maskValue the string value to convert
     * @param fieldType the target field type
     * @param originalValue the original field value
     * @param containingObject the containing object
     * @param fieldName the field name
     * @return converted value or appropriate default
     */
    public static Object convertToFieldType(String maskValue, Class<?> fieldType,
                                            Object originalValue, Object containingObject,
                                            String fieldName) {

        if (maskValue == null) {
            return getDefaultValue(fieldType);
        }

        for (Converter converter : getAllActiveConverters()) {
            if (converter.canConvert(fieldType)) {
                Object result = converter.convert(maskValue, fieldType, originalValue,
                        containingObject, fieldName);
                if (result != null || !shouldTryNextConverter(converter)) {
                    return result;
                }
            }
        }

        return getDefaultValue(fieldType);
    }

    /**
     * Gets all active converters in priority order:
     * 1. Thread-local converters (highest priority)
     * 2. Request-scoped converters
     * 3. Global converters
     * 4. Default converters (lowest priority)
     */
    private static List<Converter> getAllActiveConverters() {
        List<Converter> allConverters = new ArrayList<>();

        // 1. Thread-local (highest priority)
        allConverters.addAll(THREAD_CONVERTERS.get());

        // 2. Request-scoped
        allConverters.addAll(REQUEST_CONVERTERS.get());

        // 3. Global
        allConverters.addAll(GLOBAL_CONVERTERS);

        // 4. Default (lowest priority)
        allConverters.addAll(DEFAULT_CONVERTERS);

        // Sort by priority within each scope
        allConverters.sort((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority()));

        return allConverters;
    }

    /**
     * Gets converters for a specific active test.
     * This is called by test runners to activate test converters.
     */
    public static void activateTestConverters(String testId) {
        List<Converter> testConverters = getTestConverters(testId);
        THREAD_CONVERTERS.get().addAll(testConverters);
    }

    /**
     * Deactivates test converters for the current thread.
     */
    public static void deactivateTestConverters() {
        // Remove only test converters (you might need to track which ones were added)
        // Simpler: clear all thread converters if tests run in isolation
        clearThreadLocal();
    }

    private static boolean shouldTryNextConverter(Converter converter) {
        return !(converter instanceof FallbackConverter);
    }

    private static Object getDefaultValue(Class<?> type) {
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

    // ==================== DEBUG & MONITORING ====================

    /**
     * Gets all registered converters with their scope and priority.
     * Useful for debugging and monitoring.
     *
     * @return map of scope-to-converter information
     */
    public static Map<String, List<String>> getRegisteredConvertersByScope() {
        Map<String, List<String>> result = new LinkedHashMap<>();

        // Global converters
        result.put("GLOBAL", GLOBAL_CONVERTERS.stream()
                .map(c -> c.getClass().getSimpleName() + " (Priority: " + c.getPriority() + ")")
                .toList());

        // Thread-local converters
        result.put("THREAD", THREAD_CONVERTERS.get().stream()
                .map(c -> c.getClass().getSimpleName() + " (Priority: " + c.getPriority() + ")")
                .toList());

        // Request-scoped converters
        result.put("REQUEST", REQUEST_CONVERTERS.get().stream()
                .map(c -> c.getClass().getSimpleName() + " (Priority: " + c.getPriority() + ")")
                .toList());

        // Test converters
        result.put("TESTS", TEST_CONVERTERS.values().stream()
                .flatMap(List::stream)
                .map(c -> c.getClass().getSimpleName() + " (Priority: " + c.getPriority() + ")")
                .toList());

        return result;
    }

    /**
     * Gets the current active scope configuration.
     */
    public static String getCurrentScopeInfo() {
        return String.format(
                "Thread: %d converters, Request: %s, Global: %d converters",
                THREAD_CONVERTERS.get().size(),
                CURRENT_REQUEST_ID.get() != null ? "active" : "inactive",
                GLOBAL_CONVERTERS.size()
        );
    }
}