/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.dto;

import com.javamsdt.maskme.api.annotation.MaskMe;
import com.javamsdt.maskme.implementation.condition.AlwaysMaskMeCondition;

import java.util.UUID;

public record GeoLocationDto(
        @MaskMe(conditions = {AlwaysMaskMeCondition.class}, maskValue = "00000000-0000-0000-0000-000000000000")
        UUID id,
        Double longitude,
        @MaskMe(conditions = {AlwaysMaskMeCondition.class}, maskValue = "00.0000")
        Double latitude
) {
}
