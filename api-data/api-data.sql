-- ----------------------------
-- Table structure for data_dashboard
-- ----------------------------
DROP TABLE IF EXISTS `data_dashboard`;
CREATE TABLE `data_dashboard`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `ui` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'uiState',
  `time_restore` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'timeRestore',
  `thumbnail` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缩略图片',
  `top` int(11) NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_dashboard
-- ----------------------------
INSERT INTO `data_dashboard` VALUES (1, 'aaa', 'bbb', '[{\"x\":31,\"y\":10,\"w\":25,\"h\":30,\"uuid\":\"w-1599536369428\",\"type\":\"bar\",\"title\":\"dsfa\",\"source\":\"0\",\"fns\":[\"table\",\"max\",\"range\"],\"timeRestore\":\"1014\",\"metric\":[{\"type\":\"sum\",\"field\":\"all_bytes\",\"label\":\"总包大小\",\"fieldType\":\"long\"}],\"option\":{\"duration\":0,\"barType\":\"default\",\"fillStyle\":\"\",\"_colors\":false,\"itemWidth\":12,\"topValue\":false,\"_onClick\":false,\"_labelFormat\":\"\",\"valueFormat\":\"size\",\"xAxis\":{\"show\":true,\"mode\":\"common\",\"label\":\"\",\"arrow\":false,\"width\":24},\"yAxis\":{\"show\":true,\"mode\":\"common\",\"label\":\"\",\"arrow\":true,\"width\":60}},\"indexId\":\"probe-netflow-*\",\"filter\":[],\"bucket\":{\"type\":\"terms\",\"label\":\"目的IP\",\"field\":\"dst_ip\",\"size\":10,\"order\":\"all_bytes\",\"by\":\"desc\"},\"icon\":\"build\",\"titleSub\":\"fdsafdsa\",\"titleDesc\":\"fdsafdsa\",\"help\":\"fdsafddas\"}]', '1004', '/images/3.png', 1);
INSERT INTO `data_dashboard` VALUES (2, 'ft32', 'a11', '[{\"uuid\":\"144m3\",\"type\":\"container\",\"layout\":{\"uuid\":\"bes9p\",\"mode\":\"v\",\"split\":60,\"nodes\":[{\"uuid\":\"brgkm\",\"mode\":\"h\",\"split\":50,\"nodes\":[{\"mode\":\"v\",\"uuid\":\"1v9vp\",\"split\":50,\"nodes\":[{\"mode\":\"n\",\"uuid\":\"f5p28\"},{\"mode\":\"n\",\"uuid\":\"1bsd8\"}]},{\"mode\":\"n\",\"uuid\":\"6iiee\",\"widget\":{\"type\":\"bar\",\"source\":\"0\",\"timeRestore\":0,\"indexId\":\"host-virus-*\",\"showSelect\":false,\"loaded\":true,\"filter\":[],\"bucket\":{\"type\":\"terms\",\"label\":\"设备ip\",\"field\":\"device_ip\",\"size\":10},\"metric\":[],\"option\":{\"duration\":0,\"barType\":\"default\",\"fillStyle\":\"\",\"_colors\":false,\"itemWidth\":12,\"topValue\":false,\"_onClick\":false,\"_labelFormat\":\"\",\"valueFormat\":\"\",\"xAxis\":{\"show\":true,\"mode\":\"common\",\"label\":\"\",\"arrow\":false,\"width\":24},\"yAxis\":{\"show\":true,\"mode\":\"common\",\"label\":\"\",\"arrow\":true,\"width\":60}}}}]},{\"uuid\":\"3fkg6\",\"mode\":\"h\",\"split\":50,\"nodes\":[{\"uuid\":\"a1hkk\",\"mode\":\"n\"},{\"uuid\":\"27s65\",\"mode\":\"n\"}]}]},\"x\":34,\"y\":19,\"w\":30,\"h\":50}]', '1006', '/images/1.png', NULL);

