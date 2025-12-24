/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.dto;


import com.javamsdt.masking.mask.AlwaysMaskCondition;
import com.javamsdt.masking.mask.Mask;
import com.javamsdt.masking.mask.MaskOnInput;

import java.time.LocalDate;

public record UserDto(
        @Mask(conditions = {AlwaysMaskCondition.class}, maskValue = "[ID]")
         Long id,
         @Mask(conditions = {MaskOnInput.class}, maskValue = "[USER_NAME]")
         String name,
         String email,
         @Mask(conditions = {AlwaysMaskCondition.class})
         String password,
         String phone,
         @Mask(conditions = {AlwaysMaskCondition.class})
         AddressDto address,
        @Mask(conditions = {AlwaysMaskCondition.class}, maskValue = "1900-01-01")
         LocalDate birthDate,
         String genderId,
         String genderName
) {
}
