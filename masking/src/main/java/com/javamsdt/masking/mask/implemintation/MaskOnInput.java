/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.mask.implemintation;

import com.javamsdt.masking.mask.api.MaskCondition;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class MaskOnInput implements MaskCondition {

    private String input;

    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        return input.equalsIgnoreCase("MaskMe");
    }

    @Override
    public void setInput(Object input) {
        if (input instanceof String) {
            this.input = (String) input;
        }
    }
}
