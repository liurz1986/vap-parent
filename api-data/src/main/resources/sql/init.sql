--liquibase formatted sql
--changeset wangneng:20220309-apidata-001 labels:init

CREATE TABLE IF NOT EXISTS `data_dashboard`  (
                                   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `title` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
                                   `description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
                                   `ui` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'uiState',
                                   `time_restore` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'timeRestore',
                                   `thumbnail` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缩略图片',
                                   `top` int(11) NULL DEFAULT NULL COMMENT '置顶',
                                   `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;



CREATE TABLE IF NOT EXISTS `data_discover_edge`  (
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




CREATE TABLE IF NOT EXISTS `data_discover_entity`  (
                                         `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'KEY',
                                         `name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '实体名称',
                                         `icon` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体ICON',
                                         `description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体描述',
                                         `tip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '搜索提示',
                                         `reg` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '输入时的正则规则',
                                         `built_in_type` tinyint(4) NULL DEFAULT 0 COMMENT '1：身份证，2：ip地址，3：系统编号',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '图形探索实体表' ROW_FORMAT = Dynamic;



CREATE TABLE IF NOT EXISTS `data_discover_entity_rel`  (
                                             `id` int(11) NOT NULL AUTO_INCREMENT,
                                             `entity_id` int(11) NOT NULL COMMENT '实体 ID',
                                             `source_id` int(11) NOT NULL COMMENT '数据源 ID',
                                             `field` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字段',
                                             `description` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段描述',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             UNIQUE INDEX `entity_id`(`entity_id`, `source_id`, `field`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '图形探索实体索引字段映射表' ROW_FORMAT = Dynamic;



CREATE TABLE IF NOT EXISTS `data_discover_record`  (
                                         `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                         `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
                                         `keyword` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关键词',
                                         `entity_id` int(11) NOT NULL COMMENT '实体ID',
                                         `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                         `start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
                                         `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;



CREATE TABLE IF NOT EXISTS `data_maintain`  (
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



CREATE TABLE IF NOT EXISTS `data_report`  (
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



CREATE TABLE IF NOT EXISTS `data_report_catalog`  (
                                        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'KEY',
                                        `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分类名称',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;



CREATE TABLE IF NOT EXISTS `data_report_crontab`  (
                                        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'KEY',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;



CREATE TABLE IF NOT EXISTS `data_report_theme`  (
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



CREATE TABLE IF NOT EXISTS `data_screen`  (
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



CREATE TABLE IF NOT EXISTS `data_screen_template`  (
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


CREATE TABLE IF NOT EXISTS `data_search_condition`  (
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


CREATE TABLE IF NOT EXISTS `data_search_topic`  (
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

CREATE TABLE IF NOT EXISTS `data_source`  (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `name` varchar(63) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '索引/数据表/视图名',
                                `title` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源标题',
                                `icon` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '代表性Icon',
                                `type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '1： 本地ES ,2: 本地Mysql ,3: 远程Mysql, 4远程mysql',
                                `time_field` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '时间字段 ES 必选，MySql 可选',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '索引配置表' ROW_FORMAT = Dynamic;


CREATE TABLE IF NOT EXISTS `data_source_connection`  (
                                           `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                           `source_id` int(11) NOT NULL COMMENT '数据源',
                                           `host` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'host',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '索引配置表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `data_source_field`  (
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

CREATE TABLE IF NOT EXISTS `data_source_monitor`  (
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

--changeset wangneng:20220310-apidata-001 labels:update
ALTER TABLE `data_source` ADD COLUMN `description`  varchar(512);

--changeset lilang:20220519-apidata-001 labels:update
ALTER TABLE `data_source` ADD COLUMN `time_format`  varchar(128) NULL COMMENT '时间字段格式';
ALTER TABLE `data_source` ADD COLUMN `data_type`  tinyint(4) NULL COMMENT '1:原始日志，2:基线数据';
ALTER TABLE `data_source` ADD COLUMN `topic_name`  varchar(128) NULL COMMENT '对应kakfa主题';
ALTER TABLE `data_source_field` ADD COLUMN `extend_conf`  varchar(512) NULL COMMENT '拓展字段json';
ALTER TABLE `data_source_field` ADD COLUMN `alias`  varchar(128) NULL COMMENT '字段别名';
ALTER TABLE `data_source_field` ADD COLUMN `analysis_sort`  smallint(6) NULL COMMENT '分析事件排序';
ALTER TABLE `data_source_field` ADD COLUMN `analysis_type`  varchar(64) NULL COMMENT '分析事件字段类型';
ALTER TABLE `data_source_field` ADD COLUMN `analysis_type_length`  int(10) NULL COMMENT '分析事件字段类型长度';

--changeset lilang:20220524-apidata-001 labels:update
ALTER TABLE `data_source` ADD COLUMN `domain_field`  varchar(100) NULL COMMENT '安全域字段';
ALTER TABLE `data_source` ADD COLUMN `topic_alias`  varchar(128) NULL COMMENT '主题别名';

--changeset lilang:20220613-apidata-001 labels:update
ALTER TABLE `data_source_field`
MODIFY COLUMN `field`  varchar(100) NOT NULL COMMENT '字段名';
ALTER TABLE `data_source_field`
MODIFY COLUMN `type`  varchar(50) NOT NULL COMMENT '类型，支持：keyword text long double date object json';

--changeset lilang:20220623-apidata-001 labels:update
ALTER TABLE `data_source_monitor`
CHANGE COLUMN `rows` `data_count`  bigint(20) NULL DEFAULT '0' COMMENT '数据条数';

--changeset lilang:20221013-apidata-001 labels:update
ALTER TABLE `data_source_field` ADD COLUMN `analysis_show`  bit(1) NULL COMMENT '分析字段是否展示，0：不显示，1：显示';

--changeset lilang:20230322-apiData-001
ALTER TABLE `data_source`
ADD COLUMN `change_inform`  tinyint(4) NULL DEFAULT 1 COMMENT '变更通知来源，0:关闭，1:默认开启';

--changeset xuda:20230522-apiData-001
ALTER TABLE `data_source_field` ADD COLUMN `auth_mark`  int NULL DEFAULT 0 COMMENT '权限标记';

