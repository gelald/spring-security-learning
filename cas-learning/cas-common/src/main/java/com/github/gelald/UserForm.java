package com.github.gelald;

import lombok.Data;

import java.io.Serializable;

/**
 * @author WuYingBin
 * Date 2022/12/22 0022
 */
@Data
public class UserForm implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String backUrl;
}
