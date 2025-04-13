# 数据库初始化
# @author
# @from

-- 创建库
create database if not exists employment_admin;

-- 切换库
use employment_admin;

-- 学生信息表
create table if not exists student
(
    id            bigint auto_increment comment 'id' primary key,
    userAccount   varchar(256)                           not null comment '账号',
    userPassword  varchar(512)                           not null comment '密码',
    userName      varchar(256)                           null comment '姓名',
    userAvatar    varchar(1024)                          null comment '头像',
    gender        tinyint      default 2 comment '性别 0-男 1-女 2-未知',
    studentNumber varchar(256)                           null comment '编号',
    phone         varchar(11)                            null comment '手机号',
    email         varchar(256)                           null comment '邮箱',
    status        tinyint      default 0 comment '状态 0-待审核 1-已通过 2-已拒绝',
    userProfile   varchar(512)                           null comment '用户简介',
    userRole      varchar(256) default 'student'         not null comment '用户角色：admin/student/teacher/enterprise',
    createTime    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (userAccount)
) comment '用户' collate = utf8mb4_unicode_ci;

ALTER TABLE student
    ADD COLUMN deptId BIGINT COMMENT '所属部门ID';

# 部门表
create table if not exists department
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100)                       NOT NULL COMMENT '部门名称',
    parentId   BIGINT   DEFAULT 0 COMMENT '上级部门ID',
    sort       INT      DEFAULT 0 COMMENT '排序字段',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    INDEX idx_parentId (parentId)
) comment '部门表' collate = utf8mb4_unicode_ci;

-- 教师信息表
use employment_admin;
create table if not exists teacher
(
    id            bigint auto_increment comment 'id' primary key,
    userAccount   varchar(256)                           not null comment '账号',
    userPassword  varchar(512)                           not null comment '密码',
    userName      varchar(256)                           null comment '姓名',
    userAvatar    varchar(1024)                          null comment '头像',
    gender        tinyint      default 2 comment '性别 0-男 1-女 2-未知',
    teacherNumber varchar(256)                           null comment '编号',
    job           varchar(256)                           null comment '职务',
    qualification varchar(256)                           null comment '学历',
    phone         varchar(11)                            null comment '手机号',
    email         varchar(256)                           null comment '邮箱',
    deptId        BIGINT                                 null comment '所属部门ID',
    status        tinyint      default 0 comment '状态 0-待审核 1-已通过 2-已拒绝',
    userProfile   varchar(512)                           null comment '用户简介',
    userRole      varchar(256) default 'teacher'         not null comment '用户角色：admin/student/teacher/enterprise',
    createTime    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (userAccount)
) comment '教师' collate = utf8mb4_unicode_ci;


-- 企业信息表
use employment_admin;
create table if not exists enterprise
(
    id             bigint auto_increment comment 'id' primary key,
    userAccount    varchar(256)                           not null comment '账号',
    userPassword   varchar(512)                           not null comment '密码',
    userName       varchar(256)                           null comment '姓名',
    userAvatar     varchar(1024)                          null comment '头像',
    gender         tinyint      default 2 comment '性别 0-男 1-女 2-未知',
    job            varchar(256)                           null comment '职务',
    phone          varchar(11)                            null comment '手机号',
    email          varchar(256)                           null comment '邮箱',
    enterpriseName varchar(256)                           null comment '企业名称',
    licenseNum     varchar(128)                           null COMMENT '统一社会信用代码',
    address        varchar(256)                           null COMMENT '办公地址',
    businessScope  varchar(512)                           null COMMENT '经营范围',
    industry       varchar(256)                           null COMMENT '所属行业',
    isAuthorized   TINYINT      DEFAULT 0 COMMENT '是否认证 0未认证 1已认证',
    status         tinyint      default 0 comment '状态 0-待审核 1-已通过 2-已拒绝',
    userProfile    varchar(512)                           null comment '用户简介',
    userRole       varchar(256) default 'enterprise'      not null comment '用户角色：admin/student/teacher/enterprise',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (userAccount)
) comment '企业员工' collate = utf8mb4_unicode_ci;

-- 简历表
use employment_admin;
create table if not exists resume
(
    id         bigint auto_increment comment 'id' primary key,
    filePath   varchar(256)                       not null comment '简历路径',
    fileName   varchar(256)                       null comment '简历名称',
    userId     BIGINT                             not null comment '创建人',
    remark     varchar(512)                       null comment '备注',
    isActive   tinyint  default 0                 not null comment '生效状态 0否 1是',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '简历' collate = utf8mb4_unicode_ci;


-- 企业资质表
use employment_admin;
ALTER TABLE enterprise_certification
    ADD COLUMN status TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-已通过 2-已拒绝' AFTER remark,
    ADD COLUMN rejectReason VARCHAR(256) NULL COMMENT '拒绝原因';
create table if not exists enterprise_certification
(
    id         bigint auto_increment comment 'id' primary key,
    filePath   varchar(256)                       not null comment '路径',
    fileName   varchar(256)                       null comment '名称',
    userId     BIGINT                             not null comment '创建人',
    remark     varchar(512)                       null comment '备注',
    certType   varchar(128)                       null comment '资质类型',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '企业资质' collate = utf8mb4_unicode_ci;


-- 合同表
use employment_admin;
ALTER TABLE contract
    ADD COLUMN status       TINYINT default 0 not null comment '合同状态 0-待学生同意 1-待老师同意 2-已完成 3-学生拒绝 4-老师拒绝',
    ADD COLUMN rejectReason varchar(256)      null comment '拒绝原因';
ALTER TABLE contract
    ADD COLUMN enterpriseName varchar(256)                       null comment '企业名称',
    ADD COLUMN studentName  varchar(256)                       null comment '学生姓名',
    ADD COLUMN teacherName  varchar(256)                       null comment '教师姓名';

create table if not exists contract
(
    id           bigint auto_increment comment 'id' primary key,
    filePath     varchar(256)                       null comment '路径',
    fileName     varchar(256)                       null comment '名称',
    enterpriseId BIGINT                             not null comment '企业id(创建人)',
    enterpriseName varchar(256)                       null comment '企业名称',
    studentId    BIGINT                             not null comment '学生id',
    studentName  varchar(256)                       null comment '学生姓名',
    teacherId    BIGINT                             null comment '教师id(审核人)',
    teacherName  varchar(256)                       null comment '教师姓名',
    remark       varchar(512)                       null comment '备注',
    signDate     datetime                           null comment '签约日期',
    status       TINYINT  default 0                 not null comment '合同状态 0-待学生同意 1-待老师同意 2-已完成 3-学生拒绝 4-老师拒绝',
    rejectReason varchar(256)                       null comment '拒绝原因',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    index idx_enterpriseId (enterpriseId)
) comment '合同' collate = utf8mb4_unicode_ci;


-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子收藏';
