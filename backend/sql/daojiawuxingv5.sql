/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 8.0.44 : Database - daojiawuxing
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

/*Table structure for table `cognition_integration_step` */

DROP TABLE IF EXISTS `cognition_integration_step`;

CREATE TABLE `cognition_integration_step` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '步骤标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '步骤说明',
  `actions` json DEFAULT NULL COMMENT '动作列表',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认知整合步骤表';

/*Data for the table `cognition_integration_step` */

insert  into `cognition_integration_step`(`id`,`title`,`description`,`actions`,`sort`,`createTime`,`updateTime`,`isDelete`) values 
(1,'第一步：观己','先用道家的方式看见自己的欲望、执念和情绪起伏，不急着纠正，先学会观察。','[\"记录今天最强烈的情绪触发点\", \"识别自己真正害怕失去的东西\", \"把我必须改写成我以为我必须\"]',1,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(2,'第二步：明势','借纳瓦尔和芒格的方法看清长期方向、收益结构和认知偏差，不做短期情绪决策。','[\"每个决定先问一年后是否还重要\", \"列出最坏结果与承受方式\", \"把问题切换为长期复利问题\"]',2,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(3,'第三步：定行','用斯多葛和道家的方式把认知落到行动，专注可控范围，形成稳态修行节律。','[\"只给自己一个今日核心动作\", \"遇到变化先回到呼吸和身体\", \"每周只优化一个长期习惯\"]',3,'2026-04-16 10:00:00','2026-04-16 10:00:00',0);

/*Table structure for table `cognition_origin` */

DROP TABLE IF EXISTS `cognition_origin`;

CREATE TABLE `cognition_origin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `originKey` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'daoist/naval/munger/stoic',
  `title` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `source` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源',
  `color` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '颜色',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '摘要',
  `principles` json DEFAULT NULL COMMENT '原则',
  `practices` json DEFAULT NULL COMMENT '修行动作',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_originKey` (`originKey`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认知修行来源表';

/*Data for the table `cognition_origin` */

insert  into `cognition_origin`(`id`,`originKey`,`title`,`source`,`color`,`summary`,`principles`,`practices`,`sort`,`createTime`,`updateTime`,`isDelete`) values 
(1,'daoist','道家认知根基','《道德经》/《庄子》','#5a8f6e','强调少私寡欲、返观内心、顺势而为，让人先从看见自己开始修行。','[\"少则得，多则惑\", \"先安内在，再应外在\", \"不与情绪硬碰，先观后动\"]','[\"每日 10 分钟静观起心动念\", \"遇事先停三息再表达\", \"每周复盘一次自己的执念\"]',1,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(2,'naval','纳瓦尔长期主义','纳瓦尔人生访谈 / The Almanack of Naval Ravikant','#2c3e50','强调长期复利、杠杆、清晰判断和宁静心智，把人生当成长期博弈而非短期冲动。','[\"用长期回报筛选短期选择\", \"用清晰感代替忙碌感\", \"幸福建立在平静心智之上\"]','[\"每天写下最重要的一件事\", \"减少低价值社交与信息噪音\", \"把时间投入可复利的技能与关系\"]',2,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(3,'munger','芒格思维模型','Charlie Munger','#d4a574','强调多元思维模型、反向思考和避免愚蠢，用更高质量的判断替代情绪化决策。','[\"先问怎样会失败\", \"用多个模型而非单一视角\", \"识别偏见比证明自己更重要\"]','[\"做决定前写出反面结果\", \"给重大判断列出 3 个不同学科视角\", \"每周记录一次认知偏差\"]',3,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(4,'stoic','斯多葛情绪训练','Marcus Aurelius / Epictetus','#c75b39','强调区分可控与不可控，把情绪管理转化为日常训练，帮助人减少内耗。','[\"只处理可控之事\", \"把困境当作训练材料\", \"先稳定心，再行动\"]','[\"每天区分可控/不可控事项\", \"遇到压力时做情境重构\", \"睡前复盘今天最失衡的时刻\"]',4,'2026-04-16 10:00:00','2026-04-16 10:00:00',0);

/*Table structure for table `cognition_reading` */

DROP TABLE IF EXISTS `cognition_reading`;

CREATE TABLE `cognition_reading` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '书名',
  `author` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者',
  `focus` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '阅读重点',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认知修行阅读表';

/*Data for the table `cognition_reading` */

insert  into `cognition_reading`(`id`,`title`,`author`,`focus`,`sort`,`createTime`,`updateTime`,`isDelete`) values 
(1,'《道德经》','老子','训练少私寡欲、反观自心与顺势而为。',1,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(2,'《庄子》','庄子','训练摆脱执念、拉开视角和减少自我束缚。',2,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(3,'The Almanack of Naval Ravikant','Eric Jorgenson','建立长期主义、杠杆意识和清晰决策框架。',3,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(4,'Poor Charlie’s Almanack','Charlie Munger','训练多元思维模型、反向思考与识别偏差。',4,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(5,'Meditations','Marcus Aurelius','训练情绪稳定、接受无常与专注可控。',5,'2026-04-16 10:00:00','2026-04-16 10:00:00',0);

/*Table structure for table `cognition_routine` */

DROP TABLE IF EXISTS `cognition_routine`;

CREATE TABLE `cognition_routine` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `period` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '晨间/白天/夜间',
  `title` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '说明',
  `checklist` json DEFAULT NULL COMMENT '清单',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认知修行节律表';

/*Data for the table `cognition_routine` */

insert  into `cognition_routine`(`id`,`period`,`title`,`description`,`checklist`,`sort`,`createTime`,`updateTime`,`isDelete`) values 
(1,'晨间','晨起定心','不先刷手机，先校准认知，再进入信息流。','[\"静坐或站立呼吸 3 分钟\", \"写下今天唯一最重要的事\", \"提醒自己今天只解决可控问题\"]',1,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(2,'白天','决策练习','把情绪反应转成结构化判断，避免高冲动决策。','[\"重大决定前做反向思考\", \"问自己这件事是否符合长期主义\", \"检查是不是因为面子、焦虑或比较心\"]',2,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(3,'夜间','晚间复盘','把一天的经验沉淀成认知，不把波动带进第二天。','[\"复盘一次失衡时刻\", \"写下一个今日偏见\", \"确认明天继续复利的小动作\"]',3,'2026-04-16 10:00:00','2026-04-16 10:00:00',0);

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='抗炎日记表';

/*Data for the table `diary` */

insert  into `diary`(`id`,`userId`,`title`,`content`,`images`,`tags`,`dietScore`,`energy`,`mood`,`sleep`,`digestion`,`practices`,`wisdomInsight`,`likes`,`isApproved`,`createTime`,`updateTime`,`isDelete`) values 
(1,2043960374098558978,'今天开始五行清养','早餐改成了小米南瓜粥，晚餐喝了五行抗炎蔬菜汤，感觉身体轻了很多。','[]','[\"清淡饮食\", \"养脾\"]',9,8,8,8,8,'[\"晚餐清淡\", \"饭后步行\"]','真正的调理不是猛补，而是每天都少一点负担。',3,1,'2026-04-14 15:51:42','2026-04-16 10:00:00',0),
(2,2043961020872818690,'今天开始吃抗炎蔬菜汤','心情变好了，下午没有那么困，晚上也少吃了很多零食。','[]','[\"食饮有节\"]',8,8,8,8,8,'[\"晚餐清淡少油\"]','少油少盐会让自己变健康。',5,1,'2026-04-14 15:55:12','2026-04-16 10:00:00',0),
(3,2043962489193791490,'练习长期主义养生','不再追求一周见效，而是先把睡眠、饮食和散步稳定下来。','[]','[\"长期主义\", \"认知修行\"]',8,8,8,8,8,'[\"早睡\", \"散步\", \"呼吸练习\"]','养生也是复利，慢一点反而更快。',2,1,'2026-04-14 16:00:07','2026-04-16 10:00:00',0),
(4,2043961020872818690,'我的日记','我的日记我的日记我的日记我的日记我的日记我的日记','[]','[\"食饮有节\"]',8,8,8,8,0,'[]','我的日记我的日记我的日记',1,1,'2026-04-14 16:01:57','2026-04-16 10:00:00',0),
(5,2044388697794584577,'认知修行第一天','今天把可控和不可控分开写下来，焦虑一下子少了很多，也更愿意接受慢慢改变。','[]','[\"认知修行\", \"斯多葛\"]',9,8,9,7,8,'[\"可控不可控练习\", \"睡前复盘\"]','先稳定自己的心，再去处理世界。',4,1,'2026-04-16 08:30:00','2026-04-16 10:00:00',0);

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日记评论表';

/*Data for the table `diary_comment` */

insert  into `diary_comment`(`id`,`diaryId`,`userId`,`content`,`createTime`,`updateTime`,`isDelete`) values 
(1,1,2043961020872818690,'这种日常清养方式很适合长期坚持。','2026-04-16 09:10:00','2026-04-16 09:10:00',0),
(2,5,2043957467760459777,'可控与不可控这个练习真的很有效。','2026-04-16 09:20:00','2026-04-16 09:20:00',0);

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
  `fiber` float DEFAULT '0' COMMENT '膳食纤维',
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
(19,'道家五行抗炎蔬菜汤','五色入五脏，温和抗炎','以番茄、白萝卜、西芹、胡萝卜、香菇等五色蔬菜为基础，兼顾清润、温养与高纤维特点，适合作为道家五行抗炎饮食的日常基础汤。汤体清爽，负担轻，既可帮助控制总热量，又利于长期执行。','https://images.unsplash.com/photo-1547592180-85f173990554?w=1200','earth','脾','[\"番茄 120g\", \"白萝卜 80g\", \"西芹 80g\", \"胡萝卜 80g\", \"洋葱 60g\", \"香菇 50g\", \"姜片 3片\"]',96,4.8,1.2,18.5,6.1,'[\"高纤维组合有助于改善肠道炎症环境\", \"番茄与菌菇富含抗氧化成分\", \"低油低盐适合慢性炎症人群长期食用\"]','[\"五色配伍以应五行，帮助整体调和\", \"温养中焦，减轻脾胃负担\", \"适合日常清养与季节交替调理\"]','1. 番茄、白萝卜、胡萝卜切块，西芹切段，洋葱切丝，香菇切片；2. 少量橄榄油煸香洋葱和姜片；3. 放入其余蔬菜翻炒 2 分钟后加清水；4. 中小火煮 20 分钟，最后少量海盐调味即可。',30,1,'[\"湿热体质\", \"熬夜上火\", \"久坐少动人群\"]','[\"脾胃极虚寒\", \"急性腹泻期\"]','《黄帝内经》有言：五色入五脏。饮食调和，不偏不倚，方能久养正气。',131,73,'2026-04-14 16:28:25','2026-04-16 10:00:00',0),
(20,'五行养肝青蔬汤','青色入肝，疏肝解郁','以西兰花、菠菜、芹菜和毛豆构成青绿色基底，突出春养肝木的理念。适合情绪郁闷、久看屏幕、眼干口苦的人群作为轻食晚餐或工作日午汤。','https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=1200','wood','肝','[\"西兰花 120g\", \"菠菜 80g\", \"芹菜 80g\", \"毛豆 60g\", \"苹果醋 5ml\", \"姜片 2片\"]',102,6.9,2,15.7,5.8,'[\"深绿叶菜富含叶酸和多酚\", \"西兰花中的萝卜硫素有助于抗炎\", \"高钾低脂有利于减轻水肿与疲劳\"]','[\"疏肝理气，帮助情志舒展\", \"青色养肝，适合春季顺时调养\", \"清而不寒，适合轻断食和清养周期\"]','1. 西兰花掰小朵，菠菜焯水备用；2. 芹菜切段，毛豆煮熟；3. 清水加姜片煮开，放入西兰花、芹菜煮 6 分钟；4. 加毛豆与菠菜再煮 2 分钟；5. 出锅前少量盐和苹果醋提味。',18,1,'[\"肝火偏旺\", \"眼疲劳\", \"情绪紧绷\"]','[\"胃寒明显\", \"术后虚弱初期\"]','春宜养肝，肝喜条达。青蔬和之，则气机舒展，目亦得养。',93,44,'2026-04-14 16:28:25','2026-04-16 10:00:00',0),
(21,'五行养心番茄甜椒汤','红色入心，轻盈暖身','以番茄、红甜椒、红腰豆为核心，兼顾植物蛋白与番茄红素，适合作为火行养心的抗炎暖汤。口感酸甜清爽，适合秋冬或运动后补充。','https://images.unsplash.com/photo-1473093295043-cdd812d0e601?w=1200','fire','心','[\"番茄 200g\", \"红甜椒 100g\", \"红腰豆 80g\", \"洋葱 50g\", \"蒜末 10g\", \"黑胡椒 少许\"]',118,6.2,1.8,20.4,6.4,'[\"番茄红素和维生素 C 共同提升抗氧化能力\", \"豆类提供稳定植物蛋白与可溶性膳食纤维\", \"低饱和脂肪有助于心血管友好\"]','[\"赤色养心，帮助温养心气\", \"清补兼施，适合压力大时调理\", \"适合搭配粗粮主食形成完整抗炎餐盘\"]','1. 番茄去皮切块，甜椒切丁，洋葱切碎；2. 少量橄榄油炒香洋葱和蒜末；3. 加番茄煮出汁后加入甜椒和红腰豆；4. 加适量清水小火煮 15 分钟，最后黑胡椒调味。',22,2,'[\"压力大\", \"心烦易燥\", \"需要控制油脂摄入者\"]','[\"胃酸反流明显\", \"腹胀严重时\"]','心主血脉，色赤入心。善用天然食材之色味，可助内外相应。',94,51,'2026-04-14 16:28:25','2026-04-16 10:00:00',0),
(22,'哈佛蔬菜汤','高纤轻负担的经典蔬菜组合','参考哈佛健康饮食理念，以十字花科、番茄、洋葱、南瓜和胡萝卜构成高纤、低热量、低负担的蔬菜汤。适合作为体重管理、清淡饮食和术后恢复期的基础蔬菜摄入方案。','https://images.unsplash.com/photo-1511690743698-d9d85f2fbf38?w=1200','earth','脾','[\"卷心菜 150g\", \"番茄 150g\", \"洋葱 80g\", \"南瓜 100g\", \"胡萝卜 80g\", \"西芹 60g\"]',109,4.1,1.5,22.8,7.2,'[\"多样化蔬菜提高植化素摄入\", \"低热量高饱腹感利于减重期抗炎\", \"十字花科与橙红色蔬菜协同抗氧化\"]','[\"清淡而有饱足感，便于长期坚持\", \"养脾和胃，适合晚餐轻断食\", \"适合作为抗炎饮食的入门菜品\"]','1. 所有蔬菜切块；2. 洋葱下锅略炒出香；3. 加入番茄、卷心菜、南瓜、胡萝卜、西芹；4. 加水后中火煮 25 分钟；5. 根据口味加入少量海盐和黑胡椒。',35,1,'[\"体重管理人群\", \"三高风险人群\", \"饮食油腻后调理\"]','[\"过度消瘦且食量不足者可搭配蛋白质主菜\"]','食不过精，味不过厚。蔬菜为本，久服则身轻气和。',176,109,'2026-04-14 16:28:25','2026-04-16 10:00:00',0),
(23,'地中海鹰嘴豆蔬菜汤','橄榄油与豆类的抗炎组合','结合地中海饮食中的橄榄油、鹰嘴豆、番茄、洋葱与香草，以植物蛋白和单不饱和脂肪酸为特色，适合希望兼顾饱腹感和炎症管理的人群。','https://images.unsplash.com/photo-1608500218890-c4f2cc74996b?w=1200','fire','心','[\"鹰嘴豆 120g\", \"番茄 150g\", \"胡萝卜 70g\", \"洋葱 60g\", \"橄榄油 8ml\", \"牛至碎 2g\"]',186,8.7,5.1,24.3,7.5,'[\"橄榄油中的单不饱和脂肪酸有助于抗炎\", \"鹰嘴豆提升饱腹感并稳定餐后血糖\", \"地中海香草有助于减少额外盐分依赖\"]','[\"和胃养气，适合替代高油浓汤\", \"心脉喜和，此汤清润不腻\", \"适合做工作日晚餐主食化汤品\"]','1. 鹰嘴豆提前浸泡煮熟；2. 洋葱碎用橄榄油炒香；3. 加番茄、胡萝卜翻炒出汁；4. 加入鹰嘴豆与清水煮 15 分钟；5. 出锅前加入牛至碎和黑胡椒。',28,2,'[\"控糖控脂人群\", \"久坐办公人群\", \"需要提升饱腹感者\"]','[\"豆类消化能力弱且腹胀明显者\"]','地中海饮食贵在简、鲜、和，道家饮食贵在顺、缓、久，二者皆重日用之养。',142,82,'2026-04-14 16:28:25','2026-04-16 10:00:00',0),
(24,'地中海橄榄油烤时蔬藜麦碗','彩色蔬菜与全谷物的清洁餐','以彩椒、西葫芦、茄子、圣女果搭配藜麦，突出地中海饮食中的彩色蔬菜、橄榄油和全谷物理念。适合午餐、减脂期和训练后恢复餐。','https://images.unsplash.com/photo-1543332164-6e82f355badc?w=1200','metal','肺','[\"藜麦 80g\", \"彩椒 120g\", \"西葫芦 100g\", \"茄子 80g\", \"圣女果 80g\", \"橄榄油 10ml\"]',214,7.5,6.8,28.6,6.9,'[\"全谷物搭配彩色蔬菜有助于稳定炎症指标\", \"橄榄油替代动物油可减少饱和脂肪\", \"高钾高纤适合清淡抗炎饮食\"]','[\"肺喜清肃，少油少腻更利气机\", \"彩蔬配全谷，利于轻身养气\", \"适合作为四时常备的清洁餐模板\"]','1. 藜麦洗净煮熟备用；2. 彩椒、西葫芦、茄子切块后拌橄榄油；3. 烤箱 200 度烤 18 分钟；4. 加入圣女果再烤 5 分钟；5. 与藜麦混合，撒少许海盐和黑胡椒即可。',30,2,'[\"减脂期\", \"高油饮食后调理\", \"久咳痰黏需清淡饮食者\"]','[\"脾胃特别虚寒者建议热食并减少生冷配料\"]','饮食之道，不在猎奇，而在常行。清简可久，五味得和，则形神自安。',118,67,'2026-04-14 16:28:25','2026-05-07 16:27:57',0);

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='帖子收藏';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='帖子点赞';

/*Data for the table `post_thumb` */

/*Table structure for table `practice_content` */

DROP TABLE IF EXISTS `practice_content`;

CREATE TABLE `practice_content` (
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '前端内容 id',
  `type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块类型',
  `title` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `wuxing` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对应五行',
  `duration` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '时长',
  `difficulty` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '难度',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '摘要',
  `suitableCrowd` json DEFAULT NULL COMMENT '适合人群',
  `steps` json DEFAULT NULL COMMENT '步骤',
  `tips` json DEFAULT NULL COMMENT '注意事项',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_wuxing` (`wuxing`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='修行内容表';

/*Data for the table `practice_content` */

insert  into `practice_content`(`id`,`type`,`title`,`wuxing`,`duration`,`difficulty`,`summary`,`suitableCrowd`,`steps`,`tips`,`sort`,`createTime`,`updateTime`,`isDelete`) values 
('body-baduanjin-start','body','八段锦入门舒展','wood','12 分钟','入门','用温和的拉伸与导引动作打开肩颈、腰背和胸腔，适合久坐人群。','[\"久坐办公人群\", \"肩颈紧张人群\", \"缺少运动基础者\"]','[\"先站定调息 1 分钟\", \"做双手托天、左右开弓、摇头摆尾等基础动作\", \"动作结束后原地静站 1 分钟\"]','[\"动作慢于平时习惯\", \"保持鼻吸鼻呼\", \"练后喝温水\"]',5,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('body-neck-shoulder','body','肩颈五行导引','metal','8 分钟','入门','专门针对久坐导致的肩颈僵硬，用导引动作疏通肺经与大肠经。','[\"长期使用电脑的人\", \"肩颈酸痛人群\", \"呼吸浅短的人\"]','[\"坐直，双手放膝上，先做 3 次深呼吸\", \"缓慢低头，下巴贴近胸口，保持 5 秒，再缓慢抬头\", \"头向右侧倾，右耳靠近右肩，保持 5 秒，换左侧\", \"双手十指交叉，翻掌向上推，同时抬头，保持 5 秒\", \"双臂向后展开，肩胛骨夹紧，深吸气，呼气时放松\", \"最后做肩膀绕环 5 次，感受肩颈的松动\"]','[\"动作要慢，不要用力拉扯\", \"如有颈椎问题请先咨询医生\", \"每工作 1 小时做一次效果最好\"]',7,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('body-walk-meditation','body','饭后正念步行','earth','15 分钟','入门','饭后缓慢步行，配合呼吸觉察，同时促进脾胃运化和身心放松。','[\"脾胃消化不好的人\", \"饭后容易犯困的人\", \"想把运动融入日常的人\"]','[\"饭后 20 分钟开始，选择安静的路线\", \"步速比平时慢 30%，感受每一步脚踩地面的感觉\", \"配合呼吸：吸气走 3 步，呼气走 3 步\", \"将注意力放在腹部，感受消化的温热感\", \"如果思绪飘走，轻轻把注意力带回脚步和呼吸\"]','[\"不要边走边看手机\", \"天气不好时可在室内缓慢踱步\", \"步行后喝一小杯温水\"]',8,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('body-zhan-zhuang','body','站桩基础练习','earth','10 分钟','入门','通过静止站立培养身体根基感，改善姿势、稳定气血、增强专注力。','[\"身体虚弱需要温和调理的人\", \"想改善体态的人\", \"需要提升专注力的人\"]','[\"双脚与肩同宽，脚尖微微外八，膝盖微弯不超过脚尖\", \"双手抱球姿势置于腹前，肩膀放松下沉\", \"下颌微收，头顶虚领，感受脊柱自然拉伸\", \"闭眼或半闭眼，将注意力放在小腹（丹田）\", \"保持 5-10 分钟，感受身体的细微感觉\"]','[\"初学者从 3 分钟开始，逐渐增加\", \"腿酸是正常的，但膝盖不应该痛\", \"站完后缓慢活动关节，不要立刻剧烈运动\"]',6,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('cognition-anti-inflammation','cognition','抗炎饮食的长期主义','earth','8 分钟阅读','基础','建立不是吃一顿就见效，而是长期微调生活方式的认知框架。','[\"想减轻焦虑型养生的人\", \"总想速成的人\", \"需要长期陪伴的用户\"]','[\"理解炎症是长期累积结果\", \"接受饮食改善需要时间\", \"把饮食、睡眠、运动、情绪一起看待\", \"从每天 1 个可执行动作开始\"]','[\"避免追求极端饮食\", \"少做短期高压计划\", \"记录自己的小变化\"]',13,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('cognition-long-term-filter','cognition','长期主义决策过滤器','water','10 分钟','进阶','用纳瓦尔的长期主义框架，建立一套过滤短期冲动决策的思维工具。','[\"容易做冲动决定的人\", \"想建立长期思维的人\", \"在重要选择前需要清晰框架的人\"]','[\"面对一个决定时，先停下来，不要立刻行动\", \"问：一年后，这个决定还重要吗？\", \"问：这个选择是在积累复利，还是在消耗资源？\", \"问：如果我的最尊敬的人看到这个决定，他会怎么评价？\", \"问：这个决定是出于恐惧、焦虑、比较心，还是出于真正的价值判断？\", \"基于以上回答，重新做出决定\"]','[\"重大决定不要在情绪激动时做\", \"可以把这 4 个问题贴在显眼处\", \"不是所有决定都需要这个流程，用于重要选择\"]',16,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('cognition-stoic-control','cognition','可控与不可控的区分练习','metal','8 分钟','基础','用斯多葛哲学的核心工具，把精力集中在可控事项上，减少对不可控事物的内耗。','[\"容易为不可控事情焦虑的人\", \"有控制欲困扰的人\", \"想减少内耗的人\"]','[\"拿出纸笔，写下当前最让你焦虑的 3 件事\", \"对每件事，问：这件事我能直接控制吗？\", \"把每件事分类：完全可控 / 部分可控 / 完全不可控\", \"对完全不可控的事，练习说：这不在我的控制范围内，我选择接受\", \"对部分可控的事，只列出你能做的那部分，专注于此\", \"对完全可控的事，立刻制定一个最小行动\"]','[\"这个练习需要诚实，不要把不可控当借口\", \"每天做一次，遇到压力时随时可用\", \"坚持 2 周后会发现焦虑明显减少\"]',15,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('cognition-wuxing-self','cognition','五行体质自我认知','wood','10 分钟阅读','基础','了解五行体质的基本框架，学会用五行视角观察自己的情绪、饮食和身体倾向。','[\"刚接触中医养生的人\", \"想了解自己体质的人\", \"对五行感兴趣的人\"]','[\"阅读五行基本对应：木（肝/怒/春）、火（心/喜/夏）、土（脾/思/长夏）、金（肺/悲/秋）、水（肾/恐/冬）\", \"观察自己最容易出现哪种情绪（怒/焦虑/思虑过多/悲/恐）\", \"观察自己身体最常见的不适（眼疲劳/心悸/消化差/呼吸浅/腰酸）\", \"初步判断自己的主导五行倾向\", \"根据对应五行，找到一个今天可以做的调理动作\"]','[\"体质不是固定标签，会随季节和状态变化\", \"不要过度执着于我是什么体质\", \"重点是找到当下最需要调理的方向\"]',14,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('meditation-478-breath','meditation','4-7-8 呼吸法','metal','5 分钟','入门','用特定节奏的呼吸激活副交感神经，快速降低焦虑和心率，适合压力时刻使用。','[\"容易焦虑紧张的人\", \"需要快速平静的人\", \"有轻度失眠的人\"]','[\"坐直或躺下，舌尖轻抵上颚\", \"完全呼出肺中气体\", \"闭嘴，用鼻子吸气，心中默数 4 秒\", \"屏住呼吸，默数 7 秒\", \"用嘴呼气，发出轻微呼声，默数 8 秒\", \"重复以上循环 4 次\"]','[\"初学者可能感到轻微头晕，属正常，减慢节奏即可\", \"每天练习 2 次效果最佳\", \"不要在驾驶或操作机械时练习\"]',12,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('meditation-body-scan','meditation','身体扫描放松','water','15 分钟','入门','从脚趾到头顶逐步扫描身体，释放深层肌肉紧张，适合睡前或高压后使用。','[\"身体长期紧绷的人\", \"有睡眠困难的人\", \"想深度放松的人\"]','[\"躺下或坐稳，闭眼，做 5 次深呼吸\", \"将注意力移到双脚，感受脚趾、脚掌、脚跟的感觉，不评判，只观察\", \"缓慢向上移动：小腿、膝盖、大腿、臀部，每个部位停留 20-30 秒\", \"继续扫描腹部、胸口、双手、肩膀、颈部、面部\", \"到达头顶后，感受整个身体作为一个整体的存在\", \"做 3 次深呼吸，缓慢睁眼\"]','[\"如果某个部位特别紧绷，多停留一会儿\", \"不需要强迫放松，觉察本身就是放松\", \"可以配合轻柔的背景音乐\"]',10,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('meditation-breath-5','meditation','五分钟呼吸冥想','metal','5 分钟','入门','通过专注呼吸和觉察身体，快速帮助用户从高压状态回到稳定状态。','[\"情绪起伏大的人\", \"午后疲惫的人\", \"睡前难放松的人\"]','[\"坐稳身体并放松肩膀\", \"将注意力放在鼻尖呼吸\", \"吸气数 1，呼气数 2，循环到 10\", \"分心时轻轻把注意力带回来\"]','[\"不追求完全无念\", \"环境尽量安静\", \"每天固定时间更容易坚持\"]',9,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('meditation-wuxing-visualize','meditation','五色观想冥想','fire','12 分钟','进阶','用五行对应的五种颜色进行观想，调和五脏能量，是道家传统的内观练习。','[\"对道家修行感兴趣的人\", \"有一定冥想基础的人\", \"想深化身心连接的人\"]','[\"盘坐或端坐，闭眼，调息 3 分钟\", \"观想绿色光芒从肝脏升起，随呼吸扩散到全身（木行，疏肝）\", \"观想红色温暖光从心脏散出，感受心跳平稳有力（火行，养心）\", \"观想黄色柔和光在腹部中央，感受脾胃温暖运化（土行，健脾）\", \"观想白色清澈光在胸腔，随每次呼吸清洁肺部（金行，润肺）\", \"观想深蓝色沉静光在腰肾，感受深层能量储存（水行，补肾）\", \"最后观想五色光汇聚成白光，充满全身，缓慢睁眼\"]','[\"不需要真的看到颜色，意念到即可\", \"每种颜色停留约 1-2 分钟\", \"练习后可能感到温热或轻微酸胀，属正常现象\"]',11,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('spirit-emotion-observe','spirit','情绪观察日记','wood','10 分钟','入门','用文字记录情绪触发点，训练先观察再反应的内在稳定能力。','[\"容易被情绪带走的人\", \"想提升自我觉察的人\", \"有情绪内耗困扰的人\"]','[\"找一个安静的时间，拿出纸笔或手机备忘录\", \"写下今天最强烈的一次情绪：是什么触发了它？\", \"描述当时身体的感受（胸口紧、呼吸浅、肩膀僵等）\", \"问自己：这个情绪背后，我真正在意的是什么？\", \"写一句话总结：下次遇到类似情境，我可以怎么做\"]','[\"不评判自己的情绪，只是观察\", \"坚持 7 天后会发现情绪模式\", \"不需要写很多，一段话即可\"]',2,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('spirit-evening-release','spirit','睡前情绪释放','water','8 分钟','入门','通过身体扫描和意象引导，把白天积累的情绪压力在入睡前释放。','[\"睡前思绪纷乱的人\", \"容易把白天情绪带入睡眠的人\", \"有轻度焦虑的人\"]','[\"躺下，闭眼，做 3 次深长呼吸\", \"从头顶开始，缓慢扫描身体，感受哪里有紧绷\", \"对每个紧绷部位，吸气时想象光照进去，呼气时想象紧张随气流散出\", \"回想今天一件让自己感到满足或感恩的小事\", \"告诉自己：今天已经结束，明天重新开始\"]','[\"不要强迫自己放松，顺其自然\", \"可以配合轻柔的白噪音\", \"如果思绪乱，把注意力拉回呼吸即可\"]',3,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('spirit-gratitude-practice','spirit','感恩三件事练习','earth','5 分钟','入门','每天记录三件值得感恩的小事，训练大脑从缺乏感转向丰盛感。','[\"容易陷入负面思维的人\", \"长期处于压力中的人\", \"想建立积极心态的人\"]','[\"每天固定时间（推荐晚饭后或睡前）\", \"写下今天三件让你感到感激的事，可以很小\", \"对每件事，写一句为什么它让你感到好\", \"闭眼回味 30 秒，让这种感受在身体里停留\"]','[\"不要重复同样的事，每天找新的\", \"越具体越有效，避免泛泛而谈\", \"坚持 21 天效果最明显\"]',4,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
('spirit-morning-heart','spirit','晨间静心三问','fire','5 分钟','入门','通过三组简单问题，帮助用户每天早晨稳定心神、建立情绪边界。','[\"容易焦躁的人\", \"工作压力较大者\", \"想建立晨间仪式感的人\"]','[\"闭眼深呼吸 3 次\", \"问自己今天最重要的一件事是什么\", \"问自己今天最想放下的情绪是什么\", \"写下一句今日心法\"]','[\"建议起床后 30 分钟内完成\", \"不要一边刷手机一边做\", \"可以配合温热茶饮\"]',1,'2026-04-16 10:00:00','2026-04-16 10:00:00',0);

/*Table structure for table `practice_module` */

DROP TABLE IF EXISTS `practice_module`;

CREATE TABLE `practice_module` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'spirit/body/meditation/cognition',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块名称',
  `title` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主标题',
  `subtitle` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '副标题',
  `icon` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标字符',
  `color` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主题色',
  `highlights` json DEFAULT NULL COMMENT '亮点标签',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='修行模块表';

/*Data for the table `practice_module` */

insert  into `practice_module`(`id`,`type`,`name`,`title`,`subtitle`,`icon`,`color`,`highlights`,`sort`,`createTime`,`updateTime`,`isDelete`) values 
(1,'spirit','精神修行','稳定情绪，涵养心神','通过心法、情绪观察和日常觉察，建立平和持续的内在能量。','心','#c75b39','[\"一日一悟\", \"情绪观察\", \"每日心法\"]',1,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(2,'body','身体修行','导引气血，舒展筋骨','用轻量动作、导引术、拉伸与站桩，让身体进入更稳的节律。','形','#5a8f6e','[\"八段锦\", \"站桩\", \"五行导引\"]',2,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(3,'meditation','冥想修行','静心观息，回到当下','通过呼吸、静坐、观想与身体扫描，降低长期压力消耗。','静','#2c3e50','[\"呼吸冥想\", \"睡前放松\", \"五色观想\"]',3,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(4,'cognition','认知修行','升级认知，形成长期主义','从道家养生观到抗炎科学，建立更清晰稳定的自我理解框架。','知','#d4a574','[\"五行认知\", \"抗炎误区\", \"身心关系\"]',4,'2026-04-16 10:00:00','2026-04-16 10:00:00',0);

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `unionId` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信开放平台id',
  `mpOpenId` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号openId',
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
) ENGINE=InnoDB AUTO_INCREMENT=2051931534617206786 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

/*Data for the table `user` */

insert  into `user`(`id`,`userAccount`,`userPassword`,`unionId`,`mpOpenId`,`userName`,`userAvatar`,`userEmail`,`userProfile`,`userRole`,`wood`,`fire`,`earth`,`metal`,`water`,`createTime`,`updateTime`,`isDelete`) values 
(2043957242320814082,'user277385','d9c31c2e16653d206cc598a539fc39c3',NULL,NULL,'青木',NULL,'user277385@test.com','喜欢春季养肝与轻断食。','user',30,18,20,17,15,'2026-04-14 15:39:16','2026-04-16 10:00:00',0),
(2043957467760459777,'user628941','d9c31c2e16653d206cc598a539fc39c3',NULL,NULL,'静心者',NULL,'user628941@test.com','坚持冥想，关注睡眠质量。','user',15,22,18,20,25,'2026-04-14 15:40:09','2026-04-16 10:00:00',0),
(2043960374098558978,'user616736','d9c31c2e16653d206cc598a539fc39c3',NULL,NULL,'晨光',NULL,'user616736@test.com','正在练习五行导引和情绪记录。','user',24,16,18,20,22,'2026-04-14 15:51:42','2026-04-16 10:00:00',0),
(2043961020872818690,'zhangsanlisi','d906e3cd7bef50f82b1397963ceb5eed',NULL,NULL,'zhangsanlisi',NULL,'zhangsanlisi@qq.com','管理员账号，维护菜品与修行内容。','admin',20,20,20,20,20,'2026-04-14 15:54:16','2026-04-16 10:00:00',0),
(2043962489193791490,'user964600','d9c31c2e16653d206cc598a539fc39c3',NULL,NULL,'松鹤',NULL,'user964600@test.com','更关注脾胃调理与长期主义养生。','user',16,14,30,18,22,'2026-04-14 16:00:07','2026-04-16 10:00:00',0),
(2044388697794584577,'zhangsanlisi123','d906e3cd7bef50f82b1397963ceb5eed',NULL,NULL,'zhangsanlisi123',NULL,'zhangsanlisi123@qq.com','认知修行爱好者，喜欢纳瓦尔和道家思想结合。','user',10,12,25,38,15,'2026-04-15 20:13:43','2026-04-16 10:00:00',0),
(2046400551786057730,'abcdabcd','f84558940161dee7821e967fd3101e27',NULL,NULL,'abcdabcd',NULL,'abcd@qq.com',NULL,'user',20,20,20,20,20,'2026-04-21 09:28:06','2026-04-21 09:28:06',0),
(2048572160532275202,'wangwuzhaoliu','ead36286033e5386a7b028692d235404',NULL,NULL,'计算机四班','https://api.dicebear.com/7.x/avataaars/svg?seed=wangwuzhaoliu','wangwuzhaoliu@qq.com','','user',20,20,20,27,13,'2026-04-27 09:17:18','2026-04-27 09:18:14',0),
(2051909099780038658,'sansansan','ad7c1f3c2b3ea13445c307d61d42f98c',NULL,NULL,'sansansan',NULL,'2440222798@qq.com',NULL,'user',20,20,20,20,20,'2026-05-06 14:17:06','2026-05-06 14:17:06',0),
(2051915042706153474,'15896264805','0349d4d623013df9724151e41c657b50',NULL,NULL,'橘子海',NULL,'1294544717@qq.com',NULL,'user',20,20,20,20,20,'2026-05-06 14:40:43','2026-05-06 14:40:43',0),
(2051931534617206785,'abcde','b0dd3697a192885d7c055db46155b26a',NULL,NULL,'abcde',NULL,'3308180779@qq.com',NULL,'user',20,20,20,20,20,'2026-05-06 15:46:15','2026-05-06 15:46:15',0);

/*Table structure for table `user_checkin` */

DROP TABLE IF EXISTS `user_checkin`;

CREATE TABLE `user_checkin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userId` bigint NOT NULL COMMENT '用户 id',
  `recordDate` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '打卡日期，兼容前端 zh-CN 日期格式',
  `dietDone` tinyint NOT NULL DEFAULT '0' COMMENT '清淡抗炎饮食',
  `practiceDone` tinyint NOT NULL DEFAULT '0' COMMENT '完成修行练习',
  `moodStable` tinyint NOT NULL DEFAULT '0' COMMENT '情绪稳定',
  `restEnough` tinyint NOT NULL DEFAULT '0' COMMENT '按时休息',
  `practiceTypes` json DEFAULT NULL COMMENT '修行类型数组',
  `sleepScore` int NOT NULL DEFAULT '70' COMMENT '睡眠质量',
  `moodScore` int NOT NULL DEFAULT '70' COMMENT '情绪状态',
  `balanceScore` int NOT NULL DEFAULT '75' COMMENT '五行平衡评分',
  `completedCount` int NOT NULL DEFAULT '0' COMMENT '完成项数量',
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '打卡备注',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_recordDate` (`userId`,`recordDate`),
  KEY `idx_recordDate` (`recordDate`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户修行打卡表';

/*Data for the table `user_checkin` */

insert  into `user_checkin`(`id`,`userId`,`recordDate`,`dietDone`,`practiceDone`,`moodStable`,`restEnough`,`practiceTypes`,`sleepScore`,`moodScore`,`balanceScore`,`completedCount`,`note`,`createTime`,`updateTime`,`isDelete`) values 
(1,2044388697794584577,'2026/4/14',1,1,1,0,'[\"cognition\", \"meditation\"]',72,80,78,3,'今天完成了可控与不可控练习，也做了 5 分钟呼吸冥想。','2026-04-14 21:00:00','2026-04-14 21:00:00',0),
(2,2044388697794584577,'2026/4/15',1,1,1,1,'[\"cognition\", \"body\"]',85,82,88,4,'今天早睡、散步、做了长期主义决策过滤器，状态很好。','2026-04-15 21:00:00','2026-04-15 21:00:00',0),
(3,2043961020872818690,'2026/4/15',1,1,0,1,'[\"spirit\", \"body\", \"meditation\"]',76,68,74,3,'今天饮食比较清淡，做了八段锦，但下午情绪略躁。','2026-04-15 22:00:00','2026-04-15 22:00:00',0),
(4,2044388697794584577,'2026/4/23',1,1,1,1,'[\"spirit\", \"body\", \"meditation\", \"cognition\"]',70,70,75,4,'感受不错','2026-04-23 14:00:51','2026-04-23 14:00:51',0),
(5,2044388697794584577,'2026/4/27',1,1,1,1,'[\"spirit\", \"body\", \"meditation\", \"cognition\"]',70,70,75,4,'AAA','2026-04-27 07:56:02','2026-04-27 07:56:02',0),
(6,2051909099780038658,'2026/5/6',1,1,0,0,'[]',100,70,100,2,'','2026-05-06 14:23:57','2026-05-06 14:23:57',0),
(7,2051931534617206785,'2026/5/6',1,1,1,1,'[\"spirit\", \"meditation\", \"body\", \"cognition\"]',60,100,100,4,'1','2026-05-06 15:47:28','2026-05-06 20:58:06',0);

/*Table structure for table `wuxing_topic` */

DROP TABLE IF EXISTS `wuxing_topic`;

CREATE TABLE `wuxing_topic` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `topicKey` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'wood/fire/earth/metal/water',
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '五行名称',
  `organ` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对应脏腑',
  `season` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对应季节',
  `color` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '展示色值',
  `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '简介',
  `dietFocus` json DEFAULT NULL COMMENT '饮食重点',
  `dietRecipes` json DEFAULT NULL COMMENT '饮食方案',
  `emotionFocus` json DEFAULT NULL COMMENT '情绪重点',
  `emotionSteps` json DEFAULT NULL COMMENT '情绪调理步骤',
  `bodyFocus` json DEFAULT NULL COMMENT '身体重点',
  `bodyExercise` json DEFAULT NULL COMMENT '对应功法',
  `practiceFocus` json DEFAULT NULL COMMENT '修行重点',
  `seasonalTips` json DEFAULT NULL COMMENT '季节提示',
  `dailyAdvice` json DEFAULT NULL COMMENT '日常建议',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_topicKey` (`topicKey`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='五行专题表';

/*Data for the table `wuxing_topic` */

insert  into `wuxing_topic`(`id`,`topicKey`,`name`,`organ`,`season`,`color`,`intro`,`dietFocus`,`dietRecipes`,`emotionFocus`,`emotionSteps`,`bodyFocus`,`bodyExercise`,`practiceFocus`,`seasonalTips`,`dailyAdvice`,`sort`,`createTime`,`updateTime`,`isDelete`) values 
(1,'wood','木','肝','春','#5a8f6e','木行主生发、疏泄与舒展，适合关注情绪流动、筋骨舒展和春季养肝。','[\"青色蔬菜\", \"疏肝茶饮\", \"低油清鲜食物\"]','[{\"desc\": \"枸杞 10 粒 + 菊花 5 朵，热水冲泡，养肝明目，适合眼疲劳人群每日饮用。\", \"name\": \"枸杞菊花茶\"}, {\"desc\": \"芹菜切段，大蒜爆香，快炒出锅，疏肝理气，减少油腻，春季首选。\", \"name\": \"清炒芹菜\"}, {\"desc\": \"绿豆 30g + 薏米 30g + 大米 50g，清热利湿，适合春季肝火旺盛时食用。\", \"name\": \"绿豆薏米粥\"}]','[\"怒气管理\", \"压力疏导\", \"情绪舒展\"]','[\"当感到愤怒或烦躁时，先停下来，做 3 次深长呼吸\", \"把情绪写下来：我现在感到___，因为___\", \"问自己：这件事一年后还重要吗？\", \"做 5 分钟拉伸或散步，让情绪通过身体流动出去\", \"回来后再处理问题，而不是在情绪高峰时做决定\"]','[\"拉伸筋膜\", \"肩颈舒展\", \"眼疲劳调理\"]','{\"name\": \"春季养肝导引\", \"steps\": [\"站立，双脚与肩同宽，深呼吸 3 次\", \"双手向上伸展，想象自己是一棵树向上生长，保持 10 秒\", \"身体缓慢向左侧弯，感受右侧肋骨下方（肝区）的拉伸，保持 15 秒\", \"换右侧，同样保持 15 秒\", \"双手叉腰，缓慢转动腰部，顺时针 5 圈，逆时针 5 圈\", \"最后做眼部运动：眼球缓慢转动，上下左右各 3 次\"]}','[\"晨起舒展\", \"情绪观察\", \"春季养肝导引\"]','[\"春季是肝气最旺的季节，也是最容易肝气郁结的时候\", \"春天多吃绿色食物，少吃酸味（酸入肝，过多反而伤肝）\", \"春季适合早起，顺应阳气生发，不要赖床\", \"春天情绪容易波动，遇事先深呼吸，不要压抑情绪\"]','[\"早睡 30 分钟养肝血\", \"减少辛辣油炸\", \"每天进行 10 分钟拉伸\"]',1,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(2,'fire','火','心','夏','#c75b39','火行主心神、血脉与热能，适合关注睡眠、安神、情绪稳定和夏季清养。','[\"清心红色食材\", \"少油少躁饮食\", \"安神茶饮\"]','[{\"desc\": \"莲子 20g + 百合 15g + 大米 60g，清心安神，适合心烦失眠、夏季燥热时食用。\", \"name\": \"莲子百合粥\"}, {\"desc\": \"红枣 5 颗 + 桂圆 8 颗，热水冲泡，养心补血，适合心悸、睡眠浅的人。\", \"name\": \"红枣桂圆茶\"}, {\"desc\": \"苦瓜切片焯水，与鸡蛋同炒，清心降火，夏季去燥首选，每周 2-3 次。\", \"name\": \"苦瓜炒蛋\"}]','[\"心烦缓解\", \"情绪稳定\", \"睡前放松\"]','[\"感到心烦时，立刻离开当前环境，去喝一杯温水\", \"找一个安静的地方，做 4-7-8 呼吸（吸 4 秒，屏 7 秒，呼 8 秒）\", \"问自己：我现在的心烦是因为什么？是疲劳、饥饿还是真正的问题？\", \"如果是疲劳，先休息；如果是真正的问题，写下来，等情绪平稳后再处理\", \"睡前 1 小时停止使用手机，做轻柔的拉伸或听舒缓音乐\"]','[\"心肺轻运动\", \"午后降躁\", \"入睡准备\"]','{\"name\": \"心肺舒缓练习\", \"steps\": [\"站立或坐下，双手交叠放在胸口（心脏位置）\", \"闭眼，感受心跳，做 5 次深呼吸，每次呼气时想象心脏放松\", \"双臂缓慢向两侧展开，胸腔打开，吸气；呼气时双臂收回，重复 5 次\", \"轻拍胸口（胸骨位置）30 次，促进胸腺激活\", \"最后双手搓热，捂住心脏位置，感受温热，保持 1 分钟\"]}','[\"静心呼吸\", \"晚间放松\", \"五色观想\"]','[\"夏季心火旺，容易心烦、失眠、口舌生疮\", \"夏天午睡 20-30 分钟可以养心，但不要超过 1 小时\", \"夏季少吃辛辣油炸，多吃苦味食物（苦瓜、莲子心）清心火\", \"夏天出汗多，注意补充水分和电解质，不要贪凉\"]','[\"下午减少咖啡因\", \"睡前减少高刺激内容\", \"晚间做 5 分钟呼吸练习\"]',2,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(3,'earth','土','脾','长夏','#d4a574','土行主运化与稳定，适合关注脾胃、节律、湿气管理与规律生活。','[\"健脾谷物\", \"温和炖汤\", \"低负担主食\"]','[{\"desc\": \"山药 100g + 薏米 30g + 大米 50g，健脾祛湿，适合消化不好、身体沉重的人每周 3 次。\", \"name\": \"山药薏米粥\"}, {\"desc\": \"红豆 50g + 陈皮 5g，煮 40 分钟，健脾利湿，改善腹胀、大便不成形。\", \"name\": \"红豆陈皮汤\"}, {\"desc\": \"小米 60g + 南瓜 100g，温补脾胃，适合早餐，特别适合脾胃虚寒、食欲不振的人。\", \"name\": \"小米南瓜粥\"}]','[\"过度思虑缓解\", \"内在稳定\", \"规律感建立\"]','[\"当发现自己反复思考同一件事时，设定一个思虑时间：每天只允许自己思虑 15 分钟\", \"把所有担忧写下来，写完后合上本子，告诉自己：已经记录了，现在可以放下\", \"建立一个固定的日常节律（固定起床、吃饭、睡觉时间），节律感本身就能减少焦虑\", \"当思绪乱时，做一件具体的小事（洗碗、整理桌面），把注意力带回当下\", \"每天给自己一个稳定锚点：一杯固定的茶、一首固定的音乐\"]','[\"脾胃调理\", \"饭后轻走\", \"腹部温养\"]','{\"name\": \"脾胃调和导引\", \"steps\": [\"饭后 20 分钟，站立或坐下，双手搓热\", \"将双手叠放在肚脐上，顺时针缓慢按摩腹部 36 圈\", \"再逆时针按摩 36 圈，力度适中，感受腹部温热\", \"双手握拳，轻轻叩击腹部（不要用力），从上到下，重复 3 遍\", \"最后站起来，缓慢步行 10 分钟，步速比平时慢\"]}','[\"规律作息\", \"饭后步行\", \"脾胃调和练习\"]','[\"长夏（夏末秋初）湿气最重，脾最容易受湿邪困扰\", \"这个季节要少吃生冷、甜腻食物，多吃健脾祛湿的食物\", \"保持规律作息，脾喜欢节律，不规律的生活最伤脾\", \"适当运动但不要过度出汗，出汗过多会耗伤脾气\"]','[\"三餐尽量定时\", \"晚餐不过饱\", \"饭后步行 10 分钟\"]',3,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(4,'metal','金','肺','秋','#b8b8b8','金行主呼吸、收敛与清理，适合关注肺部保养、呼吸练习与秋燥调理。','[\"润肺白色食材\", \"清淡少油\", \"润燥汤羹\"]','[{\"desc\": \"银耳半朵 + 雪梨 1 个 + 冰糖适量，炖 1 小时，润肺止咳，秋季必备，每周 2-3 次。\", \"name\": \"银耳雪梨汤\"}, {\"desc\": \"百合 20g + 莲藕 100g + 大米 60g，润肺清热，适合秋燥咳嗽、皮肤干燥的人。\", \"name\": \"百合莲藕粥\"}, {\"desc\": \"白萝卜 200g + 排骨 300g，炖 1.5 小时，化痰润肺，秋冬季节增强肺部功能。\", \"name\": \"白萝卜排骨汤\"}]','[\"悲忧梳理\", \"释放执念\", \"清理杂念\"]','[\"当感到悲伤或忧郁时，允许自己感受这个情绪，不要压抑\", \"找一个安全的空间，做几次深长的叹气（叹气是肺的自然释放方式）\", \"写下你正在执着的事情，然后问：如果放下这个，我会失去什么？真的会失去吗？\", \"做一次断舍离：清理一个抽屉或一个角落，物理上的清理帮助心理清理\", \"每天做 5 分钟深呼吸练习，悲忧情绪会随着呼吸逐渐疏散\"]','[\"呼吸训练\", \"胸背打开\", \"秋季润养\"]','{\"name\": \"肺经舒展练习\", \"steps\": [\"站立，双脚与肩同宽，双臂自然下垂\", \"吸气，双臂缓慢向两侧展开至与肩同高，胸腔充分打开\", \"屏气 3 秒，感受胸腔的扩张\", \"呼气，双臂缓慢收回，同时身体微微前倾，将气完全呼出\", \"重复 8 次，每次呼气时想象肺部的浊气被清除\", \"最后用手掌轻拍胸部（从上到下），促进肺部气血流通\"]}','[\"呼吸冥想\", \"胸廓舒展\", \"断舍离练习\"]','[\"秋季燥气当令，肺最容易受燥邪侵袭，出现干咳、皮肤干燥\", \"秋天要多喝水，多吃润燥食物，少吃辛辣\", \"秋季是收敛的季节，适合整理、清理，减少新的开始\", \"秋天情绪容易低落，多晒太阳，多做深呼吸\"]','[\"每日做 3 轮深呼吸\", \"减少过甜过辣\", \"注意空气湿度与饮水\"]',4,'2026-04-16 10:00:00','2026-04-16 10:00:00',0),
(5,'water','水','肾','冬','#2c3e50','水行主藏精、恢复与意志，适合关注精力修复、睡眠恢复与长期坚持。','[\"黑色食材\", \"温补饮食\", \"恢复型餐食\"]','[{\"desc\": \"黑芝麻 30g + 核桃 20g，打成糊，加蜂蜜调味，补肾益精，适合腰酸、脱发、记忆力下降的人。\", \"name\": \"黑芝麻核桃糊\"}, {\"desc\": \"黑豆 40g + 枸杞 15g + 大米 50g，补肾养血，冬季每周 3 次，改善精力不足。\", \"name\": \"枸杞黑豆粥\"}, {\"desc\": \"羊肉 200g + 白萝卜 150g + 生姜 3 片，温肾散寒，冬季补阳，适合怕冷、手脚冰凉的人。\", \"name\": \"羊肉萝卜汤\"}]','[\"恐惧缓解\", \"安全感建立\", \"意志培养\"]','[\"当感到恐惧或不安全时，先把双脚踩实地面，感受地面的支撑\", \"做腹式呼吸：吸气时腹部鼓起，呼气时腹部收缩，重复 10 次\", \"列出你现在拥有的资源：健康、技能、关系、财务，感受自己的根基\", \"把恐惧写下来，然后问：这个恐惧的最坏结果是什么？我能承受吗？\", \"每天做一件需要意志力的小事（冷水洗脸、早起 5 分钟），积累意志感\"]','[\"腰肾温养\", \"精力恢复\", \"冬季蓄能\"]','{\"name\": \"腰肾温养功\", \"steps\": [\"站立，双脚与肩同宽，双手搓热至发烫\", \"将双手放在腰部（肾俞穴位置，腰椎两侧），感受手掌的温热传入腰部\", \"缓慢做腰部前后弯曲：向前弯腰，双手沿腿向下滑，再缓慢直起\", \"双手叉腰，缓慢转动腰部，顺时针 8 圈，逆时针 8 圈\", \"最后双手握拳，用拳背轻轻叩击腰部（肾区）各 36 次\", \"收功：双手搓热，再次捂住腰部，深呼吸 3 次\"]}','[\"早睡养精\", \"缓慢训练\", \"专注沉淀\"]','[\"冬季是藏精蓄能的季节，不适合大量消耗，要以恢复为主\", \"冬天要早睡晚起，顺应自然的收藏之气\", \"冬季饮食以温补为主，少吃生冷，多吃黑色食物补肾\", \"冬天适合做内向的事：阅读、冥想、整理思路，为来年积蓄能量\"]','[\"尽量 23 点前休息\", \"避免过度透支\", \"每周预留安静恢复时间\"]',5,'2026-04-16 10:00:00','2026-04-16 10:00:00',0);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
