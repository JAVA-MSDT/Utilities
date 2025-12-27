/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking fields that require conditional masking.
 * Applied to fields, method parameters, or record components to enable
 * dynamic data hiding based on runtime conditions.
 * 
 * <p>Use cases:
 * - Hide sensitive data based on user permissions
 * - MaskMe PII in audit logs
 * - Apply different masking rules per client type
 * - Context-aware field replacement using placeholders
 * 
 * <p>Example usage:
 * <pre>{@code
 * @Mask(conditions = {AdminOnlyCondition.class}, maskValue = "***")
 * private String sensitiveData;
 * 
 * @Mask(conditions = {RoleBasedCondition.class}, maskValue = "[name]@masked.com")
 * private String email;
 * }</pre>
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaskMe {
    /**
     * Array of condition classes that determine when masking should occur.
     * All conditions are evaluated, and masking happens if any condition returns true.
     * Conditions can be Spring-managed beans for dependency injection support.
     * 
     * @return array of MaskCondition classes
     */
    Class<? extends MaskCondition>[] conditions();

    /**
     * The value to use when masking the field.
     * Supports field placeholders like [fieldName] for context-aware masking.
     * Empty string triggers original value manipulation in converters.
     * 
     * <p>Examples:
     * - "***" - Simple maskme
     * - "[name]@company.com" - Email domain replacement
     * - "" - Trigger converter-specific logic
     * 
     * @return the maskme value or placeholder pattern
     */
    String maskValue() default "****";
}
