drop table if exists test;
create table test(
                     id bigint not null  comment 'id',
                     name varchar(50) comment '名称',
                     password varchar(50) comment '密码',
                     primary key (id)
)engine =innodb default  charset =utf8mb4 comment ='测试';
insert into test (id, name, password) values (1,'测试','password');


drop table if exists demo;
create table demo(
                     id bigint not null  comment 'id',
                     name varchar(50) comment '名称',
                     primary key (id)
)engine =innodb default  charset =utf8mb4 comment ='测试';
insert into demo (id, name) values (1,'测试');


DROP TABLE IF EXISTS `ebook`;
CREATE TABLE `ebook`  (
                          `id` bigint NOT NULL COMMENT '主键ID',
                          `name` varchar(50)  COMMENT '名称',
                          `category1_id` bigint  COMMENT '分类1',
                          `category2_id` bigint  COMMENT '分类2',
                          `description` varchar(200)  COMMENT '描述',
                          `cover` varchar(200)  COMMENT '封面',
                          `doc_count` int COMMENT '文章数',
                          `view_count` int COMMENT '阅读数',
                          `vote_count` int COMMENT '点赞数',
                          PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '电子书';

-- ----------------------------
-- Records of ebook
-- ----------------------------
insert into ebook (id,name,description) values (1,'Spring Boot 入门教程','零基础入门java开发，企业级应用开发最佳首选框架');
insert into ebook (id,name,description) values (2,'Vue 入门教程','零基础入门Vue开发，企业级应用开发最佳首选框架');
insert into ebook (id,name,description) values (3,'Python 入门教程','零基础入门Python开发，企业级应用开发最佳首选框架');
insert into ebook (id,name,description) values (4,'Mysql 入门教程','零基础入门Mysql开发，企业级应用开发最佳首选框架');
insert into ebook (id,name,description) values (5,'c++ 入门教程','零基础入门c++开发，企业级应用开发最佳首选框架');
