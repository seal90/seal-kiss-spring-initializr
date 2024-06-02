create database demo;
create table demo (
    id BIGINT AUTO_INCREMENT COMMENT '主键',
    string_attr VARCHAR(64) COMMENT '字符串测试',
    integer_attr INT COMMENT '整型测试',
    long_attr BIGINT COMMENT '大整型测试',
    big_decimal_attr DECIMAL(10,2) COMMENT '高精度测试',
    local_time_attr TIME COMMENT '时间测试',
    local_date_attr DATE COMMENT '日期测试',
    local_date_time_attr DATETIME COMMENT '日期时间测试',
    duration_attr BIGINT COMMENT '时长测试',
    PRIMARY KEY (id),
    KEY string_index (string_attr)
);

-- 数据类型
-- https://dev.mysql.com/doc/refman/8.4/en/data-types.html
-- 连接数据库
mysql -u root -p -h 192.168.X.XXX -P 3306 --default-character-set=utf8
-- 查看所有数据库
show databases;
-- 查看当前数据库
select database();
-- 查看所有表
show tables;
-- 查看表信息
describe demo;
-- 查看建表语句
show create table demo\G;