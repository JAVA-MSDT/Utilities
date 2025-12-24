/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.mask.api;


public interface MaskCondition {
    boolean shouldMask(Object fieldValue, Object containingObject);

    // Default method to accept input
    default void setInput(Object input) {
        // Default implementation does nothing
    }
}
