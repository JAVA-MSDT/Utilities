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
import com.javamsdt.masking.mask.MaskOnInput;
import com.javamsdt.masking.mask.MaskProcessor;
import com.javamsdt.masking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable final Long id) {
        return userMapper.toDto(userService.findUserById(id));
    }

    @GetMapping("/masked/{id}")
    public UserDto getMaskedUserById(@PathVariable final Long id) {
        return  MaskProcessor.getInstance().process(userMapper.toDto(userService.findUserById(id)));
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable final Long id) {
        new MaskOnInput("maskMe");
        return   MaskProcessor.getInstance().process(userService.findUserById(id));
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.findUsers().stream()
                .map(userMapper::toDto)
                .toList();
    }
}
