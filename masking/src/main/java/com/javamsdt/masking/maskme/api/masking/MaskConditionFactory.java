/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.api.masking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaskConditionFactory {
    
    /**
     * Spring ApplicationContext for bean lookup.
     * Set by MaskingConfiguration during Spring initialization.
     */
    @Setter
    private static ApplicationContext applicationContext;

    /**
     * Creates a MaskCondition instance using Spring context or reflection.
     * Prioritizes Spring-managed beans for dependency injection support,
     * with automatic fallback to direct instantiation.
     * 
     * <p>Creation strategy:
     * 1. Try Spring ApplicationContext.getBean() if available
     * 2. Fall back to reflection-based constructor invocation
     * 3. Throw MaskingException if both approaches fail
     * 
     * @param <T> the specific MaskCondition type
     * @param conditionClass the condition class to instantiate
     * @return new condition instance
     * @throws MaskingException if condition cannot be created
     */
    public static <T extends MaskCondition> T createCondition(Class<T> conditionClass) {
        // Try Spring context first
        if (applicationContext != null) {
            try {
                return applicationContext.getBean(conditionClass);
            } catch (Exception e) {
                // Bean not found in Spring context, use reflection
            }
        }
        
        // Reflection fallback for non-Spring contexts or non-managed beans
        try {
            return conditionClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new MaskingException("Failed to create condition: " + conditionClass.getName(), e);
        }
    }
}