/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.dto;


import com.javamsdt.masking.mask.api.Mask;
import com.javamsdt.masking.mask.implemintation.AlwaysMaskCondition;

import java.util.UUID;

public record GeoLocationDto(
        @Mask(conditions = {AlwaysMaskCondition.class}, maskValue = "00000000-0000-0000-0000-000000000000")
        UUID id,
        Double longitude,
        @Mask(conditions = {AlwaysMaskCondition.class}, maskValue = "00.0000")
        Double latitude
) {
}
