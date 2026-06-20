-- ========================================
-- Cloud Museum 数据库升级脚本 v2
-- 使用方式: mysql -u root -p cloud_museum < sql/migration.sql
-- ========================================

-- ===== 1. 添加 model_url 列（如果不存在） =====
-- MySQL 不支持 ADD COLUMN IF NOT EXISTS，用预处理语句动态检查
SET @dbname = (SELECT DATABASE());
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = 'collection' AND COLUMN_NAME = 'model_url');

-- 如果列不存在则添加（用 CONCAT 避免中文在预处理语句中出错）
SET @sql = IF(@col_exists = 0,
    CONCAT('ALTER TABLE `collection` ADD COLUMN `model_url` VARCHAR(500) DEFAULT '''' COMMENT ''', '3D模型URL (.glb格式)', ''' AFTER `cover_image`'),
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ===== 2. 插入新藏品（如果不存在） =====
-- 使用 INSERT ... SELECT WHERE NOT EXISTS 避免重复插入
INSERT INTO `collection` (`name`, `category_id`, `dynasty`, `era`, `material`, `size_desc`, `origin_place`, `description`, `cultural_significance`, `cover_image`, `model_url`, `status`, `is_featured`)
SELECT '唐三彩骆驼载乐俑', 1, '唐', '唐代开元年间', '陶器', '高58cm，长43cm', '陕西西安', '此件唐三彩骆驼载乐俑出土于陕西西安，是盛唐时期的珍贵文物。骆驼昂首挺立，背上驮载着由七名乐师组成的乐队，分别演奏琵琶、箜篌、排箫等乐器，生动再现了丝绸之路上的文化交流盛景。釉色以黄、绿、褐为主，色彩斑斓，是三彩工艺的巅峰之作。', '唐三彩是中国唐代独创的低温铅釉陶器，代表了中国古代陶瓷工艺的最高成就。这件骆驼载乐俑不仅是艺术珍品，更是丝绸之路中外文化交流的实物见证。', 'https://picsum.photos/seed/tang-camel/400/300', '', 'on_display', 1
WHERE NOT EXISTS (
    SELECT 1 FROM `collection` WHERE `name` = '唐三彩骆驼载乐俑'
) LIMIT 1;

INSERT INTO `collection` (`name`, `category_id`, `dynasty`, `era`, `material`, `size_desc`, `origin_place`, `description`, `cultural_significance`, `cover_image`, `model_url`, `status`, `is_featured`)
SELECT '秦兵马俑-跪射俑', 1, '秦', '秦代', '陶器', '高128cm，肩宽38cm', '陕西西安临潼', '此跪射俑出土于秦始皇陵兵马俑二号坑，左腿蹲曲，右膝跪地，双手置于身体右侧作握弓状。俑身通体彩绘，神态逼真，盔甲、发髻等细节刻画入微，展现了秦代雕塑艺术的高度写实水平。', '兵马俑被誉为"世界第八大奇迹"，是秦始皇陵的重要组成部分，展示了秦代强大的军事力量和高超的工艺水平。', 'https://picsum.photos/seed/terracotta/400/300', 'https://cdn.jsdelivr.net/npm/@google/model-viewer@4.1.0/examples/assets/Astronaut.glb', 'on_display', 1
WHERE NOT EXISTS (
    SELECT 1 FROM `collection` WHERE `name` = '秦兵马俑-跪射俑'
) LIMIT 1;

INSERT INTO `collection` (`name`, `category_id`, `dynasty`, `era`, `material`, `size_desc`, `origin_place`, `description`, `cultural_significance`, `cover_image`, `model_url`, `status`, `is_featured`)
SELECT '法门寺秘色瓷碗', 1, '唐', '唐代', '瓷器', '高6.8cm，口径15.2cm', '陕西宝鸡法门寺', '此秘色瓷碗出土于陕西法门寺地宫，釉色青翠欲滴，如冰似玉，胎质细腻轻薄。秘色瓷是唐代越窑专门为皇室烧造的贡瓷，其配方和工艺在历史上长期失传，直至法门寺地宫开启才重现于世。', '秘色瓷的发现破解了中国陶瓷史上的千年之谜，为研究唐代制瓷工艺和宫廷用瓷制度提供了无可替代的实物证据。', 'https://picsum.photos/seed/mi-se-porcelain/400/300', '', 'on_display', 1
WHERE NOT EXISTS (
    SELECT 1 FROM `collection` WHERE `name` = '法门寺秘色瓷碗'
) LIMIT 1;

INSERT INTO `collection` (`name`, `category_id`, `dynasty`, `era`, `material`, `size_desc`, `origin_place`, `description`, `cultural_significance`, `cover_image`, `model_url`, `status`, `is_featured`)
SELECT '鎏金舞马衔杯银壶', 5, '唐', '唐代', '银、金', '高18.5cm，口径2.3cm', '陕西西安何家村', '此鎏金舞马衔杯银壶出土于西安何家村唐代窖藏，造型仿皮囊壶，壶身浮雕一匹翩翩起舞的骏马，口衔酒杯，形象生动。通体鎏金，工艺精湛，是唐代金银器中的稀世珍品。', '何家村窖藏被誉为"唐代金银器宝库"，舞马衔杯的造型印证了史料中关于唐玄宗时期"舞马"表演的记载。', 'https://picsum.photos/seed/tang-horse/400/300', 'https://cdn.jsdelivr.net/npm/@google/model-viewer@4.1.0/examples/assets/Astronaut.glb', 'on_display', 1
WHERE NOT EXISTS (
    SELECT 1 FROM `collection` WHERE `name` = '鎏金舞马衔杯银壶'
) LIMIT 1;

INSERT INTO `collection` (`name`, `category_id`, `dynasty`, `era`, `material`, `size_desc`, `origin_place`, `description`, `cultural_significance`, `cover_image`, `model_url`, `status`, `is_featured`)
SELECT '汉阳陵陶俑', 1, '汉', '西汉', '陶器', '高55-62cm', '陕西西安', '汉阳陵陶俑出土于汉景帝阳陵陪葬坑，体型约为真人的三分之一，神态生动，表情丰富，展现了汉代社会的各个阶层。这些陶俑原本装有木质手臂和丝质衣物，其含蓄的微笑被誉为"东方的蒙娜丽莎"。', '汉阳陵陶俑是"文景之治"时期社会生活的真实写照，体现了汉代"休养生息"政策下安定祥和的社会氛围。', 'https://picsum.photos/seed/han-figurine/400/300', '', 'on_display', 0
WHERE NOT EXISTS (
    SELECT 1 FROM `collection` WHERE `name` = '汉阳陵陶俑'
) LIMIT 1;

-- ===== 3. 填充分类名称 =====
-- 如果只有默认分类，大概率分类已存在，跳过
-- （业务代码中有 fillCategoryName 处理）

-- ===== 4. 为旧藏品补全 cultural_significance（如为 NULL） =====
UPDATE `collection` SET `cultural_significance` = '青花瓷是中国陶瓷史上的里程碑，永乐青花以其独特的钴料发色和精湛的工艺闻名于世。'
WHERE `name` LIKE '%青花%' AND (`cultural_significance` IS NULL OR `cultural_significance` = '');

UPDATE `collection` SET `cultural_significance` = '清明上河图以其宏大的规模成为中国风俗画的最高典范，为研究宋代城市生活提供了极其珍贵的图像史料。'
WHERE `name` LIKE '%清明上河图%' AND (`cultural_significance` IS NULL OR `cultural_significance` = '');

UPDATE `collection` SET `cultural_significance` = '和田玉被誉为"玉中之王"，汉代玉器在继承传统的基础上创新发展，龙纹玉佩代表了汉代贵族的精神追求。'
WHERE `name` LIKE '%玉%' AND (`cultural_significance` IS NULL OR `cultural_significance` = '');

UPDATE `collection` SET `cultural_significance` = '司母戊鼎是中国古代最重的青铜器，代表了商代青铜铸造技术的巅峰。'
WHERE `name` LIKE '%鼎%' AND (`cultural_significance` IS NULL OR `cultural_significance` = '');

UPDATE `collection` SET `cultural_significance` = '金缕玉衣反映了汉代"事死如事生"的丧葬观念和精湛的玉器加工技术。'
WHERE `name` LIKE '%玉衣%' AND (`cultural_significance` IS NULL OR `cultural_significance` = '');

UPDATE `collection` SET `cultural_significance` = '黄花梨家具代表了中国古代硬木家具的最高水平，其独特的木纹和精湛的榫卯工艺备受推崇。'
WHERE `name` LIKE '%黄花梨%' AND (`cultural_significance` IS NULL OR `cultural_significance` = '');

UPDATE `collection` SET `cultural_significance` = '宋刻本是中国古籍中的珍品，其版刻精美、校勘精审，具有极高的学术价值和收藏价值。'
WHERE `name` LIKE '%刻本%' AND (`cultural_significance` IS NULL OR `cultural_significance` = '');

-- ===== 5. 封面图替换为 picsum 美观占位 =====
UPDATE `collection` SET `cover_image` = CONCAT('https://picsum.photos/seed/', REPLACE(LOWER(`name`), ' ', '-'), '/400/300')
WHERE `cover_image` LIKE '%placeholder%' OR `cover_image` LIKE '%via.placeholder.com%' OR `cover_image` = '';

SELECT CONCAT('✅ 升级完成！当前共 ', COUNT(*), ' 件藏品') AS result FROM `collection` WHERE `is_deleted` = 0;

-- ===== 6. 修复3D模型URL（原CDN链接已失效 -> 本地静态资源） =====
UPDATE `collection` SET `model_url` = '/models/DamagedHelmet.glb'
WHERE `name` = '秦兵马俑-跪射俑' AND `model_url` LIKE '%Astronaut.glb';

UPDATE `collection` SET `model_url` = '/models/Duck.glb'
WHERE `name` = '鎏金舞马衔杯银壶' AND `model_url` LIKE '%Astronaut.glb';
