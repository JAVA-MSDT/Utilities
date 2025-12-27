/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.dto;


import com.javamsdt.masking.maskme.api.MaskMe;
import com.javamsdt.masking.maskme.implemintation.AlwaysMaskCondition;

public record AddressDto(
         Long id,
         String street,
         String building,
         @MaskMe(conditions = {AlwaysMaskCondition.class})
         String city,
         String state,
         String zipCode,
         String country,
         GeoLocationDto geoLocation
) {
}
