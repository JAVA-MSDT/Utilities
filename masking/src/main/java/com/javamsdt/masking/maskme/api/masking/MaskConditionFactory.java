/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.api.masking;

import org.jspecify.annotations.NonNull;

/**
 * Factory for creating MaskCondition instances with Spring integration support.
 * Attempts to retrieve conditions from Spring ApplicationContext first,
 * falling back to reflection-based instantiation for non-Spring environments.
 * 
 * <p>This enables dependency injection in maskme conditions while maintaining
 * compatibility with non-Spring applications.
 * 
 * <p>Use cases:
 * - Spring-managed conditions with @Autowired services
 * - Fallback support for standalone applications
 * - Hybrid environments with mixed condition types
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
public class MaskConditionFactory {

    // Single framework provider (null for pure Java)
    private static volatile FrameworkProvider frameworkProvider = null;

    /**
     * Registers your framework's provider for dependency injection support.
     * Call this once at application startup if using Spring, Quarkus, etc.
     *
     * @param provider your framework's provider implementation
     */
    public static void setFrameworkProvider(FrameworkProvider provider) {
        frameworkProvider = provider;
    }

    /**
     * Removes the framework provider (falls back to pure Java).
     * Useful for testing or framework switching scenarios.
     */
    public static void clearFrameworkProvider() {
        frameworkProvider = null;
    }


    /**
     * Gets the current framework provider name (or "Pure-Java").
     */
    public static String getCurrentProvider() {
        return frameworkProvider != null ?
                frameworkProvider.getClass().getSimpleName() : "Pure-Java";
    }

    /**
     * Creates a MaskCondition instance using Spring context or reflection.
     * Prioritizes Spring-managed beans for dependency injection support,
     * with automatic fallback to direct instantiation.
     * 
     * <p>Creation strategy:
     * 1. Try the framework provided if available
     * 2. Fall back to reflection-based constructor invocation
     * 3. Throw MaskingException if both approaches fail
     * 
     * @param <T> the specific MaskCondition type
     * @param conditionClass the condition class to instantiate
     * @return new condition instance
     * @throws MaskingException if the condition cannot be created
     */
    public static <T extends MaskCondition> T createCondition(Class<T> conditionClass) {
        // Try Spring context first
        if (frameworkProvider != null) {
            try {
                T instance = frameworkProvider.getInstance(conditionClass);
                if (instance != null) {
                    return instance;
                }
                // Provider returned null, fall through to reflection
            } catch (Exception e) {
                // Provider failed, fall through to reflection
            }
        }

        return getConditionUsingReflection(conditionClass);
    }

    private static <T extends MaskCondition> @NonNull T getConditionUsingReflection(Class<T> conditionClass) {
        // Reflection fallback for non-Spring contexts or non-managed beans
        try {
            return conditionClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new MaskingException("Failed to create condition: " + conditionClass.getName(), e);
        }
    }
}