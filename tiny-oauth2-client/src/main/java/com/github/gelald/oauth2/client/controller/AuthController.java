package com.github.gelald.oauth2.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class AuthController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal OAuth2User principal) {
        log.info("OAuth2User: {}", principal.toString());
        model.addAttribute("name", principal.getAttribute("login"));
        model.addAttribute("avatar_url", principal.getAttribute("avatar_url"));
        return "home";
    }
}
