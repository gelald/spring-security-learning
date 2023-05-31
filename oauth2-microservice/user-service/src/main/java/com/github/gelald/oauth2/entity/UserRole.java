package com.github.gelald.oauth2.entity;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Data
@Entity
@Table(name = "oauth2_user_role")
public class UserRole implements Serializable {
    @EmbeddedId
    private UserRolePK userRolePk;
}
