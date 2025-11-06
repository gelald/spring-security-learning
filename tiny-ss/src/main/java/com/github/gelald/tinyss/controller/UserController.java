package com.github.gelald.tinyss.controller;

import com.github.gelald.tinyss.dto.LoginDto;
import com.github.gelald.tinyss.dto.RegistrationDto;
import com.github.gelald.tinyss.entity.User;
import com.github.gelald.tinyss.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registrationDto") RegistrationDto registrationDto,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        if (userService.userExists(registrationDto.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "register";
        }

        if (userService.emailExists(registrationDto.getEmail())) {
            result.rejectValue("email", "error.email", "Email already exists");
            return "register";
        }

        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(registrationDto.getPassword());

        userService.registerUser(user);

        model.addAttribute("message", "Registration successful. Please log in.");
        return "login";
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
            return "home";
        }
        return "welcome";
    }

    @GetMapping("/admin")
    public String adminPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
            return "admin";
        }
        return "redirect:/login";
    }
}