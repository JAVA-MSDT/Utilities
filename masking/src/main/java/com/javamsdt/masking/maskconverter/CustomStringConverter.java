/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskconverter;


import com.javamsdt.masking.maskme.api.converter.Converter;

/**
 * User's custom String converter with higher priority
 */
public class CustomStringConverter implements Converter {

    @Override
    public int getPriority() {
        return 10; // Higher than default converters (0)
    }

    @Override
    public boolean canConvert(Class<?> type) {
        return type == String.class;
    }

    @Override
    public Object convert(String value, Class<?> targetType, Object originalValue,
                          Object containingObject, String fieldName) {

        // User's custom logic for String fields
        if ("password".equals(fieldName)) {
            return "************";
        }

        if ("email".equals(fieldName)) {
            return "[EMAIL PROTECTED]";
        }

        // Default fallback to the original value if not handled
        return value;
    }
}
