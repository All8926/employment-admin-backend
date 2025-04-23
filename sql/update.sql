# 2525-04-23
ALTER TABLE student
    ADD COLUMN isEmployed TINYINT DEFAULT 0 COMMENT '是否就业' AFTER status;

# 签约信息表
CREATE TABLE IF NOT EXISTS `sign_info`
(
    `id`          bigint(20)                             NOT NULL AUTO_INCREMENT COMMENT '主键',
    `contractId` bigint(20)                             NOT NULL COMMENT '合同ID',
    `studentId`  bigint(20)                             NOT NULL COMMENT '学生ID',
    post          varchar(128)                           null comment '岗位',
    salary        varchar(128)                           null comment '薪资',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    createTime    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint      default 0                 not null comment '是否删除',
    PRIMARY KEY (`id`)
) COMMENT ='签约信息表' COLLATE = utf8mb4_unicode_ci;