-- ----------------------------
-- Table structure for data_discover_edge
-- ----------------------------
DROP TABLE IF EXISTS `data_discover_edge`;
CREATE TABLE `data_discover_edge`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'KEY',
  `name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关系名称',
  `icon` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '对象图标',
  `description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关系描述',
  `source_id` int(11) NOT NULL COMMENT '数据源',
  `search_field` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '搜索字段',
  `search_entity_id` int(11) NULL DEFAULT NULL COMMENT '搜索实体编号',
  `goal_field` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '目标字段',
  `goal_entity_id` int(11) NULL DEFAULT NULL COMMENT '目标实体编号',
  `reverse` bit(1) NULL DEFAULT NULL COMMENT '是否反向： 0：否，1：是',
  `agg` bit(1) NULL DEFAULT b'0' COMMENT '是否聚合（目标字段）： 0：否，1：是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '图形探索关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_discover_edge
-- ----------------------------
INSERT INTO `data_discover_edge` VALUES (3, '访问外网', NULL, '访问外网', 1, 'src_ip', 2, 'dst_ip', 2, NULL, b'0');
INSERT INTO `data_discover_edge` VALUES (4, '发起攻击', NULL, '发起攻击', 2, 'src_ip', 2, 'dst_ip', 2, NULL, b'1');
INSERT INTO `data_discover_edge` VALUES (5, '访问应用', NULL, '访问应用', 3, 'terminal_id', 2, 'terminal_id', 2, NULL, b'0');
INSERT INTO `data_discover_edge` VALUES (6, '复制粘贴', NULL, '复制粘贴', 4, 'host', 2, '', NULL, NULL, b'0');
INSERT INTO `data_discover_edge` VALUES (7, 'MYSQL 测试', NULL, 'MYSQL 测试', 5, 'email', 2, '', NULL, NULL, b'0');

-- ----------------------------
-- Table structure for data_discover_entity
-- ----------------------------
DROP TABLE IF EXISTS `data_discover_entity`;
CREATE TABLE `data_discover_entity`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'KEY',
  `name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '实体名称',
  `icon` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体ICON',
  `description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体描述',
  `tip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '搜索提示',
  `reg` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '输入时的正则规则',
  `built_in_type` tinyint(4) NULL DEFAULT 0 COMMENT '1：身份证，2：ip地址，3：系统编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '图形探索实体表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_discover_entity
-- ----------------------------
INSERT INTO `data_discover_entity` VALUES (1, '人员', 'play-circle', '', '请输入身份证', NULL, 1);
INSERT INTO `data_discover_entity` VALUES (2, '设备', 'symbol-device', '', '请输入 IP 地址', '(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}', 2);
INSERT INTO `data_discover_entity` VALUES (3, '应用', 'symbol-app', '', '请输入应用系统编号', 'tsd', 3);

-- ----------------------------
-- Table structure for data_discover_entity_rel
-- ----------------------------
DROP TABLE IF EXISTS `data_discover_entity_rel`;
CREATE TABLE `data_discover_entity_rel`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `entity_id` int(11) NOT NULL COMMENT '实体 ID',
  `source_id` int(11) NOT NULL COMMENT '数据源 ID',
  `field` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字段',
  `description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段描述',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `entity_id`(`entity_id`, `source_id`, `field`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '图形探索实体索引字段映射表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_discover_entity_rel
-- ----------------------------
INSERT INTO `data_discover_entity_rel` VALUES (1, 2, 1, 'src_ip', '源IP - ( src_ip )');
INSERT INTO `data_discover_entity_rel` VALUES (2, 2, 1, 'report_ip', '上报IP - ( report_ip )');
INSERT INTO `data_discover_entity_rel` VALUES (3, 2, 1, 'dst_ip', '目标IP - ( dst_ip )');
INSERT INTO `data_discover_entity_rel` VALUES (4, 2, 2, 'dst_ip', '目标IP - ( dst_ip )');
INSERT INTO `data_discover_entity_rel` VALUES (5, 2, 2, 'report_ip', '上报IP - ( report_ip )');
INSERT INTO `data_discover_entity_rel` VALUES (6, 2, 2, 'src_ip', '源IP - ( src_ip )');
INSERT INTO `data_discover_entity_rel` VALUES (7, 2, 3, 'terminal_id', '终端IP - ( terminal_id )');
INSERT INTO `data_discover_entity_rel` VALUES (8, 2, 4, 'host', '主机IP - ( host )');
INSERT INTO `data_discover_entity_rel` VALUES (9, 2, 4, 'safety_margin_ip', '安全域IP - ( safety_margin_ip )');
INSERT INTO `data_discover_entity_rel` VALUES (10, 2, 5, 'email', 'Email - ( email )');
INSERT INTO `data_discover_entity_rel` VALUES (11, 2, 5, 'idcard', '身份证号 - ( idcard )');

