/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.api.masking;

import org.jspecify.annotations.NonNull;

/**
 * Factory for creating MaskCondition instances with optional framework support.
 * Supports dependency injection for a single framework while maintaining
 * pure Java fallback for standalone applications.
 *
 * <p>Usage pattern:
 * - Framework apps: Register your framework provider once at startup
 * - Pure Java apps: Nothing to do - reflection works automatically
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
public class MaskConditionFactory {

    // Single framework provider (null for pure Java)
    private static volatile FrameworkProvider frameworkProvider = null;

    private MaskConditionFactory() {
        throw new MaskMeException("MaskConditionFactory is not to be initialized");
    }

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
     * 3. Throw MaskMeException if both approaches fail
     * 
     * @param <T> the specific MaskCondition type
     * @param conditionClass the condition class to instantiate
     * @return new condition instance
     * @throws MaskMeException if the condition cannot be created
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
            throw new MaskMeException("Failed to create condition: " + conditionClass.getName(), e);
        }
    }
}