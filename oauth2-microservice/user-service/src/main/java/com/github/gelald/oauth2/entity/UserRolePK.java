package com.github.gelald.oauth2.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Data
@Embeddable
public class UserRolePK implements Serializable {
    private Long userId;
    private Long roleId;
}
