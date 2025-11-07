package com.github.gelald.tinyss.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/rbac-test")
@RequiredArgsConstructor
public class RbacTestController {

    @GetMapping
    public String index(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        model.addAttribute("username", authentication.getName());
        model.addAttribute("authorities", authentication.getAuthorities());
        return "dashboard";
    }

    @GetMapping("/simple")
    public String simpleTest(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        model.addAttribute("username", authentication.getName());
        model.addAttribute("authorities", authentication.getAuthorities());
        return "rbac-test/simple-test";
    }

    @GetMapping("/users")
    public String listUsers(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        model.addAttribute("username", authentication.getName());
        model.addAttribute("authorities", authentication.getAuthorities());
        model.addAttribute("operation", "查看用户列表");
        return "rbac-test/users";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        model.addAttribute("username", authentication.getName());
        model.addAttribute("operation", "创建用户");
        return "rbac-test/create-user";
    }

    @PostMapping("/users/create")
    @ResponseBody
    public Map<String, Object> createUser(@RequestBody Map<String, Object> userData,
                                        Authentication authentication) {
        log.info("User {} is creating a new user with data: {}", authentication.getName(), userData);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "用户创建成功");
        response.put("createdBy", authentication.getName());
        return response;
    }

    // AJAX API 用于测试权限
    @GetMapping("/api/check-permission/{permission}")
    @ResponseBody
    public Map<String, Object> checkPermission(@PathVariable String permission,
                                             Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        boolean hasPermission = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(permission));

        response.put("username", authentication.getName());
        response.put("permission", permission);
        response.put("hasPermission", hasPermission);
        response.put("authorities", authentication.getAuthorities());

        return response;
    }
}