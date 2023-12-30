-- auto-generated definition
create table user
(
    id           bigint auto_increment comment '用户编号'
        primary key,
    username     varchar(255) null comment '昵称',
    userAccount  varchar(255) null comment '登录账号',
    avatarUrl    varchar(255) null comment '头像',
    gender       tinyint      null comment '性别',
    userPassword varchar(255) not null comment '密码',
    phone        varchar(128) null comment '电话',
    email        varchar(255) null comment '邮箱',
    userStatus   int          null comment '用户状态 0正常',
    createTime   datetime     null on update CURRENT_TIMESTAMP comment '创建时间',
    updateTime   datetime     null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      null comment '是否删除',
    userRole     tinyint      null comment '用户角色',
    planetCode   varchar(255) null comment '星球编号'
);