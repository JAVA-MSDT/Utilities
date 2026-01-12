/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.custommaskme;


import com.javamsdt.maskme.api.converter.MaskMeConverter;
import com.javamsdt.maskme.api.utils.MaskMeFieldAccessUtil;

/**
 * User's custom String converter with higher priority
 */
public class CustomStringConverter implements MaskMeConverter {

    @Override
    public int priority() {
        return 10; // Higher than default converters (0)
    }

    @Override
    public boolean canConvert(Class<?> type) {
        return type == String.class;
    }

    @Override
    public Object convert(String value, Class<?> targetType, Object originalValue,
                          Object containingObject, String fieldName) {
        System.out.println("Value:: " + value);
        value = MaskMeFieldAccessUtil.getMaskedValueFromAnotherFieldOrMaskedValue(value, containingObject);
        System.out.println("Processed Value:: " + value);
        // User's custom logic for String fields
        if (fieldName.contains("password")) {
            return "************";
        }

        if (fieldName.contains("email")) {
            return "[EMAIL PROTECTED]";
        }

        // Default fallback to the original value if not handled
        return value;
    }
}
