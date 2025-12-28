/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.maskme.implemintation.masking;

import com.javamsdt.masking.maskme.api.masking.MaskCondition;
import com.javamsdt.masking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaskOnInput implements MaskCondition {

    private final UserService userService;
    private String input;

    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        if (userService != null) {
            System.out.println("User One:: " + userService.findUserById(1L));
        }
        return input != null && input.equalsIgnoreCase("MaskMe");
    }

    @Override
    public void setInput(Object input) {
        if (input instanceof String value) {
            this.input = value;
        }
    }
}
