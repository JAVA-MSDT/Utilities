package com.javamsdt.masking.custommaskme;

import com.javamsdt.masking.service.UserService;
import com.javamsdt.maskme.api.condition.MaskMeCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PhoneMaskingCondition implements MaskMeCondition {
    private static final String EXPECTED_INPUT = "maskPhone";
    private String input;

    private final UserService userService;

    @Override
    public boolean shouldMask(Object fieldValue, Object containingObject) {

        return input != null && input.equalsIgnoreCase(EXPECTED_INPUT);
    }

    @Override
    public void setInput(Object input) {
        if (input instanceof String) {
            this.input = (String) input;
        }
    }
}
