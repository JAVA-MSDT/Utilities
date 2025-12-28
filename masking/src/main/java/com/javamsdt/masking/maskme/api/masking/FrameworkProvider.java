/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.api.masking;


/**
 * Simple interface for framework dependency injection support.
 */
@FunctionalInterface
public interface FrameworkProvider {
    /**
     * Create or retrieve an instance of the given class.
     * Return null if the framework cannot provide an instance.
     */
    <T> T getInstance(Class<T> type);

}
