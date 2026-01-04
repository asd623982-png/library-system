/*
 * Library System Database Schema
 * Version: 2.0 (旗舰版 - 对应软件工程作业报告)
 * Description: 包含完整的字段定义、约束、索引及初始化数据
 */

-- 1. 初始化数据库
DROP DATABASE IF EXISTS library_db;
CREATE DATABASE library_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

-- =========================================================================
-- 表 1: 系统用户表 (sys_user)
-- 对应报告模块: 用户管理、登录鉴权
-- =========================================================================
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名 (登录账号)',
    password VARCHAR(128) NOT NULL COMMENT '加密后的密码',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色标识: ADMIN-管理员, USER-普通读者',
    status TINYINT(1) DEFAULT 1 COMMENT '账号状态: 1-启用, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username) COMMENT '唯一索引: 防止用户名重复'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =========================================================================
-- 表 2: 图书资产表 (sys_book)
-- 对应报告模块: 图书入库、资产大屏、库存管理
-- =========================================================================
CREATE TABLE sys_book (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    isbn VARCHAR(20) NOT NULL COMMENT 'ISBN国际标准书号',
    title VARCHAR(128) NOT NULL COMMENT '图书名称',
    author VARCHAR(64) NOT NULL COMMENT '作者',
    publisher VARCHAR(128) DEFAULT NULL COMMENT '出版社 (作业要求字段)',
    category VARCHAR(32) DEFAULT '综合' COMMENT '分类: 计算机/文学/历史 (用于大屏饼图)',
    price DECIMAL(10, 2) DEFAULT 0.00 COMMENT '单价 (用于计算总资产)',
    stock INT NOT NULL DEFAULT 0 COMMENT '当前库存数量',
    version INT DEFAULT 0 COMMENT '乐观锁版本号 (处理高并发借阅)',
    status TINYINT(1) DEFAULT 1 COMMENT '状态: 1-上架, 0-下架',
    location VARCHAR(64) DEFAULT 'A区-1号架' COMMENT '馆藏位置',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_isbn (isbn) COMMENT '唯一索引: ISBN不重复',
    KEY idx_title (title) COMMENT '普通索引: 书名模糊查询优化',
    KEY idx_category (category) COMMENT '普通索引: 分类统计优化'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书资产表';

-- =========================================================================
-- 表 3: 借阅流水表 (sys_borrow)
-- 对应报告模块: 借阅管理、逾期检测、流量统计
-- =========================================================================
CREATE TABLE sys_borrow (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '借阅人ID (关联 sys_user)',
    book_id BIGINT NOT NULL COMMENT '图书ID (关联 sys_book)',
    borrow_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借出时间',
    due_date DATETIME NOT NULL COMMENT '应还时间 (通常为借出+30天)',
    return_time DATETIME DEFAULT NULL COMMENT '实际归还时间',
    status TINYINT(1) DEFAULT 1 COMMENT '状态: 1-借出中, 2-已归还, 3-已逾期',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注 (如: 图书破损记录)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id) COMMENT '索引: 查询某人的借阅记录',
    KEY idx_borrow_time (borrow_time) COMMENT '索引: 用于统计近30天借阅流量',
    CONSTRAINT fk_borrow_user FOREIGN KEY (user_id) REFERENCES sys_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_borrow_book FOREIGN KEY (book_id) REFERENCES sys_book (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书借阅流水表';

-- =========================================================================
-- 4. 初始化演示数据 (让系统运行起来就有数据)
-- =========================================================================

-- 1. 用户数据
INSERT INTO sys_user (username, password, role) VALUES 
('admin', '123456', 'ADMIN'),       -- 管理员
('zhangsan', '123456', 'USER'),    -- 测试学生1
('lisi', '123456', 'USER');        -- 测试学生2

-- 2. 图书数据 (覆盖不同分类和价格，方便展示图表)
INSERT INTO sys_book (isbn, title, author, publisher, category, price, stock, location) VALUES 
('9787111603', 'Java编程思想', 'Bruce Eckel', '机械工业出版社', '计算机', 108.00, 10, 'A-101'),
('9787115546', '深入理解计算机系统', 'Randal E.Bryant', '机械工业出版社', '计算机', 139.00, 5, 'A-102'),
('9787536692', '三体全集', '刘慈欣', '重庆出版社', '科幻文学', 98.00, 12, 'B-201'),
('9787544253', '活着', '余华', '南海出版公司', '当代文学', 45.00, 8, 'B-202'),
('9787108010', '万历十五年', '黄仁宇', '中华书局', '历史', 28.00, 3, 'C-305'),
('9787100013', '理想国', '柏拉图', '商务印书馆', '哲学', 56.00, 6, 'D-401');

-- 3. 借阅数据 (模拟一些历史记录，让折线图有数据)
INSERT INTO sys_borrow (user_id, book_id, borrow_time, due_date, status) VALUES 
(2, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 25 DAY), 1), -- 张三借了Java
(2, 3, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 28 DAY), 1), -- 张三借了三体
(3, 4, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), 1); -- 李四借了活着