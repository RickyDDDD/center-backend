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