-- ----------------------------
-- Table structure for data_discover_record
-- ----------------------------
DROP TABLE IF EXISTS `data_discover_record`;
CREATE TABLE `data_discover_record`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `keyword` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关键词',
  `entity_id` int(11) NOT NULL COMMENT '实体ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_discover_record
-- ----------------------------
INSERT INTO `data_discover_record` VALUES (1, NULL, '1.141.117.28', 2, NULL);

-- ----------------------------
-- Table structure for data_maintain
-- ----------------------------
DROP TABLE IF EXISTS `data_maintain`;
CREATE TABLE `data_maintain`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'KEY',
  `name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '命名',
  `source_id` int(11) NOT NULL COMMENT '数据源',
  `primary_key` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主键(必须自增)',
  `name_field` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称字段',
  `columns` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '自定义列，可覆盖source_field里面的定义，过滤、顺序、类型、是否必填',
  `filter` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '自定义过滤条件',
  `chmod` int(11) NOT NULL DEFAULT 0 COMMENT '开启功能列表（30位二进制）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_maintain
-- ----------------------------
INSERT INTO `data_maintain` VALUES (1, '人员', 5, 'id', 'name', '', '', 127);

-- ----------------------------
-- Table structure for data_report
-- ----------------------------
DROP TABLE IF EXISTS `data_report`;
CREATE TABLE `data_report`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `title` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题',
  `sub_title` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '副标题',
  `theme_id` int(11) NULL DEFAULT 0 COMMENT '主题ID',
  `catalog_id` int(11) NULL DEFAULT NULL COMMENT '所属分类',
  `time_restore` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '默认时间',
  `ui` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '数据模板',
  `param` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '参数',
  `dataset` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '全局数据集',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_report
-- ----------------------------
INSERT INTO `data_report` VALUES (1, '报表基本使用：一', '副标题', 1, NULL, '1002', '\"{\\\"blocks\\\":[{\\\"key\\\":\\\"a445l\\\",\\\"text\\\":\\\"ssafd\\\",\\\"type\\\":\\\"header-two\\\",\\\"depth\\\":0,\\\"inlineStyleRanges\\\":[],\\\"entityRanges\\\":[],\\\"data\\\":{}},{\\\"key\\\":\\\"6bacq\\\",\\\"text\\\":\\\" \\\",\\\"type\\\":\\\"atomic\\\",\\\"depth\\\":0,\\\"inlineStyleRanges\\\":[],\\\"entityRanges\\\":[{\\\"offset\\\":0,\\\"length\\\":1,\\\"key\\\":0}],\\\"data\\\":{}},{\\\"key\\\":\\\"2vml4\\\",\\\"text\\\":\\\"\\\",\\\"type\\\":\\\"unstyled\\\",\\\"depth\\\":0,\\\"inlineStyleRanges\\\":[],\\\"entityRanges\\\":[],\\\"data\\\":{}}],\\\"entityMap\\\":{\\\"0\\\":{\\\"type\\\":\\\"IMAGE\\\",\\\"mutability\\\":\\\"IMMUTABLE\\\",\\\"data\\\":{\\\"url\\\":\\\"/api/fs/view/2e4525a3-665f-4ece-b376-165692f7ed5d\\\",\\\"name\\\":\\\"test2.png\\\",\\\"type\\\":\\\"IMAGE\\\",\\\"meta\\\":{\\\"id\\\":\\\"2e4525a3-665f-4ece-b376-165692f7ed5d\\\",\\\"loop\\\":false,\\\"poster\\\":\\\"/api/fs/view/2e4525a3-665f-4ece-b376-165692f7ed5d\\\",\\\"autoPlay\\\":false,\\\"controls\\\":false}}}}}\"', '[]', '[]');

-- ----------------------------
-- Table structure for data_report_catalog
-- ----------------------------
DROP TABLE IF EXISTS `data_report_catalog`;
CREATE TABLE `data_report_catalog`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'KEY',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分类名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_report_catalog
-- ----------------------------

-- ----------------------------
-- Table structure for data_report_crontab
-- ----------------------------
DROP TABLE IF EXISTS `data_report_crontab`;
CREATE TABLE `data_report_crontab`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'KEY',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_report_crontab
-- ----------------------------

-- ----------------------------
-- Table structure for data_report_theme
-- ----------------------------
DROP TABLE IF EXISTS `data_report_theme`;
CREATE TABLE `data_report_theme`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主题ID',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主题名称',
  `size` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '大小',
  `margin` tinyint(4) NULL DEFAULT NULL COMMENT '边距, mm',
  `background` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '背景图片',
  `logo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'Logo',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题样式',
  `sub_title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '副标题样式',
  `topic` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '目录样式',
  `sign` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '水印',
  `header` tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '页头',
  `footer` tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '页尾',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_report_theme
-- ----------------------------
INSERT INTO `data_report_theme` VALUES (1, '北信源主题模板', 'a4', 4, '/api-common/file/show/1be57c55-0a4c-470c-810d-2046a809e5d8', '{\"url\":\"http://www.vrv.com.cn/statics/images/logo.png\",\"marginTop\":77}', '{\"marginTop\":48,\"fontSize\":49,\"fontWeight\":\"bold\",\"textAlign\":\"center\"}', '{\"marginTop\":24,\"fontSize\":24,\"fontWeight\":\"normal\",\"fontStyle\":\"italic\"}', '{\"display\":\"block\",\"marginTop\":36,\"textAlign\":\"left\"}', '北信源 VRV', '<div class=\"text\" style=\"text-align:right;padding-right:12px;width:100%;font-weight:bold;color:#666;\"> 北信源VRV - <span class=title></span> </div>', '<div class=\"text center\"><span class=\"pageNumber\"></span> / <span class=\"totalPages\"></span></div>');
INSERT INTO `data_report_theme` VALUES (2, '数据展示主题', 'a3', 12, 'aaa', '{}', '{\"marginTop\":80,\"fontSize\":46,\"fontWeight\":\"bold\",\"textAlign\":\"center\"}', '{\"marginTop\":36,\"fontSize\":23,\"fontWeight\":\"normal\",\"fontStyle\":\"italic\"}', '{\"display\":\"none\",\"marginTop\":36,\"textAlign\":\"left\"}', '0', NULL, NULL);
INSERT INTO `data_report_theme` VALUES (3, 'VAP技术平台', 'a4', 8, NULL, '{\"url\":\"\"}', '{\"marginTop\":48,\"fontSize\":36,\"fontWeight\":\"bold\",\"textAlign\":\"center\"}', '{\"marginTop\":24,\"fontSize\":24,\"fontWeight\":\"normal\",\"fontStyle\":\"normal\"}', '{\"display\":\"block\",\"marginTop\":36,\"textAlign\":\"left\"}', 'VRV Analysis Platform', NULL, NULL);
INSERT INTO `data_report_theme` VALUES (4, '环境感知', 'a4', 8, NULL, '{}', '{\"marginTop\":48,\"fontSize\":36,\"fontWeight\":\"bold\",\"textAlign\":\"center\"}', '{\"marginTop\":24,\"fontSize\":24,\"fontWeight\":\"normal\",\"fontStyle\":\"normal\"}', '{\"display\":\"block\",\"marginTop\":36,\"textAlign\":\"left\"}', '1', '豆豆', NULL);

-- ----------------------------
-- Table structure for data_screen
-- ----------------------------
DROP TABLE IF EXISTS `data_screen`;
CREATE TABLE `data_screen`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '标准化大屏Key',
  `title` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题',
  `time_restore` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ui` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '组件配置',
  `color_scheme` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配色方案',
  `background_image` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '背景图片',
  `effect` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '页面特效配置',
  `thumbnail` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缩略图',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标准化大屏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_screen
