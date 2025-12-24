/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.domain;


import com.javamsdt.masking.mask.AlwaysMaskCondition;
import com.javamsdt.masking.mask.Mask;
import com.javamsdt.masking.mask.MaskOnInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    @Mask(conditions = {MaskOnInput.class})
    private String name;
    @Mask(conditions = {AlwaysMaskCondition.class})
    private String email;
    @Mask(conditions = {AlwaysMaskCondition.class})
    private String password;
    private String phone;
    private Address address;
    @Mask(conditions = {AlwaysMaskCondition.class})
    private LocalDate birthDate;
    private Gender gender;

}
