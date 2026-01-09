/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 */
package com.javamsdt.masking.config;

import com.javamsdt.masking.maskconverter.CustomStringConverter;
import com.javamsdt.maskme.api.condition.MaskMeConditionFactory;
import com.javamsdt.maskme.api.condition.MaskMeFrameworkProvider;
import com.javamsdt.maskme.api.converter.MaskMeConverterRegistry;
import com.javamsdt.maskme.api.processor.MaskMeProcessor;
import com.javamsdt.maskme.logging.MaskMeLogger;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import java.util.logging.Level;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MaskingConfiguration {

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void registerCustomConverters() {
        // Enable with specific level
         // MaskMeLogger.enable(Level.FINE);

        registerMaskConditionProvider();
        // Clear Global
        MaskMeConverterRegistry.clearGlobal();
        // Register user's custom converters
       // MaskMeConverterRegistry.registerGlobal(new CustomStringConverter());

        // Disable completely
        // MaskMeLogger.disable();
    }

    public void registerMaskConditionProvider() {
        // One-time registration at startup
        MaskMeConditionFactory.setFrameworkProvider(new MaskMeFrameworkProvider() {
            @Override
            public <T> T getInstance(Class<T> type) {
                try {
                    return applicationContext.getBean(type);
                } catch (Exception e) {
                    return null; // Not a Spring bean
                }
            }
        });
    }

    @PreDestroy
    public void destroy() {
        MaskMeConverterRegistry.clearGlobal();
    }
}