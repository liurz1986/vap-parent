--liquibase formatted sql
--changeset lizj:20210824-apiAuditXc-001 labels:init
CREATE TABLE IF NOT EXISTS `conf_lookup` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(64) DEFAULT NULL COMMENT '字段对应类型\r\n\r\nsystem:系统\r\nwebsite:站点\r\ndevice:设备\r\nprealarm:预警\r\nalarm:报警',
  `code` varchar(64) NOT NULL COMMENT '字段对应编码',
  `value` varchar(512) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '1' COMMENT '状态\r\n1:启用(默认)\r\n0:禁用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`,`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `schedule_tasks_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_name` varchar(50) DEFAULT NULL COMMENT '名称',
  `cron_time` varchar(50) NOT NULL COMMENT '周期cron表达式',
  `classpath` varchar(100) NOT NULL COMMENT '执行任务类',
  `should_run` varchar(10) DEFAULT '1' COMMENT '是否开启:0关闭1开启',
  `description` varchar(100) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `data_dump_strategy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `data_type` int(11) DEFAULT NULL COMMENT '数据类型，1-es 2-mysql 3-hive 4-hbase',
  `data_id` varchar(512) DEFAULT NULL COMMENT '数据标识',
  `data_desc` varchar(512) DEFAULT NULL COMMENT '数据描述',
  `save_time` int(11) DEFAULT NULL COMMENT '数据保存时间，单位：天',
  `type` int(11) DEFAULT NULL COMMENT '清理模式，1-只清理不备份，2-先备份后清理',
  `dump_mode` int(11) DEFAULT NULL COMMENT '转储方式，1-手动下载，2-sftp，3-共享目录',
  `state` int(11) DEFAULT NULL COMMENT '状态，1-启用，0-关闭',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据备份策略';

CREATE TABLE IF NOT EXISTS `data_dump_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `strategy_id` int(11) DEFAULT NULL COMMENT '策略编号',
  `data_type` int(11) DEFAULT NULL COMMENT '数据类型，1-es 2-mysql 3-hive 4-hbase',
  `data_id` varchar(512) DEFAULT NULL COMMENT '数据标识',
  `data_desc` varchar(512) DEFAULT NULL COMMENT '数据描述',
  `data_detail` varchar(2048) DEFAULT NULL COMMENT '具体数据',
  `dump_time` datetime DEFAULT NULL COMMENT '备份时间',
  `dump_file_path` varchar(256) DEFAULT NULL COMMENT '备份文件路径',
  `dump_file_state` int(11) DEFAULT NULL COMMENT '备份文件状态，1-存在，0-已删除',
  `dump_file_md5` varchar(64) DEFAULT NULL COMMENT '备份文件MD5值',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据备份记录';

CREATE TABLE IF NOT EXISTS `data_clean_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `data_type` int(11) DEFAULT NULL COMMENT '数据类型，1-es 2-mysql 3-hive 4-hbase',
  `data_id` varchar(512) DEFAULT NULL COMMENT '数据标识',
  `data_desc` varchar(512) DEFAULT NULL COMMENT '数据描述',
  `data_detail` varchar(2048) DEFAULT NULL COMMENT '具体数据',
  `clean_time` datetime DEFAULT NULL COMMENT '清理时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据清理记录';

CREATE TABLE IF NOT EXISTS `data_moniotr_error_log` (
  `id` varchar(64) NOT NULL,
  `time_range` varchar(128) DEFAULT NULL COMMENT '时间范围',
  `company` varchar(256) DEFAULT NULL COMMENT '厂商',
  `type` varchar(256) DEFAULT NULL COMMENT '数据类型',
  `avg_count` bigint(20) DEFAULT NULL COMMENT '时间段内平均数据量',
  `detail` varchar(2048) DEFAULT NULL COMMENT '数据量详情',
  `log_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '记录时间',
  `up_threshold` varchar(32) DEFAULT NULL COMMENT '上限占比阈值',
  `down_threshold` varchar(32) DEFAULT NULL COMMENT '下限占比阈值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据监控异常记录';

