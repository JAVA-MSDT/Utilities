/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.domain;


import com.javamsdt.masking.maskme.api.MaskMe;
import com.javamsdt.masking.maskme.implemintation.AlwaysMaskCondition;
import com.javamsdt.masking.maskme.implemintation.MaskOnInput;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    @MaskMe(conditions = {MaskOnInput.class}, maskValue = "*****")
    private String name;
    @MaskMe(conditions = {AlwaysMaskCondition.class})
    private String email;
    @MaskMe(conditions = {AlwaysMaskCondition.class})
    private String password;
    private String phone;
    private Address address;
    @MaskMe(conditions = {AlwaysMaskCondition.class})
    private LocalDate birthDate;
    private Gender gender;
    private BigDecimal balance;
    private Instant createdAt;

}
