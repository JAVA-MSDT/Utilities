/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 */
package com.javamsdt.masking.config;

import com.javamsdt.masking.maskconverter.CustomStringConverter;
import com.javamsdt.masking.maskme.api.converter.ConverterRegistry;
import com.javamsdt.masking.maskme.api.masking.MaskConditionFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MaskingConfiguration implements ApplicationContextAware {
    
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        MaskConditionFactory.setApplicationContext(applicationContext);
    }

    @PostConstruct
    public void registerCustomConverters() {
        // Clear Global
        ConverterRegistry.clearGlobal();
        // Register user's custom converters
        ConverterRegistry.registerGlobal(new CustomStringConverter());
    }

    @PreDestroy
    public void destroy() {
        ConverterRegistry.clearGlobal();
    }
}