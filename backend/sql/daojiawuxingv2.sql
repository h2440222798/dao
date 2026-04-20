/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 8.0.41 : Database - daojiawuxing
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`daojiawuxing` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `daojiawuxing`;

/*Table structure for table `diary` */

DROP TABLE IF EXISTS `diary`;

CREATE TABLE `diary` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '用户 id',
  `title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `images` json DEFAULT NULL COMMENT '图片列表(JSON数组)',
  `tags` json DEFAULT NULL COMMENT '标签列表(JSON数组)',
  `dietScore` int DEFAULT '0' COMMENT '今日饮食评分',
  `energy` int DEFAULT '0' COMMENT '身体状态-精力',
  `mood` int DEFAULT '0' COMMENT '身体状态-情绪',
  `sleep` int DEFAULT '0' COMMENT '身体状态-睡眠',
  `digestion` int DEFAULT '0' COMMENT '身体状态-消化',
  `practices` json DEFAULT NULL COMMENT '今日践行(JSON数组)',
  `wisdomInsight` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '华夏智慧感悟',
  `likes` int DEFAULT '0' COMMENT '点赞数',
  `isApproved` tinyint NOT NULL DEFAULT '1' COMMENT '是否审核通过',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='抗炎日记表';

/*Data for the table `diary` */

insert  into `diary`(`id`,`userId`,`title`,`content`,`images`,`tags`,`dietScore`,`energy`,`mood`,`sleep`,`digestion`,`practices`,`wisdomInsight`,`likes`,`isApproved`,`createTime`,`updateTime`,`isDelete`) values 
(1,2043960374098558978,'??????','???????????','[]','[\"??\", \"??\"]',9,8,8,8,8,'[\"??\", \"????\"]','????',0,1,'2026-04-14 15:51:42','2026-04-14 15:51:42',0),
(2,2043961020872818690,'今天开始吃抗炎蔬菜汤','心情变好了','[]','[\"食饮有节\"]',8,8,8,8,8,'[\"晚餐清淡少油\"]','少油少盐会让自己变健康',0,1,'2026-04-14 15:55:12','2026-04-14 15:55:12',0),
(3,2043962489193791490,'??????','?? pageSize ??????','[]','[\"??\"]',8,8,8,8,8,'[\"??\", \"??\"]','????',0,1,'2026-04-14 16:00:07','2026-04-14 16:00:07',0),
(4,2043961020872818690,'我的日记','我的日记我的日记我的日记我的日记我的日记我的日记','[]','[\"食饮有节\"]',8,8,8,8,0,'[]','我的日记我的日记我的日记',0,1,'2026-04-14 16:01:57','2026-04-14 16:01:57',0);

/*Table structure for table `diary_comment` */

DROP TABLE IF EXISTS `diary_comment`;

CREATE TABLE `diary_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `diaryId` bigint NOT NULL COMMENT '日记 id',
  `userId` bigint NOT NULL COMMENT '用户 id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_diaryId` (`diaryId`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日记评论表';

/*Data for the table `diary_comment` */

/*Table structure for table `dish` */

DROP TABLE IF EXISTS `dish`;

CREATE TABLE `dish` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜品名称',
  `subtitle` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '副标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `image` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片链接',
  `category` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '五行分类(wood/fire/earth/metal/water)',
  `organ` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对应脏腑',
  `ingredients` json DEFAULT NULL COMMENT '食材列表(JSON数组)',
  `calories` int DEFAULT '0' COMMENT '营养成分-卡路里',
  `protein` float DEFAULT '0' COMMENT '营养成分-蛋白质',
  `fat` float DEFAULT '0' COMMENT '营养成分-脂肪',
  `carbs` float DEFAULT '0' COMMENT '营养成分-碳水化合物',
  `fiber` float DEFAULT '0' COMMENT '营养成分-膳食纤维',
  `antiInflammatory` json DEFAULT NULL COMMENT '抗炎功效(JSON数组)',
  `taoistBenefits` json DEFAULT NULL COMMENT '道家养生功效(JSON数组)',
  `cookingMethod` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '烹饪方法',
  `cookTime` int DEFAULT '0' COMMENT '烹饪时间(分钟)',
  `difficulty` int DEFAULT '1' COMMENT '难度 1-5',
  `suitableConstitution` json DEFAULT NULL COMMENT '适合体质(JSON数组)',
  `unsuitableConstitution` json DEFAULT NULL COMMENT '不适合体质(JSON数组)',
  `wisdomQuote` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '华夏智慧格言',
  `likes` int DEFAULT '0' COMMENT '点赞数',
  `favorites` int DEFAULT '0' COMMENT '收藏数',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品表';

