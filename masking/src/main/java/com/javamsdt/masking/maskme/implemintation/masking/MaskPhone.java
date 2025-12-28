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

@AllArgsConstructor
@NoArgsConstructor
public class MaskPhone implements MaskCondition {

    private String maskPhoneFlag;

    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        return "YES".equalsIgnoreCase(maskPhoneFlag) ||
                "TRUE".equalsIgnoreCase(maskPhoneFlag);
    }

    @Override
    public void setInput(Object maskPhoneFlag) {
        if (maskPhoneFlag instanceof String) {
            this.maskPhoneFlag = (String) maskPhoneFlag;
        }
    }
}
