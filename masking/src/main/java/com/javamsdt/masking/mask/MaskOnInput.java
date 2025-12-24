/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.mask;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class MaskOnInput implements MaskCondition{

    private String input;
    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        System.out.println("Input: " + input);
        return input.equalsIgnoreCase("MaskMe");
    }
}