/*Data for the table `dish` */

insert  into `dish`(`id`,`name`,`subtitle`,`description`,`image`,`category`,`organ`,`ingredients`,`calories`,`protein`,`fat`,`carbs`,`fiber`,`antiInflammatory`,`taoistBenefits`,`cookingMethod`,`cookTime`,`difficulty`,`suitableConstitution`,`unsuitableConstitution`,`wisdomQuote`,`likes`,`favorites`,`createTime`,`updateTime`,`isDelete`) values 
(19,'道家五行抗炎蔬菜汤','五色入五脏，温和抗炎','以番茄、白萝卜、西芹、胡萝卜、香菇等五色蔬菜为基础，兼顾清润、温养与高纤维特点，适合作为道家五行抗炎饮食的日常基础汤。汤体清爽，负担轻，既可帮助控制总热量，又利于长期执行。','https://images.unsplash.com/photo-1547592180-85f173990554?w=1200','earth','脾','[\"番茄 120g\", \"白萝卜 80g\", \"西芹 80g\", \"胡萝卜 80g\", \"洋葱 60g\", \"香菇 50g\", \"姜片 3片\"]',96,4.8,1.2,18.5,6.1,'[\"高纤维组合有助于改善肠道炎症环境\", \"番茄与菌菇富含抗氧化成分\", \"低油低盐适合慢性炎症人群长期食用\"]','[\"五色配伍以应五行，帮助整体调和\", \"温养中焦，减轻脾胃负担\", \"适合日常清养与季节交替调理\"]','1. 番茄、白萝卜、胡萝卜切块，西芹切段，洋葱切丝，香菇切片；2. 少量橄榄油煸香洋葱和姜片；3. 放入其余蔬菜翻炒 2 分钟后加清水；4. 中小火煮 20 分钟，最后少量海盐调味即可。',30,1,'[\"湿热体质\", \"熬夜上火\", \"久坐少动人群\"]','[\"脾胃极虚寒\", \"急性腹泻期\"]','《黄帝内经》有言：五色入五脏。饮食调和，不偏不倚，方能久养正气。',128,73,'2026-04-14 16:28:25','2026-04-14 16:30:22',0),
(20,'五行养肝青蔬汤','青色入肝，疏肝解郁','以西兰花、菠菜、芹菜和毛豆构成青绿色基底，突出春养肝木的理念。适合情绪郁闷、久看屏幕、眼干口苦的人群作为轻食晚餐或工作日午汤。','https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=1200','wood','肝','[\"西兰花 120g\", \"菠菜 80g\", \"芹菜 80g\", \"毛豆 60g\", \"苹果醋 5ml\", \"姜片 2片\"]',102,6.9,2,15.7,5.8,'[\"深绿叶菜富含叶酸和多酚\", \"西兰花中的萝卜硫素有助于抗炎\", \"高钾低脂有利于减轻水肿与疲劳\"]','[\"疏肝理气，帮助情志舒展\", \"青色养肝，适合春季顺时调养\", \"清而不寒，适合轻断食和清养周期\"]','1. 西兰花掰小朵，菠菜焯水备用；2. 芹菜切段，毛豆煮熟；3. 清水加姜片煮开，放入西兰花、芹菜煮 6 分钟；4. 加毛豆与菠菜再煮 2 分钟；5. 出锅前少量盐和苹果醋提味。',18,1,'[\"肝火偏旺\", \"眼疲劳\", \"情绪紧绷\"]','[\"胃寒明显\", \"术后虚弱初期\"]','春宜养肝，肝喜条达。青蔬和之，则气机舒展，目亦得养。',93,44,'2026-04-14 16:28:25','2026-04-14 16:30:22',0),
(21,'五行养心番茄甜椒汤','红色入心，轻盈暖身','以番茄、红甜椒、红腰豆为核心，兼顾植物蛋白与番茄红素，适合作为火行养心的抗炎暖汤。口感酸甜清爽，适合秋冬或运动后补充。','https://images.unsplash.com/photo-1473093295043-cdd812d0e601?w=1200','fire','心','[\"番茄 200g\", \"红甜椒 100g\", \"红腰豆 80g\", \"洋葱 50g\", \"蒜末 10g\", \"黑胡椒 少许\"]',118,6.2,1.8,20.4,6.4,'[\"番茄红素和维生素 C 共同提升抗氧化能力\", \"豆类提供稳定植物蛋白与可溶性膳食纤维\", \"低饱和脂肪有助于心血管友好\"]','[\"赤色养心，帮助温养心气\", \"清补兼施，适合压力大时调理\", \"适合搭配粗粮主食形成完整抗炎餐盘\"]','1. 番茄去皮切块，甜椒切丁，洋葱切碎；2. 少量橄榄油炒香洋葱和蒜末；3. 加番茄煮出汁后加入甜椒和红腰豆；4. 加适量清水小火煮 15 分钟，最后黑胡椒调味。',22,2,'[\"压力大\", \"心烦易燥\", \"需要控制油脂摄入者\"]','[\"胃酸反流明显\", \"腹胀严重时\"]','心主血脉，色赤入心。善用天然食材之色味，可助内外相应。',93,51,'2026-04-14 16:28:25','2026-04-14 16:30:22',0),
(22,'哈佛蔬菜汤','高纤轻负担的经典蔬菜组合','参考哈佛健康饮食理念，以十字花科、番茄、洋葱、南瓜和胡萝卜构成高纤、低热量、低负担的蔬菜汤。适合作为体重管理、清淡饮食和术后恢复期的基础蔬菜摄入方案。','https://images.unsplash.com/photo-1511690743698-d9d85f2fbf38?w=1200','earth','脾','[\"卷心菜 150g\", \"番茄 150g\", \"洋葱 80g\", \"南瓜 100g\", \"胡萝卜 80g\", \"西芹 60g\"]',109,4.1,1.5,22.8,7.2,'[\"多样化蔬菜提高植化素摄入\", \"低热量高饱腹感利于减重期抗炎\", \"十字花科与橙红色蔬菜协同抗氧化\"]','[\"清淡而有饱足感，便于长期坚持\", \"养脾和胃，适合晚餐轻断食\", \"适合作为抗炎饮食的入门菜品\"]','1. 所有蔬菜切块；2. 洋葱下锅略炒出香；3. 加入番茄、卷心菜、南瓜、胡萝卜、西芹；4. 加水后中火煮 25 分钟；5. 根据口味加入少量海盐和黑胡椒。',35,1,'[\"体重管理人群\", \"三高风险人群\", \"饮食油腻后调理\"]','[\"过度消瘦且食量不足者可搭配蛋白质主菜\"]','食不过精，味不过厚。蔬菜为本，久服则身轻气和。',176,109,'2026-04-14 16:28:25','2026-04-14 16:30:22',0),
(23,'地中海鹰嘴豆蔬菜汤','橄榄油与豆类的抗炎组合','结合地中海饮食中的橄榄油、鹰嘴豆、番茄、洋葱与香草，以植物蛋白和单不饱和脂肪酸为特色，适合希望兼顾饱腹感和炎症管理的人群。','https://images.unsplash.com/photo-1608500218890-c4f2cc74996b?w=1200','fire','心','[\"鹰嘴豆 120g\", \"番茄 150g\", \"胡萝卜 70g\", \"洋葱 60g\", \"橄榄油 8ml\", \"牛至碎 2g\"]',186,8.7,5.1,24.3,7.5,'[\"橄榄油中的单不饱和脂肪酸有助于抗炎\", \"鹰嘴豆提升饱腹感并稳定餐后血糖\", \"地中海香草有助于减少额外盐分依赖\"]','[\"和胃养气，适合替代高油浓汤\", \"心脉喜和，此汤清润不腻\", \"适合做工作日晚餐主食化汤品\"]','1. 鹰嘴豆提前浸泡煮熟；2. 洋葱碎用橄榄油炒香；3. 加番茄、胡萝卜翻炒出汁；4. 加入鹰嘴豆与清水煮 15 分钟；5. 出锅前加入牛至碎和黑胡椒。',28,2,'[\"控糖控脂人群\", \"久坐办公人群\", \"需要提升饱腹感者\"]','[\"豆类消化能力弱且腹胀明显者\"]','地中海饮食贵在简、鲜、和，道家饮食贵在顺、缓、久，二者皆重日用之养。',142,82,'2026-04-14 16:28:25','2026-04-14 16:30:22',0),
(24,'地中海橄榄油烤时蔬藜麦碗','彩色蔬菜与全谷物的清洁餐','以彩椒、西葫芦、茄子、圣女果搭配藜麦，突出地中海饮食中的彩色蔬菜、橄榄油和全谷物理念。适合午餐、减脂期和训练后恢复餐。','https://images.unsplash.com/photo-1543332164-6e82f355badc?w=1200','metal','肺','[\"藜麦 80g\", \"彩椒 120g\", \"西葫芦 100g\", \"茄子 80g\", \"圣女果 80g\", \"橄榄油 10ml\"]',214,7.5,6.8,28.6,6.9,'[\"全谷物搭配彩色蔬菜有助于稳定炎症指标\", \"橄榄油替代动物油可减少饱和脂肪\", \"高钾高纤适合清淡抗炎饮食\"]','[\"肺喜清肃，少油少腻更利气机\", \"彩蔬配全谷，利于轻身养气\", \"适合作为四时常备的清洁餐模板\"]','1. 藜麦洗净煮熟备用；2. 彩椒、西葫芦、茄子切块后拌橄榄油；3. 烤箱 200 度烤 18 分钟；4. 加入圣女果再烤 5 分钟；5. 与藜麦混合，撒少许海盐和黑胡椒即可。',30,2,'[\"减脂期\", \"高油饮食后调理\", \"久咳痰黏需清淡饮食者\"]','[\"脾胃特别虚寒者建议热食并减少生冷配料\"]','饮食之道，不在猎奇，而在常行。清简可久，五味得和，则形神自安。',118,67,'2026-04-14 16:28:25','2026-04-14 16:30:22',0);

