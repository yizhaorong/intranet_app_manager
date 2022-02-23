# 创建库
drop database if exists app_manager;
drop user if exists 'app_manager'@'%';
-- 支持emoji：需要mysql数据库参数： character_set_server=utf8mb4
create database app_manager default character set utf8mb4 collate utf8mb4_unicode_ci;
use app_manager;
create user 'app_manager'@'%' identified by 'app_manager123456';
grant all privileges on *.* to 'app_manager'@'%';
flush privileges;