CREATE TABLE `oauth2_user`
(
    `id`            int(11) NOT NULL COMMENT '主键',
    `nick_name`     varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
    `username`      varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
    `password`      varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
    `gender`        tinyint(4)                              DEFAULT NULL COMMENT '性别',
    `mobile_number` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号码',
    `status`        tinyint(4)                              DEFAULT NULL COMMENT '用户状态，0:禁用，1:启用',
    `create_time`   datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime                                DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE `oauth2_role`
(
    `id`          int(11) NOT NULL COMMENT '主键',
    `name`        varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
    `type`        tinyint(4)                              DEFAULT NULL COMMENT '类型',
    `create_time` datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime                                DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE `oauth2_user_role`
(
    `userId` int(11) NOT NULL COMMENT '用户id',
    `roleId`    int(11) NOT NULL COMMENT '角色id',
    PRIMARY KEY (`userId`, `roleId`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;