/*Table structure for table `dish_favorite` */

DROP TABLE IF EXISTS `dish_favorite`;

CREATE TABLE `dish_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `dishId` bigint NOT NULL COMMENT '菜品 id',
  `userId` bigint NOT NULL COMMENT '用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_dishId` (`dishId`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品收藏关联表';

/*Data for the table `dish_favorite` */

/*Table structure for table `post` */

DROP TABLE IF EXISTS `post`;

CREATE TABLE `post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `tags` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签列表（json 数组）',
  `thumbNum` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `favourNum` int NOT NULL DEFAULT '0' COMMENT '收藏数',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_userId` (`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='帖子';

/*Data for the table `post` */

/*Table structure for table `post_favour` */

DROP TABLE IF EXISTS `post_favour`;

CREATE TABLE `post_favour` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint NOT NULL COMMENT '帖子 id',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_postId` (`postId`) USING BTREE,
  KEY `idx_userId` (`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='帖子收藏';

/*Data for the table `post_favour` */

/*Table structure for table `post_thumb` */

DROP TABLE IF EXISTS `post_thumb`;

CREATE TABLE `post_thumb` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint NOT NULL COMMENT '帖子 id',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_postId` (`postId`) USING BTREE,
  KEY `idx_userId` (`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='帖子点赞';

/*Data for the table `post_thumb` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `unionId` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信开放平台id',
  `mpOpenId` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号openId',
  `userName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
  `userAvatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像',
  `userEmail` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户邮箱',
  `userProfile` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户简介',
  `userRole` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin',
  `wood` int DEFAULT '0' COMMENT '五行体质-木',
  `fire` int DEFAULT '0' COMMENT '五行体质-火',
  `earth` int DEFAULT '0' COMMENT '五行体质-土',
  `metal` int DEFAULT '0' COMMENT '五行体质-金',
  `water` int DEFAULT '0' COMMENT '五行体质-水',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_userAccount` (`userAccount`)
) ENGINE=InnoDB AUTO_INCREMENT=2043962489193791491 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

/*Data for the table `user` */

insert  into `user`(`id`,`userAccount`,`userPassword`,`unionId`,`mpOpenId`,`userName`,`userAvatar`,`userEmail`,`userProfile`,`userRole`,`wood`,`fire`,`earth`,`metal`,`water`,`createTime`,`updateTime`,`isDelete`) values 
(2043957242320814082,'user277385','d9c31c2e16653d206cc598a539fc39c3',NULL,NULL,'????',NULL,'user277385@test.com',NULL,'user',20,20,20,20,20,'2026-04-14 15:39:16','2026-04-14 15:39:16',0),
(2043957467760459777,'user628941','d9c31c2e16653d206cc598a539fc39c3',NULL,NULL,'????',NULL,'user628941@test.com',NULL,'user',20,20,20,20,20,'2026-04-14 15:40:09','2026-04-14 15:40:09',0),
(2043960374098558978,'user616736','d9c31c2e16653d206cc598a539fc39c3',NULL,NULL,'????',NULL,'user616736@test.com',NULL,'user',20,20,20,20,20,'2026-04-14 15:51:42','2026-04-14 15:51:42',0),
(2043961020872818690,'zhangsanlisi','d906e3cd7bef50f82b1397963ceb5eed',NULL,NULL,'zhangsanlisi',NULL,'zhangsanlisi@qq.com',NULL,'admin',20,20,20,20,20,'2026-04-14 15:54:16','2026-04-14 16:02:32',0),
(2043962489193791490,'user964600','d9c31c2e16653d206cc598a539fc39c3',NULL,NULL,'????',NULL,'user964600@test.com',NULL,'user',20,20,20,20,20,'2026-04-14 16:00:07','2026-04-14 16:00:07',0);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
