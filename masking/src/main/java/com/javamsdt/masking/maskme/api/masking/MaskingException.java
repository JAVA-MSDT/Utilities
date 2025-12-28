package com.javamsdt.masking.maskme.api.masking;

/**
 * Runtime exception thrown when masking operations encounter unrecoverable errors.
 * Wraps underlying exceptions from reflection, instantiation, or conversion failures
 * to provide consistent error handling across the masking library.
 * 
 * <p>Common scenarios:
 * - Failed to create MaskCondition instances
 * - Field access errors during processing
 * - Type conversion failures in converters
 * - Missing required fields or methods
 * 
 * <p>Use cases:
 * - Condition factory instantiation failures
 * - Reflection-based field access errors
 * - Invalid converter configurations
 * - Missing Spring context dependencies
 * 
 * @author Ahmed Samy
 * @since 1.0.0
 */
public class MaskingException extends RuntimeException {
    
    /**
     * Creates a new MaskingException with detailed message and root cause.
     * 
     * @param message descriptive error message
     * @param cause the underlying exception that triggered this error
     */
    public MaskingException(String message, Throwable cause) {
        super(message, cause);
    }
}