-- ----------------------------
INSERT INTO `data_screen` VALUES (1, 'fddsfds', '1016', '[{\"type\":\"earth\",\"l\":777.056974459725,\"t\":26.11620294599018,\"w\":320,\"h\":320,\"title\":\"旋转地球-1\",\"uuid\":\"6ev2f\",\"zIndex\":903},{\"type\":\"fullscreen\",\"l\":627,\"t\":545,\"w\":180,\"h\":60,\"title\":\"全屏按钮\",\"uuid\":\"3clg3\",\"zIndex\":902},{\"type\":\"earth\",\"l\":346.6679764243614,\"t\":127.55973813420621,\"w\":320,\"h\":320,\"title\":\"旋转地球\",\"uuid\":\"s-868317404_1599633262205\",\"zIndex\":901}]', 'dark', '/images/screen-bg/03.png', '{\"saturate\":135}', NULL);

-- ----------------------------
-- Table structure for data_screen_template
-- ----------------------------
DROP TABLE IF EXISTS `data_screen_template`;
CREATE TABLE `data_screen_template`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '标准化大屏Key',
  `title` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题',
  `time_restore` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ui` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '组件配置',
  `color_scheme` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配色方案',
  `background_image` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '背景图片',
  `effect` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '页面特效配置',
  `thumbnail` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缩略图',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标准化大屏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_screen_template
-- ----------------------------
INSERT INTO `data_screen_template` VALUES (1, 'fddsfds', '1016', '[{\"type\":\"earth\",\"l\":777.056974459725,\"t\":26.11620294599018,\"w\":320,\"h\":320,\"title\":\"旋转地球-1\",\"uuid\":\"6ev2f\",\"zIndex\":903},{\"type\":\"fullscreen\",\"l\":627,\"t\":545,\"w\":180,\"h\":60,\"title\":\"全屏按钮\",\"uuid\":\"3clg3\",\"zIndex\":902},{\"type\":\"earth\",\"l\":346.6679764243614,\"t\":127.55973813420621,\"w\":320,\"h\":320,\"title\":\"旋转地球\",\"uuid\":\"s-868317404_1599633262205\",\"zIndex\":901}]', 'dark', '/images/screen-bg/03.png', '{\"saturate\":135}', NULL);

-- ----------------------------
-- Table structure for data_search_condition
-- ----------------------------
DROP TABLE IF EXISTS `data_search_condition`;
CREATE TABLE `data_search_condition`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'KEY',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '条件命名',
  `q` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '输入的关键字',
  `start_time` char(19) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '时间范围-开始时间',
  `end_time` char(19) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '时间范围-结束时间',
  `topic_id` int(11) NOT NULL COMMENT '选择的主题',
  `source_ids` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '选择的索引',
  `filter` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '过滤条件',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for data_search_topic
-- ----------------------------
DROP TABLE IF EXISTS `data_search_topic`;
CREATE TABLE `data_search_topic`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(4) NULL DEFAULT NULL COMMENT '1 索引主题 2 索引分组',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主题名称',
  `parent_id` int(11) NULL DEFAULT NULL COMMENT '父ID',
  `source_ids` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主题内的索引ID，以逗号区分',
  `filter` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '过滤条件(Query语句)',
  `sort` smallint(6) NULL DEFAULT 0 COMMENT '分组排序',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态 0 可用，1禁用',
  `role_ids` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分配角色权限，以逗号区分',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_search_topic
-- ----------------------------
INSERT INTO `data_search_topic` VALUES (1, 2, '主题A', 0, '1,2', NULL, 1, 1, NULL);
INSERT INTO `data_search_topic` VALUES (2, 1, '主题盒子', 0, NULL, NULL, 2, 1, NULL);
INSERT INTO `data_search_topic` VALUES (3, 2, '主题B', 2, '3,4', NULL, 1, 1, NULL);
INSERT INTO `data_search_topic` VALUES (4, 2, '主题C', 0, '2', '', 2, 1, NULL);
INSERT INTO `data_search_topic` VALUES (5, 2, 'MYSQL测试', 0, '5', NULL, 5, 1, NULL);

-- ----------------------------
-- Table structure for data_source
-- ----------------------------
DROP TABLE IF EXISTS `data_source`;
CREATE TABLE `data_source`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(63) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '索引/数据表/视图名',
  `title` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源标题',
  `icon` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '代表性Icon',
  `type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '1： 本地ES ,2: 本地Mysql ,3: 远程Mysql, 4远程mysql',
  `time_field` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '时间字段 ES 必选，MySql 可选',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '索引配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_source
