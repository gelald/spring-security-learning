package com.github.gelald.tinyss.dto;

import lombok.Data;

@Data
public class RegistrationDto {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
}