/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.dto;


import com.javamsdt.masking.mask.AlwaysMaskCondition;
import com.javamsdt.masking.mask.Mask;

import java.time.LocalDate;

public record UserDto(
         Long id,
         @Mask(conditions = {AlwaysMaskCondition.class})
         String name,
         String email,
         @Mask(conditions = {AlwaysMaskCondition.class})
         String password,
         String phone,
         AddressDto address,
         LocalDate birthDate,
         String genderId,
         String genderName
) {
}
