create table `user` (
`id` bigint(64) not null auto_increment comment '主键id',
`user_id` char(64) not null comment '用户id',
`balance` decimal(10,2) not null default 0 comment '余额',
`create_time` datetime not null comment '创建时间',
`update_time` datetime not null comment '更新时间',
`is_delete` tinyint(1) not null comment '逻辑删除 0-正常 1-已删除',
primary key (`id`),
unique key `uk_user_id` (`user_id`)
);

create table `trip` (
`id` bigint(64) not null auto_increment comment '主键id',
`trip_id` char(64) not null comment '车次id',
`price` decimal(4,2) not null default 0 comment '价格',
`count` integer(16) not null default 0 comment '余票数量',
`trip_date` datetime not null comment '开车日期',
`create_time` datetime not null comment '创建时间',
`update_time` datetime not null comment '更新时间',
`is_delete` tinyint(1) not null comment '逻辑删除 0-正常 1-已删除',
primary key (`id`),
unique key `uk_trip_id` (`trip_id`),
index `idx_trip_date` (`trip_date`)
)