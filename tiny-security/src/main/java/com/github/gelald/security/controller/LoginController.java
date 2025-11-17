package com.github.gelald.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @ResponseBody
    @GetMapping("/.well-known/appspecific/com.chrome.devtools.json")
    public ResponseEntity<?> handleChromeDevToolsRequest() {
        return ResponseEntity.ok().body("{}");
    }

    @GetMapping("/home")
    public String home(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Object principal = authentication.getPrincipal();
        log.info("Authenticated User: {}", principal.toString());
        if (principal instanceof UserDetails userDetails) {
            model.addAttribute("name", userDetails.getUsername());
        } else {
            log.error("get userDetails failed, return empty");
            model.addAttribute("name", "null");
        }
        return "home";
    }
}
