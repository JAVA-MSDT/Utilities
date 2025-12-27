/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.implemintation;


import com.javamsdt.masking.maskme.api.MaskCondition;

public class AlwaysMaskCondition implements MaskCondition {
    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        return true;
    }
}
