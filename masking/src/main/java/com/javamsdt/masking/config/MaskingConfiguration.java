/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 */
package com.javamsdt.masking.config;

import com.javamsdt.masking.maskme.api.MaskConditionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MaskingConfiguration implements ApplicationContextAware {
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        MaskConditionFactory.setApplicationContext(applicationContext);
    }
}