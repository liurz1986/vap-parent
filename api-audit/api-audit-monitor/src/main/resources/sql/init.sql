--liquibase formatted sql
--changeset cz:20230316-apiMonitor-001 labels:init
CREATE TABLE IF NOT EXISTS `monitor2_asset_indicator_view` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `asset_type` varchar(255) DEFAULT NULL COMMENT '资产类别',
  `sno_unicode` varchar(255) DEFAULT NULL COMMENT 'sno唯一编码',
  `layout_setting` text COMMENT '页面布局配置json',
  `view_setting` text COMMENT '页面展示配置json',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ui_asset_type_sno_unicode` (`asset_type`,`sno_unicode`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='V2-资产类型监控页面配置';


CREATE TABLE IF NOT EXISTS `monitor2_asset_indicator_view_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `asset_type` varchar(255) DEFAULT NULL COMMENT '资产类别',
  `sno_unicode` varchar(255) DEFAULT NULL COMMENT 'sno唯一编码',
  `layout_setting` text COMMENT '页面布局配置json',
  `view_setting` text COMMENT '页面展示配置json',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` varchar(255) DEFAULT NULL COMMENT '版本号',
  `asset_indicator_view_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='V2-资产类型关联展示指标';

CREATE TABLE IF NOT EXISTS `monitor2_asset_info` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `dev_id` VARCHAR(255) DEFAULT NULL COMMENT '设备id',
  `dev_name` VARCHAR(255) DEFAULT NULL COMMENT '设备名称',
  `dev_ip` VARCHAR(120) DEFAULT NULL COMMENT '设备IP',
  `other_info` VARCHAR(120) DEFAULT NULL COMMENT '预留字段-设备其他信息',
  `asset_type` VARCHAR(255) DEFAULT NULL COMMENT '资产类别',
  `sno_unicode` VARCHAR(255) DEFAULT NULL COMMENT 'sno唯一编码',
  `monitor_protocol` VARCHAR(255) DEFAULT NULL COMMENT '监控协议',
  `monitor_setting` VARCHAR(2550) DEFAULT NULL COMMENT '监控连接配置',
  `layout_setting` TEXT COMMENT '页面布局配置json',
  `view_setting` TEXT COMMENT '页面展示配置json',
  `startup_state` INT(11) DEFAULT '0' COMMENT '启用状态:1启动监控',
  `connect_state` INT(11) DEFAULT '0' COMMENT '连通状态:1=连通',
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='V2-资产监控表';

CREATE TABLE IF NOT EXISTS `monitor2_asset_oid_alg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `asset_type` varchar(255) DEFAULT NULL COMMENT '资产类别',
  `sno_unicode` varchar(100) DEFAULT NULL COMMENT '资产类别-子类',
  `sno_unicode_desc` varchar(255) DEFAULT NULL COMMENT '资产类别描述',
  `oid` varchar(255) DEFAULT NULL COMMENT 'oid(0到多个)',
  `indicator_field` varchar(64) NOT NULL COMMENT '存储字段',
  `indicator_name` varchar(100) DEFAULT NULL COMMENT '指标名称',
  `algorithm_type` varchar(255) DEFAULT NULL COMMENT '算法类型(公式/正则/取值/列表)',
  `algo` varchar(500) DEFAULT NULL COMMENT '算法',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `deleteable` int(11) DEFAULT NULL COMMENT '可删除',
  `available` int(11) DEFAULT '1' COMMENT '状态可用',
  `real_query` int(11) NOT NULL DEFAULT '0' COMMENT '实时查询(1=实时查询,0=周期采集)',
  `test_res` varchar(5000) DEFAULT NULL COMMENT '测试结果',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '添加时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `u_asset_type_sno_unicode_indicator` (`asset_type`,`sno_unicode`,`indicator_field`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='V2-指标算法';

CREATE TABLE IF NOT EXISTS `monitor2_indicator` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `indicator_name` varchar(100) NOT NULL COMMENT '指标名称(内存占比/cpu利用率/磁盘等)',
  `indicator_type` varchar(100) NOT NULL COMMENT '指标分类',
  `indicator_field` varchar(255) NOT NULL COMMENT '存储字段',
  `es_type` varchar(32) DEFAULT NULL COMMENT 'es存储字段类型',
  `data_type` varchar(32) DEFAULT NULL COMMENT '数据类型(1=值;2=数组列表;3=磁盘或对象)',
  `support_view` varchar(60) DEFAULT NULL COMMENT '支持的面板类型',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位(例如G,%,个等,可为空)',
  `available` int(11) DEFAULT '1' COMMENT '是否可用',
  `real_query` int(11) NOT NULL DEFAULT '0' COMMENT '实时查询(1=实时查询,0=周期采集)',
  `dict` varchar(100) DEFAULT NULL COMMENT '字典转义(json形式)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_indicator_field` (`indicator_field`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='V2-指标';

CREATE TABLE IF NOT EXISTS `monitor2_indicator_view` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `indicators` varchar(500) NOT NULL COMMENT '指标标识(多个逗号分割)',
  `indicator_name` varchar(500) DEFAULT NULL COMMENT '指标名称(内存占比/cpu利用率/磁盘等)',
  `view_type` varchar(100) DEFAULT NULL COMMENT '展示形式(val=值/trend=趋势/list=列表/disk=磁盘)',
  `view_title` varchar(100) DEFAULT NULL COMMENT '展示标题',
  `param_desc` varchar(600) DEFAULT NULL COMMENT '参数说明',
  `data_sample` text COMMENT '数据示例',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='V2-指标展示面板';

--changeset sj:20231009-apiMonitor-001 labels:add_table
CREATE TABLE IF NOT EXISTS `monitor2_asset_type` (
  `guid` varchar(36) NOT NULL,
  `unique_code` varchar(50) DEFAULT NULL,
  `tree_code` varchar(128) DEFAULT NULL,
  `icon_cls` varchar(50) DEFAULT NULL,
  `title` varchar(50) DEFAULT NULL,
  `type_level` int(11) DEFAULT NULL,
  `parent_tree_code` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='算法类型';
