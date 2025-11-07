package com.github.gelald.tinyss.controller;

import com.github.gelald.tinyss.entity.User;
import com.github.gelald.tinyss.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    // 注册功能已移除 - RBAC系统主要关注权限管理，用户通过系统初始化创建

    @GetMapping("/")
    public String home() {
        return "welcome";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}