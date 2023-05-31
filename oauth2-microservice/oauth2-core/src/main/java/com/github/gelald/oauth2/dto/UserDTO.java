package com.github.gelald.oauth2.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Data
public class UserDTO implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String mobile;
    private Integer status;
    private List<String> roles;
}
