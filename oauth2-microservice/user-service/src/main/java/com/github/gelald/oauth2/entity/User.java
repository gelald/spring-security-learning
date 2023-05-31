package com.github.gelald.oauth2.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */
@Data
@Entity
@Table(name = "oauth2_user")
public class User implements Serializable {
    private static final Long serialVersionUID = 1L;

    @Id
    private Long id;
    private String nickName;
    private String username;
    private String password;
    private Integer gender;
    private String mobileNumber;
    /**
     * 0:禁用
     * 1:启用
     */
    private Integer status;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private LocalDateTime updateTime;
}
