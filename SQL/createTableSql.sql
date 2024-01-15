create table tag
(
    id         int auto_increment
        primary key,
    tagName    varchar(256) null comment '标签名称',
    userId     bigint       null comment '用户id',
    parentId   bigint       null comment '父标签id',
    isParent   tinyint      null comment '0-不是，1-父标签',
    createTime datetime     null on update CURRENT_TIMESTAMP comment '创建时间',
    updateTime datetime     null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint      not null comment '是否删除',
    constraint uniIdx_tagName
        unique (tagName)
);

create index idx_userId
    on tag (userId);

create table team
(
    id          bigint auto_increment comment '主键'
        primary key,
    name        varchar(255)                 not null comment '队伍名称',
    description varchar(1024)                null comment '描述',
    maxNum      int default 1                not null comment '最大人数',
    expireTime  datetime                     null comment '过期时间',
    userId      bigint                       null comment '队长ID',
    password    varchar(512)                 null comment '密码',
    status      int unsigned zerofill        not null comment '队伍状态：0-公开，1-私有，2-加密',
    createTime  datetime                     null on update CURRENT_TIMESTAMP comment '创建时间',
    updateTime  datetime                     null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete    tinyint(1) unsigned zerofill not null comment '是否删除'
);

create table user
(
    id           bigint auto_increment comment '用户编号'
        primary key,
    username     varchar(255)                 null comment '昵称',
    userAccount  varchar(255)                 null comment '登录账号',
    avatarUrl    varchar(255)                 null comment '头像',
    profile      varchar(512)                 null comment '个人简介',
    gender       tinyint                      null comment '性别',
    userPassword varchar(255)                 not null comment '密码',
    phone        varchar(128)                 null comment '电话',
    email        varchar(255)                 null comment '邮箱',
    userStatus   int                          null comment '用户状态 0正常',
    createTime   datetime                     null on update CURRENT_TIMESTAMP comment '创建时间',
    updateTime   datetime                     null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint(1) unsigned zerofill null comment '是否删除 0正常 1 已删除',
    userRole     tinyint                      null comment '用户角色，0：普通用户，1管理员',
    planetCode   varchar(255)                 null comment '星球编号',
    tags         varchar(1024)                null comment '标签列表'
);

create table user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             null comment '用户id',
    teamId     bigint                             null comment '队伍id',
    joinTime   datetime                           null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '用户队伍关系';
