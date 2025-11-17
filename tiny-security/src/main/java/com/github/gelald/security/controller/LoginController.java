package com.github.gelald.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
}
