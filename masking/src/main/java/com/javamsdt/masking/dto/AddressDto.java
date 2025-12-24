/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.dto;


import com.javamsdt.masking.mask.api.Mask;
import com.javamsdt.masking.mask.implemintation.AlwaysMaskCondition;

public record AddressDto(
         Long id,
         String street,
         String building,
         @Mask(conditions = {AlwaysMaskCondition.class})
         String city,
         String state,
         String zipCode,
         String country
) {
}
