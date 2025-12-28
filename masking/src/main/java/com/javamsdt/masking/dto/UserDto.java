/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.dto;


import com.javamsdt.masking.maskme.api.masking.MaskMe;
import com.javamsdt.masking.maskme.implemintation.masking.AlwaysMaskCondition;
import com.javamsdt.masking.maskme.implemintation.masking.MaskOnInput;
import com.javamsdt.masking.maskme.implemintation.masking.MaskPhone;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record UserDto(
        @MaskMe(conditions = {AlwaysMaskCondition.class}, maskValue = "1000")
        Long id,
        @MaskMe(conditions = {MaskOnInput.class}, maskValue = "[id]-[genderId]")
        String name,

        @MaskMe(conditions = {AlwaysMaskCondition.class}, maskValue = "[name] it is me")
        String email,
        @MaskMe(conditions = {AlwaysMaskCondition.class})
        String password,
        @MaskMe(conditions = {MaskPhone.class}, maskValue = "[PHONE_MASKED]")
        String phone,
        AddressDto address,
        @MaskMe(conditions = {AlwaysMaskCondition.class}, maskValue = "01/01/1800")
        LocalDate birthDate,
        String genderId,
        String genderName,
        @MaskMe(conditions = {AlwaysMaskCondition.class}, maskValue = "")
        BigDecimal balance,
        @MaskMe(conditions = {AlwaysMaskCondition.class}, maskValue = "1900-01-01T00:00:00.00Z")
        Instant createdAt
) {
}
