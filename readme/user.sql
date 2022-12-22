create database user;
use user;


drop table if exists role;
create table role
(
    id     int unsigned primary key auto_increment,
    name   varchar(255) comment '角色',
    name_zh varchar(255) comment '权限'
);
insert into role(name, name_zh) VALUES ('神仙','所有权限');

drop table if exists user;
create table user
(
    id       int unsigned primary key auto_increment,
    username varchar(255) comment '用户名',
    password varchar(255) comment '密码',
    phone    varchar(32) comment '手机号'
);
insert into user(username, password, phone)
VALUES ('123456', '123456', '110'),
       ('123', '123', '120'),
       ('456', '456', '119');


drop table if exists user_role;
create table user_role
(
    id  int unsigned primary key auto_increment,
    rid int unsigned,
    uid int unsigned
);
insert into user_role(rid, uid) VALUES (1,1);