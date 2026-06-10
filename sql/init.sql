-- Cloud Museum 数据库初始化脚本
-- 华为杯人工智能比赛 - 云博物馆系统

CREATE DATABASE IF NOT EXISTS cloud_museum DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE cloud_museum;

-- ==================== 用户表 ====================
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `phone` VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    `nickname` VARCHAR(50) DEFAULT '' COMMENT '昵称',
    `avatar` VARCHAR(500) DEFAULT '' COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除 0-否 1-是',
    INDEX idx_phone (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ==================== 管理员表 ====================
CREATE TABLE IF NOT EXISTS `admin` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(200) NOT NULL COMMENT '密码(加密)',
    `real_name` VARCHAR(50) DEFAULT '' COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
    `role` VARCHAR(20) DEFAULT 'admin' COMMENT '角色 admin-管理员 super-超级管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ==================== 藏品分类表 ====================
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(500) DEFAULT '' COMMENT '分类描述',
    `icon` VARCHAR(500) DEFAULT '' COMMENT '分类图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除 0-否 1-是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='藏品分类表';

-- ==================== 藏品表 ====================
CREATE TABLE IF NOT EXISTS `collection` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '藏品ID',
    `name` VARCHAR(200) NOT NULL COMMENT '藏品名称',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `dynasty` VARCHAR(100) DEFAULT '' COMMENT '朝代',
    `era` VARCHAR(100) DEFAULT '' COMMENT '年代',
    `material` VARCHAR(100) DEFAULT '' COMMENT '材质',
    `size_desc` VARCHAR(500) DEFAULT '' COMMENT '尺寸描述',
    `origin_place` VARCHAR(200) DEFAULT '' COMMENT '出土地点',
    `description` TEXT COMMENT '藏品描述',
    `cultural_significance` TEXT COMMENT '文化意义',
    `cover_image` VARCHAR(500) DEFAULT '' COMMENT '封面图片',
    `images` TEXT COMMENT '图片列表(JSON数组)',
    `video_url` VARCHAR(500) DEFAULT '' COMMENT '视频URL',
    `status` VARCHAR(20) DEFAULT 'on_display' COMMENT '状态 on_display-展出 in_storage-库房 maintenance-修复中',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `is_featured` TINYINT DEFAULT 0 COMMENT '是否推荐 0-否 1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除 0-否 1-是',
    INDEX idx_category (`category_id`),
    INDEX idx_status (`status`),
    INDEX idx_featured (`is_featured`),
    FULLTEXT INDEX ft_name_desc (`name`, `description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='藏品表';

-- ==================== 预约表 ====================
CREATE TABLE IF NOT EXISTS `reservation` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `visitor_name` VARCHAR(50) NOT NULL COMMENT '参观人姓名',
    `visitor_phone` VARCHAR(20) NOT NULL COMMENT '参观人手机号',
    `visit_date` DATE NOT NULL COMMENT '参观日期',
    `visit_time_slot` VARCHAR(50) NOT NULL COMMENT '参观时段 上午/下午',
    `visitor_count` INT DEFAULT 1 COMMENT '参观人数',
    `id_card` VARCHAR(30) DEFAULT '' COMMENT '身份证号',
    `notes` VARCHAR(500) DEFAULT '' COMMENT '备注',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态 pending-待审核 approved-已通过 rejected-已拒绝 cancelled-已取消 completed-已完成',
    `reject_reason` VARCHAR(500) DEFAULT '' COMMENT '拒绝原因',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user (`user_id`),
    INDEX idx_date (`visit_date`),
    INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- ==================== 短信验证码表 ====================
CREATE TABLE IF NOT EXISTS `sms_code` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `code` VARCHAR(10) NOT NULL COMMENT '验证码',
    `type` VARCHAR(20) DEFAULT 'login' COMMENT '类型 login-登录 register-注册',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `used` TINYINT DEFAULT 0 COMMENT '是否使用 0-否 1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_phone_type (`phone`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信验证码表';

-- ==================== 初始数据 ====================
-- 插入默认管理员 (密码: admin123, MD5加密)
INSERT INTO `admin` (`username`, `password`, `real_name`, `role`, `status`) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 'super', 1);

-- 插入默认藏品分类
INSERT INTO `category` (`name`, `description`, `sort_order`) VALUES
('陶瓷', '陶瓷类藏品，包括陶器、瓷器等', 1),
('书画', '书画类藏品，包括书法、绘画等', 2),
('玉器', '玉器类藏品，包括玉雕、玉佩等', 3),
('青铜器', '青铜器类藏品', 4),
('金银器', '金银器类藏品', 5),
('木雕', '木雕类藏品', 6),
('古籍', '古籍类藏品', 7),
('杂项', '其他各类藏品', 8);

-- 插入示例藏品
INSERT INTO `collection` (`name`, `category_id`, `dynasty`, `era`, `material`, `size_desc`, `origin_place`, `description`, `cover_image`, `status`, `is_featured`) VALUES
('青花瓷瓶', 1, '明', '明代永乐年间', '陶瓷', '高45cm，口径12cm，底径15cm', '江西景德镇', '此青花瓷瓶造型优美，釉色晶莹，青花发色浓郁，是明代永乐年间的典型器物。瓶身绘有缠枝莲纹，线条流畅自然，具有极高的艺术价值和历史研究价值。', 'https://via.placeholder.com/400x300?text=青花瓷瓶', 'on_display', 1),
('清明上河图(仿)', 2, '宋', '北宋', '绢本', '宽24.8cm，长528.7cm', '河南开封', '清明上河图是中国十大传世名画之一，为北宋画家张择端所作。此图为高清仿制品，生动记录了北宋都城东京（今河南开封）的城市面貌和社会各阶层人民的生活状态。', 'https://via.placeholder.com/400x300?text=清明上河图', 'on_display', 1),
('和田玉龙纹佩', 3, '汉', '汉代', '和田玉', '直径5.6cm，厚0.8cm', '新疆和田', '此玉佩采用上等和田玉制成，玉质温润细腻，雕刻龙纹图案，线条流畅有力。龙纹象征权威和吉祥，是汉代贵族常用的佩饰之一。', 'https://via.placeholder.com/400x300?text=和田玉佩', 'on_display', 1),
('司母戊鼎(仿)', 4, '商', '商代晚期', '青铜', '高133cm，口长110cm，口宽79cm', '河南安阳', '司母戊鼎是商代晚期青铜器的代表作，原器现藏于中国国家博物馆。此仿制品严格按照原器1:1比例制作，纹饰精美，体现了商代青铜铸造工艺的最高水平。', 'https://via.placeholder.com/400x300?text=司母戊鼎', 'on_display', 1),
('金缕玉衣(仿)', 5, '汉', '西汉', '玉、金', '长172cm，肩宽53cm', '河北满城', '金缕玉衣是汉代最高规格的丧葬殓服，此仿制品按照出土原器精心制作。玉衣由数千片玉片用金丝编缀而成，工艺极其复杂，展现了汉代玉器制作的高超技艺。', 'https://via.placeholder.com/400x300?text=金缕玉衣', 'on_display', 0),
('黄花梨木雕屏风', 6, '清', '清代乾隆年间', '黄花梨木', '高240cm，宽180cm', '福建', '此屏风采用名贵的黄花梨木精雕而成，屏心雕刻山水人物图案，布局巧妙，层次分明。黄花梨木纹理美观，木质坚硬，是明清家具中的上品。', 'https://via.placeholder.com/400x300?text=黄花梨木雕', 'on_display', 1),
('宋刻本《论语》', 7, '宋', '南宋', '纸本', '每页高28cm，宽18cm', '浙江杭州', '该宋刻本《论语》为南宋时期刊印，纸张精良，刻工精湛，字迹清晰。书中保留了宋代学者的注释，对研究儒家经典具有重要的版本价值。', 'https://via.placeholder.com/400x300?text=宋刻论语', 'on_display', 0);