-- ----------------------------
INSERT INTO `data_source` VALUES (1, 'test-probe-netflow-*', '流量日志', 'cloud-download', 1, 'event_time');
INSERT INTO `data_source` VALUES (2, 'test-probe-attack-*', '攻击', 'alert', 1, 'event_time');
INSERT INTO `data_source` VALUES (3, 'test-app-audit-*', '应用访问', 'audit', 1, 'indate');
INSERT INTO `data_source` VALUES (4, 'test-copy-*', '复制粘贴', 'copy', 1, 'event_time');
INSERT INTO `data_source` VALUES (5, 'demo_user', '用户', 'smile', 2, 'last_login');
INSERT INTO `data_source` VALUES (6, 'data_source', '数据源', 'crown', 2, NULL);

-- ----------------------------
-- Table structure for data_source_connection
-- ----------------------------
DROP TABLE IF EXISTS `data_source_connection`;
CREATE TABLE `data_source_connection`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `source_id` int(11) NOT NULL COMMENT '数据源',
  `host` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'host',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '索引配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_source_connection
-- ----------------------------

-- ----------------------------
-- Table structure for data_source_field
-- ----------------------------
DROP TABLE IF EXISTS `data_source_field`;
CREATE TABLE `data_source_field`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'KEY',
  `source_id` int(11) NOT NULL COMMENT '数据源ID',
  `field` varchar(24) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字段名',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字段标题',
  `type` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型，支持：keyword text long double date object json',
  `origin` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原始字段类型',
  `dict` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典',
  `link` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '链接类型 person / device / app (单机版不支持)',
  `unit` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数量格式',
  `show` bit(1) NULL DEFAULT b'1' COMMENT '是否显示',
  `sorter` bit(1) NULL DEFAULT b'0' COMMENT '是否可排序',
  `filter` bit(1) NULL DEFAULT b'1' COMMENT '是否过滤',
  `tag` bit(1) NULL DEFAULT b'0' COMMENT '是否标签',
  `sort` smallint(6) NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for data_source_monitor
-- ----------------------------
DROP TABLE IF EXISTS `data_source_monitor`;
CREATE TABLE `data_source_monitor`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `source_id` int(11) NOT NULL COMMENT '数据源',
  `health` tinyint(4) NULL DEFAULT NULL COMMENT '健康状态',
  `rows` bigint(20) NULL DEFAULT NULL COMMENT '数据条数',
  `data_size` bigint(20) NULL DEFAULT NULL COMMENT '数据占用空间',
  `index_size` bigint(20) NULL DEFAULT NULL COMMENT '索引占用空间',
  `shards` int(11) NULL DEFAULT NULL COMMENT '分片数量（ES）',
  `indices` int(11) NULL DEFAULT NULL COMMENT '索引数量（ES）',
  `time` datetime(0) NOT NULL COMMENT '检测时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '索引配置表' ROW_FORMAT = Dynamic;

