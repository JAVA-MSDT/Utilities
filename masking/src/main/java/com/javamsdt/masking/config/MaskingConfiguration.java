/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 */
package com.javamsdt.masking.config;

import com.javamsdt.masking.maskconverter.CustomStringConverter;
import com.javamsdt.masking.maskme.api.converter.ConverterRegistry;
import com.javamsdt.masking.maskme.api.masking.FrameworkProvider;
import com.javamsdt.masking.maskme.api.masking.MaskConditionFactory;
import com.javamsdt.masking.maskme.api.masking.MaskProcessor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MaskingConfiguration {

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void registerCustomConverters() {
        registerMaskConditionProvider();
        // Clear Global
        ConverterRegistry.clearGlobal();
        // Register user's custom converters
        ConverterRegistry.registerGlobal(new CustomStringConverter());
    }

    @Bean
    public MaskProcessor maskProcessor() {
        return new MaskProcessor();
    }

    public void registerMaskConditionProvider() {
        // One-time registration at startup
        MaskConditionFactory.setFrameworkProvider(new FrameworkProvider() {
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
        ConverterRegistry.clearGlobal();
    }
}