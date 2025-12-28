/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.implemintation.masking;

import com.javamsdt.masking.maskme.api.masking.MaskCondition;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class MaskPhone implements MaskCondition {

    public static final String MASK_PHONE_KEY_ONE = "maskPhoneOne";
    public static final String MASK_PHONE_KEY_TWO = "maskPhoneTwo";
    private String maskPhoneFlag;
    private String expectedMaskPhone;

    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        return maskPhoneFlag.equalsIgnoreCase(expectedMaskPhone);
    }

    @Override
    public void setInput(Map<String, Object> input) {
        if (!input.isEmpty()) {
            if(input.containsKey(MASK_PHONE_KEY_ONE)) {
                this.maskPhoneFlag = (String) input.get(MASK_PHONE_KEY_ONE);
            }
            if(input.containsKey(MASK_PHONE_KEY_TWO)) {
                this.expectedMaskPhone = (String) input.get(MASK_PHONE_KEY_TWO);
            }
        }
    }
}
