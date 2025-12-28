/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.controller;

import com.javamsdt.masking.domain.User;
import com.javamsdt.masking.dto.UserDto;
import com.javamsdt.masking.mapper.UserMapper;
import com.javamsdt.masking.maskme.api.masking.MaskProcessor;
import com.javamsdt.masking.maskme.implemintation.masking.MaskOnInput;
import com.javamsdt.masking.maskme.implemintation.masking.MaskPhone;
import com.javamsdt.masking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final MaskProcessor processor;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable final Long id) {
        return userMapper.toDto(userService.findUserById(id));
    }

    @GetMapping("/masked/{id}")
    public UserDto getMaskedUserById(@PathVariable final Long id,
                                     @RequestHeader("Mask-Input") String maskInput,
                                     @RequestHeader("Mask-Phone") String maskPhone) {

        try {
            processor.setConditionInput(MaskOnInput.class, maskInput);
            processor.setConditionInput(MaskPhone.class, maskPhone);
            return processor.process(userMapper.toDto(userService.findUserById(id)));
        } finally {
            processor.clearInputs();
        }
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable final Long id) {
        return processor.process(userService.findUserById(id));
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestHeader("Mask-Input") String maskInput) {
        try {
            processor.setConditionInput(MaskOnInput.class, maskInput);
            return userService.findUsers().stream()
                    .map(user -> processor.process(userMapper.toDto(user)))
                    .toList();
        } finally {
            processor.clearInputs();
        }
    }
}
