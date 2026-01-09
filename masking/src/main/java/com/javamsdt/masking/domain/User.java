/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.domain;


import com.javamsdt.maskme.api.annotation.MaskMe;
import com.javamsdt.maskme.implementation.condition.AlwaysMaskMeCondition;
import com.javamsdt.maskme.implementation.condition.MaskMeOnInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    @MaskMe(conditions = {MaskMeOnInput.class}, maskValue = "*****")
    private String name;
    @MaskMe(conditions = {AlwaysMaskMeCondition.class})
    private String email;
    @MaskMe(conditions = {AlwaysMaskMeCondition.class})
    private String password;
    private String phone;
    private Address address;
    @MaskMe(conditions = {AlwaysMaskMeCondition.class})
    private LocalDate birthDate;
    private Gender gender;
    private BigDecimal balance;
    private Instant createdAt;

}