CREATE TABLE IF NOT EXISTS `conf_excel_enum` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `excel_code` varchar(64) NOT NULL COMMENT '模板编码',
  `file_name` varchar(64) NOT NULL COMMENT '文件名称',
  `column_info` text DEFAULT NULL COMMENT '字段信息 [{columnEn:英文字段名,columnCn:中文字段名,size:长度限制,isNull:能否为空:0是1否}]',
  `excel_type` int(10) DEFAULT 0 COMMENT '0导出 1导入',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `description` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `object_analyse_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_account` varchar(32) DEFAULT NULL COMMENT '用户登录账号',
  `type` varchar(32) DEFAULT NULL COMMENT '1用户行为分析,2运维行为分析,3应用行为分析,4单位互联分析,5涉密信息情况',
  `value` varchar(500) DEFAULT NULL,
  `description` varchar(128) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `kafka_operate_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cmd` varchar(255) DEFAULT NULL COMMENT '命令',
  `message` varchar(2550) DEFAULT NULL COMMENT '执行信息',
  `cmd_type` int(11) DEFAULT NULL COMMENT '命令类型',
  `cmd_type_name` varchar(255) DEFAULT NULL COMMENT '命令类型名称',
  `kafka_topic` varchar(255) DEFAULT NULL COMMENT '主题',
  `operate_time` datetime DEFAULT current_timestamp() COMMENT '执行时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `kafka_producer` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `system` varchar(255) NOT NULL COMMENT '生产者系统名称',
  `ip` varchar(255) NOT NULL COMMENT '生产者IP',
  `topic` varchar(255) NOT NULL COMMENT '主题',
  `avaiable` int(11) NOT NULL COMMENT '开启',
  `add_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '修改时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `kafka_subscriber` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `system` varchar(255) NOT NULL COMMENT '订阅者名称',
  `ip` varchar(255) NOT NULL COMMENT '订阅者IP',
  `topic` varchar(255) NOT NULL COMMENT '订阅主题',
  `avaiable` int(11) NOT NULL COMMENT '开启',
  `group_id` varchar(255) NOT NULL COMMENT '消费组',
  `add_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '修改时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `kafka_topic` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `topic` varchar(255) NOT NULL COMMENT '主题',
  `name` varchar(255) NOT NULL COMMENT '数据名称',
  `avaiable` int(11) NOT NULL COMMENT '开启',
  `add_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '修改时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `topic_i` (`topic`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `white_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `devid` varchar(80) DEFAULT NULL,
  `company` varchar(60) DEFAULT NULL,
  `number` varchar(32) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL,
  `status` varchar(10) NOT NULL DEFAULT '0',
  `insert_time` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='XC-数据源管理';

CREATE TABLE IF NOT EXISTS `conf_data_monitor_index_simple` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(32) NOT NULL COMMENT '索引类型编号：1=流量类；2=告警类；3=审计类',
  `type_name` varchar(32) DEFAULT NULL COMMENT '索引类型编号对应名称：流量类、告警类、审计类',
  `index_prefix` varchar(32) NOT NULL COMMENT '索引(前缀)',
  `date_suffix` varchar(32) NOT NULL COMMENT '索引日期格式(后缀)',
  `index_format` varchar(32) DEFAULT NULL COMMENT '索引格式(如${index}-${time})',
  `index_name` varchar(32) NOT NULL COMMENT '索引名',
  `time_column` varchar(32) NOT NULL COMMENT '时间字段',
  `company_column` varchar(32) DEFAULT NULL COMMENT '厂商字段',
  `number_column` varchar(32) DEFAULT NULL COMMENT '产品字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='XC-数据监控索引信息配置表';

