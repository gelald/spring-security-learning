package com.github.gelald.oauth2.controller;

import com.github.gelald.oauth2.dto.UserDTO;
import com.github.gelald.oauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/registry")
    public void registry(@NotBlank @RequestParam("username") String username,
                         @NotBlank @RequestParam("password") String password) {
        this.userService.registry(username, password);
    }

    @GetMapping("/loadByUsername")
    public UserDTO loadByUsername(@NotBlank @RequestParam("username") String username) {
        return this.userService.loadByUsername(username);
    }
}
