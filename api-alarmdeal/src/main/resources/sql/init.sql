-- liquibase formatted sql
-- changeset wudi:20190418-alarmdeal-001 labels:"告警初始化脚本"
SET FOREIGN_KEY_CHECKS=0;
CREATE TABLE IF NOT EXISTS `alarm_deal` (
  `guid` varchar(255) NOT NULL,
  `alarm_guid` varchar(5000) DEFAULT NULL,
  `create_people` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  `deal_detail` varchar(255) DEFAULT NULL,
  `deal_status` varchar(255) DEFAULT NULL,
  `end_time` varchar(255) DEFAULT NULL,
  `risk_event_name` varchar(255) DEFAULT NULL,
  `deal_type` varchar(255) DEFAULT NULL,
  `create_people_Id` varchar(255) DEFAULT NULL,
  `dead_line` varchar(50) DEFAULT NULL,
  `deal_person` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE IF NOT EXISTS `alarm_item_deal` (
  `guid` varchar(255) NOT NULL,
  `deal_guid` varchar(255) DEFAULT NULL,
  `happen_time` varchar(255) DEFAULT NULL,
  `item_people` varchar(255) DEFAULT NULL,
  `item_status` varchar(255) DEFAULT NULL,
  `item_type` varchar(255) DEFAULT NULL,
  `json_info` varchar(5000) DEFAULT NULL,
  `last_exe_time` varchar(255) DEFAULT NULL,
  `item_people_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




CREATE TABLE IF NOT EXISTS `deal_common_log` (
  `guid` varchar(255) NOT NULL,
  `deal_instance_id` varchar(255) DEFAULT NULL,
  `happen_time` varchar(255) DEFAULT NULL,
  `item_guid` varchar(255) DEFAULT NULL,
  `item_type` varchar(255) DEFAULT NULL,
  `json_info` varchar(5000) DEFAULT NULL,
  `lastversion_flag` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE IF NOT EXISTS `event_category` (
  `id` varchar(255) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `code_level` varchar(255) DEFAULT NULL,
  `created_time` varchar(255) DEFAULT NULL,
  `event_desc` varchar(255) DEFAULT NULL,
  `modified_time` varchar(255) DEFAULT NULL,
  `order_num` int(11) DEFAULT NULL,
  `parent_id` varchar(255) DEFAULT NULL,
  `priority_level` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `weight` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




CREATE TABLE IF NOT EXISTS `event_table` (
  `id` varchar(50) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `devicetype` varchar(255) DEFAULT NULL,
  `devicetypelevel` varchar(255) DEFAULT NULL,
  `eventtype` varchar(255) DEFAULT NULL,
  `eventtypelevel` varchar(255) DEFAULT NULL,
  `groupName` varchar(255) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `is_multi_table` bit(10) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `event_column` (
  `id` varchar(50) NOT NULL,
  `label` varchar(50) DEFAULT NULL,
  `len` varchar(50) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `not_null` bit(10) DEFAULT NULL,
  `is_primary_key` bit(10) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `event_table` varchar(50) DEFAULT NULL,
  `EventTable` varchar(50) DEFAULT NULL,
   PRIMARY KEY (`id`),
   KEY `FKj7v9r7pymn4lv4qkrlgho6vci` (`event_table`),
   KEY `FKa1jtnpkquj0ba7wpwg3uqte0t` (`EventTable`),
  CONSTRAINT `FKa1jtnpkquj0ba7wpwg3uqte0t` FOREIGN KEY (`EventTable`) REFERENCES `event_table` (`id`),
  CONSTRAINT `FKj7v9r7pymn4lv4qkrlgho6vci` FOREIGN KEY (`event_table`) REFERENCES `event_table` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE IF NOT EXISTS `risk_event_rule` (
  `id` varchar(32) NOT NULL,
  `riskEventId` varchar(50) NOT NULL COMMENT '风险事件id',
  `name_` varchar(255) NOT NULL,
  `type_` int(11) NOT NULL COMMENT '规则类型',
  `desc_` text COMMENT '描述',
  `note` text COMMENT '备注',
  `isStarted` bit(10) NOT NULL COMMENT '是否启动',
  `createdTime` varchar(19) NOT NULL,
  `modifiedTime` varchar(19) DEFAULT NULL,
  `extend1` text COMMENT '扩展字段',
  `extend2` text COMMENT '扩展字段',
  `assetip` varchar(32) DEFAULT NULL,
  `assetguid` varchar(32) DEFAULT NULL,
  `warmType` varchar(255) DEFAULT NULL,
  `cascadeState` varchar(32) DEFAULT NULL,
  `levelstatus` varchar(32) DEFAULT '1',
  `validations` varchar(255) DEFAULT '1' COMMENT '有效性1有效,0无效',
  `rule_code` varchar(255) DEFAULT NULL,
  `field_info` varchar(500) DEFAULT NULL,
  `data_source` varchar(255) DEFAULT NULL,
  `rule_complex` varchar(255) DEFAULT NULL,
  `attack_event` bigint(10) DEFAULT NULL,
  `main_class` varchar(500) DEFAULT NULL,
  `job_name` varchar(255) DEFAULT NULL,
  `rule_desc` text COMMENT '规则描述',
  `rule_field_json` varchar(500) DEFAULT NULL COMMENT '自定义规则字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `warn_manager` (
  `guid` varchar(50) NOT NULL,
  `come_from` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  `creater` varchar(255) DEFAULT NULL,
  `file` varchar(255) DEFAULT NULL,
  `possible_result` varchar(500) DEFAULT NULL,
  `published_person` varchar(255) DEFAULT NULL,
  `published_time` varchar(255) DEFAULT NULL,
  `solution` varchar(500) DEFAULT NULL,
  `warn_detail` varchar(255) DEFAULT NULL,
  `warn_level` varchar(255) DEFAULT NULL,
  `warn_level_name` varchar(255) DEFAULT NULL,
  `warn_name` varchar(255) DEFAULT NULL,
  `warn_status` varchar(255) DEFAULT NULL,
  `warn_status_name` varchar(255) DEFAULT NULL,
  `warn_super_type` varchar(255) DEFAULT NULL,
  `warn_type` varchar(255) DEFAULT NULL,
  `warn_type_name` varchar(255) DEFAULT NULL,
  `warn_range` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `threat_info` (
  `id` varchar(50) NOT NULL,
  `library_guid` varchar(50) DEFAULT NULL,
  `threat_frequence` bigint(10) DEFAULT NULL,
  `threat_value` bigint(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `library_foregen` (`library_guid`),
  CONSTRAINT `library_foregen` FOREIGN KEY (`library_guid`) REFERENCES `event_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE IF NOT EXISTS `threat_library` (
  `id` varchar(50) NOT NULL,
  `threat_name` varchar(255) DEFAULT NULL,
  `threat_source` text,
  `threat_desc` text,
  `motivate_desc` text,
  `motivate_assignment` int(10) DEFAULT NULL,
  `ability_desc` text,
  `ability_assignment` int(10) DEFAULT NULL,
  `effect_target` varchar(255) DEFAULT NULL,
  `relate_vulnerability` varchar(255) DEFAULT NULL,
  `threat_classification` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `threat_extra` (
  `threat_library_id` varchar(50) NOT NULL,
  `detail_info` text,
  `threat_harm` text,
  `deal_advice` text,
  `safe_advice` text,
  PRIMARY KEY (`threat_library_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- changeset wudi:20190610-alarmdeal-002
ALTER TABLE `risk_event_rule`
ADD COLUMN `unitScore`  float(10,2) NULL DEFAULT 0.1 COMMENT '告警规则添加分值字段' AFTER `rule_field_json`,
ADD COLUMN `maxScore`  float(10,2) NULL DEFAULT 5.0 COMMENT '告警规则添加最高分值' AFTER `unitScore`;
UPDATE risk_event_rule SET unitScore = 0.1;

CREATE TABLE IF NOT EXISTS `src_ip_score` (
  `guid` varchar(50) NOT NULL,
  `srcIp` varchar(255) DEFAULT NULL COMMENT '源IP',
  `create_date` date DEFAULT NULL,
  `score_value` float(10,2) DEFAULT '0.00',
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- changeset wudi:20190619-alarmdeal-003
ALTER TABLE `risk_event_rule`
ADD COLUMN `risk_sql`  varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `maxScore`,
ADD COLUMN `tableName`  varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `risk_sql`,
ADD COLUMN `logPath`  varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `tableName`,
ADD COLUMN `flag`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `logPath`;


-- changeset wudi:20190712-alarmdeal-004
ALTER TABLE `event_column` DROP FOREIGN KEY `FKa1jtnpkquj0ba7wpwg3uqte0t`;

ALTER TABLE `event_column` DROP FOREIGN KEY `FKj7v9r7pymn4lv4qkrlgho6vci`;

ALTER TABLE `event_column`
DROP COLUMN `event_table`,
ADD COLUMN `srcIp`  tinyint(1) NULL AFTER `is_primary_key`,
ADD COLUMN `dstIp`  tinyint(1) NULL AFTER `srcIp`,
ADD COLUMN `relateIp`  tinyint(1) NULL AFTER `dstIp`,
ADD COLUMN `timeLine`  tinyint(1) NULL AFTER `relateIp`;




ALTER TABLE `event_table`
ADD COLUMN `index_name`  varchar(255) NULL AFTER `is_multi_table`,
ADD COLUMN `topic_name`  varchar(255) NULL AFTER `index_name`,
ADD COLUMN `monitor`  tinyint(1) NULL AFTER `topic_name`,
ADD COLUMN `formatter`  varchar(255) NULL AFTER `monitor`;


ALTER TABLE `event_table`
MODIFY COLUMN `is_multi_table`  tinyint(1) NULL DEFAULT NULL AFTER `label`;

ALTER TABLE `event_column`
MODIFY COLUMN `not_null`  tinyint(1) NULL DEFAULT NULL AFTER `name`,
MODIFY COLUMN `is_primary_key`  tinyint(1) NULL DEFAULT NULL AFTER `not_null`;

-- changeset wudi:20190808-alarmdeal-001
ALTER TABLE `threat_info`
ADD COLUMN `threat_desc`  text NULL AFTER `threat_value`;


-- changeset wudi:201908-alarmdeal-001
ALTER TABLE `risk_event_rule`
ADD COLUMN `tableLabel`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `flag`;


-- changeset wudi:20191024-alarmdeal-001
ALTER TABLE `risk_event_rule`
ADD COLUMN `knowledgeTag`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `tableLabel`;


-- changeset dengqiyou:20190807-alarmdeal-001
SET FOREIGN_KEY_CHECKS=0;
CREATE TABLE IF NOT EXISTS `alarm_whitelist` (
  `guid` varchar(255) NOT NULL,
  `eventCategoryId` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
   `srcIp` varchar(255) DEFAULT NULL,
  `destIp` varchar(255) DEFAULT NULL,
  `update_time` varchar(19) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- changeset wudi:20190911-alarmdeal-001
CREATE TABLE IF NOT EXISTS `iptable_summary` (
  `Guid` varchar(50) NOT NULL,
  `startip` varchar(255) DEFAULT NULL,
  `endip` varchar(255) DEFAULT NULL,
  `startip_num` bigint(20) DEFAULT NULL,
  `endip_num` bigint(20) DEFAULT NULL,
  `repartition` varchar(500) DEFAULT NULL,
  `location` varchar(5500) DEFAULT NULL,
  PRIMARY KEY (`Guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- changeset wudi:20191118-alarmdeal-001
ALTER TABLE `event_category`
ADD COLUMN `attack_flag`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 1 AFTER `weight`;

CREATE TABLE  IF NOT EXISTS `interrupt_key` (
  `guid` varchar(50) NOT NULL,
  `keyword` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `area_location` (
  `guid` varchar(255) NOT NULL,
  `areaName` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT '',
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- changeset wudi:20191205-alarmdeal-001
CREATE TABLE IF NOT EXISTS `time_count` (
`guid`  varchar(50) NOT NULL ,
`timeParam`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`timeCount`  int(5) NULL DEFAULT NULL ,
PRIMARY KEY (`guid`)
)
;

-- changeset wudi:20191231-alarmdeal-001
CREATE TABLE IF NOT EXISTS `warn_result` (
  `id` varchar(50) NOT NULL,
  `riskEventId` varchar(50) DEFAULT NULL,
  `riskEventName` varchar(255) DEFAULT NULL,
  `triggerTime` datetime DEFAULT NULL,
  `statusEnum` int(5) DEFAULT NULL,
  `weight` int(5) DEFAULT NULL,
  `dstIps` varchar(255) DEFAULT NULL,
  `riskEventCode` varchar(255) DEFAULT NULL,
  `ruleCode` varchar(255) DEFAULT NULL,
  `src_ips` varchar(255) DEFAULT NULL,
  `finish_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- changeset wudi:20200103-alarmdeal-001
ALTER TABLE `warn_result`
ADD COLUMN `DATA_UP_UUID`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `finish_date`,
ADD COLUMN `DATA_UP_TIME`  datetime NULL AFTER `DATA_UP_UUID`,
ADD COLUMN `DATA_UP_STATUS`  varchar(10) NULL AFTER `DATA_UP_TIME`;



-- changeset wudi:20200210-alarmdeal-001
ALTER TABLE `risk_event_rule`
ADD COLUMN `init_status`  varchar(50) NULL AFTER `knowledgeTag`;




-- changeset wudi:20200414-alarmdeal-001
ALTER TABLE `event_category`
ADD COLUMN `threat_source`  text NULL AFTER `weight`,
ADD COLUMN `threat_classification`  varchar(500) NULL AFTER `threat_source`,
ADD COLUMN `motivate_desc`  text NULL AFTER `threat_classification`,
ADD COLUMN `motivate_assignment`  int(10) NULL AFTER `motivate_desc`,
ADD COLUMN `ability_desc`  text NULL AFTER `motivate_assignment`,
ADD COLUMN `ability_assignment`  int(10) NULL AFTER `ability_desc`,
ADD COLUMN `effect_target`  varchar(500) NULL AFTER `ability_assignment`,
ADD COLUMN `relate_vulnerability`  varchar(500) NULL AFTER `effect_target`;



-- changeset wudi:20200514-alarmdeal-001
CREATE TABLE IF NOT EXISTS `filter_operator` (
  `guid` varchar(50) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `config` json DEFAULT NULL,
  `source` json DEFAULT NULL,
  `output_fields` json DEFAULT NULL,
  `version` int(10) DEFAULT NULL,
  `delete_flag` tinyint(5) DEFAULT NULL,
  `dependencies` json DEFAULT NULL,
  `status` tinyint(2) DEFAULT NULL,
  `outputs` json DEFAULT NULL,
  `operator_type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- changeset lps:20200528-alarmdeal-001
alter table `risk_event_rule` alter column `init_status` set default 0;


-- changeset lps:20200515-alarmdeal-001
ALTER TABLE `event_table` ADD COLUMN `version` int(5) DEFAULT 0;
ALTER TABLE `event_column` ADD COLUMN `dataHint` varchar(50);
ALTER TABLE `event_column` ADD COLUMN `col_order` int(6);
ALTER TABLE `event_column` ADD COLUMN `is_event_time`  tinyint(5) NULL AFTER `col_order`;
ALTER TABLE `event_column` ADD COLUMN `is_show`  tinyint(5) NULL AFTER `is_event_time`;


ALTER TABLE `event_column`
MODIFY COLUMN `is_event_time`  tinyint(5) NULL DEFAULT 0 AFTER `col_order`;

-- changeset lps:20200610-alarmdeal-001
ALTER TABLE `risk_event_rule`
ADD COLUMN `delete_flag`  tinyint(10) NULL AFTER `init_status`;

ALTER TABLE `risk_event_rule`
ADD COLUMN `analysis_id`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `delete_flag`;


-- changeset lps:20200609-alarmdeal-001
CREATE TABLE IF NOT EXISTS `object_resource`  (
  `guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'guid',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `content` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '内容',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `object_resource_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `object_resource_source` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `version` int(5) NULL DEFAULT 0 COMMENT '0',
  `delete_flag` tinyint(4) NULL DEFAULT NULL COMMENT '-1-已删除。1__正常',
  PRIMARY KEY (`guid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


-- changeset wudi:20200709-alarmdeal-001
ALTER TABLE `object_resource`
ADD COLUMN `multi_version`  text NULL AFTER `delete_flag`,
ADD COLUMN `code`  varchar(255) NULL AFTER `multi_version`;

ALTER TABLE `filter_operator`
ADD COLUMN `multi_version`  text NULL AFTER `operator_type`,
ADD COLUMN `code`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `multi_version`;

-- changeset lps:20200722-alarmdeal-001
ALTER TABLE `risk_event_rule` ADD COLUMN `event_name` VARCHAR(255);
ALTER TABLE `risk_event_rule` ADD COLUMN `attack_line` VARCHAR(50);
ALTER TABLE `risk_event_rule` ADD COLUMN `threat_credibility` VARCHAR(255);
ALTER TABLE `risk_event_rule` ADD COLUMN `deal_advcie` text;
ALTER TABLE `risk_event_rule` ADD COLUMN `produce_threat` tinyint(5);
ALTER TABLE `risk_event_rule` ADD COLUMN `failed_status` int(5);

-- changeset lps:20200803-alarmdeal-001
CREATE TABLE IF NOT EXISTS `alarm_query` (
  `guid` varchar(64) NOT NULL,
  `query_name` varchar(255) DEFAULT NULL COMMENT '条件名',
  `query_condition` text COMMENT '查询条件信息',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `event_category` ADD COLUMN `deal_advice` text;
ALTER TABLE `event_category` ADD COLUMN `principle` text;
ALTER TABLE `event_category` ADD COLUMN `harm` text;
ALTER TABLE `event_category` ADD COLUMN `thread_summary` text;
ALTER TABLE `event_category` ADD COLUMN `threat_desc` text;


-- changeset wudi:20200806-alarmdeal-001
ALTER TABLE `filter_operator`
ADD COLUMN `label`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `code`,
ADD COLUMN `desc_`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `label`,
ADD COLUMN `create_time`  datetime NULL AFTER `desc_`,
ADD COLUMN `update_time`  datetime NULL AFTER `create_time`,
ADD COLUMN `room_type`  varchar(50) NULL AFTER `update_time`;

-- changeset lps:20200805-alarmdeal-001
ALTER TABLE `event_table` ADD COLUMN `data_source` VARCHAR(255);


-- changeset lps:20201116-alarmdeal-001
ALTER TABLE `object_resource` MODIFY COLUMN `content`  text(0) ;
-- changeset lps:20201116-alarmdeal-002
ALTER TABLE `event_category` ADD COLUMN `type` int(5);
-- changeset lps:20201118-alarmdeal-008
ALTER TABLE `risk_event_rule` MODIFY COLUMN `isStarted`  bit(1) ;


-- changeset lps:20201118-alarmdeal-008
ALTER TABLE `risk_event_rule` MODIFY COLUMN `isStarted`  bit(1) ;

-- changeset lps:20201217-alarmdeal-001
CREATE TABLE IF NOT EXISTS `rule_model_of_asset_type`  (
  `guid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `analysis_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分析器模板code',
  `asset_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `param_config` json NULL COMMENT '参数配置项',
  PRIMARY KEY (`guid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

ALTER TABLE `risk_event_rule` ADD COLUMN `rule_type` VARCHAR(32);
ALTER TABLE `risk_event_rule` ADD COLUMN `model_id` VARCHAR(255);
ALTER TABLE `risk_event_rule` ADD COLUMN `tag` VARCHAR(32);

-- changeset lps:20201224-alarmdeal-001
ALTER TABLE `risk_event_rule` ADD COLUMN `allow_start`  bit(1);
ALTER TABLE `filter_operator` ADD COLUMN `allow_start`  bit(1);




-- changeset lps:20210125-alarmdeal-001
ALTER TABLE `event_table`
ADD COLUMN `index_type`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `data_source`;


-- changeset wudi:20210331-alarmdeal-001
CREATE TABLE IF NOT EXISTS `flink_error_log` (
  `guid` varchar(50) NOT NULL,
  `rule_name` varchar(500) DEFAULT NULL,
  `log_info` text,
  `date_time` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `flink_error_log`
ADD COLUMN `rule_level`  varchar(5) NULL AFTER `date_time`,
ADD COLUMN `exception_type`  varchar(20) NULL AFTER `rule_level`;


-- changeset wd:20210421-alarmdeal-001
ALTER TABLE `filter_operator`
ADD COLUMN `newline_flag`  varchar(255) NULL AFTER `allow_start`;


ALTER TABLE `filter_operator` ADD COLUMN `config_template` json DEFAULT NULL;
ALTER TABLE `filter_operator` ADD COLUMN `param_config` json DEFAULT NULL;
ALTER TABLE `filter_operator` ADD COLUMN `param_value` json DEFAULT NULL;
ALTER TABLE `filter_operator` ADD COLUMN `tag` VARCHAR(32);
ALTER TABLE `filter_operator` ADD COLUMN `model_id` VARCHAR(255);
ALTER TABLE `filter_operator` ADD COLUMN `rule_type` VARCHAR(32);




-- changeset lps:20210806-alarmdeal-001
CREATE TABLE  IF NOT EXISTS  `supervise_task`  (
  `guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `notice_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `notice_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `send_time` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `notice_desc` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `deal_status` tinyint(4) NULL DEFAULT NULL,
  `event_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `response_note` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
   `task_create` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `response_time` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`guid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- changeset wudi:20210810-alarmdeal-001
CREATE TABLE IF NOT EXISTS `dimension_table` (
  `guid` varchar(50) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `name_en` varchar(255) DEFAULT NULL,
  `description` text,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






CREATE TABLE IF NOT EXISTS `dimension_table_field` (
  `guid` varchar(50) NOT NULL,
  `field_name` varchar(255) DEFAULT NULL,
  `field_type` varchar(50) DEFAULT NULL,
  `field_desc` text,
  `table_guid` varchar(50) DEFAULT NULL,
  `field_length` int(10) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- changeset wudi:20210811-alarmdeal-001
ALTER TABLE `alarm_deal`
ADD COLUMN `role_id`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `deal_person`,
ADD COLUMN `role_name`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `role_id`;


CREATE TABLE IF NOT EXISTS `iptable` (
  `Guid` varchar(50) NOT NULL,
  `startip` varchar(255) DEFAULT NULL,
  `endip` varchar(255) DEFAULT NULL,
  `startip_num` bigint(20) DEFAULT NULL,
  `endip_num` bigint(20) DEFAULT NULL,
  `country` varchar(500) DEFAULT NULL,
  `area` varchar(5000) DEFAULT NULL,
  `repartition` varchar(500) DEFAULT NULL,
  `location` varchar(5500) DEFAULT NULL,
  PRIMARY KEY (`Guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- changeset lps:20210830-alarmdeal-001
CREATE TABLE  IF NOT EXISTS `offline_extract_task` (
  `guid` varchar(64) NOT NULL,
  `data_config_name` varchar(255) DEFAULT NULL,
  `data_source_type` varchar(100) DEFAULT NULL,
  `data_source_name` varchar(255) DEFAULT NULL,
  `time_field` varchar(100) DEFAULT NULL,
  `event_table_name` varchar(100) DEFAULT NULL,
  `event_table_id` varchar(64) DEFAULT NULL,
  `send_frequency` int(255) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `note` varchar(1000) DEFAULT NULL,
  `delete_flag` bit(1) DEFAULT NULL,
  `cycle_params` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `filter_condition` varchar(255) DEFAULT NULL,
  `next_execute_time` varchar(64) DEFAULT NULL,
  `task_period` varchar(64) DEFAULT NULL,
  `topic` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS  `offline_extract_task_log`  (
  `guid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `data_config_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `execute_time` datetime(0) NULL DEFAULT NULL,
  `select_time_range` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `select_count` bigint(12) NULL DEFAULT NULL,
  `execute_result` bit(1) NULL DEFAULT NULL,
  `failed_result` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `offline_config_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`guid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

ALTER TABLE `event_table` ADD COLUMN `data_type`  varchar(64) default "es";




-- changeset wd:20210909-alarmdeal-001
CREATE TABLE IF NOT EXISTS `baseline_weak_password`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `password` VARCHAR(50) NOT NULL COMMENT '弱口令',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_parameter_blacklist`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `req_body` VARCHAR(50) NOT NULL COMMENT '参数',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_software_whitelist`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `software_name` VARCHAR(50) NOT NULL COMMENT '软件名称',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_command_keyword`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `keyword` VARCHAR(50) NOT NULL COMMENT '关键字',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_printburn`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `dev_ip` VARCHAR(50) NOT NULL COMMENT '设备IP',
  `dev_name` VARCHAR(50) NOT NULL COMMENT '设备名称',
  `op_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_printburn_filetype`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型',
  `op_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_printburn_filesize`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `file_size_max` VARCHAR(50) NOT NULL COMMENT '文件大小最大值',
  `op_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_printburn_filefre`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `count_max` VARCHAR(50) NOT NULL COMMENT '最大次数',
  `op_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_printburn_filebrand`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `count_max` VARCHAR(50) NOT NULL COMMENT '最大次数',
  `time_bucket` VARCHAR(50) NOT NULL COMMENT '时段',
  `op_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_user_business`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_user_filedownload`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `count_max` VARCHAR(50) NOT NULL COMMENT '最大数量',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_admin_protocol`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `protocol` VARCHAR(50) NOT NULL COMMENT '运维协议',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_admin_port`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `port` VARCHAR(50) NOT NULL COMMENT '运维端口',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_admin_filecopy`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `count_max` VARCHAR(50) NOT NULL COMMENT '最大数量',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_admin_fileburn`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `count_max` VARCHAR(50) NOT NULL COMMENT '最大数量',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_app_protocol`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `app_id` VARCHAR(50) NOT NULL COMMENT '应用id',
  `app_name` VARCHAR(50) NOT NULL COMMENT '应用名称',
  `net_type` VARCHAR(50) NOT NULL COMMENT '通信类型',
  `app_protocol` VARCHAR(50) NOT NULL COMMENT '协议',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_app_port`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `app_id` VARCHAR(50) NOT NULL COMMENT '应用id',
  `app_name` VARCHAR(50) NOT NULL COMMENT '应用名称',
  `net_type` VARCHAR(50) NOT NULL COMMENT '通信类型',
  `port` VARCHAR(50) NOT NULL COMMENT '端口',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_app_business`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `app_id` VARCHAR(50) NOT NULL COMMENT '应用id',
  `app_name` VARCHAR(50) NOT NULL COMMENT '应用名称',
  `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_app_uavisit`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `user_type` VARCHAR(50) NOT NULL COMMENT '用户类型',
  `uri` VARCHAR(255) NOT NULL COMMENT '访问链接',
  `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_dev_ip`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `dev_ip` VARCHAR(50) NOT NULL COMMENT '设备IP',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_ip_pair`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `src_org` VARCHAR(50) NOT NULL COMMENT '单位A',
  `dst_org` VARCHAR(50) NOT NULL COMMENT '单位B',
  `src_ip` VARCHAR(50) NOT NULL COMMENT '源IP',
  `dst_ip` VARCHAR(50) NOT NULL COMMENT '目的IP',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_protocol_relative`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `src_org` VARCHAR(50) NOT NULL COMMENT '单位A',
  `dst_org` VARCHAR(50) NOT NULL COMMENT '单位B',
  `app_protocol` VARCHAR(50) NOT NULL COMMENT '协议',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_port_relative`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `src_org` VARCHAR(50) NOT NULL COMMENT '单位A',
  `dst_org` VARCHAR(50) NOT NULL COMMENT '单位B',
  `dst_port` VARCHAR(50) NOT NULL COMMENT '目标端口',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `baseline_file_inoutbusiness`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `src_org` VARCHAR(50) NOT NULL COMMENT '单位A',
  `dst_org` VARCHAR(50) NOT NULL COMMENT '单位B',
  `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型',
  `file_dir` VARCHAR(50) NOT NULL COMMENT '文件流转方向',
  PRIMARY KEY (`id`)
);



-- changeset sj:20210909-alarmdeal-002
ALTER TABLE `dimension_table` ADD COLUMN `table_type` VARCHAR(50) NULL COMMENT '维表类型' AFTER `create_time`; 
ALTER TABLE `dimension_table` ADD COLUMN `baseline_index` VARCHAR(50) NULL COMMENT '维表类型' AFTER `create_time`; 


 -- changeset sj:20210916-alarmdeal-003
CREATE TABLE  IF NOT EXISTS `event_alarm_setting` (
  `guid` varchar(36) NOT NULL,
  `link_guid` varchar(36) DEFAULT NULL COMMENT '关联id',
  `link_type` varchar(12) DEFAULT NULL COMMENT '关联类型event 、rule',
  `rule_path` varchar(128) DEFAULT NULL COMMENT '关联路径',
  `is_urge` tinyint(1) DEFAULT NULL COMMENT '是否督促',
  `time_limit_num` int(11) DEFAULT NULL COMMENT '督促时间',
  `urge_reason` varchar(1024) DEFAULT NULL COMMENT '督促原因',
  `to_role` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '接收角色',
  `to_user` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '接收人',
  `to_asset_user` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



 -- changeset wd:20210917-alarmdeal-003
CREATE TABLE  IF NOT EXISTS `baseline_weak_password`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `password` VARCHAR(50) NOT NULL COMMENT '弱口令',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_parameter_blacklist`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `req_body` VARCHAR(50) NOT NULL COMMENT '参数',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_software_whitelist`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `software_name` VARCHAR(50) NOT NULL COMMENT '软件名称',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_command_keyword`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `keyword` VARCHAR(50) NOT NULL COMMENT '关键字',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_printburn`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `dev_ip` VARCHAR(50) NOT NULL COMMENT '设备IP',
  `dev_name` VARCHAR(50) NOT NULL COMMENT '设备名称',
  `op_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_printburn_filetype`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型',
  `op_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_printburn_filesize`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `file_size_max` VARCHAR(50) NOT NULL COMMENT '文件大小最大值',
  `op_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_printburn_filefre`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `count_max` VARCHAR(50) NOT NULL COMMENT '最大次数',
  `op_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_printburn_filebrand`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `count_max` VARCHAR(50) NOT NULL COMMENT '最大次数',
  `time_bucket` VARCHAR(50) NOT NULL COMMENT '时段',
  `op_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_user_business`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_user_filedownload`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `count_max` VARCHAR(50) NOT NULL COMMENT '最大数量',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_admin_protocol`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `protocol` VARCHAR(50) NOT NULL COMMENT '运维协议',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_admin_port`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `port` VARCHAR(50) NOT NULL COMMENT '运维端口',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_admin_filecopy`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `count_max` VARCHAR(50) NOT NULL COMMENT '最大数量',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_admin_fileburn`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `count_max` VARCHAR(50) NOT NULL COMMENT '最大数量',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_app_protocol`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `app_id` VARCHAR(50) NOT NULL COMMENT '应用id',
  `app_name` VARCHAR(50) NOT NULL COMMENT '应用名称',
  `net_type` VARCHAR(50) NOT NULL COMMENT '通信类型',
  `app_protocol` VARCHAR(50) NOT NULL COMMENT '协议',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_app_port`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `app_id` VARCHAR(50) NOT NULL COMMENT '应用id',
  `app_name` VARCHAR(50) NOT NULL COMMENT '应用名称',
  `net_type` VARCHAR(50) NOT NULL COMMENT '通信类型',
  `port` VARCHAR(50) NOT NULL COMMENT '端口',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_app_business`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `app_id` VARCHAR(50) NOT NULL COMMENT '应用id',
  `app_name` VARCHAR(50) NOT NULL COMMENT '应用名称',
  `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_app_uavisit`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `username` VARCHAR(50) NOT NULL COMMENT '用户',
  `user_type` VARCHAR(50) NOT NULL COMMENT '用户类型',
  `uri` VARCHAR(255) NOT NULL COMMENT '访问链接',
  `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_app_ip`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `app_id` VARCHAR(50) NOT NULL COMMENT '应用id',
  `app_name` VARCHAR(50) NOT NULL COMMENT '应用名称',
  `ip` VARCHAR(50) NOT NULL COMMENT '目标IP',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_dev_ip`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `dev_ip` VARCHAR(50) NOT NULL COMMENT '设备IP',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_ip_pair`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `src_org` VARCHAR(50) NOT NULL COMMENT '单位A',
  `dst_org` VARCHAR(50) NOT NULL COMMENT '单位B',
  `src_ip` VARCHAR(50) NOT NULL COMMENT '源IP',
  `dst_ip` VARCHAR(50) NOT NULL COMMENT '目的IP',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_protocol_relative`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `src_org` VARCHAR(50) NOT NULL COMMENT '单位A',
  `dst_org` VARCHAR(50) NOT NULL COMMENT '单位B',
  `app_protocol` VARCHAR(50) NOT NULL COMMENT '协议',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_port_relative`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `src_org` VARCHAR(50) NOT NULL COMMENT '单位A',
  `dst_org` VARCHAR(50) NOT NULL COMMENT '单位B',
  `dst_port` VARCHAR(50) NOT NULL COMMENT '目标端口',
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `baseline_file_inoutbusiness`(  
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
  `src_org` VARCHAR(50) NOT NULL COMMENT '单位A',
  `dst_org` VARCHAR(50) NOT NULL COMMENT '单位B',
  `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型',
  `file_dir` VARCHAR(50) NOT NULL COMMENT '文件流转方向',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `base_dict_all` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(32) DEFAULT NULL COMMENT '字典表编号',
  `code_value` varchar(32) DEFAULT NULL COMMENT '字典表名称',
  `type` varchar(64) DEFAULT NULL COMMENT '字典表类型',
  `parent_type` varchar(64) DEFAULT NULL COMMENT '父类型',
  `leaf` varchar(32) DEFAULT NULL COMMENT '是否是叶子节点',
  `description` varchar(32) DEFAULT NULL COMMENT '描述',
  `create_id` varchar(32) DEFAULT NULL COMMENT '创建人ID',
  `create_time` varchar(32) DEFAULT NULL COMMENT '创建时间',
  `update_id` varchar(32) DEFAULT NULL COMMENT '更新人ID',
  `update_time` varchar(32) DEFAULT NULL COMMENT '更新时间',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2088 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='字典表';


CREATE TABLE IF NOT EXISTS `baseline_command_keyword` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `keyword` varchar(50) NOT NULL COMMENT '关键字',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8;



CREATE TABLE IF NOT EXISTS `baseline_parameter_blacklist` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `req_body` varchar(50) NOT NULL COMMENT '参数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `baseline_process_list`  (
   `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
   `process_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '进程名称',
   `foreign_key_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '被引用规则id',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '可安装进程清单' ROW_FORMAT = Dynamic;




-- changeset wudi:20210922-flow-001 labels:"工单相关脚本"
SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for ACT_EVT_LOG
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_EVT_LOG` (
  `LOG_NR_` bigint(20) NOT NULL AUTO_INCREMENT,
  `TYPE_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TIME_STAMP_` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DATA_` longblob DEFAULT NULL,
  `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LOCK_TIME_` timestamp NULL DEFAULT NULL,
  `IS_PROCESSED_` tinyint(4) DEFAULT 0,
  PRIMARY KEY (`LOG_NR_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_GE_BYTEARRAY
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_GE_BYTEARRAY` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `BYTES_` longblob DEFAULT NULL,
  `GENERATED_` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`),
  CONSTRAINT `ACT_FK_BYTEARR_DEPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `ACT_RE_DEPLOYMENT` (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_GE_PROPERTY
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_GE_PROPERTY` (
  `NAME_` varchar(64) COLLATE utf8_bin NOT NULL,
  `VALUE_` varchar(300) COLLATE utf8_bin DEFAULT NULL,
  `REV_` int(11) DEFAULT NULL,
  PRIMARY KEY (`NAME_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_ACTINST
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_HI_ACTINST` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `ACT_ID_` varchar(255) COLLATE utf8_bin NOT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `CALL_PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ACT_TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `ASSIGNEE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `START_TIME_` datetime NOT NULL,
  `END_TIME_` datetime DEFAULT NULL,
  `DURATION_` bigint(20) DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_ACT_INST_START` (`START_TIME_`),
  KEY `ACT_IDX_HI_ACT_INST_END` (`END_TIME_`),
  KEY `ACT_IDX_HI_ACT_INST_PROCINST` (`PROC_INST_ID_`,`ACT_ID_`),
  KEY `ACT_IDX_HI_ACT_INST_EXEC` (`EXECUTION_ID_`,`ACT_ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_ATTACHMENT
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_HI_ATTACHMENT` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `URL_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `CONTENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TIME_` datetime DEFAULT NULL,
  PRIMARY KEY (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_COMMENT
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_HI_COMMENT` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TIME_` datetime NOT NULL,
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACTION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `MESSAGE_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `FULL_MSG_` longblob DEFAULT NULL,
  PRIMARY KEY (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_DETAIL
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_HI_DETAIL` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACT_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin NOT NULL,
  `VAR_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TIME_` datetime NOT NULL,
  `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DOUBLE_` double DEFAULT NULL,
  `LONG_` bigint(20) DEFAULT NULL,
  `TEXT_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TEXT2_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_DETAIL_PROC_INST` (`PROC_INST_ID_`),
  KEY `ACT_IDX_HI_DETAIL_ACT_INST` (`ACT_INST_ID_`),
  KEY `ACT_IDX_HI_DETAIL_TIME` (`TIME_`),
  KEY `ACT_IDX_HI_DETAIL_NAME` (`NAME_`),
  KEY `ACT_IDX_HI_DETAIL_TASK_ID` (`TASK_ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_IDENTITYLINK
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_HI_IDENTITYLINK` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `GROUP_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_IDENT_LNK_USER` (`USER_ID_`),
  KEY `ACT_IDX_HI_IDENT_LNK_TASK` (`TASK_ID_`),
  KEY `ACT_IDX_HI_IDENT_LNK_PROCINST` (`PROC_INST_ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_PROCINST
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_HI_PROCINST` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `BUSINESS_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `START_TIME_` datetime NOT NULL,
  `END_TIME_` datetime DEFAULT NULL,
  `DURATION_` bigint(20) DEFAULT NULL,
  `START_USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `START_ACT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `END_ACT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `SUPER_PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DELETE_REASON_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  UNIQUE KEY `PROC_INST_ID_` (`PROC_INST_ID_`),
  KEY `ACT_IDX_HI_PRO_INST_END` (`END_TIME_`),
  KEY `ACT_IDX_HI_PRO_I_BUSKEY` (`BUSINESS_KEY_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_TASKINST
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_HI_TASKINST` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_DEF_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PARENT_TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ASSIGNEE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `START_TIME_` datetime NOT NULL,
  `CLAIM_TIME_` datetime DEFAULT NULL,
  `END_TIME_` datetime DEFAULT NULL,
  `DURATION_` bigint(20) DEFAULT NULL,
  `DELETE_REASON_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `PRIORITY_` int(11) DEFAULT NULL,
  `DUE_DATE_` datetime DEFAULT NULL,
  `FORM_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_TASK_INST_PROCINST` (`PROC_INST_ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_VARINST
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_HI_VARINST` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin NOT NULL,
  `VAR_TYPE_` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `REV_` int(11) DEFAULT NULL,
  `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DOUBLE_` double DEFAULT NULL,
  `LONG_` bigint(20) DEFAULT NULL,
  `TEXT_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TEXT2_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `CREATE_TIME_` datetime DEFAULT NULL,
  `LAST_UPDATED_TIME_` datetime DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_PROCVAR_PROC_INST` (`PROC_INST_ID_`),
  KEY `ACT_IDX_HI_PROCVAR_NAME_TYPE` (`NAME_`,`VAR_TYPE_`),
  KEY `ACT_IDX_HI_PROCVAR_TASK_ID` (`TASK_ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_GROUP
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_ID_GROUP` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_INFO
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_ID_INFO` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `USER_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `VALUE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PASSWORD_` longblob DEFAULT NULL,
  `PARENT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_MEMBERSHIP
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_ID_MEMBERSHIP` (
  `USER_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `GROUP_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`USER_ID_`,`GROUP_ID_`),
  KEY `ACT_FK_MEMB_GROUP` (`GROUP_ID_`),
  CONSTRAINT `ACT_FK_MEMB_GROUP` FOREIGN KEY (`GROUP_ID_`) REFERENCES `ACT_ID_GROUP` (`ID_`),
  CONSTRAINT `ACT_FK_MEMB_USER` FOREIGN KEY (`USER_ID_`) REFERENCES `ACT_ID_USER` (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_USER
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_ID_USER` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `FIRST_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LAST_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `EMAIL_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PWD_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PICTURE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_PROCDEF_INFO
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_PROCDEF_INFO` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `INFO_JSON_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  UNIQUE KEY `ACT_UNIQ_INFO_PROCDEF` (`PROC_DEF_ID_`),
  KEY `ACT_IDX_INFO_PROCDEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_INFO_JSON_BA` (`INFO_JSON_ID_`),
  CONSTRAINT `ACT_FK_INFO_JSON_BA` FOREIGN KEY (`INFO_JSON_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
  CONSTRAINT `ACT_FK_INFO_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RE_DEPLOYMENT
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_RE_DEPLOYMENT` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `DEPLOY_TIME_` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RE_MODEL
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_RE_MODEL` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CREATE_TIME_` timestamp NULL DEFAULT NULL,
  `LAST_UPDATE_TIME_` timestamp NULL DEFAULT NULL,
  `VERSION_` int(11) DEFAULT NULL,
  `META_INFO_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EDITOR_SOURCE_VALUE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EDITOR_SOURCE_EXTRA_VALUE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_MODEL_SOURCE` (`EDITOR_SOURCE_VALUE_ID_`),
  KEY `ACT_FK_MODEL_SOURCE_EXTRA` (`EDITOR_SOURCE_EXTRA_VALUE_ID_`),
  KEY `ACT_FK_MODEL_DEPLOYMENT` (`DEPLOYMENT_ID_`),
  CONSTRAINT `ACT_FK_MODEL_DEPLOYMENT` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `ACT_RE_DEPLOYMENT` (`ID_`),
  CONSTRAINT `ACT_FK_MODEL_SOURCE` FOREIGN KEY (`EDITOR_SOURCE_VALUE_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
  CONSTRAINT `ACT_FK_MODEL_SOURCE_EXTRA` FOREIGN KEY (`EDITOR_SOURCE_EXTRA_VALUE_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RE_PROCDEF
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_RE_PROCDEF` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `KEY_` varchar(255) COLLATE utf8_bin NOT NULL,
  `VERSION_` int(11) NOT NULL,
  `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `RESOURCE_NAME_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DGRM_RESOURCE_NAME_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `HAS_START_FORM_KEY_` tinyint(4) DEFAULT NULL,
  `HAS_GRAPHICAL_NOTATION_` tinyint(4) DEFAULT NULL,
  `SUSPENSION_STATE_` int(11) DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  UNIQUE KEY `ACT_UNIQ_PROCDEF` (`KEY_`,`VERSION_`,`TENANT_ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_EVENT_SUBSCR
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_RU_EVENT_SUBSCR` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `EVENT_TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `EVENT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACTIVITY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `CONFIGURATION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_` timestamp NOT NULL DEFAULT current_timestamp(),
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_EVENT_SUBSCR_CONFIG_` (`CONFIGURATION_`),
  KEY `ACT_FK_EVENT_EXEC` (`EXECUTION_ID_`),
  CONSTRAINT `ACT_FK_EVENT_EXEC` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_EXECUTION
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_RU_EXECUTION` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `BUSINESS_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PARENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `SUPER_EXEC_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `IS_ACTIVE_` tinyint(4) DEFAULT NULL,
  `IS_CONCURRENT_` tinyint(4) DEFAULT NULL,
  `IS_SCOPE_` tinyint(4) DEFAULT NULL,
  `IS_EVENT_SCOPE_` tinyint(4) DEFAULT NULL,
  `SUSPENSION_STATE_` int(11) DEFAULT NULL,
  `CACHED_ENT_STATE_` int(11) DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LOCK_TIME_` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_EXEC_BUSKEY` (`BUSINESS_KEY_`),
  KEY `ACT_FK_EXE_PROCINST` (`PROC_INST_ID_`),
  KEY `ACT_FK_EXE_PARENT` (`PARENT_ID_`),
  KEY `ACT_FK_EXE_SUPER` (`SUPER_EXEC_`),
  KEY `ACT_FK_EXE_PROCDEF` (`PROC_DEF_ID_`),
  CONSTRAINT `ACT_FK_EXE_PARENT` FOREIGN KEY (`PARENT_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_EXE_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
  CONSTRAINT `ACT_FK_EXE_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ACT_FK_EXE_SUPER` FOREIGN KEY (`SUPER_EXEC_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_IDENTITYLINK
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_RU_IDENTITYLINK` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `GROUP_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_IDENT_LNK_USER` (`USER_ID_`),
  KEY `ACT_IDX_IDENT_LNK_GROUP` (`GROUP_ID_`),
  KEY `ACT_IDX_ATHRZ_PROCEDEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_TSKASS_TASK` (`TASK_ID_`),
  KEY `ACT_FK_IDL_PROCINST` (`PROC_INST_ID_`),
  CONSTRAINT `ACT_FK_ATHRZ_PROCEDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
  CONSTRAINT `ACT_FK_IDL_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_TSKASS_TASK` FOREIGN KEY (`TASK_ID_`) REFERENCES `ACT_RU_TASK` (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_JOB
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_RU_JOB` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `LOCK_EXP_TIME_` timestamp NULL DEFAULT NULL,
  `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `RETRIES_` int(11) DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DUEDATE_` timestamp NULL DEFAULT NULL,
  `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
  CONSTRAINT `ACT_FK_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_TASK
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_RU_TASK` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PARENT_TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TASK_DEF_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ASSIGNEE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DELEGATION_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PRIORITY_` int(11) DEFAULT NULL,
  `CREATE_TIME_` timestamp NULL DEFAULT NULL,
  `DUE_DATE_` datetime DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `SUSPENSION_STATE_` int(11) DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `FORM_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_TASK_CREATE` (`CREATE_TIME_`),
  KEY `ACT_FK_TASK_EXE` (`EXECUTION_ID_`),
  KEY `ACT_FK_TASK_PROCINST` (`PROC_INST_ID_`),
  KEY `ACT_FK_TASK_PROCDEF` (`PROC_DEF_ID_`),
  CONSTRAINT `ACT_FK_TASK_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_TASK_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
  CONSTRAINT `ACT_FK_TASK_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_VARIABLE
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ACT_RU_VARIABLE` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin NOT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DOUBLE_` double DEFAULT NULL,
  `LONG_` bigint(20) DEFAULT NULL,
  `TEXT_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TEXT2_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_VARIABLE_TASK_ID` (`TASK_ID_`),
  KEY `ACT_FK_VAR_EXE` (`EXECUTION_ID_`),
  KEY `ACT_FK_VAR_PROCINST` (`PROC_INST_ID_`),
  KEY `ACT_FK_VAR_BYTEARRAY` (`BYTEARRAY_ID_`),
  CONSTRAINT `ACT_FK_VAR_BYTEARRAY` FOREIGN KEY (`BYTEARRAY_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
  CONSTRAINT `ACT_FK_VAR_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_VAR_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



CREATE TABLE IF NOT EXISTS `business_intance` (
  `guid` varchar(255) NOT NULL,
  `busi_args` text,
  `code` varchar(255) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `create_user_id` varchar(255) DEFAULT NULL,
  `deal_peoples` text,
  `name` varchar(255) DEFAULT NULL,
  `process_def_guid` varchar(255) DEFAULT NULL,
  `process_instance_id` varchar(255) DEFAULT NULL,
  `stat_enum` varchar(255) DEFAULT NULL,
  `process_def_name` varchar(255) DEFAULT NULL,
  `create_user_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `business_task` (
  `id` varchar(50) NOT NULL,
  `actions` varchar(255) DEFAULT NULL,
  `busi_id` varchar(255) DEFAULT NULL,
  `busi_key` varchar(255) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `dead_date` datetime DEFAULT NULL,
  `execution_id` varchar(255) DEFAULT NULL,
  `related_infos` varchar(255) DEFAULT NULL,
  `task_code` varchar(255) DEFAULT NULL,
  `task_defind_name` varchar(255) DEFAULT NULL,
  `task_define_key` varchar(255) DEFAULT NULL,
  `task_id` varchar(255) DEFAULT NULL,
  `task_type` varchar(255) DEFAULT NULL,
  `instance_guid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjt7k6adjegh94q4yq1e5ma96f` (`instance_guid`),
  CONSTRAINT `FKjt7k6adjegh94q4yq1e5ma96f` FOREIGN KEY (`instance_guid`) REFERENCES `business_intance` (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `business_task_candidate` (
  `id` varchar(50) NOT NULL,
  `busi_task_id` varchar(255) DEFAULT NULL,
  `candidate` varchar(255) DEFAULT NULL,
  `candidate_name` varchar(255) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `task_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3ha73vxftouqujsx8ud16qyb0` (`busi_task_id`),
  CONSTRAINT `FK3ha73vxftouqujsx8ud16qyb0` FOREIGN KEY (`busi_task_id`) REFERENCES `business_task` (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `business_task_log` (
  `id` varchar(255) NOT NULL,
  `action` varchar(255) DEFAULT NULL,
  `advice` varchar(255) DEFAULT NULL,
  `operation` varchar(255) DEFAULT NULL,
  `people_id` varchar(255) DEFAULT NULL,
  `people_name` varchar(255) DEFAULT NULL,
  `process_instance_id` varchar(255) DEFAULT NULL,
  `process_key` varchar(255) DEFAULT NULL,
  `task_defind_name` varchar(255) DEFAULT NULL,
  `task_define_key` varchar(255) DEFAULT NULL,
  `time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `myticket_inner_form` (
  `guid` varchar(255) NOT NULL,
  `form_infos_guid` varchar(255) DEFAULT NULL,
  `formtype` varchar(255) DEFAULT NULL,
  `parent_id` varchar(255) DEFAULT NULL,
  `process_desc` varchar(255) DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `myticket_template` (
  `guid` varchar(255) NOT NULL,
  `delete_flag` bit(1) NOT NULL,
  `form_data` text,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;




CREATE TABLE IF NOT EXISTS `my_ticket` (
  `guid` varchar(255) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(255) DEFAULT NULL,
  `deploy_id` varchar(255) DEFAULT NULL,
  `form_data` text,
  `form_type` varchar(255) DEFAULT NULL,
  `inner_guid` varchar(255) DEFAULT NULL,
  `mark` varchar(255) DEFAULT NULL,
  `model_id` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `order_num` int(11) DEFAULT NULL,
  `privildge_type` varchar(255) DEFAULT NULL,
  `ticket_status` varchar(255) DEFAULT NULL,
  `ticket_version` int(10) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(255) DEFAULT NULL,
  `used` bit(1) DEFAULT NULL,
  `template_guid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guid`),
  KEY `FKi94iiw03dmmqjhu3qi5kd2fqt` (`template_guid`),
  CONSTRAINT `FKi94iiw03dmmqjhu3qi5kd2fqt` FOREIGN KEY (`template_guid`) REFERENCES `myticket_template` (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;




CREATE TABLE IF NOT EXISTS `my_ticket_privildge` (
  `guid` varchar(255) NOT NULL,
  `data_guid` varchar(255) DEFAULT NULL,
  `my_ticket_guid` varchar(255) DEFAULT NULL,
  `user_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `t_event_pub` (
  `id` bigint(19) NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `delete_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `biz_type` varchar(64) NOT NULL COMMENT '业务类型',
  `event_status` tinyint(4) NOT NULL COMMENT '事件状态, -128为未知错误, -3为NOT_FOUND(找不到exchange), -2为NO_ROUTE(找到exchange但是找不到queue), -1为FAILED(如类型尚未注册等的业务失败), 0为NEW(消息落地), 1为PENDING, 2为DONE',
  `payload` varchar(1024) NOT NULL COMMENT '请求时的描述负载',
  `lock_version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '锁版本号',
  `pub_guid` varchar(64) NOT NULL COMMENT '消息发布时的GUID，用于消费者作去重',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_event_pub_pg` (`pub_guid`),
  KEY `idx_event_pub_es_ut` (`event_status`,`update_time`)
  ) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;



CREATE TABLE IF NOT EXISTS `collaboration_task` (
  `guid` varchar(50) NOT NULL,
  `task_type` varchar(255) DEFAULT NULL,
  `assign` varchar(255) DEFAULT NULL,
  `assgin_time` datetime DEFAULT NULL,
  `task_status` varchar(255) DEFAULT NULL,
  `ticket_content` text,
  `map_region_info` text,
  `ticket_id` varchar(50) DEFAULT NULL,
  `collaboration_ticket_id` varchar(50) DEFAULT NULL,
  `up_ip` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE business_intance ADD COLUMN `deadline_date`datetime DEFAULT NULL;
ALTER TABLE business_intance ADD COLUMN `finish_date`datetime DEFAULT NULL;


ALTER TABLE business_task_log ADD COLUMN `finish_date`datetime DEFAULT NULL;
ALTER TABLE business_task_log ADD COLUMN `deadline_date`datetime DEFAULT NULL;


ALTER TABLE business_task ADD COLUMN `deadline_date` datetime DEFAULT NULL ;


ALTER TABLE my_ticket ADD COLUMN `deadline_time`int(4) DEFAULT NULL;
ALTER TABLE my_ticket ADD COLUMN `canedit_deadline` tinyint(2) DEFAULT NULL;

-- changeset wudi:20200108-flow-001 labels:"工单记录日志"
ALTER TABLE `business_task_log`
  ADD COLUMN `params`  text NULL AFTER `finish_date`;

ALTER TABLE `my_ticket`
  ADD COLUMN `ticket_name_rule`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `canedit_deadline`;
ALTER TABLE `my_ticket`
  ADD COLUMN `can_edit_ticket_name`  tinyint(10) NULL AFTER `ticket_name_rule`;


-- changeset lps:202004-04-flow-001 labels:"增加字段存储空间"
ALTER TABLE `act_hi_taskinst`  MODIFY `FORM_KEY_` VARCHAR(2000);

-- changeset lps:202004-04-flow-002 labels:"增加事件配置表"
CREATE TABLE IF NOT EXISTS `event_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `event_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '事件名称',
  `config_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配置事件id',
  `config_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '配置事件路径',
  `attachment` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '附件模板',
  PRIMARY KEY (`id`) USING BTREE
  ) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- changeset lps:202004-09-flow-001 labels:"增加字段存储空间"
ALTER TABLE `act_hi_taskinst` MODIFY `FORM_KEY_` TEXT;
ALTER TABLE `act_ru_task` MODIFY `FORM_KEY_` TEXT;

-- changeset lps:202004-23-flow-001 labels:"增加字段"
ALTER TABLE `business_task_candidate`
  ADD COLUMN `assign_type`  tinyint(1) DEFAULT 1;




-- changeset lps:2021-01-13-flow-001 labels:"增加会签分权字段"
ALTER TABLE `business_intance`
  ADD COLUMN `context_key`  varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `finish_date`,
  ADD COLUMN `context_id`  text NULL AFTER `context_key`;


ALTER TABLE `business_task`
  ADD COLUMN `context_key`  varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `deadline_date`,
  ADD COLUMN `context_id`  text NULL AFTER `context_key`;


ALTER TABLE `business_task_log`
  ADD COLUMN `context_key`  varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `deadline_date`,
  ADD COLUMN `context_id`  text NULL AFTER `context_key`;


-- changeset lps:2021-01-14-flow-001 labels:"增加会签分权字段"

ALTER TABLE `business_task`
  ADD COLUMN `context_label`  varchar(500) NULL AFTER `context_id`;


ALTER TABLE `business_intance`
  ADD COLUMN `context_label`  varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `context_id`;

ALTER TABLE `business_task_log`
  ADD COLUMN `context_label`  varchar(500) NULL AFTER `context_id`;

ALTER TABLE `business_task_log`
  MODIFY COLUMN `advice`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `action`;

-- changeset lrz:202010830--my_ticket-004 labels:流程表新增字段 2021-08-30
alter table my_ticket add flow_content json  DEFAULT NULL COMMENT '流程所有节点信息';
alter table my_ticket add node_status varchar(255) DEFAULT NULL COMMENT '节点状态';




-- changeset songjia:20210922-apiAsset-001 labels:资产相关脚本
CREATE TABLE  IF NOT EXISTS  `asset_ip_set` (
  `guid` varchar(255) NOT NULL,
  `assetId` varchar(50) DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `main_ip` bigint(10) DEFAULT NULL COMMENT '是否是主ip',
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `device_table` (
  `IP` varchar(64) NOT NULL,
  `MACAddress` varchar(64) DEFAULT NULL,
  `MACVendor` varchar(64) DEFAULT NULL,
  `OS` mediumtext DEFAULT NULL,
  `OSSrvData` mediumtext,
  `OSGuess` mediumtext,
  `OSFingerPrint` mediumtext,
  `ComputerName` varchar(64) DEFAULT NULL,
  `Workgroup` varchar(64) DEFAULT NULL,
  `OSStartTime` varchar(64) DEFAULT NULL,
  `SystemTime` varchar(64) DEFAULT NULL,
  `Uptime` varchar(64) DEFAULT NULL,
  `TraceRoute` varchar(256) CHARACTER SET tis620 DEFAULT NULL,
  `CheckTime` datetime DEFAULT NULL,
  PRIMARY KEY (`IP`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;




CREATE TABLE IF NOT EXISTS  `asset_risk_value_day` (
  `guid` varchar(36) NOT NULL,
  `asset_guid` varchar(50) NOT NULL COMMENT '资产guid',
  `in_date` varchar(18) DEFAULT NULL COMMENT '记录时间',
  `asset_worth` int(11) DEFAULT NULL COMMENT '资产价值',
  `weak_ness_worth` int(11) DEFAULT NULL COMMENT '弱点权值',
  `threatenfreq_worth` int(11) DEFAULT NULL COMMENT '事件威胁频率的权值',
  `risk_worth` int(11) DEFAULT NULL COMMENT '风险的权值',
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE  IF NOT EXISTS `asset_risk_value_month` (
  `guid` varchar(36) NOT NULL,
  `asset_guid` varchar(50) NOT NULL COMMENT '资产guid',
  `in_date` varchar(16) DEFAULT NULL COMMENT '记录时间',
  `asset_worth` int(11) DEFAULT NULL COMMENT '资产价值',
  `weak_ness_worth` int(11) DEFAULT NULL COMMENT '弱点权值',
  `threatenfreq_worth` int(11) DEFAULT NULL COMMENT '事件威胁频率的权值',
  `risk_worth` int(11) DEFAULT NULL COMMENT '风险的权值',
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE  IF NOT EXISTS `asset_risk_value_year` (
  `guid` varchar(36) NOT NULL,
  `asset_guid` varchar(50) NOT NULL COMMENT '资产guid',
  `in_date` varchar(16) DEFAULT NULL COMMENT '记录时间',
  `asset_worth` int(11) DEFAULT NULL COMMENT '资产价值',
  `weak_ness_worth` int(11) DEFAULT NULL COMMENT '弱点权值',
  `threatenfreq_worth` int(11) DEFAULT NULL COMMENT '事件威胁频率的权值',
  `risk_worth` int(11) DEFAULT NULL COMMENT '风险的权值',
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE  IF NOT EXISTS `asset_riskvalue` (
  `asset_guid` varchar(50) NOT NULL,
  `last_threaten_time` json DEFAULT NULL COMMENT '最后产生威胁时间',
  `weak_ness_worth` int(11) DEFAULT NULL COMMENT '弱点权值',
  `threatenfreq_worth` int(11) DEFAULT NULL COMMENT '事件威胁频率的权值',
  `risk_worth` int(11) DEFAULT NULL COMMENT '风险的权值',
  `asset_worth` int(11) DEFAULT NULL COMMENT '资产权重',
  `asset_name` varchar(100) DEFAULT NULL COMMENT '资产名称',
  `ip` varchar(24) DEFAULT NULL COMMENT 'ip地址',
  PRIMARY KEY (`asset_guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `source_img` (
  `Guid` varchar(255) NOT NULL,
  `ImgUrl` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `topo_canvas` (
  `Guid` varchar(32) NOT NULL COMMENT '画布表Guid',
  `CanvasName` varchar(255) DEFAULT NULL COMMENT '画布名称',
  `CanvasBgColor` varchar(255) DEFAULT NULL COMMENT '画布背景颜色',
  `CanvasBgImage` varchar(255) DEFAULT NULL COMMENT '画布背景图片',
  `CanvasKey` varchar(255) DEFAULT NULL COMMENT '画布key',
  `CanvasTop` int(1) DEFAULT '0' COMMENT '是否置顶 1是，0是',
  PRIMARY KEY (`Guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `topo_edages` (
  `id` varchar(50) NOT NULL ,
  `topo_id` varchar(255) DEFAULT NULL COMMENT '拓扑数据标识',
  `snode` varchar(255) DEFAULT NULL COMMENT '源节点',
  `tnode` varchar(255) DEFAULT NULL COMMENT '目标节点',
  `width` int(11) DEFAULT NULL COMMENT '连线宽度',
  `color` varchar(255) DEFAULT NULL COMMENT '连线颜色',
  `toolTip` varchar(255) DEFAULT NULL COMMENT '鼠标悬浮提示',
  `edgeType` varchar(255) DEFAULT NULL COMMENT '连线类型',
  `from_arrow` varchar(255) DEFAULT NULL COMMENT '起始连线箭头',
  `to_Arrow` varchar(255) DEFAULT NULL COMMENT '结束连线箭头',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  `tag` varchar(255) DEFAULT NULL COMMENT 'tag标识',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `topo_nodes` (
  `id` varchar(50) NOT NULL,
  `topo_id` varchar(255) DEFAULT NULL COMMENT '拓扑数据标识',
  `node_id` varchar(255) DEFAULT NULL COMMENT '节点标识',
  `node_Name` varchar(255) DEFAULT NULL COMMENT '节点名称',
  `nodeX` double DEFAULT NULL COMMENT '横坐标',
  `nodeY` double DEFAULT NULL COMMENT '纵坐标',
  `tool_tip` varchar(255) DEFAULT NULL COMMENT '鼠标悬浮提示',
  `label_color` varchar(255) DEFAULT NULL COMMENT '文字颜色',
  `node_image` varchar(255) DEFAULT NULL COMMENT '节点图片',
  `node_type` varchar(255) DEFAULT NULL COMMENT '节点类型',
  `sub_canvas_Id` varchar(255) DEFAULT NULL COMMENT '关联的子画布ID',
  `group_id` varchar(36) DEFAULT NULL COMMENT '分组id',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `topo_node_group` (
  `guid` varchar(36) NOT NULL,
  `group_name` varchar(50) DEFAULT NULL COMMENT '分组名称',
  `title` varchar(100) DEFAULT NULL COMMENT '分组标题',
  `image` varchar(150) DEFAULT NULL COMMENT '图片',
  `topo_id` varchar(50) DEFAULT NULL,
  `x` double DEFAULT NULL,
  `y` double DEFAULT NULL,
  PRIMARY KEY (`guid`),
  KEY `topo_id` (`topo_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `network_topology` (
  `guid` varchar(36) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `swip` varchar(18) DEFAULT NULL,
  `ifindex` int(11) DEFAULT NULL,
  `swport` varchar(50) DEFAULT NULL,
  `linkip` varchar(18) DEFAULT NULL,
  `linkifindex` int(11) DEFAULT NULL,
  `linkport` varchar(8) DEFAULT NULL,
  `flag` int(11) DEFAULT NULL,
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `cabinet` (
  `guid` varchar(36) NOT NULL,
  `code` varchar(255) DEFAULT NULL COMMENT '机柜编码',
  `type` varchar(255) DEFAULT NULL COMMENT '机架类型',
  `height` int(11) DEFAULT NULL COMMENT 'U口数',
  `roomGuid` varchar(36) DEFAULT NULL COMMENT '所在机房',
  `marginTop` int(11) DEFAULT NULL COMMENT '机房中距离上面的机柜个数',
  `marginLeft` int(11) DEFAULT NULL COMMENT '机房中距离最左边的机柜个数',
  `roomWidth` int(11) DEFAULT '1' COMMENT '占机柜个数，宽度方向',
  `roomHeight` int(11) DEFAULT '1' COMMENT '占机柜个数，高度方向',
  `positionX` double DEFAULT NULL COMMENT 'X坐标',
  `positionY` double DEFAULT NULL COMMENT 'Y坐标',
  `positionZ` double DEFAULT NULL COMMENT 'Z坐标',
  `moved` int(1) DEFAULT NULL COMMENT '是否移动过，0没有1有',
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `machineroom` (
  `guid` varchar(36) NOT NULL,
  `code` varchar(255) DEFAULT NULL COMMENT '机房编码，唯一且必填',
  `width` int(11) DEFAULT NULL COMMENT '宽度，可放单位机柜的个数',
  `height` int(11) DEFAULT NULL COMMENT '长度，可放单位机柜的个数',
  `sort` int(11) DEFAULT NULL COMMENT '排序字段',
  `openMonitor` int(1) DEFAULT NULL COMMENT '是否开启监控0开启1关闭',
  `pushMonitor` int(1) DEFAULT NULL COMMENT '是否开启推送监控信息0开启1关闭',
  `openAnalysis` int(1) DEFAULT NULL COMMENT '是否开启分析0开启1关闭',
  `pushAnalysis` int(1) DEFAULT NULL COMMENT '是否开启分析推送0开启1关闭',
  `showWall` int(1) DEFAULT NULL COMMENT '是否显示墙空调等0显示1不显示',
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;




CREATE TABLE IF NOT EXISTS `asset_operation_log` (
  `guid` varchar(36) NOT NULL,
  `asset_guid` varchar(36) DEFAULT NULL,
  `operation_type` varchar(36) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `operate_time` datetime DEFAULT NULL,
  PRIMARY KEY (`guid`)
  ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;





CREATE TABLE  IF NOT EXISTS `vul_info_history` (
  `guid` varchar(36) NOT NULL,
  `import_index` int(11) NOT NULL COMMENT '序号',
  `vul_id` varchar(255) NOT NULL,
  `cve_no` varchar(255) DEFAULT NULL,
  `descript` text,
  `file_guid` varchar(255) DEFAULT NULL,
  `ignore_value` bit(1) DEFAULT NULL,
  `import_time` datetime DEFAULT NULL,
  `import_type` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `port` varchar(255) DEFAULT NULL,
  `risk_level` varchar(255) DEFAULT NULL,
  `scaninfo` varchar(255) DEFAULT NULL,
  `solution` varchar(255) DEFAULT NULL,
  `solve_remark` varchar(255) DEFAULT NULL,
  `vul_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guid`)
  ) ENGINE=MyISAM DEFAULT CHARSET=utf8;



CREATE TABLE IF NOT EXISTS `asset_type_group` (
  `Guid` varchar(32) NOT NULL,
  `TreeCode` varchar(255) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Name_en` varchar(255) DEFAULT NULL,
  `uniqueCode` varchar(255) NOT NULL,
  `Icon` varchar(100) DEFAULT NULL,
  `status` INT(1) DEFAULT 0 COMMENT '状态：1 禁用, 0启用 ',
  `orderNum` int(11) DEFAULT NULL COMMENT '排序字段',
  PRIMARY KEY (`Guid`),
  UNIQUE KEY `uniqueCode` (`uniqueCode`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE IF NOT EXISTS `asset_type` (
  `Guid` varchar(32) NOT NULL,
  `TreeCode` varchar(255) NOT NULL,
  `uniqueCode` varchar(255) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Name_en` varchar(255) DEFAULT NULL,
  `Icon` varchar(100) DEFAULT NULL,
  `monitorProtocols` varchar(255) DEFAULT NULL,
  `status` INT(1) DEFAULT 0 COMMENT '状态：1 禁用, 0启用 ',
  `orderNum` int(11) DEFAULT NULL COMMENT '排序字段',
  PRIMARY KEY (`Guid`),
  UNIQUE KEY `uniqueCode` (`uniqueCode`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `asset_type_sno` (
  `Guid` varchar(32) NOT NULL,
  `TreeCode` varchar(255) NOT NULL,
  `uniqueCode` varchar(255) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Name_en` varchar(255) DEFAULT NULL,
  `Icon` varchar(100) DEFAULT NULL,
  `canSyslog` varchar (30) DEFAULT NULL,
  `canMonitor` varchar (30) DEFAULT NULL,
  `canRCtrl` varchar (30) DEFAULT NULL,
  `status` INT(1) DEFAULT 0 COMMENT '状态：1 禁用, 0启用 ',
  `orderNum` int(11) DEFAULT NULL COMMENT '排序字段',
  PRIMARY KEY (`Guid`),
  UNIQUE KEY `uniqueCode` (`uniqueCode`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `asset` (
  `Guid` varchar(50) NOT NULL,
  `Name` varchar(50) NOT NULL,
  `Name_en` varchar(100) DEFAULT NULL,
  `IP` varchar(255) DEFAULT NULL,
  `securityGuid` varchar(50) DEFAULT NULL,
  `ipNum` bigint(20) DEFAULT NULL,
  `Type_Guid` varchar(50) NOT NULL,
  `Type_Sno_Guid` varchar(32) DEFAULT NULL,
  `Version_info` varchar(255) DEFAULT NULL,
  `Tags` varchar(255) DEFAULT NULL COMMENT '资产标签，不同标签之间以,隔开',
  `CreateTime` datetime NOT NULL,
  `typeUnicode` VARCHAR(32) DEFAULT NULL,
  `snoUnicode` VARCHAR(32) DEFAULT NULL,
  `mac` VARCHAR(128) DEFAULT NULL,
  `employee_Code1` VARCHAR(2048) DEFAULT NULL,
  `employee_Code2` VARCHAR(2048) DEFAULT NULL,
  `monitor` VARCHAR(128) DEFAULT NULL COMMENT '性能监控',
  `special` VARCHAR(128) DEFAULT NULL COMMENT '采集事件是否开启',
  `canMonitor` VARCHAR(128) DEFAULT NULL COMMENT '监控开启',
  `canRCtrl` VARCHAR(128) DEFAULT NULL COMMENT '远程控制',
  `worth` VARCHAR(128) DEFAULT NULL COMMENT '资产价值',
  `secrecy` VARCHAR(128) DEFAULT NULL COMMENT '机密性权值',
  `integrity` VARCHAR(128) DEFAULT NULL COMMENT '完整性权值',
  `availability` VARCHAR(128) DEFAULT NULL COMMENT '可用性权值',
  `protocol` VARCHAR(32) DEFAULT NULL COMMENT '监控协议',
  `assetNum` VARCHAR(128) DEFAULT NULL COMMENT '资产编号',
  `assetUse` VARCHAR(1000) DEFAULT NULL COMMENT '资产用途',
  `location` VARCHAR(255) DEFAULT NULL COMMENT '物理位置',
  `AssetDescribe` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `cabinetGuid` varchar(32) DEFAULT NULL COMMENT '所属机柜guid',
  `marginBottom` int(20) DEFAULT '0' COMMENT '距离底部高度',
  `height` int(20) DEFAULT '0' COMMENT '占U口个数',
  `lng` decimal(19,8) DEFAULT NULL COMMENT '经度',
  `lat` decimal(19,8) DEFAULT NULL COMMENT '纬度',
  `gatewayName` varchar(32) DEFAULT NULL COMMENT '网关名称',
  `gatewayNum` varchar(32) DEFAULT NULL COMMENT '网关序列号',
  `gatewayUser` varchar(32) DEFAULT NULL COMMENT '主管名称',
  `gatewayDepartment` varchar(32) DEFAULT NULL COMMENT '主管部门',
  `phoneNum` varchar(32) DEFAULT NULL COMMENT '电话号码',
  `remarkInfo` varchar(32) DEFAULT NULL COMMENT '说明',
  PRIMARY KEY (`Guid`)
  ) ENGINE=MYISAM DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `asset_extend` (
  `assetGuid` varchar(50) NOT NULL,
  `extendInfos` text NOT NULL,
  PRIMARY KEY (`assetGuid`)
  ) ENGINE=MYISAM DEFAULT CHARSET=utf8;


CREATE TABLE  IF NOT EXISTS  `asset_threshold` (
  `guid` varchar(36) NOT NULL,
  `comparison_key` varchar(20) NOT NULL COMMENT '大于，小于，大于等于，小于等于，范围值',
  `first_threshold` varchar(50) NOT NULL,
  `second_threshold` varchar(50) DEFAULT NULL,
  `unit` varchar(8) NOT NULL COMMENT '百分比、数值',
  `description` varchar(100) DEFAULT NULL,
  `attribute_name` varchar(50) DEFAULT NULL COMMENT '属性名称',
  `attribute_description` varchar(100) DEFAULT NULL COMMENT '属性描述',
  `asset_type_guid` varchar(36) NOT NULL,
  PRIMARY KEY (`guid`)
  ) ENGINE=MYISAM DEFAULT CHARSET=utf8mb4;

CREATE TABLE   IF NOT EXISTS  `asset_type_template` (
  `asset_type_guid` varchar(36) NOT NULL,
  `delete_flag` bit(1) DEFAULT NULL,
  `form_data` json DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `key_data` json DEFAULT NULL,
  PRIMARY KEY (`asset_type_guid`)
  ) ENGINE=MYISAM DEFAULT CHARSET=utf8;



-- changeset songjia:20190516-apiAsset-002 labels:资产网络链接关系
CREATE TABLE   IF NOT EXISTS   `asset_network_link_relations`(
  `guid` VARCHAR(36) NOT NULL,
  `left_asset_guid` VARCHAR(36),
  `left_ifindex` INT,
  `left_ifname` VARCHAR(50),
  `right_asset_guid` VARCHAR(36),
  `right_ifindex` INT,
  `right_ifname` VARCHAR(50),
  PRIMARY KEY (`guid`)
  ) ENGINE=MYISAM CHARSET=utf8mb4;


-- changeset songjia:20190429-apiAsset-003 labels:机构表更新
ALTER TABLE `asset`
  ADD COLUMN `org` VARCHAR(50) NULL   COMMENT '机构' ;
ALTER TABLE `asset`
  ADD COLUMN `core` BOOL  DEFAULT 0  NULL   COMMENT '核心资产';


-- changeset songjia:20190515-apiAsset-004 labels:增加环境脆弱性
ALTER TABLE `asset_riskvalue`
  ADD COLUMN `environment_vul_worth` INT(11)   DEFAULT 0    NULL   COMMENT '环境脆弱性' ;

-- changeset songjia:20190520-apiAsset-005 labels:风险天月年表，增加环境风险值
ALTER TABLE `asset_risk_value_day`
  ADD COLUMN `environment_vul_worth` INT(11) NULL   COMMENT '环境风险值' ;

ALTER TABLE `asset_risk_value_month`
  ADD COLUMN `environment_vul_worth` INT(11) NULL   COMMENT '环境风险值' ;

ALTER TABLE `asset_risk_value_year`
  ADD COLUMN `environment_vul_worth` INT(11) NULL   COMMENT '环境风险值' ;


-- changeset songjia:20190528-apiAsset-006 labels:增加资产风险
ALTER TABLE `asset_riskvalue`
  ADD COLUMN `risk_threat_worth`  int(11) NULL DEFAULT 0 AFTER `environment_vul_worth`,
  ADD COLUMN `risk_vul_worth`  int(11) NULL DEFAULT 0 AFTER `risk_threat_worth`,
  ADD COLUMN `risk_value`  int(11) NULL DEFAULT 0 AFTER `risk_vul_worth`;

ALTER TABLE `asset_risk_value_day`
  ADD COLUMN `risk_threat_worth`  int(11) NULL DEFAULT 0 AFTER `environment_vul_worth`,
  ADD COLUMN `risk_vul_worth`  int(11) NULL DEFAULT 0 AFTER `risk_threat_worth`,
  ADD COLUMN `risk_value`  int(11) NULL DEFAULT 0 AFTER `risk_vul_worth`;


ALTER TABLE `asset_risk_value_month`
  ADD COLUMN `risk_threat_worth`  int(11) NULL DEFAULT 0 AFTER `environment_vul_worth`,
  ADD COLUMN `risk_vul_worth`  int(11) NULL DEFAULT 0 AFTER `risk_threat_worth`,
  ADD COLUMN `risk_value`  int(11) NULL DEFAULT 0 AFTER `risk_vul_worth`;

ALTER TABLE `asset_risk_value_year`
  ADD COLUMN `risk_threat_worth`  int(11) NULL DEFAULT 0 AFTER `environment_vul_worth`,
  ADD COLUMN `risk_vul_worth`  int(11) NULL DEFAULT 0 AFTER `risk_threat_worth`,
  ADD COLUMN `risk_value`  int(11) NULL DEFAULT 0 AFTER `risk_vul_worth`;


-- changeset songjia:20190610-apiAsset-007 labels:增加周期作业表
CREATE TABLE   IF NOT EXISTS `cron_config`(
  `cron_guid` VARCHAR(50) NOT NULL,
  `cron_class` VARCHAR(200) COMMENT '包名+.+类名',
  `cron` VARCHAR(30),
  `cron_remark` VARCHAR(100),
  `cron_status` BOOL COMMENT '是否开启',
  `class_parma` VARCHAR(150),
  PRIMARY KEY (`cron_guid`)
  );


-- changeset songjia:20190614-apiAsset-009 labels:终端同步审计数据
CREATE TABLE   IF NOT EXISTS `device_change_log`(
  `guid` VARCHAR(36) NOT NULL,
  `create_time` datetime,
  `old_data` VARCHAR(1000) COMMENT 'json',
  `new_data` VARCHAR(1000) COMMENT 'json',
  `operation_type` INT COMMENT '操作类型 增删改 123',
  `batch_guid` VARCHAR(36) COMMENT '日志记录的批次',
  PRIMARY KEY (`guid`)
  );

-- changeset songjia:20190617-apiAsset-010 labels:终端同步审计批次记录
CREATE TABLE  IF NOT EXISTS `device_change_batch`(
  `guid` VARCHAR(36) NOT NULL,
  `create_time` datetime,
  `total_count` INT COMMENT '本次同步之后数据总量',
  `del_count` INT COMMENT '本次删除数据量',
  `add_count` INT COMMENT '本次新增数据量',
  `edit_count` INT COMMENT '本次修改数据量',
  PRIMARY KEY (`guid`)
  );


-- changeset songjia:20190624-apiAsset-011 labels:修改之前表的引擎
ALTER TABLE `device_change_log`
  ENGINE=MYISAM;


-- changeset songjia:20190911-apiAsset-011 labels:增加topo外链配置信息
CREATE TABLE   IF NOT EXISTS  `topo_outside_setting`(
  `guid` VARCHAR(36) NOT NULL,
  `topo_guid` VARCHAR(36) NOT NULL,
  `permission_type` INT NOT NULL COMMENT '0(任何人),1(用户),2(角色)',
  `ids` VARCHAR(1000),
  `time_type` INT NOT NULL,
  `begin_time` DATETIME COMMENT '结束时间',
  `end_time` DATETIME COMMENT '开始时间',
  PRIMARY KEY (`guid`)
  );

-- changeset yangming:20191126-apiAsset-012 labels:增加漏洞历史表批次信息
ALTER TABLE `vul_info_history`
  ADD COLUMN `import_batch` VARCHAR (100) NULL COMMENT '当前批次信息' AFTER `vul_name`;


-- changeset songjia:20191212-apiAsset-013 labels:修改资产表引擎
ALTER TABLE `asset`
  ENGINE=MYISAM;

-- changeset yangming:20191225-apiAsset-014 labels:增加漏洞历史表索引
ALTER TABLE `vul_info_history` ADD INDEX (`import_batch`), ADD INDEX (`ip`);

-- changeset songjia:20191227-apiAsset-012 labels:asset表增加应用系统相关信息
ALTER TABLE  `asset`
  ADD COLUMN `app_id` VARCHAR(100) NULL   COMMENT '应用系统id',
  ADD COLUMN `app_name` VARCHAR(200) NULL   COMMENT '应用系统名称';





-- changeset songjia:20200107-apiAsset-013 labels:增加拓扑引用表
CREATE TABLE  IF NOT EXISTS  `topo_canvas_quote`(
  `guid` VARCHAR(36) NOT NULL,
  `canvas_guid` VARCHAR(36) NOT NULL COMMENT '画布guid',
  `quote_guid` VARCHAR(36) NOT NULL COMMENT '被引用的画布guid',
  `point_x` DOUBLE DEFAULT 0 COMMENT '作标x',
  `point_y` DOUBLE DEFAULT 0 COMMENT '作标y',
  PRIMARY KEY (`guid`)
  );

-- changeset yangming:20200403-apiAsset-01 labels:增加拓扑类型字段
ALTER TABLE `topo_canvas` ADD COLUMN `topo_type` VARCHAR(50) NULL COMMENT '拓扑类型' AFTER `CanvasTop`;

-- changeset yangming:20200403-apiAsset-02 labels:增加拓扑类型字段-数据修复
UPDATE topo_canvas SET topo_canvas.topo_type ='networkTopo'  WHERE topo_canvas.`Guid` = '1';
UPDATE topo_canvas SET topo_canvas.topo_type ='tenantTopo'  WHERE topo_canvas.`Guid`  LIKE 'Tenant_%';
UPDATE topo_canvas SET topo_canvas.topo_type ='basicTopo'  WHERE topo_canvas.`topo_type`  IS NULL;

-- changeset yangming:20200407-apiAsset-01 labels:增加应用系统表
CREATE TABLE  IF NOT EXISTS   `sysdomain_combination` (
  `guid` varchar(36) COLLATE utf8_bin NOT NULL,
  `sys_name` varchar(36) COLLATE utf8_bin DEFAULT NULL COMMENT '名字',
  `parent_id` varchar(36) COLLATE utf8_bin DEFAULT NULL COMMENT '父节点',
  `iportance_level` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '重要级别',
  `maintainer_id` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '责任人Id',
  `maintainer` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '责任人',
  `description` text COLLATE utf8_bin COMMENT '业务描述',
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- changeset songjia:20200416-apiAsset-016 labels:增加公式配置表
CREATE TABLE  IF NOT EXISTS  `formula` (
  `guid` varchar(36) NOT NULL,
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `title` varchar(50) DEFAULT NULL COMMENT '标题',
  `remark` varchar(300) DEFAULT NULL COMMENT '说明',
  `formula` varchar(150) NOT NULL COMMENT '公式',
  `default_value` double NOT NULL DEFAULT '0' COMMENT '默认值',
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- changeset songjia:20200730-apiAsset-017 labels:资产类型添加字段  内置数据

ALTER TABLE  `asset_type`
  ADD COLUMN `predefine` BOOL  DEFAULT 0  NOT NULL   COMMENT '是否内置数据' ;


ALTER TABLE   `asset_type_group`
  ADD COLUMN `predefine` BOOL  DEFAULT 0  NOT NULL   COMMENT '是否内置数据'  ;

ALTER TABLE    `asset_type_sno`
  ADD COLUMN `predefine` BOOL  DEFAULT 0  NOT NULL   COMMENT '是否内置数据'  ;


-- changeset songjia:20200811-apiAsset-018 labels:资产标签

CREATE TABLE  IF NOT EXISTS  `asset_labels` (
  `asset_guid` varchar(36) NOT NULL COMMENT '资产guid',
  `update_time` datetime DEFAULT NULL,
  `failed_status` int(11) DEFAULT NULL COMMENT '失陷标签',
  `link_sys` int(11) DEFAULT NULL COMMENT '是否关联应用系统',
  `link_vul` int(11) DEFAULT NULL COMMENT '是否存在漏洞',
  PRIMARY KEY (`asset_guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE  IF NOT EXISTS  `asset_labels_day` (
  `guid` varchar(36) NOT NULL,
  `asset_guid` varchar(36) DEFAULT NULL,
  `in_date` varchar(16) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `failed_status` int(11) DEFAULT NULL,
  `link_sys` int(11) DEFAULT NULL COMMENT '是否关联应用系统',
  `link_vul` int(11) DEFAULT NULL COMMENT '是否存在漏洞',
  PRIMARY KEY (`guid`),
  KEY `in_date` (`in_date`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE  IF NOT EXISTS `asset_labels_log` (
  `guid` varchar(36) NOT NULL,
  `asset_guid` varchar(36) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `label_type` varchar(40) DEFAULT NULL,
  `label_value` int(20) DEFAULT NULL,
  PRIMARY KEY (`guid`),
  KEY `asset_guid` (`asset_guid`,`label_type`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE  IF NOT EXISTS `asset_labels_month` (
  `guid` varchar(36) NOT NULL,
  `asset_guid` varchar(36) DEFAULT NULL,
  `in_date` varchar(16) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `failed_status` int(11) DEFAULT NULL,
  `link_sys` int(11) DEFAULT NULL,
  `link_vul` int(11) DEFAULT NULL,
  PRIMARY KEY (`guid`),
  KEY `in_date` (`in_date`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE  IF NOT EXISTS `asset_labels_year` (
  `guid` varchar(36) NOT NULL,
  `asset_guid` varchar(36) DEFAULT NULL,
  `in_date` varchar(16) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `failed_status` int(11) DEFAULT NULL,
  `link_sys` int(11) DEFAULT NULL,
  `link_vul` int(11) DEFAULT NULL,
  PRIMARY KEY (`guid`),
  KEY `in_date` (`in_date`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- changeset songjia:20200824-apiAsset-019 labels:资产标签
ALTER TABLE `asset`
  ADD COLUMN `labels` VARCHAR(200) NULL ;

-- changeset songjia:20200902-apiAsset-020 labels:业务系统功能扩展
ALTER TABLE `sysdomain_combination`
  ADD COLUMN `domain_code` VARCHAR(50) NULL   COMMENT '所属安全域' AFTER `description`,
  ADD COLUMN `create_user_id` VARCHAR(50) NULL   COMMENT '创建人id' AFTER `domain_code`;


-- changeset songjia:20200904-apiAsset-021 labels:是否发布画布
ALTER TABLE  `topo_canvas`
  ADD COLUMN `is_release` BOOL DEFAULT 0  NULL   COMMENT '是否发布 1是，0是' AFTER `topo_type`;


-- changeset lps:20201015--apiAsset-001 labels:增加字段
ALTER TABLE `asset` add column  `employee_guid` varchar(1000) DEFAULT NULL;


-- changeset lps:2021032501-apiAsset-001 labels:资产偏好设置

CREATE TABLE  IF NOT EXISTS `asset_system_attribute_settings`(
  `guid` VARCHAR(36),
  `name` VARCHAR(50),
  `type` VARCHAR(50),
  `system_settings` json,
  `custom_settings` json,
  `panel` VARCHAR(50),
  `visible` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`guid`)
  );


CREATE TABLE  IF NOT EXISTS  `asset_settings`(
  `guid` VARCHAR(36) NOT NULL,
  `title` VARCHAR(50),
  `data` json,
  PRIMARY KEY (`guid`)
  );


UPDATE  asset_type_template SET form_data =
                                  REPLACE(REPLACE(REPLACE(form_data,'", "field": "','", "title": "'),'"context": {"code":','"context": {"name":'),'", "require":','", "isMust":')
WHERE NOT EXISTS (SELECT * FROM  `asset_system_attribute_settings`);

-- changeset lps:20210817-apiAsset-001 labels:自监基础数据管理
CREATE TABLE IF NOT EXISTS `app_sys_manager`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `app_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `department_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `department_guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `domain_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `secret_level` varchar(4) NULL DEFAULT NULL,
  `service_id` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
  ) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


CREATE TABLE IF NOT EXISTS  `app_account_manage`  (
  `guid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `account_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `app_role_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `app_role_id` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `person_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `cancel_time` datetime(0) NULL DEFAULT NULL,
  `app_id` int(11) NULL DEFAULT NULL,
  `app_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`guid`) USING BTREE
  ) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS  `app_resource_manage`  (
  `guid` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `app_resource_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `app_resource_url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `resource_type` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `app_id` int(11) NULL DEFAULT NULL,
  `app_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`guid`) USING BTREE
  ) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS   `app_role_manage`  (
  `guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `app_role_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `app_role_desc` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `cancel_time` datetime(0) NULL DEFAULT NULL,
  `app_id` int(11) NULL DEFAULT NULL,
  `app_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`guid`) USING BTREE
  ) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS   `data_info_manage`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `data_flag` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `business_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `secret_level` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
  ) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS   `internet_info_manage`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `internet_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `internet_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `secret_level` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `protect_level` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
  ) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS   `net_info_manage`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `net_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `net_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `secret_level` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `protect_level` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
  ) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- changeset lps:202010919--apiAsset-001 labels:增加字段
ALTER TABLE `net_info_manage` add column  `domain` varchar(64) DEFAULT NULL;
ALTER TABLE `app_sys_manager` add column  `secret_company` varchar(64) DEFAULT NULL;

-- changeset lrz:202010825--apiAsset labels:资产表新增字段 2021-08-20
alter table asset add equipment_intensive varchar(11) DEFAULT NULL COMMENT '涉密等级:绝密5，机密4，秘密3，内部2，非密1';
alter table asset add serial_number varchar(100) DEFAULT NULL COMMENT '序列号';
alter table asset add org_name varchar(255) DEFAULT NULL COMMENT '组织结构名称';
alter table asset add org_code varchar(100) DEFAULT NULL COMMENT '组织结构code';
alter table asset add responsible_name varchar(255) DEFAULT NULL COMMENT '责任人名称';
alter table asset add responsible_code varchar(100) DEFAULT NULL COMMENT '责任人code(用户编号）';
alter table asset add term_type varchar(11) DEFAULT NULL COMMENT '1：表示国产 2：非国产';
alter table asset add ismonitor_agent varchar(2) DEFAULT NULL COMMENT '1.已安装；2.未安装';
alter table asset add os_setup_time datetime COMMENT '终端类型操作系统安装时间';
alter table asset add os_list varchar(255) DEFAULT NULL COMMENT '终端类型安装操作系统';
alter table asset add terminal_type varchar(10) DEFAULT NULL COMMENT '终端类型 ：运维终端/用户终端';
alter table asset add domain_sub_code varchar(50) DEFAULT NULL COMMENT '安全域subcode';

-- changeset lrz:202010825--safe_secret_produce labels:编排记录表
CREATE TABLE IF NOT EXISTS `safe_secret_produce` (
  `guid` varchar(55) NOT NULL COMMENT '编排主键',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '产品名称',
  `manufacturer` varchar(255) NOT NULL DEFAULT '' COMMENT '生产厂商',
  `version` varchar(255) NOT NULL DEFAULT '' COMMENT '版本号',
  `asset_guid` varchar(36) DEFAULT '' COMMENT '关联的asset表guid',
  PRIMARY KEY (`guid`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='编排记录表';

-- changeset lrz:202010825--asset_system_attribute_settings labels:添加字段
alter table asset_system_attribute_settings add asset_settings_guid varchar(200);

-- changeset wd:202010825--devinfo labels:nac相关库
CREATE TABLE IF NOT EXISTS `devinfo` (
  `id` bigint(11) NOT NULL DEFAULT '0' COMMENT '设备id',
  `dev_name` varchar(255) NOT NULL DEFAULT ' ' COMMENT '设备名',
  `sid` bigint(11) NOT NULL DEFAULT '0' COMMENT '设备SID',
  `ip` varchar(255) NOT NULL DEFAULT ' ' COMMENT 'IP地址',
  `mac` varchar(255) NOT NULL DEFAULT ' ' COMMENT 'MAC地址',
  `state` varchar(255) NOT NULL DEFAULT ' ' COMMENT '设备状态',
  `state_id` tinyint(3) NOT NULL DEFAULT '0' COMMENT '设备状态值',
  `mng` varchar(1024) NOT NULL DEFAULT ' ' COMMENT '管控域',
  `accredited` varchar(1024) NOT NULL DEFAULT ' ' COMMENT '授权域',
  `isolated` varchar(1024) NOT NULL DEFAULT ' ' COMMENT '隔离域',
  `swip` varchar(255) NOT NULL DEFAULT ' ' COMMENT '交换机IP',
  `swifindex` bigint(11) NOT NULL DEFAULT '0' COMMENT '端口索引',
  `interface` varchar(255) NOT NULL DEFAULT ' ' COMMENT '交换机端口',
  `vlan` smallint(6) NOT NULL DEFAULT '1' COMMENT 'VLAN',
  `devtype` varchar(255) NOT NULL DEFAULT '' COMMENT '设备类型',
  `devtype_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '设备类型id',
  `devclass_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '设备大类id',
  `devtype_priority` smallint(6) NOT NULL DEFAULT '0' COMMENT '设备类型优先级',
  `systype_detail` varchar(255) NOT NULL DEFAULT '' COMMENT '系统类型',
  `systype_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '系统类型id',
  `systype` varchar(255) NOT NULL DEFAULT '' COMMENT '系统大类类型',
  `sysclass_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '系统大类id',
  `systype_priority` smallint(6) NOT NULL DEFAULT '0' COMMENT '系统类型优先级',
  `mac_vendor` varchar(255) NOT NULL DEFAULT ' ' COMMENT 'MAC厂商',
  `mac_vendor_abbr` varchar(255) NOT NULL DEFAULT ' ' COMMENT 'MAC厂商简称',
  `vendor` varchar(255) NOT NULL DEFAULT ' ' COMMENT '设备厂商',
  `model` varchar(255) NOT NULL DEFAULT ' ' COMMENT '设备型号',
  `policy_name` varchar(255) NOT NULL DEFAULT ' ' COMMENT '策略名称',
  `realname_state` varchar(255) NOT NULL DEFAULT '' COMMENT '实名状态',
  `verify_state` varchar(64) NOT NULL DEFAULT '0' COMMENT '审核状态',
  `verify_state_id` tinyint(3) NOT NULL DEFAULT '0' COMMENT '审核状态ID,0:不需要审核,1:审核通过,2:审核不通过,3:待审核',
  `webregister` tinyint(3) NOT NULL DEFAULT '0' COMMENT 'WEB注册状态,0:无WEB注册,1:WEB注册',
  `check_result` varchar(255) NOT NULL DEFAULT ' ' COMMENT '安检结果',
  `isolation_duration` varchar(255) NOT NULL DEFAULT ' ' COMMENT '隔离时间',
  `isolation_reason` varchar(1024) NOT NULL DEFAULT ' ' COMMENT '隔离原因',
  `user` varchar(255) NOT NULL DEFAULT ' ' COMMENT '认证用户',
  `auth_depart` varchar(255) NOT NULL DEFAULT ' ' COMMENT '认证部门',
  `bkstate` varchar(255) NOT NULL DEFAULT '' COMMENT '客户端阻断状态',
  `register` varchar(255) NOT NULL DEFAULT '' COMMENT '注册人',
  `job_num` varchar(255) NOT NULL DEFAULT '' COMMENT '工号',
  `version` varchar(255) NOT NULL DEFAULT '' COMMENT '终端版本',
  `regdepart` varchar(255) NOT NULL DEFAULT '' COMMENT '注册部门',
  `company` varchar(255) NOT NULL DEFAULT '' COMMENT '注册单位',
  `email` varchar(255) NOT NULL DEFAULT '' COMMENT '邮箱',
  `telephony` varchar(255) NOT NULL DEFAULT '' COMMENT '电话',
  `username` varchar(255) NOT NULL DEFAULT '' COMMENT '绑定用户',
  `responsible` varchar(255) NOT NULL DEFAULT '' COMMENT '责任人',
  `use_person` varchar(255) NOT NULL DEFAULT '' COMMENT '使用人',
  `date` varchar(255) NOT NULL DEFAULT '' COMMENT '登记时间',
  `duration` varchar(255) NOT NULL DEFAULT ' ' COMMENT '在线时长',
  `devgroup` varchar(255) NOT NULL DEFAULT ' ' COMMENT '所属终端分组',
  `probe_gw_ip` varchar(255) NOT NULL DEFAULT ' ' COMMENT '探针网关IP地址',
  `reserved1` varchar(255) NOT NULL DEFAULT '' COMMENT '保留字段1',
  `reserved2` varchar(255) NOT NULL DEFAULT '' COMMENT '保留字段2',
  `reserved3` varchar(255) NOT NULL DEFAULT '' COMMENT '保留字段3',
  `reserved4` varchar(255) NOT NULL DEFAULT '' COMMENT '保留字段4',
  `reserved5` varchar(255) NOT NULL DEFAULT '' COMMENT '保留字段5',
  `reserved6` varchar(255) NOT NULL DEFAULT '' COMMENT '保留字段6',
  `reserved7` varchar(255) NOT NULL DEFAULT '' COMMENT '保留字段7',
  `reserved8` varchar(255) NOT NULL DEFAULT '' COMMENT '保留字段8',
  `reserved9` varchar(255) NOT NULL DEFAULT '' COMMENT '保留字段9',
  `reserved10` varchar(255) NOT NULL DEFAULT '' COMMENT '保留字段10',
  `reg_state` tinyint(3) NOT NULL DEFAULT '0' COMMENT '终端信息列表页面的注册状态',
  `assets_register` tinyint(3) NOT NULL DEFAULT '0' COMMENT '资产注册,0:有资产注册,1:无资产注册',
  `control_state` varchar(255) NOT NULL DEFAULT '' COMMENT '管控状态',
  `port` varchar(512) NOT NULL DEFAULT '' COMMENT '端口指纹',
  `portcount` bigint(11) NOT NULL DEFAULT '0' COMMENT '端口数',
  `netbios` smallint(6) NOT NULL DEFAULT '0' COMMENT 'netbios名称标记',
  `netbios_name` varchar(255) NOT NULL DEFAULT ' ' COMMENT 'netbios名称',
  `secret` varchar(255) NOT NULL DEFAULT '' COMMENT '密级',
  `agenttype` bigint(11) NOT NULL DEFAULT '0' COMMENT '客户端类型:1,edp客户端;2,windows客户端;3,移动客户端;4,国产客户端;5,泛终端;6,MAC客户端;',
  `OSinfo` varchar(255) NOT NULL DEFAULT '' COMMENT '操作系统版本',
  `OSinstalltime` bigint(11) NOT NULL DEFAULT '0' COMMENT '操作系统安装时间',
  `diskserial` varchar(255) NOT NULL DEFAULT '' COMMENT '硬盘序列号',
  `UnifssoVer` varchar(255) NOT NULL DEFAULT '' COMMENT '桌管客户端版本',
  `UnifssoDeviceUse` varchar(255) NOT NULL DEFAULT '' COMMENT '设备用途',
  `LoginUser` varchar(255) NOT NULL DEFAULT '' COMMENT '系统登陆用户',
  `UnifssoState` smallint(6) NOT NULL DEFAULT '0' COMMENT '桌管状态',
  `camera_assets_reg` tinyint(3) NOT NULL DEFAULT '0' COMMENT '摄像头资产是否已经登记',
  `isolation_reason_id` tinyint(3) NOT NULL DEFAULT '0' COMMENT '隔离原因ID',
  `ip_invalid` tinyint(3) NOT NULL DEFAULT '0' COMMENT 'IP地址无效标记',
  `ident_ver` varchar(255) NOT NULL DEFAULT '' COMMENT '客户端识别库版本',
  `dgrpid` smallint(6) NOT NULL DEFAULT '0' COMMENT '所在终端分组ID',
  `dgrptype` smallint(6) NOT NULL DEFAULT '0' COMMENT '所在终端分组分配类型',
  `accessgrp_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '所在接入分组ID',
  `accessgrp_hub_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '所在接入HUB或WAP分组ID',
  PRIMARY KEY (`ip`)
  ) ENGINE=InnoDB DEFAULT CHARSET=gbk;
-- changeset lrz:202010831--asset_terminal_install_count labels:终端设备安装统计表
CREATE TABLE `asset_terminal_install_count` (
                                              `guid` varchar(36) NOT NULL,
                                              `is_install` int(11) DEFAULT NULL COMMENT '已安装的个数',
                                              `un_install` int(11) DEFAULT NULL COMMENT '未安装的个数',
                                              PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='终端设备安装统计表';

-- changeset lrz:202010831--asset_terminal_install_time labels:终端类型操作系统安装时间记录表
CREATE TABLE `asset_terminal_install_time` (
                                             `guid` varchar(36) NOT NULL COMMENT '主键',
                                             `asset_id` varchar(36) DEFAULT NULL COMMENT '设备id',
                                             `last_install_time` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp() COMMENT '上次安装时间',
                                             PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='终端类型操作系统安装时间记录表(上次)';



 -- changeset wd:20210918-alarmdeal-004
ALTER TABLE  `risk_event_rule`   
  ADD COLUMN `harm` TEXT NULL ,
  ADD COLUMN `principle` TEXT NULL ;

-- changeset sj:20210926-alarmdeal-005
CREATE TABLE  IF NOT EXISTS  `selef_concern_asset`( 
`guid` VARCHAR(36) NOT NULL,
 `user_id` VARCHAR(36) NOT NULL,
 `ip` VARCHAR(20) NOT NULL, PRIMARY KEY (`guid`)
 ) ENGINE=MYISAM CHARSET=utf8mb4 COLLATE=utf8mb4_bin; 
 
 
-- changeset sj:20210929-alarmdeal-006
 ALTER TABLE `risk_event_rule`  
  CHANGE `harm` `harm` TEXT CHARSET utf8 COLLATE utf8_general_ci NULL   COMMENT '危害',
  CHANGE `principle` `principle` TEXT CHARSET utf8 COLLATE utf8_general_ci NULL   COMMENT '原理',
  ADD COLUMN `violation_scenario` VARCHAR(1024) NULL   COMMENT '违规场景' AFTER `principle`,
  ADD COLUMN `recommend` BOOL DEFAULT 0  NULL   COMMENT '推荐' AFTER `violation_scenario`,
  ADD COLUMN `is_built_in_data` BOOL DEFAULT 0  NULL   COMMENT '是否内置' AFTER `recommend`;
  
-- changeset sj:20210929-alarmdeal-007
  ALTER TABLE  `dimension_table_field`   
  ADD COLUMN `enum_type` VARCHAR(50) NULL   COMMENT '字典表type' AFTER `field_length`;

-- changeset sj:20211011-alarmdeal-008
ALTER TABLE  `dimension_table_field`   
  ADD COLUMN `format_type` VARCHAR(50) NULL   COMMENT '格式化方式' AFTER `enum_type`;

-- changeset sj:20231026-alarmdeal-009
CREATE TABLE  IF NOT EXISTS  `self_concern_asset` (
  `guid` varchar(36) COLLATE utf8mb4_bin NOT NULL,
  `user_id` varchar(36) COLLATE utf8mb4_bin NOT NULL,
  `ip` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`guid`)
  ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- changeset tyj:20231016-alarmdeal-10
ALTER TABLE `self_concern_asset` ADD COLUMN `type` int(2) COMMENT '0资产ip 1应用系统id 2网络边界id' AFTER `ip`;

-- changeset lrz:20211018-flow-001
ALTER TABLE business_intance MODIFY busi_args JSON DEFAULT NULL;


-- changeset wudi:20211125-asset-001
ALTER TABLE `asset`
ADD COLUMN `self_secret_product_num` int(10) DEFAULT 0 AFTER `terminal_type`;


DROP TABLE IF EXISTS `import_asset_log`;
CREATE TABLE `import_asset_log` (
                                    `guid` varchar(50) NOT NULL COMMENT '导入日志的主键ID',
                                    `user_name` varchar(255) DEFAULT NULL COMMENT '用户名',
                                    `user_id` varchar(255) DEFAULT NULL COMMENT '用户ID',
                                    `import_create_time` timestamp NULL DEFAULT NULL COMMENT '导入开始时间',
                                    `import_finish_time` timestamp NULL DEFAULT NULL COMMENT '导入完成时间',
                                    `import_asset_count` bigint(255) DEFAULT NULL COMMENT '倒入的数量',
                                    `import_asset_result` tinyint(5) DEFAULT NULL COMMENT '导入结果：0表示成功、1表示失败、2导入进行中',
                                    `Import_asset_error_message` text COMMENT '导入失败原因',
                                    PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='资产导入日志记录表';

-- changeset liurz：20220210 labels:修改resource_type类型
alter table app_resource_manage modify column resource_type int(3);




-- changeset zzf:20220411 labels:新增字段
ALTER TABLE `supervise_task`
  ADD COLUMN `task_desc` varchar(255)  NULL,
  ADD COLUMN `create_time` varchar(64) NULL,
  ADD COLUMN `assist_id` varchar(64) NULL,
  ADD COLUMN `apply_unit` varchar(255)  NULL,
  ADD column `assist_unit` varchar(255)  NULL,
  ADD column  `apply_attachment` text  NULL,
  ADD  COLUMN  `response_attachment` text  NULL,
  ADD COLUMN  `notice_id` varchar(64)  NULL;


-- changeset liurz:20220413-modelmanage-001 labels:模型管理
CREATE TABLE `model_manage`  (
                                 `guid` varchar(50)  NOT NULL,
                                 `model_id` varchar(50)  NOT NULL COMMENT '模型id',
                                 `model_name` varchar(100)  DEFAULT NULL COMMENT '模型名称',
                                 `version` varchar(50)  DEFAULT NULL COMMENT '版本号',
                                 `label` varchar(255)  DEFAULT NULL COMMENT '标签',
                                 `version_desc` varchar(255)  DEFAULT NULL COMMENT '版本说明',
                                 `model_version_create_time` datetime(0) DEFAULT NULL COMMENT '版本创建时间',
                                 `model_desc` text  COMMENT '模型描述',
                                 `model_file_path` varchar(255)  DEFAULT NULL COMMENT '资源导入路径',
                                 `model_file_name` varchar(200)  DEFAULT NULL COMMENT '导入模型文件名称',
                                 `model_test_url` varchar(255)  DEFAULT NULL COMMENT '模型测试接口URL',
                                 `model_run_url` varchar(255)  DEFAULT NULL COMMENT '模型运行接口URL',
                                 `data_customer_model` varchar(100)  DEFAULT NULL COMMENT '数据消费模式',
                                 `data_customer_period` varchar(255)  DEFAULT NULL COMMENT '数据消费周期',
                                 `model_input_params` json DEFAULT NULL COMMENT '模型入参集',
                                 `model_start_parm` varchar(255)  DEFAULT NULL COMMENT '模型启动参数',
                                 `model_log_path` varchar(255)  DEFAULT NULL COMMENT '模型日志记录路径',
                                 `model_log_level` varchar(50)  DEFAULT NULL COMMENT '模型日志级别',
                                 `used` int(2) DEFAULT 0 COMMENT '是否可用(0：可用，-1：不可用)',
                                 `status` int(2) DEFAULT NULL COMMENT '模型当前状态(待测试(1)、已经测试(2)、发布(3)、启动(4)、停用(5)、下架(6))',
                                 `is_delete` int(2) DEFAULT 0 COMMENT '是否删除(-1表示删除，0表示可用)',
                                 `create_user` varchar(50)  DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime(0) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                 `update_user` varchar(50)  DEFAULT NULL COMMENT '更新人',
                                 `update_time` datetime(0) DEFAULT NULL COMMENT '更新时间',
                                 `remark` varchar(255)  DEFAULT NULL COMMENT '备注',
                                 PRIMARY KEY (`guid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '资源管理主表' ;

CREATE TABLE `model_param_config`  (
                                       `guid` varchar(255)  NOT NULL,
                                       `model_manage_id` varchar(50)  NOT NULL COMMENT '模型配置id',
                                       `name` varchar(255)  DEFAULT NULL COMMENT '参数名称',
                                       `param_desc` varchar(255)  DEFAULT NULL COMMENT '参数描述',
                                       `param_value_type` varchar(20)  DEFAULT NULL COMMENT '参数值类型(数值类型-int，\r\n字符串-String，日期时间-Date)',
                                       `param_type` varchar(50)  DEFAULT NULL COMMENT '参数类型(业务参数/技术参数)',
                                       `param_value` varchar(255)  DEFAULT NULL COMMENT '当前值',
                                       `param_default_value` varchar(255)  DEFAULT NULL COMMENT '参数默认值',
                                       PRIMARY KEY (`guid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '模型管理参数配置';

CREATE TABLE `model_test_result`  (
                                      `guid` varchar(255)  NOT NULL,
                                      `status` int(2) NOT NULL COMMENT '测试结果（0：通过、-1：测试未通过）',
                                      `create_time` datetime(0) DEFAULT NULL COMMENT '测试时间',
                                      `create_user` varchar(50)  DEFAULT NULL COMMENT '测试人员',
                                      `model_manage_id` varchar(50)  DEFAULT NULL COMMENT '模型配置id',
                                      PRIMARY KEY (`guid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8  COMMENT = '模型验证结果表';

-- changeset liurz:20220413-apiAsset-1001 labels:新增NTDS同步数据录入资产表字段
alter table asset ADD COLUMN `domain_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '安全域名称(登陆域名称)';
alter table asset ADD COLUMN `install_anti_virus_status` int(255) DEFAULT NULL COMMENT '杀毒软件安装情况';
alter table asset ADD COLUMN `client_status` int(10) DEFAULT NULL COMMENT '主审客户端在线状态';
alter table asset ADD COLUMN `device_status` int(255) DEFAULT NULL COMMENT '设备在线情况';
alter table asset ADD COLUMN `client_up_last_time` datetime(0) DEFAULT NULL COMMENT '主审客户端最近一次上报时间';
alter table asset ADD COLUMN  `device_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '设备Id';
alter table asset ADD COLUMN `update_time` datetime(0) DEFAULT NULL COMMENT '更新时间';
alter table asset ADD COLUMN `clinet_time_difference` bigint(20) DEFAULT NULL COMMENT '当前时间与主审客户端最近一次上报时间的差值,分钟表示';
alter table asset_terminal_install_time ADD COLUMN `current_install_time` datetime DEFAULT NULL COMMENT '记录当前系统安装时间(定时任务跑的时候记录)';
-- changeset zzf:20220415-apiAsset-001 labels:新增策略统计表
CREATE TABLE IF NOT EXISTS  `strategy_statistics` (
    `guid` varchar(64) NOT NULL,
    `strategy_name` varchar(255) DEFAULT NULL COMMENT '策略名称',
    `strategy_num` bigint(20) DEFAULT '0' COMMENT '下发策略统计数量',
    `register_device_num` bigint(20) DEFAULT '0' COMMENT '注册设备数量',
    PRIMARY KEY (`guid`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- changeset liurz:20220420-apiflow-100 labels:工单2.0代码迁移
ALTER TABLE `my_ticket` ADD COLUMN `bpmn_path` varchar(500) AFTER `node_status`;
ALTER TABLE `my_ticket` ADD COLUMN `bpmn_info` text AFTER `bpmn_path`;
ALTER TABLE `my_ticket` ADD COLUMN `bpmn_json` json AFTER `bpmn_info`;
ALTER TABLE `my_ticket` ADD COLUMN `process_version` varchar(50) AFTER `bpmn_json`;
ALTER TABLE `myticket_template` ADD COLUMN `form_version` varchar(255) AFTER `name`;


-- changeset liurz:20220420-apiappaccount-101 labels:应用系统账户信息增加登录ip
ALTER TABLE `app_account_manage` ADD COLUMN `ip` text AFTER `app_name`;

-- changeset liurz:20220512-assetname-101 labels:资产表将name修改为非必填
alter table `asset` modify COLUMN `Name`varchar(50) NULL DEFAULT NULL;

-- changeset liurz:20220517-asset-002 labels:数据同步一期数据
alter table asset ADD COLUMN `data_source_type` int(11) DEFAULT 1 COMMENT '数据来源类型1、手动录入，2 数据同步；3资产发现';
alter table asset ADD COLUMN `sync_source` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '外部来源信息 1、综审；2、准入；3、融一';
alter table asset ADD COLUMN `sync_uid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '外部来源主键ID';

-- changeset liurz:20220517-asset-00 labels:数据同步一期数据
update asset set data_source_type=1 ;
CREATE TABLE `asset_verify`  (
                                 `Guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
                                 `Name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `type` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '类型：一级资产类型名称-二级资产类型名称',
                                 `Name_en` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `IP` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `securityGuid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `ipNum` bigint(20) DEFAULT NULL,
                                 `Type_Guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `Type_Sno_Guid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `Version_info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `Tags` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资产标签，不同标签之间以,隔开',
                                 `CreateTime` datetime(0) DEFAULT NULL,
                                 `typeUnicode` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `snoUnicode` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `mac` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `employee_Code1` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `employee_Code2` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `monitor` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '性能监控',
                                 `special` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '采集事件是否开启',
                                 `canMonitor` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '监控开启',
                                 `canRCtrl` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '远程控制',
                                 `worth` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资产价值',
                                 `secrecy` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '机密性权值',
                                 `integrity` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '完整性权值',
                                 `availability` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '可用性权值',
                                 `protocol` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '监控协议',
                                 `assetNum` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资产编号',
                                 `assetUse` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资产用途',
                                 `location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '物理位置',
                                 `AssetDescribe` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注',
                                 `cabinetGuid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所属机柜guid',
                                 `marginBottom` int(20) DEFAULT 0 COMMENT '距离底部高度',
                                 `height` int(20) DEFAULT 0 COMMENT '占U口个数',
                                 `lng` decimal(19, 8) DEFAULT NULL COMMENT '经度',
                                 `lat` decimal(19, 8) DEFAULT NULL COMMENT '纬度',
                                 `gatewayName` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '网关名称',
                                 `gatewayNum` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '网关序列号',
                                 `gatewayUser` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '主管名称',
                                 `gatewayDepartment` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '主管部门',
                                 `phoneNum` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '电话号码',
                                 `remarkInfo` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '说明',
                                 `org` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '机构',
                                 `core` tinyint(1) DEFAULT 0 COMMENT '核心资产',
                                 `app_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '应用系统id',
                                 `app_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '应用系统名称',
                                 `labels` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `employee_guid` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `domain_sub_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                 `equipment_intensive` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '设备密集',
                                 `serial_number` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '序列号',
                                 `term_type` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '1：表示国产 2：非国产',
                                 `org_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '组织结构名称',
                                 `org_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组织结构code',
                                 `responsible_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '责任人名称(比如普通用户、管理员)',
                                 `responsible_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '责任人code(用户账号code）',
                                 `ismonitor_agent` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '终端类型）1.已安装；2.未安装',
                                 `os_setup_time` datetime(0) DEFAULT NULL COMMENT '终端类型操作系统安装时间',
                                 `os_list` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '终端类型安装操作系统',
                                 `terminal_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '终端类型 ：运维终端/用户终端',
                                 `self_secret_product_num` int(10) DEFAULT NULL,
                                 `domain_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '安全域名称(登陆域名称)',
                                 `install_anti_virus_status` int(255) DEFAULT NULL COMMENT '杀毒软件安装情况',
                                 `client_status` int(10) DEFAULT NULL COMMENT '主审客户端在线状态',
                                 `device_status` int(255) DEFAULT NULL COMMENT '设备在线情况',
                                 `client_up_last_time` datetime(0) DEFAULT NULL COMMENT '主审客户端最近一次上报时间',
                                 `device_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '设备Id',
                                 `update_time` datetime(0) DEFAULT NULL COMMENT '更新时间',
                                 `clinet_time_difference` bigint(20) DEFAULT NULL COMMENT '当前时间与主审客户端最近一次上报时间的差值,分钟表示',
                                 `data_source_type` int(11) DEFAULT NULL COMMENT '数据来源类型1、手动录入，2 数据同步；3资产发现',
                                 `sync_source` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '外部来源信息 1、综审；2、准入；3、融一',
                                 `sync_uid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '外部来源主键ID',
                                 `sync_status` int(11) DEFAULT NULL COMMENT '状态：1、待编辑；2、待入库、3、入库失败 ;4、已入库(入库成功);5、已忽略',
                                 `sync_time` datetime(0) DEFAULT NULL COMMENT '同步时间',
                                 `asset_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '关联正式库资产ID',
                                 `sync_message` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '同步错误信息',
                                 PRIMARY KEY (`Guid`) USING BTREE,
                                 INDEX `Type_Guid`(`Type_Guid`) USING BTREE,
                                 INDEX `Type_Sno_Guid`(`Type_Sno_Guid`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备审核表' ROW_FORMAT = Dynamic;


alter table data_info_manage ADD COLUMN  `file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件名称';
alter table data_info_manage ADD COLUMN  `file_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件类型';
alter table data_info_manage ADD COLUMN  `file_size` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT ' 文件大小 (单位MB)';
alter table data_info_manage ADD COLUMN  `file_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件管理状态';
alter table data_info_manage ADD COLUMN  `draft_user` text CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件起草人';
alter table data_info_manage ADD COLUMN  `datermine_user` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件定密人';
alter table data_info_manage ADD COLUMN  `sale_user` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件签发人';
alter table data_info_manage ADD COLUMN  `aware_scope` text CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '知悉范围';
alter table data_info_manage ADD COLUMN  `sercet_period` text CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '保密期限';
alter table data_info_manage ADD COLUMN  `datermine_reason` text CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '定密依据';
alter table data_info_manage ADD COLUMN  `file_auth` text CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件授权';
alter table data_info_manage ADD COLUMN  `data_source_type` int(11) DEFAULT NULL COMMENT '/数据来源类型：1、手动录入；2 数据同步；3资产发现';
alter table data_info_manage ADD COLUMN  `sync_source` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs';
alter table data_info_manage ADD COLUMN  `sync_uid` varchar(100) DEFAULT NULL COMMENT '外部来源主键ID';

-- changeset liangguolu:20220620-rule-filter-001 labels:rule-filter
CREATE TABLE IF NOT EXISTS `rule_filter`  (
    `guid` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'guid',
    `rule_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '策略ID',
    `filter_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '规则ID',
    `params` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '参数',
    `isStarted` varchar(10) DEFAULT NULL COMMENT '是否启用',
    PRIMARY KEY (`guid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- changeset liangguolu:20220620-filter-operator-001 labels:filter-operator
alter table filter_operator add filter_type varchar(10) DEFAULT NULL COMMENT '规则分类';
alter table filter_operator add rule_filter_type varchar(10) DEFAULT NULL COMMENT '策略规则类型';
alter table filter_operator add init_status varchar(2) DEFAULT NULL COMMENT '是否内置';
alter table risk_event_rule modify column isStarted varchar(10) DEFAULT NULL;
alter table risk_event_rule add create_username varchar(64) DEFAULT NULL COMMENT '创建人名称';
alter table risk_event_rule add create_userno varchar(64) DEFAULT NULL COMMENT '创建人编号';
alter table risk_event_rule add update_username varchar(64) DEFAULT NULL COMMENT '更新人名称';
alter table risk_event_rule add update_userno varchar(64) DEFAULT NULL COMMENT '更新人编号';
alter table event_alarm_setting add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_admin_filecopy add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_admin_filecopy add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_protocol_relative add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_protocol_relative add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_file_inoutbusiness add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_file_inoutbusiness add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_app_protocol add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_app_protocol add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_app_port add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_app_port add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_printburn_filetype add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_printburn_filetype add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_printburn_filesize add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_printburn_filesize add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_port_relative add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_port_relative add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_printburn add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_printburn add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_admin_fileburn add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_admin_fileburn add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_ip_pair add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_ip_pair add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_app_ip add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_app_ip add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_app_business add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_app_business add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_user_business add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_user_business add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_user_filedownload add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_user_filedownload add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_printburn_filebrand add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_printburn_filebrand add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_dev_ip add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_dev_ip add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_admin_protocol add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_admin_protocol add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_admin_port add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_admin_port add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_printburn_filefre add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_printburn_filefre add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_app_uavisit add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_app_uavisit add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

-- changeset liangguolu:20220711-baseline_user_login-001 labels:baseline_user_login
CREATE TABLE `baseline_user_login`(
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
    `dev_ip` VARCHAR(50) NOT NULL COMMENT '设备IP',
    `login_hour` INT NOT NULL COMMENT '登录时间段',
    `login_count_total` INT NOT NULL COMMENT '登录次数',
    `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
    `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
    PRIMARY KEY (`id`)
);

-- changeset liangguolu:20220713-baseline_printburn_filebrand-001 labels:baseline_printburn_filebrand
alter table baseline_printburn_filebrand change time_bucket op_hour varchar(50) NOT NULL COMMENT '时段';

-- changeset liurz:20220713-asset_terminal_install_count labels:删除该表
drop table  asset_terminal_install_count;

-- changeset liurz:20220705-asset_extend_verify-001 labels:应用系统待审表，资产扩展待审表
CREATE TABLE `asset_extend_verify`  (
                                        `assetGuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资产guid',
                                        `extendInfos` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '扩展信息json格式'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备扩展信息审核库' ROW_FORMAT = Dynamic;

CREATE TABLE `app_sys_manager_verify`  (
                                           `id` int(11) NOT NULL AUTO_INCREMENT,
                                           `app_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '应用编号',
                                           `app_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '应用系统名称',
                                           `department_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '单位名称',
                                           `department_guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '单位Code',
                                           `domain_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '域名',
                                           `secret_level` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '涉密等级',
                                           `create_time` datetime(0) DEFAULT NULL COMMENT '创建时间',
                                           `secret_company` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '涉密厂商',
                                           `data_source_type` int(2) DEFAULT NULL COMMENT '数据来源类型1、手动录入，2 数据同步；3资产发现',
                                           `sync_source` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '外部来源信息 1、综审；2、准入；3、融一',
                                           `sync_uid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '外部来源主键ID',
                                           `sync_status` int(2) DEFAULT NULL COMMENT '状态：1、待编辑；2、待入库、3、入库失败 ;4、已入库(入库成功);5、已忽略',
                                           `sync_time` datetime(0) DEFAULT NULL COMMENT '同步时间',
                                           `app_id` int(11) DEFAULT NULL COMMENT '关联应用系统id',
                                           `sync_message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '同步结果信息',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '应用系统待审表' ROW_FORMAT = Dynamic;

-- changeset liurz:20220713-app_sys_manager labels:增加字段,id去掉自增
alter table app_sys_manager ADD COLUMN `data_source_type` int(11) DEFAULT 1 COMMENT '数据来源类型1、手动录入，2 数据同步；3资产发现';
alter table app_sys_manager ADD COLUMN `sync_source` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '外部来源信息 1、综审；2、准入；3、融一';
alter table app_sys_manager ADD COLUMN `sync_uid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '外部来源主键ID';
alter table app_sys_manager MODIFY COLUMN `id` int(11) NOT NULL ;

-- changeset liangguolu:2210-api-dimension_table_field-20220913
alter table baseline_printburn change username user_no varchar(50) DEFAULT NULL COMMENT '用户';
alter table baseline_printburn_filetype change username user_no varchar(50) DEFAULT NULL COMMENT '用户';
alter table baseline_printburn_filesize change username user_no varchar(50) DEFAULT NULL COMMENT '用户';
alter table baseline_printburn_filefre change username user_no varchar(50) DEFAULT NULL COMMENT '用户';
alter table baseline_printburn_filebrand change username user_no varchar(50) DEFAULT NULL COMMENT '用户';
alter table baseline_user_business change username user_no varchar(50) DEFAULT NULL COMMENT '用户';
alter table baseline_user_filedownload change username user_no varchar(50) DEFAULT NULL COMMENT '用户';
alter table baseline_admin_protocol change username user_no varchar(50) DEFAULT NULL COMMENT '用户';
alter table baseline_admin_port change username user_no varchar(50) DEFAULT NULL COMMENT '用户';
alter table baseline_admin_filecopy change username user_no varchar(50) DEFAULT NULL COMMENT '用户';
alter table baseline_admin_fileburn change username user_no varchar(50) DEFAULT NULL COMMENT '用户';
alter table baseline_app_uavisit change username user_no varchar(50) DEFAULT NULL COMMENT '用户';

alter table rule_filter character set utf8;
alter table rule_filter modify guid varchar(64) CHARACTER SET utf8;
alter table rule_filter modify rule_id varchar(64) CHARACTER SET utf8;
alter table rule_filter modify filter_code varchar(64) CHARACTER SET utf8;
alter table rule_filter modify params varchar(255) CHARACTER SET utf8;
alter table rule_filter modify isStarted varchar(10) CHARACTER SET utf8;
alter table baseline_app_uavisit drop column `business_type`;
alter table baseline_admin_port change port dport varchar(50) DEFAULT NULL COMMENT '运维端口';
alter table baseline_admin_protocol change protocol app_protocol varchar(50) DEFAULT NULL COMMENT '运维协议';

alter table baseline_command_keyword add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_command_keyword add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_process_list add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_process_list add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_software_whitelist add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_software_whitelist add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

alter table baseline_weak_password add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_weak_password add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

CREATE TABLE IF NOT EXISTS `baseline_page_result` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
    `user_ip` varchar(64) DEFAULT NULL COMMENT '用户标识',
    `sys_id` varchar(64) DEFAULT NULL COMMENT '系统id',
    `frequency_avg` double(12,4) DEFAULT NULL COMMENT '频率均值',
    `frequency_dev` double(12,4) DEFAULT NULL COMMENT '频率标准差',
    `inefficiency_avg` double(12,4) DEFAULT NULL COMMENT '无效率均值',
    `inefficiency_dev` double(12,4) DEFAULT NULL COMMENT '无效率标注差',
    `purity_avg` double(12,4) DEFAULT NULL COMMENT '纯度均值',
    `purity_dev` double(12,4) DEFAULT NULL COMMENT '纯度标准差',
    `type` char(1) DEFAULT NULL COMMENT '类型',
    `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
    `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=201697 DEFAULT CHARSET=utf8;
-- changeset liurz:20221013-t_flow_config labels:流程配置表
CREATE TABLE `t_flow_config`  (
                                  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                  `value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                  PRIMARY KEY (`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '流程配置表' ROW_FORMAT = Dynamic;
-- changeset liurz:20221028-t_my_ticket labels:增加工单类型字段
ALTER TABLE `my_ticket` ADD COLUMN `ticket_type` varchar(50) DEFAULT NULL COMMENT ' 工单类型：内部工单、外部工单 1，表示内部工单 ，2表示外部工单';

-- changeset liurz:202211-baseline_page_result-20221110 labels:重新创建维表
drop table baseline_page_result;
CREATE TABLE IF NOT EXISTS `baseline_page_result`(
   `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
   `foreign_key_id` VARCHAR(50) NOT NULL COMMENT '被引用规则id',
   `user_ip` varchar(64) DEFAULT NULL COMMENT '用户标识',
   `sys_id` varchar(64) DEFAULT NULL COMMENT '系统id',
   `frequency_min` double(12,4) DEFAULT NULL COMMENT '频率最小值',
  `frequency_max` double(12,4) DEFAULT NULL COMMENT '频率最大值',
  `inefficiency_min` double(12,4) DEFAULT NULL COMMENT '无效率最小值',
  `inefficiency_max` double(12,4) DEFAULT NULL COMMENT '无效率最大值',
  `purity_min` double(12,4) DEFAULT NULL COMMENT '纯度最小值',
  `purity_max` double(12,4) DEFAULT NULL COMMENT '纯度最大值',
  `type` char(1) DEFAULT NULL COMMENT '类型',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  PRIMARY KEY (`id`)
);

-- changeset liangguolu:202211-dimension_sync labels:新建筛选条件表
CREATE TABLE IF NOT EXISTS `dimension_sync` (
      `guid` varchar(255) NOT NULL COMMENT 'guid',
      `filter_code` varchar(255) DEFAULT NULL COMMENT '规则编码',
      `rule_code` varchar(255) DEFAULT NULL COMMENT '策略编码',
      `dimension_table_name` varchar(255) DEFAULT NULL COMMENT '维表名称',
      `conditions` text COMMENT '筛选条件',
      PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- changeset liangguolu:202211-baseline labels:增加同步字段
alter table baseline_admin_fileburn add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_admin_filecopy add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_admin_port add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_admin_protocol add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_app_business add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_app_ip add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_app_port add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_app_protocol add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_app_uavisit add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_command_keyword add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_dev_ip add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_file_inoutbusiness add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';

alter table baseline_ip_pair add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_page_result add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_parameter_blacklist add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_port_relative add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_printburn add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_printburn_filebrand add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_printburn_filefre add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_printburn_filesize add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_printburn_filetype add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_process_list add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_protocol_relative add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_software_whitelist add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';

alter table baseline_user_business add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_user_filedownload add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_user_login add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';
alter table baseline_weak_password add is_sync bigint(1) DEFAULT NULL COMMENT '是否同步';

alter table baseline_printburn_filesize change file_size_max file_size_max varchar(50) DEFAULT NULL COMMENT '文件大小最大值';

-- changeset liurz:20221130-t_flow_listener_config labels:流程自定义监听器配置表
CREATE TABLE `t_flow_listener_config`  (
                                           `listener_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '监听器code',
                                           `listener_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '监听器名称',
                                           `status` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否开启，默认开启 \"0\"表示开启，\"1\"表示取消',
                                           `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '监听器类型： process：过程监听器、task : 任务监听器',
                                           PRIMARY KEY (`listener_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '流程自定义监听器配置表' ROW_FORMAT = Dynamic;

-- changeset liangguolu:202212-filter_operator labels:规则描述
alter table filter_operator add attack_line text DEFAULT NULL COMMENT '攻击链阶段';
alter table filter_operator add threat_credibility text DEFAULT NULL COMMENT '威胁可信度';
alter table filter_operator add deal_advcie text DEFAULT NULL COMMENT '处置建议';
alter table filter_operator add harm text DEFAULT NULL COMMENT '危害';
alter table filter_operator add principle text DEFAULT NULL COMMENT '原理';
alter table filter_operator add violation_scenario text DEFAULT NULL COMMENT '违规场景';
alter table filter_operator add filter_desc text DEFAULT NULL COMMENT '规则描述';

alter table baseline_app_protocol drop app_id;
alter table baseline_app_port drop app_id;
alter table baseline_app_ip drop app_id;
alter table baseline_app_business drop app_id;
alter table baseline_app_uavisit drop user_type;

-- changeset liangguolu:202212-baseline_parameter_blacklist-001 labels:增加字段
alter table baseline_parameter_blacklist add filter_code varchar(64) DEFAULT NULL COMMENT '规则编码';
alter table baseline_parameter_blacklist add rule_code varchar(64) DEFAULT NULL COMMENT '策略编码';

-- changeset liurz:20230103flow_email_template labels:邮件模板内容配置表
CREATE TABLE `flow_email_template`  (
                                        `key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '邮件模板唯一标识',
                                        `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '邮件标题',
                                        `content` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '邮件内容',
                                        `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '邮件模板名称',
                                        PRIMARY KEY (`key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- changeset liangguolu:20230104_关保 labels:关保威胁迁移
CREATE TABLE IF NOT EXISTS `process_job` (
       `id` int(10) NOT NULL AUTO_INCREMENT,
       `job_name` varchar(50) DEFAULT NULL COMMENT '任务名',
       `status` tinyint(1) DEFAULT '1' COMMENT '1正常 0暂停',
       `create_time` datetime DEFAULT NULL,
       `job_cron` varchar(50) DEFAULT NULL COMMENT '运行表达式',
       `type` tinyint(1) DEFAULT '0' COMMENT '0每天 1每周 2每月 3每分钟',
       `day` int(5) DEFAULT NULL COMMENT 'type为每周每月时，保存日每周（1代表周日 7代表周六 可选值：1、2、3、4、5、6、7，每月时具体数字。\r\n为每分钟时保存分钟数',
       `time_str` varchar(22) DEFAULT NULL COMMENT '保存运行时间',
       `describe` varchar(255) DEFAULT NULL COMMENT '描述',
       `service_id` varchar(50) DEFAULT NULL COMMENT '服务模块',
       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='任务配置表';

CREATE TABLE IF NOT EXISTS `threat_level_manage` (
       `guid` varchar(255) NOT NULL COMMENT 'guid',
       `start_threat_value` int(10) DEFAULT NULL COMMENT '开始威胁值',
       `end_threat_value` int(10) DEFAULT NULL COMMENT '结束威胁值',
       `threat_level` int(10) DEFAULT NULL COMMENT '威胁程度等级',
       `desc` varchar(255) DEFAULT NULL COMMENT '描述',
       PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `threat_rate_manage` (
      `guid` varchar(255) NOT NULL COMMENT 'guid',
      `start_day_count` int(10) DEFAULT NULL COMMENT '开始天数',
      `end_day_count` int(10) DEFAULT NULL COMMENT '结束天数',
      `threat_rate_rank` int(10) DEFAULT NULL COMMENT '威胁频率等级',
      `desc` varchar(255) DEFAULT NULL COMMENT '描述',
      PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--changeset liurz:20230104-busisystem labels：业务系统表、业务系统资产关联表
CREATE TABLE `busisystem_asset`  (
     `guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
     `sysdomain_guid` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '业务系统guid',
     `asset_guid` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产guid',
     `asset_order` int(10) NOT NULL COMMENT '资产展示序号',
     PRIMARY KEY (`guid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '业务系统与资产关联表' ROW_FORMAT = Dynamic;


CREATE TABLE `busisystem_combination`  (
       `guid` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
       `sys_name` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '名字',
       `parent_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '父级业务系统',
       `iportance_level` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '重要级别',
       `maintainer_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '责任人Id',
       `maintainer` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '责任人',
       `description` text CHARACTER SET utf8 COLLATE utf8_bin COMMENT '业务描述',
       `domain_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所属安全域code',
       `domain_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所属安全域名称',
       `create_user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '创建人id',
       `create_time` datetime(0) DEFAULT NULL COMMENT '创建时间',
       PRIMARY KEY (`guid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '业务系统' ROW_FORMAT = Dynamic;

-- changeset liurz:20230104-asset_inline  labels:资产在线、资产变更表、资产表新增资产价值两个字段
CREATE TABLE `asset_online`  (
     `guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
     `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资产名称',
     `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产ip',
     `mac` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'mac',
     `group_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '一级资产类型名称',
     `group_guid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '一级资产类型guid',
     `type_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '二级资产类型名称',
     `type_guid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '二级资产类型guid',
     `os` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '操作系统',
     `scan_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '发现方式',
     `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '在线状态：\"0\"表示在线，“1”表示离线',
     `first_time` datetime(0) DEFAULT NULL COMMENT '首次发现时间',
     `cur_time` datetime(0) DEFAULT NULL COMMENT '最近发现时间',
     `org_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '归属单位名称',
     `org_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '归属单位code',
     `person_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '责任用户code',
     `person_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '责任用户名称',
     `create_time` datetime(0) DEFAULT NULL COMMENT '记录创建时间',
     `is_delete` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '是否删除：0表示正常，-1表示删除',
     `is_import` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '是否导入台账：0表示没有导入，1表示导入',
     PRIMARY KEY (`guid`) USING BTREE,
     UNIQUE INDEX `ip`(`ip`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资产在线表' ROW_FORMAT = Dynamic;

CREATE TABLE `asset_change`  (
     `guid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
     `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'ip',
     `asset_type_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '台账资产类型(资产表中二级资产类型名称)',
     `scan_type_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '发现资产类型(发现的资产小类名称)',
     `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '状态',
     `handle_user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '处理人id',
     `handle_user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '处理人姓名',
     `handle_time` datetime(0) DEFAULT NULL COMMENT '处理时间',
     `opinion` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '处理意见',
     `create_time` datetime(0) DEFAULT NULL COMMENT '记录时间',
     `handle_status` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '处理状态：\"0\"表示已经处理',
     PRIMARY KEY (`guid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资产变更表' ROW_FORMAT = Dynamic;

alter table asset ADD COLUMN `importance` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '业务重要性';
alter table asset ADD COLUMN `loadBear` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '系统资产业务承载性';

--changeset liurz:20230104-asset_analysis_online_statistic labels：在线资产统计、资产总数统计、资产数量变化统计
CREATE TABLE `asset_analysis_online_statistic`  (
        `guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
        `create_time` datetime(0) DEFAULT NULL COMMENT '统计时间',
        `num` int(11) DEFAULT NULL COMMENT '数量',
        PRIMARY KEY (`guid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '在线资产统计' ROW_FORMAT = Dynamic;


CREATE TABLE `asset_analysis_total_statistic`  (
       `guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
       `create_time` datetime(0) DEFAULT NULL COMMENT '统计时间',
       `num` int(11) DEFAULT NULL COMMENT '数量',
       PRIMARY KEY (`guid`) USING BTREE,
       INDEX `createTime`(`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资产总数统计' ROW_FORMAT = Dynamic;

CREATE TABLE `asset_analysis_type_statistic`  (
      `guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
      `create_time` datetime(0) DEFAULT NULL COMMENT '统计时间',
      `num` int(11) DEFAULT NULL COMMENT '数量',
      `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '二级资产类型名称',
      PRIMARY KEY (`guid`) USING BTREE,
      INDEX `createTime`(`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资产数量变化统计' ROW_FORMAT = Dynamic;

--changeset liangguolu:20230106-baseline labels：基线维表字段修改
alter table baseline_user_filedownload modify  count_max int(50) COMMENT '最大数量';
alter table baseline_printburn_filefre modify  count_max int(50) COMMENT '最大数量';
alter table baseline_printburn_filebrand modify  count_max int(50) COMMENT '最大数量';
alter table baseline_admin_filecopy modify  count_max int(50) COMMENT '最大数量';
alter table baseline_admin_fileburn modify  count_max int(50) COMMENT '最大数量';

alter table baseline_admin_fileburn add insert_time varchar(64) DEFAULT NULL COMMENT '数据时间';
alter table baseline_printburn_filefre add insert_time varchar(64) DEFAULT NULL COMMENT '数据时间';
alter table baseline_printburn_filebrand add insert_time varchar(64) DEFAULT NULL COMMENT '数据时间';
alter table baseline_page_result add insert_time varchar(64) DEFAULT NULL COMMENT '数据时间';
alter table baseline_user_login add insert_time varchar(64) DEFAULT NULL COMMENT '数据时间';
alter table baseline_user_filedownload add insert_time varchar(64) DEFAULT NULL COMMENT '数据时间';

alter table dimension_table add days int(10) DEFAULT NULL COMMENT '数据存储天数';

--changeset liangguolu:20230328-filter_source_status labels：规则数据源状态
CREATE TABLE `filter_source_status` (
        `id` varchar(64) NOT NULL COMMENT '记录ID',
        `data_source_id` int(10) DEFAULT NULL COMMENT '数据源ID',
        `open_status` int(10) DEFAULT NULL COMMENT '开启状态',
        `data_status` int(10) DEFAULT NULL COMMENT '数据状态',
        `msg` varchar(255) DEFAULT NULL COMMENT '信息',
        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='规则数据源状态';

--changeset liurz:202300413-asset_book labels：资产台账比对
CREATE TABLE `asset_book_diff`  (
                                    `guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
                                    `type_guid` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '类型',
                                    `device_desc` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '设备类型(小类)',
                                    `asset_num` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '设备编号',
                                    `name` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '名称',
                                    `responsible_name` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '责任人',
                                    `ip` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT 'ip',
                                    `mac` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT 'mac',
                                    `org_name` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '部门',
                                    `register_time` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '最后注册时间(启用时间)',
                                    `serial_number` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '序列号',
                                    `equipment_intensive` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '设备密集',
                                    `location` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '位置',
                                    `extend_disk_number` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '磁盘序列号',
                                    `os_list` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '操作系统版本',
                                    `os_setup_time` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '操作系统安装时间',
                                    `type_Sno_Guid` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '品牌型号',
                                    `remark_info` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '备注',
                                    `handle_status` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '处理状态',
                                    `ref_detail_guid` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '关联详情的guid',
                                    `create_time` datetime(0) DEFAULT NULL COMMENT '记录生成时间',
                                    `ref_asset_guid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '关联正式库的guid',
                                    `device_arch` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '架构',
                                    PRIMARY KEY (`guid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '统一台账差异表' ROW_FORMAT = Dynamic;
CREATE TABLE `asset_book_detail`  (
                                      `guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
                                      `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '名称',
                                      `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'ip',
                                      `mac` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'mac',
                                      `type_guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '类型(二级资产)',
                                      `type_unicode` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '二级资产类型UniqueCode',
                                      `org_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '组织结构名称',
                                      `org_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组织结构code',
                                      `responsible_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '责任人名称(比如普通用户、管理员)',
                                      `responsible_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '责任人code(用户账号code）',
                                      `security_guid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '安全域code',
                                      `domain_sub_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '安全域subcode',
                                      `domain_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '安全域名称',
                                      `type_sno_guid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '品牌型号',
                                      `extend_disk_number` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '磁盘序列号',
                                      `equipment_intensive` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '设备密集',
                                      `serial_number` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '序列号',
                                      `term_type` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '1：表示国产 2：非国产',
                                      `terminal_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '终端类型 ：运维终端/用户终端',
                                      `ismonitor_agent` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '终端类型）1.已安装；2.未安装',
                                      `os_setup_time` datetime(0) DEFAULT NULL COMMENT '终端类型操作系统安装时间',
                                      `os_list` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '终端类型安装操作系统',
                                      `install_anti_virus_status` int(255) DEFAULT NULL COMMENT '杀毒软件安装情况',
                                      `client_status` int(10) DEFAULT NULL COMMENT '主审客户端在线状态',
                                      `device_status` int(255) DEFAULT NULL COMMENT '设备在线情况',
                                      `client_up_last_time` datetime(0) DEFAULT NULL COMMENT '主审客户端最近一次上报时间',
                                      `device_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '设备Id',
                                      `clinet_time_difference` bigint(20) DEFAULT NULL COMMENT '当前时间与主审客户端最近一次上报时间的差值,分钟表示',
                                      `data_source_type` int(11) DEFAULT NULL COMMENT '数据来源类型1、手动录入，2 数据同步；3资产发现',
                                      `sync_source` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '外部来源信息 1、综审；2、准入；3、融一',
                                      `sync_uid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '外部来源主键ID',
                                      `location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '物理位置',
                                      `remark_info` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注',
                                      `asset_num` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资产编号',
                                      `device_arch` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '架构',
                                      `register_time` datetime(0) DEFAULT NULL COMMENT '启用时间',
                                      `device_desc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '设备类型(小类)',
                                      `create_time` datetime(0) DEFAULT NULL COMMENT '创建时间',
                                      `extend_infos` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '扩展字段',
                                      PRIMARY KEY (`guid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '统一台账明细表' ROW_FORMAT = Dynamic;

alter table asset_verify ADD COLUMN `device_arch` varchar(255) DEFAULT NULL COMMENT '架构';
alter table asset_verify ADD COLUMN `device_desc` varchar(255) DEFAULT NULL COMMENT '设备类型(小类)';
alter table asset_verify ADD COLUMN `register_time`  datetime(0) DEFAULT NULL COMMENT '架构';

-- changeset liurz:20230420-asset_book_detail labels:增加批次字段
alter table asset_book_detail ADD COLUMN `batch_no` varchar(255) DEFAULT NULL COMMENT '批次';
-- changeset liurz:20230523-asset_extend_verify labels:assetGuid设置为主键
ALTER TABLE asset_extend_verify ADD PRIMARY KEY (assetGuid)

-- changeset liurz:20230625-baseline_summary labels:用户扫描维表建表
DROP TABLE IF EXISTS `baseline_summary_dip_ten`;
DROP TABLE IF EXISTS `baseline_summary_dip_thirty`;
DROP TABLE IF EXISTS `baseline_summary_dport_ten`;
DROP TABLE IF EXISTS `baseline_summary_dport_thirty`;
DROP TABLE IF EXISTS `baseline_summary_protocol_ten`;
DROP TABLE IF EXISTS `baseline_summary_protocol_thirty`;
CREATE TABLE IF NOT EXISTS `baseline_summary_dip_ten`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `foreign_key_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '被引用规则id',
    `dip_count_avg` int(5) DEFAULT NULL COMMENT '目的IP个数',
    `filter_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '规则编码',
    `rule_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '策略编码',
    `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
    `process` float(10, 0) DEFAULT NULL COMMENT '进度',
    `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '源ip',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8026 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS`baseline_summary_dip_thirty`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `foreign_key_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '被引用规则id',
    `dip_count_avg` int(5) DEFAULT NULL COMMENT '目的IP个数',
    `filter_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '规则编码',
    `rule_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '策略编码',
    `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
    `process` float(10, 0) DEFAULT NULL COMMENT '进度',
    `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '源ip',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE = InnoDB AUTO_INCREMENT = 547 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `baseline_summary_dport_ten`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `foreign_key_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '被引用规则id',
    `dport_count_avg` int(5) DEFAULT NULL COMMENT '目的端口个数',
    `filter_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '规则编码',
    `rule_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '策略编码',
    `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
    `process` float(10, 0) DEFAULT NULL COMMENT '进度',
    `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '源ip',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE = InnoDB AUTO_INCREMENT = 548 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `baseline_summary_dport_thirty`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `foreign_key_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '被引用规则id',
    `dport_count_avg` int(5) DEFAULT NULL COMMENT '目的端口个数',
    `filter_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '规则编码',
    `rule_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '策略编码',
    `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
    `process` float(10, 0) DEFAULT NULL COMMENT '进度',
    `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '源ip',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE = InnoDB AUTO_INCREMENT = 547 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `baseline_summary_protocol_ten`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `foreign_key_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '被引用规则id',
    `protocol_count_avg` int(5) DEFAULT NULL COMMENT '协议个数',
    `filter_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '规则编码',
    `rule_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '策略编码',
    `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
    `process` float(10, 0) DEFAULT NULL COMMENT '进度',
    `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '源ip',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE = InnoDB AUTO_INCREMENT = 548 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
CREATE TABLE IF NOT EXISTS `baseline_summary_protocol_thirty`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `foreign_key_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '被引用规则id',
    `protocol_count_avg` int(5) DEFAULT NULL COMMENT '协议个数',
    `filter_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '规则编码',
    `rule_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '策略编码',
    `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
    `process` float(10, 0) DEFAULT NULL COMMENT '进度',
    `src_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '源ip',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE = InnoDB AUTO_INCREMENT = 548 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
-- changeset liurz:20230713-asset labels:增加pid和vid两个字段
alter table asset ADD COLUMN `pid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'PID';
alter table asset ADD COLUMN `vid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'VID';
-- changeset tuyijiang:20230725-asset labels:添加窃密两个表
CREATE TABLE IF NOT EXISTS `asset_steal_leak_value`  (
                                         `id` int(22) NOT NULL AUTO_INCREMENT,
                                         `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                                         `steal_leak_value` int(22) DEFAULT NULL COMMENT '窃泄密值',
                                         `create_time` datetime(0) DEFAULT NULL COMMENT '统计时间',
                                         `ip_num` bigint(20) DEFAULT NULL,
                                         PRIMARY KEY (`id`) USING BTREE,
                                         INDEX `steal_leak_value`(`steal_leak_value`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资产窃泄密值' ROW_FORMAT = Dynamic;
SET FOREIGN_KEY_CHECKS = 1;
CREATE TABLE IF NOT EXISTS `app_steal_leak_value` (
                                      `id` int(22) NOT NULL AUTO_INCREMENT,
                                      `app_id` int(20) DEFAULT NULL,
                                      `steal_leak_value` int(22) DEFAULT NULL COMMENT '窃泄密值',
                                      `create_time` datetime DEFAULT NULL COMMENT '统计时间',
                                      `type` int(1) DEFAULT '0' COMMENT '类型：0应用服务器，1网络边界',
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
-- changeset tyj:20230825-dimension_table labels:添加条件过滤字段filter_con；dimension_table_field添加字段alias_name(别名)
alter table dimension_table ADD COLUMN `filter_con` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '条件过滤字段';
alter table dimension_table_field ADD COLUMN `alias_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '别名';
-- changeset liurz:20230828-base_auth_config labels:审批信息涉及相关新建表
CREATE TABLE IF NOT EXISTS `base_auth_config`  (
                                     `id` int(10) NOT NULL AUTO_INCREMENT,
                                     `src_obj_label` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '源对象名称',
                                     `src_obj` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '源对象标识',
                                     `dst_obj_label` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '目的对象名称',
                                     `dst_obj` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '目的对象标识',
                                     `opt` int(5) DEFAULT NULL COMMENT '操作类型',
                                     `type_id` int(10) DEFAULT NULL COMMENT '审批类型ID',
                                     `extend_label` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '扩展对象名称',
                                     `extend_obj` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '扩展对象标识',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '审批信息配置表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `base_auth_type_config`  (
                                          `id` int(10) NOT NULL AUTO_INCREMENT,
                                          `label` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '审批类型名称',
                                          `src_obj_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '源对象类型',
                                          `dst_obj_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '目的对象类型',
                                          `opt` int(5) DEFAULT NULL COMMENT '动作',
                                          `src_obj_label` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '源对象类型名称',
                                          `dst_obj_label` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '目标对象类型名称',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '审批类型配置表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `base_auth_common_config`  (
                                            `conf_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'key',
                                            `conf_value` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT 'value',
                                            `conf_time` datetime(0) DEFAULT NULL COMMENT '时间',
                                            `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注',
                                            PRIMARY KEY (`conf_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '审批类型基础配置表' ROW_FORMAT = Dynamic;
-- changeset liurz:20230901-base_auth_config labels:新增时间字段
alter table base_auth_config ADD COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间';

-- changeset tyj:20230908-app_sys_manager labels:添加业务与管理入口
alter table app_sys_manager ADD COLUMN `app_url` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '业务入口';
alter table app_sys_manager ADD COLUMN `operation_url` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '管理入口';
-- changeset liurz:20230911-evaluation_tables labels:自查自评需求关联表
CREATE TABLE IF NOT EXISTS `self_inspection_evaluation`  (
                                               `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
                                               `check_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '检查大类',
                                               `genetic_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '成因类型',
                                               `org_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '待查部门',
                                               `occur_count` int(10) DEFAULT NULL COMMENT '发生次数',
                                               `status` int(2) DEFAULT NULL COMMENT '自查自评状态(0:未开始,1:已自查自评)',
                                               `create_time` datetime(0) DEFAULT NULL COMMENT '自查自评项产生时间',
                                               `ev_time` datetime(0) DEFAULT NULL COMMENT '自查自评开展时间',
                                               `ev_user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '自查自评人员名称',
                                               `ev_user_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '自查自评人员code',
                                               `ev_result` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '自查自评结果',
                                               `rectification` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '整改情况',
                                               `event_ids` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关联事件ID(多个逗号分割)',
                                               `ref_process_id` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关联自查自评策略中间表id(多个逗号分割)',
                                                `ref_id` int(5) DEFAULT NULL COMMENT '关联策略id',
                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '自查自评结果表' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `self_inspection_evaluation_config`  (
                                                      `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                      `check_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '检查大类',
                                                      `genetic_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '成因类型',
                                                       `department_num` int(5) DEFAULT NULL COMMENT '事件部门默认数量(0表示全部)',
                                                      `department_modify` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '部门数量是否支持修改(是、否)',
                                                      `threshold_count` int(5) DEFAULT NULL COMMENT '事件频率阀值',
                                                      `check_department` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '待查部门',
                                                      `sell_conditions` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '推荐条件',
                                                      `sell_reason` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '推荐原因',
                                                      `role_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '涉事人角色',
                                                      `dimension` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '策略维度(部门、人员)',
                                                      PRIMARY KEY (`id`) USING BTREE,
                                                      UNIQUE INDEX `cofig_ref`(`check_type`, `genetic_type`, `check_department`) USING BTREE COMMENT '检查大类、成因类型、待查部门唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '自查自评策略配置表' ROW_FORMAT = Dynamic;

CREATE TABLE `self_inspection_evaluation_process`  (
                                                       `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键ID',
                                                       `org_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '部门名称',
                                                       `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '事件责任人名称',
                                                       `check_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '检查大类',
                                                       `check_dep` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '待查部门(策略中)',
                                                       `genetic_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '成因类型',
                                                       `event_count` int(5) DEFAULT NULL COMMENT '事件数量',
                                                       `event_ids` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关联事件ID(多个逗号分割)',
                                                       `ref_id` int(11) DEFAULT NULL COMMENT '关联策略Id',
                                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '自查自评策略中间表' ROW_FORMAT = Dynamic;
-- changeset zzf:20230914-event_deal_busi_data_tables labels:事件处置业务数据表
CREATE TABLE `event_deal_busi_data` (
                                        `guid` varchar(64) NOT NULL COMMENT '主键id',
                                        `business_need` tinyint(2) DEFAULT NULL COMMENT '是否正常业务需要  1：是 0：否',
                                        `false_positive` tinyint(2) DEFAULT NULL COMMENT '是否误报 1：是 0：否',
                                        `approve_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '审批登记情况\n',
                                        `device_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '设备基本情况\n',
                                        `device_res_person_list` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '设备责任人情况',
                                        `event_res_person_list` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '涉事人员情况',
                                        `protection_strategy` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '防护策略情况\n',
                                        `malware_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '恶意程序情况',
                                        `download_files_list` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '文件下载或刻录记录',
                                        `result_evaluation` tinyint(4) DEFAULT NULL COMMENT '失泄密评估',
                                        `result_details` varchar(1255) DEFAULT NULL COMMENT '详细描述失泄密情况\n',
                                        `confidentiality_publicity` varchar(1550) DEFAULT NULL COMMENT '保密宣传教育情况',
                                        `confidentiality_rules` varchar(2150) DEFAULT NULL COMMENT '相关保密制度情况',
                                        `cause_type` varchar(200) DEFAULT NULL COMMENT '事件成因类型',
                                        `cause` varchar(1200) DEFAULT NULL COMMENT '事件成因说明',
                                        `event_inquriy` varchar(1200) DEFAULT NULL COMMENT '事件详细过程',
                                        `rectification` varchar(1255) DEFAULT NULL COMMENT '整改措施',
                                        `deal_status` int(10) NOT NULL DEFAULT '1' COMMENT '处置状态 1：处置中 2：处置完成',
                                        `rule_id` varchar(64) NOT NULL COMMENT '事件策略id',
                                        `event_id` varchar(64) NOT NULL COMMENT '告警事件id',
                                        `create_time` datetime NOT NULL COMMENT '事件处置工单创建时间',
                                        `finish_time` datetime DEFAULT NULL COMMENT '事件处置完成时间',
                                        PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='事件处置业务数据表';
-- changeset zzf:20230920-filter_operator labels:规则表新增字段start_config
ALTER TABLE filter_operator
    ADD COLUMN `start_config` text AFTER `rule_type`;

-- changeset liurz:20230919-asset labels:管理URL
alter table asset ADD COLUMN `operation_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '管理入口url';
-- changeset liurz:20231009-asset labels:事件关联流程配置表
CREATE TABLE IF NOT EXISTS  `flow_event_ref_config`  (
    `event_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '事件分类Id',
    `event_type_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '事件分类名称',
    `flow_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '流程名称',
    PRIMARY KEY (`event_type`) USING BTREE
    ) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '事件关联流程配置表' ROW_FORMAT = Dynamic;

-- changeset liurz:20231010-supervise_task labels:事件ID长度修改
alter table supervise_task modify  event_id varchar(200) COMMENT '事件ID';
-- changeset zzf:20231010-myticket_template my_ticket event_alarm_setting labels:修改有关字段的长度
ALTER TABLE myticket_template MODIFY COLUMN form_data longtext;
ALTER TABLE my_ticket MODIFY COLUMN form_data longtext;
ALTER TABLE event_alarm_setting MODIFY COLUMN link_guid varchar(200);
-- changeset liurz:20231010-event_deal_busi_data labels:子段长度修改
alter table event_deal_busi_data modify  event_id varchar(255) COMMENT '事件ID';
alter table event_deal_busi_data modify  guid varchar(255) COMMENT 'id';


-- changeset tuyj:20231020 labels:新标准维表建表
CREATE TABLE IF NOT EXISTS `baseline_admin_operation_protocol` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `app_protocol` varchar(500) DEFAULT NULL COMMENT 'null',
  `network_protocol` varchar(500) DEFAULT NULL COMMENT 'null',
  `transport` varchar(500) DEFAULT NULL COMMENT 'null',
  `session_protocol` varchar(500) DEFAULT NULL COMMENT 'null',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;



CREATE TABLE IF NOT EXISTS `baseline_admin_use_dport` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `dport` varchar(500) DEFAULT NULL COMMENT '目标端口',
  `sip` varchar(500) DEFAULT NULL COMMENT 'null',
  `dip` varchar(500) DEFAULT NULL COMMENT 'null',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `baseline_net_all_protocol` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `app_protocol` varchar(500) DEFAULT NULL COMMENT 'null',
  `network_protocol` varchar(500) DEFAULT NULL COMMENT 'null',
  `transport` varchar(500) DEFAULT NULL COMMENT 'null',
  `session_protocol` varchar(500) DEFAULT NULL COMMENT 'null',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `baseline_app_communicate_sys` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `sys_id` varchar(500) DEFAULT NULL COMMENT 'null',
  `ip` varchar(500) DEFAULT NULL COMMENT 'null',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `baseline_app_provide_protocols` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `dst_std_sys_id` varchar(500) DEFAULT NULL,
  `network_protocol` varchar(500) DEFAULT NULL,
  `transport` varchar(500) DEFAULT NULL,
  `session_protocol` varchar(500) DEFAULT NULL,
  `app_protocol` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `baseline_server_all_ports` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `dport` varchar(500) DEFAULT NULL COMMENT 'null',
  `dip` varchar(500) DEFAULT NULL COMMENT 'null',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `baseline_external_access_protocol` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `app_protocol` varchar(500) DEFAULT NULL COMMENT 'null',
  `network_protocol` varchar(500) DEFAULT NULL COMMENT 'null',
  `transport` varchar(500) DEFAULT NULL COMMENT 'null',
  `session_protocol` varchar(500) DEFAULT NULL COMMENT 'null',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `baseline_external_access_port` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `dport` int(11) DEFAULT NULL COMMENT 'null',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE IF NOT EXISTS `baseline_src_total_bytes` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `client_total_byte_max` double(10,2) DEFAULT NULL COMMENT '最大值',
  `src_std_dev_safety_marign` varchar(500) DEFAULT NULL COMMENT 'null',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `baseline_dst_total_bytes` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `server_total_byte_max` double(10,2) DEFAULT NULL COMMENT '最大值',
  `dst_std_dev_safety_marign` varchar(500) DEFAULT NULL COMMENT 'null',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `baseline_admin_down_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `sip` varchar(500) DEFAULT NULL COMMENT 'null',
  `sip_doc_count_max` double(10,2) DEFAULT NULL COMMENT '最大值',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `baseline_user_down_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `sip` varchar(500) DEFAULT NULL COMMENT 'null',
  `sip_doc_count_max` double(10,2) DEFAULT NULL COMMENT '最大值',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `baseline_admin_print_brun` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `dev_ip` varchar(500) DEFAULT NULL COMMENT 'null',
  `file_count_max` double(10,2) DEFAULT NULL COMMENT '最大值',
  `op_type` varchar(500) DEFAULT NULL COMMENT 'null',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `baseline_user_print_brun` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `foreign_key_id` varchar(50) NOT NULL COMMENT '被引用规则id',
  `filter_code` varchar(64) DEFAULT NULL COMMENT '规则编码',
  `rule_code` varchar(64) DEFAULT NULL COMMENT '策略编码',
  `is_sync` bigint(1) DEFAULT NULL COMMENT '是否同步',
  `dev_ip` varchar(500) DEFAULT NULL COMMENT 'null',
  `file_count_max` double(10,2) DEFAULT NULL COMMENT '最大值',
  `op_type` varchar(500) DEFAULT NULL COMMENT 'null',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS `flink_offline_log` (
                                     `guid` varchar(64) NOT NULL,
                                     `rule_code` varchar(255) DEFAULT NULL COMMENT '策略code',
                                     `filter_code` varchar(128) DEFAULT NULL COMMENT '规则code',
                                     `create_time` datetime NOT NULL COMMENT '创建时间',
                                     PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='离线任务日志记录表';
-- changeset zzf:20231026 labels:新增字段
ALTER TABLE `supervise_task`
  ADD COLUMN  `disposal_describe` varchar(255)  NULL,
  ADD COLUMN  `warnning_id` varchar(64)  NULL,
  ADD column  `busi_args` text  NULL;


-- changeset tyj:20231106 labels:新增字段
ALTER TABLE `internet_info_manage`
  ADD COLUMN  `ip` text  NULL,
  ADD COLUMN  `name` varchar(225)  NULL;

-- changeset tyj:20231109 labels:新增审批表

CREATE TABLE IF NOT EXISTS `base_auth_app` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_id` int(11) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL COMMENT 'ip',
  `type` int(1) DEFAULT 0 COMMENT '0ip为内部访问 1ip为外部访问',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE IF NOT EXISTS `base_auth_internet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `internet_id` int(11) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE IF NOT EXISTS `base_auth_operation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varchar(255) DEFAULT NULL,
  `dst_ip` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `type` int(1) DEFAULT 0 COMMENT '0资产类型 1应用系统',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE IF NOT EXISTS `base_auth_print_burn` (
  `id` int(22) NOT NULL AUTO_INCREMENT,
  `ip` varchar(100) DEFAULT NULL,
  `type` int(1) DEFAULT 1 COMMENT '1打印 2刻录',
  `decide` int(1) DEFAULT 0 COMMENT '0允许 1不允许',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
  )