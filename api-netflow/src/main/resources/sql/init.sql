--liquibase formatted sql
--changeset lilang:20220401-apiNetflow-0401 labels:inittables

CREATE TABLE IF NOT EXISTS `collector_offline_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `creator` varchar(255) DEFAULT NULL COMMENT '导入人员',
  `create_time` datetime DEFAULT NULL COMMENT '导入时间',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态，1：已读取，2：发送中，3：发送成功，4：发送失败',
  `template_id` int(11) DEFAULT NULL COMMENT '模板ID',
  `template_name` varchar(255) DEFAULT NULL COMMENT '模板名称',
  `collector_id` int(11) DEFAULT NULL COMMENT '事件接收器ID',
  `collector_name` varchar(255) DEFAULT NULL COMMENT '事件接收器名称',
  `error_file` varchar(1000) DEFAULT NULL COMMENT '失败文件地址',
  `total_count` int(11) DEFAULT NULL COMMENT '总数据量',
  `success_count` int(11) DEFAULT '0' COMMENT '发送成功数据量',
  `error_count` int(11) DEFAULT '0' COMMENT '发送失败数据量',
  `type` tinyint(4) DEFAULT NULL COMMENT '格式，1：excel，2：xml',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=462 DEFAULT CHARSET=utf8 COMMENT='离线导入日志';

CREATE TABLE IF NOT EXISTS `collector_offline_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '模板名称',
  `type` int(11) DEFAULT NULL COMMENT '类型，1：xls，2：xml',
  `path` varchar(1000) DEFAULT NULL COMMENT '文件路径',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_update_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `oper_type` int(4) DEFAULT NULL COMMENT '操作类型，0：自定义，1：预定义',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='离线模板管理';

--changeset lilang:20230414-apiNetflow-001
ALTER TABLE `collector_offline_template`
ADD COLUMN `source_id`  int(11) NULL DEFAULT NULL COMMENT '数据源ID';

--changeset sj:20231019-apiNetflow-001
CREATE TABLE IF NOT EXISTS `network_monitor_audited` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `device_id` varchar(64) DEFAULT NULL,
  `device_belong` varchar(255) DEFAULT NULL,
  `device_location` varchar(255) DEFAULT NULL,
  `device_soft_version` varchar(64) DEFAULT NULL,
  `report_time` datetime DEFAULT NULL,
  `device_sys_version` varchar(64) DEFAULT NULL,
  `device_bios_version` varchar(64) DEFAULT NULL,
  `interface_info` varchar(512) DEFAULT NULL,
  `mem_total` int(11) DEFAULT NULL,
  `cpu_info` varchar(256) DEFAULT NULL,
  `disk_info` varchar(256) DEFAULT NULL,
  `address_code` varchar(64) DEFAULT NULL,
  `contact` text,
  `memo` varchar(256) DEFAULT NULL,
  `api_key` varchar(64) DEFAULT NULL,
  `reg_type` int(11) DEFAULT NULL,
  `network_monitor_status` int(11) DEFAULT NULL COMMENT '网络状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;



CREATE TABLE IF NOT EXISTS `network_monitor_current_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `device_id` varchar(64) DEFAULT NULL,
  `ip` varchar(32) DEFAULT NULL,
  `device_location` varchar(256) DEFAULT NULL,
  `device_belong` varchar(256) DEFAULT NULL,
  `device_soft_version` varchar(64) DEFAULT NULL,
  `device_cpu_uasge` double DEFAULT NULL,
  `device_mem_uasge` double DEFAULT NULL,
  `device_disk_uasge` double DEFAULT NULL,
  `device_status` int(11) DEFAULT NULL,
  `device_status_description` varchar(256) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `network_monitor_reg_audit_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `device_id` varchar(50) DEFAULT NULL,
  `reg_id` int(11) DEFAULT NULL,
  `audit_time` datetime DEFAULT NULL,
  `audit_result` int(11) DEFAULT NULL,
  `memo` varchar(256) DEFAULT NULL,
  `audit_account` varchar(50) DEFAULT NULL,
  `audit_account_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `network_monitor_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id` varchar(64) DEFAULT NULL COMMENT '设备ID',
  `device_sys_version` varchar(255) DEFAULT NULL COMMENT '设备系统版本号',
  `device_soft_version` varchar(255) DEFAULT NULL COMMENT '设备软件版本号',
  `device_bios_version` varchar(255) DEFAULT NULL COMMENT '固件版本',
  `device_cpu_core` int(11) DEFAULT NULL COMMENT '设备cpu物理核数量',
  `device_cpu_usage` int(11) DEFAULT NULL COMMENT 'CPU利用率',
  `device_mem_size` int(11) DEFAULT NULL COMMENT '内存大小',
  `device_hdisk_size` int(11) DEFAULT NULL COMMENT '硬盘大小',
  `device_hdisk_num` varchar(255) DEFAULT NULL COMMENT '硬盘序列号',
  `device_mem_usage` int(11) DEFAULT NULL COMMENT '内存利用率',
  `device_hdisk_usage` int(11) DEFAULT NULL COMMENT '硬盘使用率',
  `report_time` datetime DEFAULT NULL COMMENT '上报时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;