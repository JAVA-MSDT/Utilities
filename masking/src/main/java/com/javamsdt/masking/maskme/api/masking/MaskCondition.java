/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.api.masking;

/**
 * Interface for implementing conditional masking logic.
 * Determines whether a field should be masked based on runtime conditions,
 * field values, and containing object context.
 * 
 * <p>Implementations can be Spring-managed beans to leverage dependency injection.
 * 
 * <p>Use cases:
 * - Role-based field visibility (AdminOnlyCondition)
 * - Time-based masking (BusinessHoursCondition)
 * - Value-based conditions (EmptyFieldCondition)
 * - Request context masking (HeaderBasedCondition)
 * 
 * <p>Example implementation:
 * <pre>{@code
 * @Component
 * public class RoleBasedCondition implements MaskCondition {
 *     @Autowired
 *     private SecurityService securityService;
 *     
 *     private UserRole requiredRole;
 *     
 *     @Override
 *     public void setInput(Object input) {
 *         this.requiredRole = (UserRole) input;
 *     }
 *     
 *     @Override
 *     public boolean shouldMask(Object fieldValue, Object containingObject) {
 *         return !securityService.hasRole(requiredRole);
 *     }
 * }
 * }</pre>
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
public interface MaskCondition {
    
    /**
     * Determines whether the field should be masked based on current context.
     * Called for each field with @MaskMe annotation during processing.
     * 
     * @param fieldValue the current value of the field being evaluated
     * @param containingObject the object that contains this field
     * @return true if the field should be masked, false otherwise
     */
    boolean shouldMask(Object fieldValue, Object containingObject);

    /**
     * Accepts runtime input for condition evaluation.
     * Called by MaskProcessor before shouldMask() to provide context-specific data.
     * 
     * <p>Use cases:
     * - Pass user role for permission-based masking
     * - Provide request headers for context-aware decisions
     * - Supply configuration flags for dynamic behavior
     * 
     * @param input the runtime input for this condition (can be null)
     */
    default void setInput(Object input) {
        // Default implementation does nothing
    }
}