--changeset jiangcz:20210825-apiAudit labels:add tables
CREATE TABLE IF NOT EXISTS `rpt_user_secret_info_day`
(
    `id`               varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
    `user_no`          varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '员工编号',
    `user_name`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '员工姓名',
    `device_ip`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备ip',
    `depart_no`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门编号',
    `depart_name`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门名称',
    `secret_file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '涉密文件名称',
    `business`         varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '业务类型',
    `count`            bigint NULL DEFAULT NULL COMMENT '处理次数',
    `data_time`        varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据日期',
    `time`             datetime NULL DEFAULT NULL COMMENT '入库时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '每日人员处理涉密文件情况统计表' ROW_FORMAT = Dynamic;


CREATE TABLE IF NOT EXISTS `rpt_user_secret_info_total`
(
    `user_no`          varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '员工编号',
    `user_name`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '员工姓名',
    `device_ip`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备ip',
    `depart_no`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门编号',
    `depart_name`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门名称',
    `secret_file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '涉密文件名称',
    `business`         varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '业务类型',
    `count`            bigint NULL DEFAULT NULL COMMENT '处理次数',
    `time`             datetime NULL DEFAULT NULL COMMENT '统计日期',
    `version`          varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据版本',
    PRIMARY KEY (`device_ip`, `secret_file_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '全量人员处理涉密文件情况统计表' ROW_FORMAT = Dynamic;


CREATE TABLE IF NOT EXISTS `rpt_user_visit_app_day`
(
    `id`          varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
    `user_no`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '员工编号',
    `user_name`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '员工姓名',
    `device_ip`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备ip',
    `depart_no`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门编号',
    `depart_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门名称',
    `app_no`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用编号',
    `app_name`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用名',
    `count`       bigint NULL DEFAULT NULL COMMENT '访问次数',
    `data_time`   varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据日期',
    `time`        datetime NULL DEFAULT NULL COMMENT '入库时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '每日人员访问应用情况统计表' ROW_FORMAT = Dynamic;


CREATE TABLE IF NOT EXISTS `rpt_user_visit_app_total`
(
    `user_no`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '员工编号',
    `user_name`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '员工姓名',
    `device_ip`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备ip',
    `depart_no`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门编号',
    `depart_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门名称',
    `app_no`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用编号',
    `app_name`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用名',
    `count`       bigint NULL DEFAULT NULL COMMENT '访问次数',
    `time`        datetime NULL DEFAULT NULL COMMENT '统计日期',
    `version`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据版本',
    PRIMARY KEY (`device_ip`, `app_no`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '全量人员访问应用情况统计表' ROW_FORMAT = Dynamic;


CREATE TABLE IF NOT EXISTS `rpt_app_business_his`
(
    `id`            varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '主键',
    `ip`            varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '终端ip',
    `file_name`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '业务文件名',
    `file_level`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件密级',
    `file_business` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件业务',
    `count`         int NULL DEFAULT NULL COMMENT '文件审计次数',
    `data_time`     varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据日期',
    `time`          datetime NULL DEFAULT NULL COMMENT '入库时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '主机业务数据历史表' ROW_FORMAT = Dynamic;


CREATE TABLE IF NOT EXISTS `rpt_app_business_total`
(
    `ip`            varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '终端ip',
    `file_name`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '业务文件名',
    `file_level`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件密级',
    `file_business` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件业务',
    `count`         int NULL DEFAULT NULL COMMENT '文件审计次数',
    `time`          datetime NULL DEFAULT NULL COMMENT '入库时间',
    PRIMARY KEY (`ip`, `file_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '主机业务数据汇总表' ROW_FORMAT = Dynamic;


CREATE TABLE IF NOT EXISTS `rpt_depart_secret_info_total`
(
    `depart_no`         varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '部门编号',
    `depart_name`       varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门名称',
    `secret_file_count` bigint NULL DEFAULT NULL COMMENT '处理涉密文件个数',
    `secret_file_num`   bigint NULL DEFAULT NULL COMMENT '处理涉密文件次数',
    `business_count`    int NULL DEFAULT NULL COMMENT '涉及业务类别个数',
    `time`              datetime NULL DEFAULT NULL COMMENT '最后一次更新时间',
    `version`           varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据版本',
    PRIMARY KEY (`depart_no`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '全量部门处理涉密文件情况统计表' ROW_FORMAT = Dynamic;


CREATE TABLE IF NOT EXISTS `rpt_depart_visit_app_total`
(
    `depart_no`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '部门编号',
    `depart_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门名称',
    `app_count`   bigint NULL DEFAULT NULL COMMENT '应用个数',
    `visit_num`   bigint NULL DEFAULT NULL COMMENT '总访问次数',
    `time`        datetime NULL DEFAULT NULL COMMENT '统计日期',
    `version`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据版本',
    PRIMARY KEY (`depart_no`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '全量部门访问应用情况统计表' ROW_FORMAT = Dynamic;


CREATE TABLE IF NOT EXISTS `rpt_dev_copy_info`
(
    `id`        int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_key`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户key',
    `dst_ip`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '目标主机ip',
    `data_time` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '统计时间',
    `count`     int NULL DEFAULT NULL COMMENT '拷贝数量',
    `time`      datetime NULL DEFAULT NULL COMMENT '入库时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备拷贝数据量表' ROW_FORMAT = Dynamic;


CREATE TABLE IF NOT EXISTS `rpt_url_param_length`
(
    `data_time` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '统计时间',
    `length`    double(11, 2) NULL DEFAULT NULL COMMENT '参数平均长度',
    `time` datetime NULL DEFAULT NULL COMMENT '入库时间',
    PRIMARY KEY (`data_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'url请求参数平均长度表' ROW_FORMAT = Dynamic;

--changeset cz:20210928-apiAuditXc-001 labels:alter table
ALTER TABLE `data_dump_log`
ADD COLUMN `snapshot_name`  varchar(100) NULL COMMENT '快照标识' AFTER `data_detail`;

-- changeset jiangcz:20211227-apiAudit labels:add tables
CREATE TABLE IF NOT EXISTS `rpt_user_login` (
    `key_id` varchar(64) NOT NULL COMMENT '主键',
    `dev_ip` varchar(64) DEFAULT NULL COMMENT '终端ip',
    `hour` int(2) DEFAULT NULL COMMENT '时间段，小时数',
    `count` int(11) DEFAULT NULL COMMENT '登录次数',
    `last_update_time` datetime DEFAULT NULL COMMENT '最后更新时间',
    PRIMARY KEY (`key_id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8 ROW_FORMAT = DYNAMIC COMMENT = '终端人员登陆表';
CREATE TABLE IF NOT EXISTS `rpt_user_login_his` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `key_id` varchar(64) NOT NULL COMMENT '',
    `dev_ip` varchar(64) DEFAULT NULL COMMENT '终端ip',
    `hour` int(2) DEFAULT NULL COMMENT '时间段，小时数',
    `count` int(11) DEFAULT NULL COMMENT '登录次数',
    `data_time` varchar(32) DEFAULT NULL COMMENT '数据日期',
    `insert_time` datetime DEFAULT NULL COMMENT '入库时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8 ROW_FORMAT = DYNAMIC COMMENT = '终端人员登陆历史表';

--changeset jiangcz:20220125-apiAudit labels:modify tables
ALTER TABLE `rpt_user_login`
CHANGE COLUMN `hour` `login_hour` int NULL DEFAULT NULL COMMENT '时间段，小时数' AFTER `dev_ip`,
CHANGE COLUMN `count` `login_count` int NULL DEFAULT NULL COMMENT '登录次数' AFTER `login_hour`;
ALTER TABLE `rpt_user_login_his`
CHANGE COLUMN `hour` `login_hour` int NULL DEFAULT NULL COMMENT '时间段，小时数' AFTER `dev_ip`,
CHANGE COLUMN `count` `login_count` int NULL DEFAULT NULL COMMENT '登录次数' AFTER `login_hour`;

--changeset jiangcz:20220408-apiAudit labels:add tables
CREATE TABLE IF NOT EXISTS `base_line` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '名称',
    `config` text COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '算法配置\r\n[{\r\n "indexId": "源索引id",\r\n "column": "name",\r\n "filter": "name",\r\n "type": "1",\r\n  "value": "",\r\n  "calculation": [{\r\n   "column": "ip",\r\n   "algorithm": "1",\r\n   "agg": "true",\r\n    "aggLevel": "true"\r\n  }]\r\n}]',
    `source_type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '数据源类型1:es 2:mysql',
    `type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '基线类型',
    `open_group` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '是否计算群体基线',
    `days` int(11) DEFAULT NULL COMMENT '计算天数',
    `alias` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '标识字段别名',
    `label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '标识字段含义',
    `multiple` int(11) DEFAULT NULL COMMENT '正负范围倍数',
    `save_index` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '入库索引',
    `save_columns` varchar(5000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '入库字段配置\r\n[{\r\n    "src":"dev_ip",\r\n    "dest":"ip",\r\n    "type":"keyword",\r\n    "format":""\r\n}]',
    `save_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '入库类型（1：es；2：kafka；3：es+kafka）',
    `time_slot` char(1) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '时段（1小时 2天）',
    `cron` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '执行cron表达式',
    `status` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态 0初始化 1启用 2停用',
    `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '描述',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COLLATE = utf8_unicode_ci COMMENT = '动态基线表';
CREATE TABLE IF NOT EXISTS `base_line_source` (
    `id` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '主键',
    `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '标题',
    `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '索引',
    `time_field` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '时间字段',
    `time_format` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '时间字段格式',
    `description` varchar(512) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '描述',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COLLATE = utf8_unicode_ci COMMENT = '基线源索引表';
CREATE TABLE IF NOT EXISTS `base_line_source_field` (
    `id` varchar(64) NOT NULL COMMENT '主键',
    `source_id` varchar(64) DEFAULT NULL COMMENT '数据源id',
    `field` varchar(255) DEFAULT NULL COMMENT '字段名',
    `name` varchar(255) DEFAULT NULL COMMENT '字段标题',
    `type` varchar(8) DEFAULT NULL COMMENT '类型，支持：keyword text long double date object json',
    `format` varchar(255) DEFAULT NULL COMMENT '格式',
    `alias` varchar(255) DEFAULT NULL COMMENT '字段别名',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COMMENT = '基线数据源字段表';
ALTER TABLE `base_line_source`
ADD COLUMN `type` char(1) NULL COMMENT '类型 1：es  2：mysql' AFTER `time_format`;

--changeset jiangcz:20220616-apiAudit labels:add tables
CREATE TABLE IF NOT EXISTS `base_line_result` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `result` varchar(255) DEFAULT NULL,
    `base_line_id` int(11) DEFAULT NULL,
    `time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='基线运行结果表';
ALTER TABLE `base_line` ADD COLUMN `special_id` int NULL COMMENT '特殊模型标识' AFTER `status`;
CREATE TABLE IF NOT EXISTS `base_line_special` (
     `id` int(11) NOT NULL,
     `name` varchar(255) DEFAULT NULL COMMENT '名称',
     `frequent_class` varchar(255) DEFAULT NULL COMMENT '计算频繁项类',
     `score_class` varchar(255) DEFAULT NULL COMMENT '计算得分类',
     `frequent_cron` varchar(255) DEFAULT NULL COMMENT '频繁项cron',
     `score_cron` varchar(255) DEFAULT NULL COMMENT '得分cron',
     `time` datetime DEFAULT NULL COMMENT '入库时间',
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='基线特殊模型表';

ALTER TABLE `base_line` ADD COLUMN `fields` varchar(10000) NULL COMMENT '字段信息' AFTER `save_type`;
ALTER TABLE `base_line` ADD COLUMN `save_days` int NULL COMMENT '数据保存天数' AFTER `save_type`;

--changeset jiangcz:20220804-apiAudit labels:update tables
ALTER TABLE `base_line` auto_increment=100000;

--changeset jiangcz:20220914-apiAudit labels:update tables
ALTER TABLE `base_line_special`
DROP COLUMN `score_cron`,
CHANGE COLUMN `frequent_class` `main_class` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主类' AFTER `name`,
CHANGE COLUMN `score_class` `main_cron` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主类执行cron表达式' AFTER `main_class`,
CHANGE COLUMN `frequent_cron` `type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型' AFTER `main_cron`,
ADD COLUMN `actual_class` varchar(255) NULL COMMENT '实时计算类' AFTER `main_class`,
ADD COLUMN `monitor_id` varchar(255) NULL COMMENT '监控任务id' AFTER `main_cron`,
ADD COLUMN `config` varchar(2000) NULL COMMENT '配置' AFTER `monitor_id`,
ADD COLUMN `jar_name` varchar(255) NULL COMMENT 'flink代码jar包名称' AFTER `config`,
ADD COLUMN `actual_cron` varchar(255) NULL COMMENT '实时监控执行cron' AFTER `main_cron`;

--changeset jiangcz:2210-apiAudit-20221011 labels:add tables
CREATE TABLE IF NOT EXISTS `base_line_page` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_ip` varchar(64) DEFAULT NULL,
    `sys_id` varchar(64) DEFAULT NULL,
    `frequency` float(10,4) DEFAULT NULL COMMENT '频率',
    `inefficiency` float(6,4) DEFAULT NULL COMMENT '无效率',
    `purity` float(6,4) DEFAULT NULL COMMENT '纯度',
    `size` int(11) DEFAULT NULL COMMENT '访问总量',
    `time_total` int(11) DEFAULT NULL COMMENT '总时间（秒）',
    `invalid_num` int(11) DEFAULT NULL COMMENT '无效总数',
    `resource_num` int(11) DEFAULT NULL COMMENT '资源总数',
    `type` char(1) DEFAULT NULL COMMENT '1：主机 2：主机+应用',
    `data_time` date DEFAULT NULL COMMENT '数据时间',
    `insert_time` datetime DEFAULT NULL COMMENT '入库时间',
    `guid` varchar(64) DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1976879 DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS `base_line_page_result` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_ip` varchar(64) DEFAULT NULL,
    `sys_id` varchar(64) DEFAULT NULL,
    `size` int(11) DEFAULT NULL,
    `frequency_avg` float(12,4) DEFAULT NULL,
    `frequency_dev` float(12,4) DEFAULT NULL,
    `inefficiency_avg` float(12,4) DEFAULT NULL,
    `inefficiency_dev` float(12,4) DEFAULT NULL,
    `purity_avg` float(12,4) DEFAULT NULL,
    `purity_dev` float(12,4) DEFAULT NULL,
    `type` char(1) DEFAULT NULL,
    `insert_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3189758 DEFAULT CHARSET=utf8;

--changeset jiangcz:2210-apiAudit-20221024 labels:update tables
ALTER TABLE `base_line` ADD COLUMN `special_param` varchar(255) NULL COMMENT '特殊模型参数' AFTER `special_id`;
ALTER TABLE `base_line_special` MODIFY COLUMN `id` int(11) NOT NULL AUTO_INCREMENT FIRST;

--changeset jiangcz:2210-apiAudit-20221026 labels:add tables
CREATE TABLE IF NOT EXISTS `base_line_frequent` (
    `id` int NOT NULL AUTO_INCREMENT,
    `user_id` varchar(64) DEFAULT NULL COMMENT '用户编号',
    `frequents` text COMMENT '频繁项集',
    `count` int DEFAULT NULL COMMENT '次数',
    `is_continue` char(1) DEFAULT NULL COMMENT '是否连续',
    `type` char(1) DEFAULT NULL COMMENT '类型',
    `org` varchar(255) DEFAULT NULL COMMENT '组织机构',
    `role` varchar(255) DEFAULT NULL COMMENT '角色',
    `sys_id` varchar(255) DEFAULT NULL COMMENT '系统id',
    `time` datetime DEFAULT NULL COMMENT '入库时间',
   PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb3 COMMENT = '用户访问频繁序列表';
CREATE TABLE IF NOT EXISTS `base_line_frequent_attr` (
    `id` int NOT NULL AUTO_INCREMENT,
    `item` varchar(64) DEFAULT NULL COMMENT '单项值',
    `hour` int DEFAULT NULL COMMENT '时间',
    `pck` float(16,2) DEFAULT NULL COMMENT '包大小',
    `start_time` varchar(255) DEFAULT NULL COMMENT '开始时间',
    `end_time` varchar(255) DEFAULT NULL COMMENT '结束时间',
    `ukey` varchar(255) DEFAULT NULL COMMENT '用户标识',
    `sys_id` varchar(64) DEFAULT NULL COMMENT '系统id',
    `insert_time` datetime DEFAULT NULL COMMENT '入库时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='频繁序列属性表';
CREATE TABLE IF NOT EXISTS `base_line_frequent_org` (
    `org` varchar(255) NOT NULL COMMENT '机构',
    `frequents` text COMMENT '频繁序列',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
   PRIMARY KEY (`org`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb3 COMMENT = '频繁序列机构表';
CREATE TABLE IF NOT EXISTS `base_line_frequent_role` (
    `role` varchar(255) NOT NULL COMMENT '角色',
    `frequents` text COMMENT '频繁序列',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
   PRIMARY KEY (`role`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb3 COMMENT = '频繁序列角色表';
CREATE TABLE IF NOT EXISTS `base_line_score` (
    `id` int NOT NULL AUTO_INCREMENT,
    `frequent_id` int DEFAULT NULL COMMENT '频繁序列id',
    `similarity_score` float(6, 4) DEFAULT NULL COMMENT '个体相似度',
    `similarity_score_org` float(6, 4) DEFAULT NULL COMMENT '组织机构相似度',
    `similarity_score_role` float(6, 4) DEFAULT NULL COMMENT '角色相似度',
    `hour_score` float(6, 4) DEFAULT NULL COMMENT '时间维度得分',
    `packge_score` float(6, 4) DEFAULT NULL COMMENT '包段维度得分',
    `compress` varchar(20000) DEFAULT NULL COMMENT '序列压缩值',
    `user_key` varchar(255) DEFAULT NULL COMMENT '用户标识',
    `type` varchar(255) DEFAULT NULL COMMENT '类型',
    `start_time` varchar(255) DEFAULT NULL COMMENT '序列开始时间',
    `end_time` varchar(255) DEFAULT NULL COMMENT '序列结束时间',
    `insert_time` datetime DEFAULT NULL COMMENT '入库时间',
    `sys_id` varchar(64) DEFAULT NULL COMMENT '系统id',
    `guid` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb3 COMMENT = '用户访问序列得分表';
CREATE TABLE IF NOT EXISTS `base_line_filter` (
    `id` int NOT NULL AUTO_INCREMENT,
    `url` varchar(20000) DEFAULT NULL,
    `count` int DEFAULT NULL,
    `type` varchar(255) DEFAULT NULL,
    `time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='用户访问序列黑白名单表';

--changeset jiangcz:2210-apiAudit-20221102 labels:update tables
ALTER TABLE `base_line_special` ADD COLUMN `params` varchar(5000) NULL COMMENT '模型参数' AFTER `type`;
ALTER TABLE `base_line_result` ADD COLUMN `message` varchar(20000) NULL AFTER `time`;
ALTER TABLE `base_line_score` ADD COLUMN `result_score` float(6, 4) NULL COMMENT '最终得分' AFTER `packge_score`,ADD COLUMN `threshold` float(6, 4) NULL COMMENT '阈值' AFTER `result_score`;
ALTER TABLE `base_line_page_result`
ADD COLUMN `confidence` float(12, 4) NULL COMMENT '置信度' AFTER `type`,
ADD COLUMN `frequency_max` float(12, 4) NULL AFTER `confidence`,
ADD COLUMN `frequency_min` float(12, 4) NULL AFTER `frequency_max`,
ADD COLUMN `inefficiency_max` float(12, 4) NULL AFTER `frequency_min`,
ADD COLUMN `inefficiency_min` float(12, 4) NULL AFTER `inefficiency_max`,
ADD COLUMN `purity_max` float(12, 4) NULL AFTER `inefficiency_min`,
ADD COLUMN `purity_min` float(12, 4) NULL AFTER `purity_max`;
ALTER TABLE `base_line` ADD COLUMN `monitor_id` varchar(255) NULL COMMENT '特殊模型实时任务id' AFTER `special_id`;

--changeset jiangcz:2211-apiAudit-20221121 labels:update tables
ALTER TABLE `base_line_special` ADD COLUMN `frame` varchar(2) NULL COMMENT '技术框架：1.java 2.flink 3.spark' AFTER `params`;

--changeset jiangcz:2211-apiAudit-20221130 labels:add tables
CREATE TABLE `base_line_sequence` (
     `id` int(11) NOT NULL AUTO_INCREMENT,
     `user_ip` varchar(64) DEFAULT NULL,
     `sys_id` varchar(64) DEFAULT NULL,
     `size` int(11) DEFAULT NULL COMMENT '访问总量',
     `distinct_size` int(11) DEFAULT NULL,
     `time_total` int(11) DEFAULT NULL COMMENT '总时间（秒）',
     `invalid_num` int(11) DEFAULT NULL COMMENT '无效总数',
     `resource_num` int(11) DEFAULT NULL COMMENT '资源总数',
     `data_time` date DEFAULT NULL COMMENT '数据时间',
     `compress` varchar(20000) DEFAULT NULL,
     `start_time` varchar(255) DEFAULT NULL,
     `end_time` varchar(255) DEFAULT NULL,
     `org` varchar(255) DEFAULT NULL,
     `role` varchar(255) DEFAULT NULL,
     `hour` int(11) DEFAULT NULL COMMENT '小时',
     `pck` float(12,2) DEFAULT NULL COMMENT '包大小',
     `insert_time` datetime DEFAULT NULL COMMENT '入库时间',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--changeset jiangcz:2211-apiAudit-20221229 labels:update tables
ALTER TABLE `base_line_frequent_org` ADD COLUMN `sys_id` varchar(255) NOT NULL COMMENT '系统id' AFTER `org`, DROP PRIMARY KEY, ADD PRIMARY KEY (`org`, `sys_id`) USING BTREE;
ALTER TABLE `base_line_frequent_role` ADD COLUMN `sys_id` varchar(255) NOT NULL COMMENT '系统id' AFTER `role`,DROP PRIMARY KEY,ADD PRIMARY KEY (`role`, `sys_id`) USING BTREE;

--changeset jiangcz:2305-apiAudit-20230508 labels:update tables
ALTER TABLE `base_line_frequent_attr` ADD INDEX `sys_index`(`sys_id`) USING BTREE COMMENT '系统id索引',ADD INDEX `user_index`(`ukey`) USING BTREE COMMENT '用户索引',ADD INDEX `item_index`(`item`) USING BTREE COMMENT '操作值索引';
ALTER TABLE `base_line_score` ADD UNIQUE INDEX `guid_index`(`guid`) USING BTREE COMMENT '唯一标识索引';
ALTER TABLE `base_line_page` ADD INDEX `user_sys_datatime_index`(`user_ip`, `sys_id`, `data_time`) USING BTREE COMMENT '用户系统时间联合索引',ADD INDEX `datatime_index`(`data_time`) USING BTREE COMMENT '时间索引';
ALTER TABLE `base_line_page_result` ADD INDEX `time_index`(`insert_time`) USING BTREE COMMENT '时间索引';
ALTER TABLE `base_line_frequent_attr` ADD COLUMN `type` char(1) NULL DEFAULT 0 COMMENT '类型' AFTER `sys_id`;
ALTER TABLE `base_line_frequent_attr` ADD COLUMN `times` varchar(20000) NULL AFTER `type`;
ALTER TABLE `base_line` ADD COLUMN `summary_num` int(11) NULL COMMENT '统计次数' AFTER `status`;
ALTER TABLE `base_line` ADD COLUMN `work_status` char(1) NULL DEFAULT 2 COMMENT '运行状态（1：进行中 2：已停止）' AFTER `summary_num`;
ALTER TABLE `base_line` ADD COLUMN `work_msg` varchar(255) NULL COMMENT '状态说明' AFTER `work_status`;

--changeset jiangcz:2305-apiAudit-20230509 labels:create tables
CREATE TABLE IF NOT EXISTS `behavior_analysis_model`  (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `base_line_id` int(11) NULL DEFAULT NULL COMMENT '基线id',
    `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
    `config` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配置信息',
    `rule_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '事件id',
    `param` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数',
    `create_time` datetime NULL DEFAULT NULL COMMENT '入库时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

--changeset jiangcz:2305-apiAudit-20230523 labels:create tables
CREATE TABLE IF NOT EXISTS `strategy_config` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '名称',
    `config` text COLLATE utf8_unicode_ci COMMENT '算法配置\r\n[{\r\n "indexId": "源索引id",\r\n "column": "name",\r\n "filter": "name",\r\n "type": "1",\r\n  "value": "",\r\n  "calculation": [{\r\n   "column": "ip",\r\n   "algorithm": "1",\r\n   "agg": "true",\r\n    "aggLevel": "true"\r\n  }]\r\n}]',
    `save_index` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '入库索引',
    `save_columns` varchar(5000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '入库字段配置\r\n[{\r\n    "src":"dev_ip",\r\n    "dest":"ip",\r\n    "type":"keyword",\r\n    "format":""\r\n}]',
    `fields` varchar(10000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '字段信息',
    `cron` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '执行cron表达式',
    `status` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态 0初始化 1启用 2停用',
    `contrast` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '对比信息',
    `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '描述',
    `insert_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '入库时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='策略配置表';

--changeset jiangcz:2306-apiAudit-20230601 labels:update tables
ALTER TABLE `strategy_config` ADD COLUMN `custom` char(1) NULL DEFAULT 0 COMMENT '是否指定类（1是 0否）' AFTER `description`,ADD COLUMN `custom_class` varchar(255) NULL COMMENT '任务指定类全路径' AFTER `custom`;
ALTER TABLE `base_line` ADD COLUMN `data_cycle` int(11) NULL COMMENT '数据周期' AFTER `special_param`;
ALTER TABLE `base_line` ADD COLUMN `run_num` int(11) DEFAULT 0 COMMENT '运行次数' AFTER `data_cycle`;
ALTER TABLE `base_line_result` ADD COLUMN `config` varchar(255) NULL AFTER `base_line_id`;

--changeset jiangcz:2306-apiAudit-20230600 labels:update tables
ALTER TABLE `strategy_config` ADD COLUMN `alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL AFTER `config`;
ALTER TABLE `strategy_config` MODIFY COLUMN `contrast` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '对比信息\r\n{\r\n	\"local\": \"num >6 && num <10\",\r\n	\"line\": {\r\n		\"id\":53,\r\n		\"join\":[{\r\n			\"src\":\"user_no\",\r\n		  	\"dst\":\"userNo\"\r\n		}],\r\n		\"script\":\"num >6 && num <10\"\r\n	}\r\n}' AFTER `status`;
ALTER TABLE `strategy_config` ADD COLUMN `rule_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '策略标识' AFTER `contrast`;
ALTER TABLE `strategy_config` ADD COLUMN `day` int(11) NULL DEFAULT NULL COMMENT '计算天数' AFTER `rule_code`;

--changeset jiangcz:2306-apiAudit-20230608 labels:add tables
CREATE TABLE IF NOT EXISTS `password_key` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(255) DEFAULT NULL COMMENT '关键字',
  `insert_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS `password_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reg_rule` varchar(255) DEFAULT NULL,
  `special_class` varchar(255) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `insert_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;