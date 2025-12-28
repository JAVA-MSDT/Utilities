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

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MaskOnInput implements MaskCondition {

    public static final String MASK_ON_INPUT_ONE_KEY = "MaskOnInputOne";
    public static final String MASK_ON_INPUT_TWO_KEY = "MaskOnInputTwo";
    private final UserService userService;
    private String input;
    private String expectedInput;

    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {
        if (userService != null) {
            System.out.println("User One:: " + userService.findUserById(1L));
        }
        return input != null && input.equalsIgnoreCase(expectedInput);
    }

    @Override
    public void setInput(Map<String, Object> input) {
        if (!input.isEmpty()) {
           if(input.containsKey(MASK_ON_INPUT_ONE_KEY)) {
               this.input = (String) input.get(MASK_ON_INPUT_ONE_KEY);
           }
           if(input.containsKey(MASK_ON_INPUT_TWO_KEY)) {
               this.expectedInput = (String) input.get(MASK_ON_INPUT_TWO_KEY);
           }
        }
    }
}
