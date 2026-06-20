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
    `model_url` VARCHAR(500) DEFAULT '' COMMENT '3D模型URL (.glb格式)',
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
INSERT INTO `collection` (`name`, `category_id`, `dynasty`, `era`, `material`, `size_desc`, `origin_place`, `description`, `cultural_significance`, `cover_image`, `model_url`, `status`, `is_featured`) VALUES
('唐三彩骆驼载乐俑', 1, '唐', '唐代开元年间', '陶器', '高58cm，长43cm', '陕西西安', '此件唐三彩骆驼载乐俑出土于陕西西安，是盛唐时期的珍贵文物。骆驼昂首挺立，背上驮载着由七名乐师组成的乐队，分别演奏琵琶、箜篌、排箫等乐器，生动再现了丝绸之路上的文化交流盛景。釉色以黄、绿、褐为主，色彩斑斓，是三彩工艺的巅峰之作。', '唐三彩是中国唐代独创的低温铅釉陶器，代表了中国古代陶瓷工艺的最高成就。这件骆驼载乐俑不仅是艺术珍品，更是丝绸之路中外文化交流的实物见证，具有极高的历史价值和艺术价值。', 'https://picsum.photos/seed/tang-camel/400/300', '', 'on_display', 1),
('青花瓷瓶', 1, '明', '明代永乐年间', '陶瓷', '高45cm，口径12cm，底径15cm', '江西景德镇', '此青花瓷瓶造型优美，釉色晶莹，青花发色浓郁，是明代永乐年间的典型器物。瓶身绘有缠枝莲纹，线条流畅自然，具有极高的艺术价值和历史研究价值。', '青花瓷是中国陶瓷史上的里程碑，永乐青花以其独特的钴料发色和精湛的工艺闻名于世，是中国陶瓷艺术的杰出代表。', 'https://picsum.photos/seed/blue-vase/400/300', '', 'on_display', 1),
('秦兵马俑-跪射俑', 1, '秦', '秦代', '陶器', '高128cm，肩宽38cm', '陕西西安临潼', '此跪射俑出土于秦始皇陵兵马俑二号坑，左腿蹲曲，右膝跪地，双手置于身体右侧作握弓状。俑身通体彩绘，神态逼真，盔甲、发髻等细节刻画入微，展现了秦代雕塑艺术的高度写实水平。', '兵马俑被誉为"世界第八大奇迹"，是秦始皇陵的重要组成部分。这些陶俑数量庞大、形态各异，展示了秦代强大的军事力量和高超的工艺水平，是研究中国古代军事、雕塑、服饰的珍贵实物资料。', 'https://picsum.photos/seed/terracotta/400/300', '/models/DamagedHelmet.glb', 'on_display', 1),
('清明上河图(仿)', 2, '宋', '北宋', '绢本', '宽24.8cm，长528.7cm', '河南开封', '清明上河图是中国十大传世名画之一，为北宋画家张择端所作。此图为高清仿制品，生动记录了北宋都城东京（今河南开封）的城市面貌和社会各阶层人民的生活状态。', '清明上河图以其宏大的规模、细腻的笔法和丰富的社会生活内容成为中国风俗画的最高典范，为研究宋代城市生活、经济文化提供了极其珍贵的图像史料。', 'https://picsum.photos/seed/river-scene/400/300', '', 'on_display', 1),
('和田玉龙纹佩', 3, '汉', '汉代', '和田玉', '直径5.6cm，厚0.8cm', '新疆和田', '此玉佩采用上等和田玉制成，玉质温润细腻，雕刻龙纹图案，线条流畅有力。龙纹象征权威和吉祥，是汉代贵族常用的佩饰之一。', '和田玉自古以来被誉为"玉中之王"，是中国玉文化的核心载体。汉代玉器在继承传统的基础上创新发展，龙纹玉佩代表了汉代贵族的精神追求和审美情趣。', 'https://picsum.photos/seed/jade-dragon/400/300', '', 'on_display', 1),
('司母戊鼎(仿)', 4, '商', '商代晚期', '青铜', '高133cm，口长110cm，口宽79cm', '河南安阳', '司母戊鼎是商代晚期青铜器的代表作，原器现藏于中国国家博物馆。此仿制品严格按照原器1:1比例制作，纹饰精美，体现了商代青铜铸造工艺的最高水平。', '司母戊鼎是中国古代最重的青铜器，代表了商代青铜铸造技术的巅峰。鼎身饕餮纹饰神秘威严，是研究商代礼制、铸铜工艺和审美观念的核心文物。', 'https://picsum.photos/seed/bronze-ding/400/300', '', 'on_display', 1),
('金缕玉衣(仿)', 5, '汉', '西汉', '玉、金', '长172cm，肩宽53cm', '河北满城', '金缕玉衣是汉代最高规格的丧葬殓服，此仿制品按照出土原器精心制作。玉衣由数千片玉片用金丝编缀而成，工艺极其复杂，展现了汉代玉器制作的高超技艺。', '金缕玉衣反映了汉代"事死如事生"的丧葬观念和精湛的玉器加工技术，是中国古代玉器工艺的杰出代表，也是研究汉代礼仪制度的重要实物。', 'https://picsum.photos/seed/jade-suit/400/300', '', 'on_display', 0),
('黄花梨木雕屏风', 6, '清', '清代乾隆年间', '黄花梨木', '高240cm，宽180cm', '福建', '此屏风采用名贵的黄花梨木精雕而成，屏心雕刻山水人物图案，布局巧妙，层次分明。黄花梨木纹理美观，木质坚硬，是明清家具中的上品。', '黄花梨家具代表了中国古代硬木家具的最高水平，其独特的木纹和精湛的榫卯工艺备受推崇。此屏风体现了清代家具工艺的高度成就。', 'https://picsum.photos/seed/wood-screen/400/300', '', 'on_display', 1),
('宋刻本《论语》', 7, '宋', '南宋', '纸本', '每页高28cm，宽18cm', '浙江杭州', '该宋刻本《论语》为南宋时期刊印，纸张精良，刻工精湛，字迹清晰。书中保留了宋代学者的注释，对研究儒家经典具有重要的版本价值。', '宋刻本是中国古籍中的珍品，其版刻精美、校勘精审，具有极高的学术价值和收藏价值。此本《论语》是研究儒家经典版本流变的重要实物资料。', 'https://picsum.photos/seed/song-book/400/300', '', 'on_display', 0),
('鎏金舞马衔杯银壶', 5, '唐', '唐代', '银、金', '高18.5cm，口径2.3cm', '陕西西安何家村', '此鎏金舞马衔杯银壶出土于西安何家村唐代窖藏，造型仿皮囊壶，壶身浮雕一匹翩翩起舞的骏马，口衔酒杯，形象生动。通体鎏金，工艺精湛，是唐代金银器中的稀世珍品。', '唐代是中国金银器制作的鼎盛时期，何家村窖藏被誉为"唐代金银器宝库"。舞马衔杯的造型印证了史料中关于唐玄宗时期"舞马"表演的记载，具有极高的艺术价值和历史研究价值。', 'https://picsum.photos/seed/tang-horse/400/300', '/models/Duck.glb', 'on_display', 1),
('法门寺秘色瓷碗', 1, '唐', '唐代', '瓷器', '高6.8cm，口径15.2cm', '陕西宝鸡法门寺', '此秘色瓷碗出土于陕西法门寺地宫，釉色青翠欲滴，如冰似玉，胎质细腻轻薄。秘色瓷是唐代越窑专门为皇室烧造的贡瓷，其配方和工艺在历史上长期失传，直至法门寺地宫开启才重现于世。', '秘色瓷的发现破解了中国陶瓷史上的千年之谜。"秘色"一词在唐诗中屡有出现，但长期不知所指。法门寺地宫出土的秘色瓷为研究唐代制瓷工艺和宫廷用瓷制度提供了无可替代的实物证据。', 'https://picsum.photos/seed/mi-se-porcelain/400/300', '', 'on_display', 1),
('汉阳陵陶俑', 1, '汉', '西汉', '陶器', '高55-62cm', '陕西西安', '汉阳陵陶俑出土于汉景帝阳陵陪葬坑，与秦兵马俑的写实风格不同，汉俑体型较小（约为真人的三分之一），但神态生动，表情丰富，身着各色服饰，展现了汉代社会的各个阶层。这些陶俑原本装有木质手臂和丝质衣物，虽已腐朽，仍可见其精巧工艺。', '汉阳陵陶俑是"文景之治"时期社会生活的真实写照。其含蓄的微笑被誉为"东方的蒙娜丽莎"，体现了汉代"休养生息"政策下安定祥和的社会氛围，是中国雕塑艺术从秦代写实向汉代写意过渡的重要见证。', 'https://picsum.photos/seed/han-figurine/400/300', '', 'on_display', 0);
