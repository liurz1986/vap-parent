--liquibase formatted sql
--changeset lilang:20191119-apiAdmin-1119 labels:inittables
CREATE TABLE IF NOT EXISTS `app` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `client_id` varchar(64) DEFAULT NULL,
  `client_secret` varchar(64) DEFAULT NULL,
  `scope` varchar(32) DEFAULT NULL,
  `authorized_grant_types` varchar(64) DEFAULT NULL,
  `third` tinyint(4) unsigned zerofill DEFAULT '0000' COMMENT '是否是第三方应用，1：是，2：否',
  `name` varchar(255) NOT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `type` tinyint(4) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `status` tinyint(4) NOT NULL,
  `createTime` datetime DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  `parent_id` int(11) DEFAULT '0',
  `folder` tinyint(4) DEFAULT '0' COMMENT '0非分类1分类',
  `search_info` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1149 DEFAULT CHARSET=utf8 COMMENT='app';


CREATE TABLE IF NOT EXISTS `app_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `sort` smallint(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5458 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `app_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `base_area` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `area_code` varchar(20) DEFAULT NULL COMMENT '区域编码',
  `area_name` varchar(32) DEFAULT NULL COMMENT '区域名称',
  `ip_range` varchar(64) DEFAULT NULL COMMENT 'ip范围',
  `parent_code` varchar(20) DEFAULT NULL COMMENT '上级编号',
  `description` varchar(64) DEFAULT NULL COMMENT '描述',
  `area_code_sub` varchar(20) DEFAULT NULL COMMENT '截取编码（确认地区）',
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区域表';


CREATE TABLE IF NOT EXISTS `base_area_ip_segment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `area_code` varchar(16) NOT NULL COMMENT '区域编码',
  `start_ip` varchar(32) NOT NULL COMMENT '开始IP地址段',
  `end_ip` varchar(32) NOT NULL COMMENT '结束IP地址段',
  `start_ip_num` bigint(11) NOT NULL COMMENT '开始IP地址段转换成整型',
  `end_ip_num` bigint(11) NOT NULL COMMENT '结束IP地址段转换成整型',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='ip地址段表';

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=923 DEFAULT CHARSET=utf8 COMMENT='字典表';


CREATE TABLE IF NOT EXISTS `base_koal_org` (
  `uu_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) DEFAULT NULL COMMENT '机构编码',
  `name` varchar(64) DEFAULT NULL COMMENT '机构名称',
  `short_name` varchar(256) DEFAULT NULL COMMENT '机构简称',
  `other_name` varchar(256) DEFAULT NULL COMMENT '其他名称',
  `type` int(11) DEFAULT NULL COMMENT '结构类型--  字典表	',
  `status` int(11) DEFAULT NULL COMMENT '机构状态--  字典表	',
  `old_code` varchar(8) DEFAULT NULL COMMENT '原机构代码',
  `parent_code` varchar(64) DEFAULT NULL COMMENT '上级机构编码',
  `start_date` varchar(32) DEFAULT NULL COMMENT '启用日期',
  `end_date` varchar(32) DEFAULT NULL COMMENT '停用日期',
  `old_code_end` varchar(32) DEFAULT NULL COMMENT '原机构代码停用日期',
  `update_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `sort` int(11) DEFAULT NULL COMMENT '排序，默认为0',
  `org_hierarchy` tinyint(4) DEFAULT NULL COMMENT '0： 部 1：省 2：市 3：区（县） 4：派出所',
  `business_line` varchar(16) DEFAULT '1',
  `sub_code` varchar(64) DEFAULT NULL,
  `secret_level` int(11) DEFAULT 5 COMMENT '保密等级',
  `secret_qualifications` int(11) DEFAULT NULL COMMENT '保密资格',
  `org_type` int(11) DEFAULT NULL COMMENT '单位类别',
  `protection_level` int(11) DEFAULT NULL COMMENT '防护等级',
  PRIMARY KEY (`uu_id`) USING BTREE,
  KEY `index_code` (`code`) USING BTREE,
  KEY `sub_code` (`sub_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `base_org_ip_segment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `area_code` varchar(16) DEFAULT NULL COMMENT '区域编码',
  `area_name` varchar(64) DEFAULT NULL COMMENT '区域名称',
  `department_code` varchar(8) DEFAULT NULL COMMENT '部门编号',
  `department_name` varchar(64) DEFAULT NULL COMMENT '部门名称',
  `parent_code` varchar(16) DEFAULT NULL COMMENT '父节点编码',
  `area_identi_code` int(6) DEFAULT NULL COMMENT '地域识别码P取值',
  `net_partition_code` int(6) DEFAULT NULL COMMENT '网络分区段',
  `start_ip_segment` varchar(32) DEFAULT NULL COMMENT '开始IP地址段',
  `end_ip_segment` varchar(32) DEFAULT NULL COMMENT '结束IP地址段',
  `start_ip_num` bigint(11) DEFAULT NULL COMMENT '开始IP地址段转换成整型',
  `end_ip_num` bigint(11) DEFAULT NULL COMMENT '结束IP地址段转换成整型',
  `subnet_mask` tinyint(4) DEFAULT NULL COMMENT '子网掩码',
  `ip_type` tinyint(4) DEFAULT NULL COMMENT 'ip分布',
  `ip_use` tinyint(4) DEFAULT NULL COMMENT '启用和预留的地域识别码',
  `description` varchar(64) DEFAULT NULL COMMENT '描述',
  `is_delete` tinyint(4) DEFAULT NULL COMMENT '是否删除',
  `modifier_id` varchar(32) DEFAULT NULL COMMENT '修改人id',
  `modifier_name` varchar(32) DEFAULT NULL COMMENT '修改人name',
  `org_hierarchy` tinyint(4) DEFAULT NULL,
  `is_allocation` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='ip地址段表';


CREATE TABLE IF NOT EXISTS `base_org_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '关系键',
  `user_id` int(255) NOT NULL COMMENT '用户ID',
  `department_code` varchar(64) NOT NULL COMMENT '机构编码',
  `is_leader` tinyint(4) DEFAULT '0' COMMENT '是否领导 ： 0=不是领导，1=是领导',
  PRIMARY KEY (`id`),
  UNIQUE KEY `userId` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=150 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `base_organinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orgcode` varchar(64) DEFAULT NULL,
  `uporgid` varchar(64) DEFAULT NULL,
  `orgtype` varchar(8) DEFAULT NULL,
  `orgname` varchar(128) DEFAULT NULL,
  `orgshortname` varchar(128) DEFAULT NULL,
  `orgothernames` varchar(128) DEFAULT NULL,
  `islisted` varchar(8) DEFAULT NULL,
  `listedorgid` varchar(64) DEFAULT NULL,
  `state` varchar(8) DEFAULT NULL,
  `updatetime` varchar(32) DEFAULT NULL,
  `orderTag` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_orgcode` (`orgcode`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `base_security_domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) DEFAULT NULL,
  `domain_name` varchar(128) DEFAULT NULL,
  `ip_range` varchar(64) DEFAULT NULL COMMENT 'ip范围',
  `parent_code` varchar(50) DEFAULT NULL COMMENT '上级编号',
  `description` varchar(64) DEFAULT NULL COMMENT '描述',
  `sort` int(11) DEFAULT NULL,
  `org_hierarchy` tinyint(5) DEFAULT NULL COMMENT '0为一级单位，1为二级单位，2为三级单位',
  `sub_code` varchar(100) DEFAULT NULL,
  `secret_level` int(11) DEFAULT NULL COMMENT '保密等级',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `sub_code` (`sub_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `base_security_domain_ip_segment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) DEFAULT NULL,
  `start_ip` varchar(32) NOT NULL COMMENT '开始IP地址段',
  `end_ip` varchar(32) NOT NULL COMMENT '结束IP地址段',
  `start_ip_num` bigint(11) NOT NULL COMMENT '开始IP地址段转换成整型',
  `end_ip_num` bigint(11) NOT NULL COMMENT '结束IP地址段转换成整型',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='ip地址段表';


CREATE TABLE IF NOT EXISTS `big_screen` (
  `guid` varchar(32) NOT NULL,
  `title` varchar(32) DEFAULT NULL COMMENT '大屏展示标题',
  `intro` varchar(200) DEFAULT NULL COMMENT '简介',
  `url` varchar(255) DEFAULT NULL COMMENT '大屏展示地址',
  `img` varchar(255) DEFAULT NULL COMMENT '图片路径',
  `resolution` varchar(255) DEFAULT NULL COMMENT '分辨率',
  `flag` int(1) DEFAULT NULL COMMENT '是否显示0显示1不显示',
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='大屏展示表';


CREATE TABLE IF NOT EXISTS `business_view` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `img_url` varchar(255) DEFAULT NULL COMMENT '展示图片地址',
  `title` varchar(255) DEFAULT NULL COMMENT 'tip名称',
  `url` varchar(255) DEFAULT NULL COMMENT '打开地址的路径',
  `intro` varchar(255) DEFAULT NULL COMMENT '简介',
  `bs_guid` varchar(32) NOT NULL COMMENT 'big_screen表的guid',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务视图表';


CREATE TABLE IF NOT EXISTS `file_info` (
  `guid` varchar(32) NOT NULL COMMENT '组件guid',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `file_name` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT 'oss' COMMENT '文件类型',
  `namespace` varchar(255) DEFAULT NULL,
  `override` int(11) DEFAULT NULL,
  `upload_type` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `bucketname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `license` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product` int(20) DEFAULT NULL,
  `module` varchar(100) DEFAULT NULL,
  `updatetime` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `log_statistics` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `category` varchar(128) DEFAULT NULL COMMENT '类别',
  `area_name` varchar(32) DEFAULT NULL COMMENT '区域[类似： 湖北省]',
  `channel` varchar(64) DEFAULT NULL COMMENT '渠道[通过什么工具来]',
  `source_position` varchar(128) DEFAULT NULL COMMENT '来源位置',
  `source_company` varchar(128) DEFAULT NULL COMMENT '来源厂商',
  `source_ip` varchar(32) DEFAULT NULL COMMENT '来源IP',
  `storage_size` bigint(20) DEFAULT NULL COMMENT '数据大小',
  `storage_count` bigint(20) DEFAULT NULL COMMENT '数据[昨天新增量]条数',
  `storage_date` varchar(16) DEFAULT NULL COMMENT '日期[数据日期，今天会统计昨天的，，这日期就昨天的日期]',
  `category_number` varchar(16) DEFAULT NULL COMMENT '日志分类编号',
  `sub_category_number` varchar(16) DEFAULT NULL COMMENT '日志子类别编号',
  `small_category_number` varchar(16) DEFAULT NULL COMMENT '日志小类别编号',
  `category_name` varchar(128) DEFAULT NULL COMMENT '日志分类名称',
  `sub_category_name` varchar(128) DEFAULT NULL COMMENT '日志子类别名称',
  `small_category_name` varchar(128) DEFAULT NULL COMMENT '日志小类别名称',
  `area_code` varchar(100) DEFAULT NULL COMMENT '区域编码',
  `small_category_tablename` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4518 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `message` (
  `id` int(36) NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `title` varchar(255) DEFAULT NULL COMMENT '消息标题',
  `content` varchar(512) DEFAULT NULL COMMENT '消息内容,支持HTML语法格式（说明：目前没有做防XSS）',
  `url` varchar(255) DEFAULT NULL COMMENT '消息可以跳转到的链接地址',
  `status` tinyint(4) DEFAULT '0' COMMENT '0：未读，1：已读',
  `source` tinyint(4) DEFAULT '0' COMMENT '0：系统公告，1：应用提示，2：用户私信',
  `from_id` int(11) DEFAULT NULL COMMENT '发起用户消息ID',
  `send_time` datetime DEFAULT NULL,
  `read_time` datetime DEFAULT NULL,
  `send_batch` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1747 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `organization` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '组织机构ID 主键',
  `businessLine` varchar(32) DEFAULT '' COMMENT '业务线，默认 default',
  `orgHierarchy` tinyint(4) NOT NULL DEFAULT '3' COMMENT '0： 部 1：省 2：市 3：区',
  `orgcode` varchar(255) NOT NULL COMMENT '机构编码',
  `treePath` varchar(255) DEFAULT NULL COMMENT '机构路径',
  `orgName` varchar(255) NOT NULL COMMENT '机构名称',
  `parentId` int(11) NOT NULL DEFAULT '0' COMMENT '所属机构 ID',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `updateTime` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `organization_ip` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `orgId` int(11) NOT NULL COMMENT '机构ID',
  `orgTreePath` varchar(255) DEFAULT NULL,
  `startIp` varchar(255) NOT NULL,
  `endIp` varchar(255) NOT NULL,
  `startIpNum` bigint(11) NOT NULL,
  `endIpNum` bigint(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `organization_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orgId` int(11) NOT NULL COMMENT '机构ID',
  `userId` int(11) NOT NULL COMMENT '用户ID',
  `isLeader` tinyint(4) DEFAULT '0' COMMENT '是否为领导，0：不是，1：是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `userId` (`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `name` varchar(64) NOT NULL COMMENT '资源类型',
  `title` varchar(128) DEFAULT NULL COMMENT '标题',
  `icon` varchar(256) DEFAULT NULL COMMENT 'icon',
  `type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '资源类型： 1=目录,2=链接,3=页面权限（不会出现在菜单里，但可以访问），4=操作权限（需要代码自行适配）',
  `path` varchar(256) DEFAULT NULL COMMENT '链接地址',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `service_id` varchar(64) NOT NULL COMMENT '资源所属的服务ID',
  `parent` int(11) DEFAULT 0 COMMENT '父级资源ID。如果是顶级：可以填分类：0= 管理平台、1=展示平台、2=大屏平台、3=用户平台',
  `sign` varchar(255) DEFAULT NULL,
  `disabled` tinyint(4) DEFAULT 0,
  `uid` varchar(64) DEFAULT NULL,
  `puid` varchar(64) DEFAULT NULL,
  `progress` tinyint(4) DEFAULT NULL COMMENT '菜单进展 1=未开始 2=待测试 3=可出货',
  `place` tinyint(4) DEFAULT NULL COMMENT '所在位置：1=管理页面 2=展示页面 3=大屏页面 4=小组件',
  `version_code` int(11) DEFAULT NULL COMMENT '版本编码',
  `three_powers` tinyint(4) DEFAULT NULL COMMENT '三权：1=安全审计员 2=系统管理员 4=安全保密员',
  `develop_status` tinyint(4) DEFAULT 0 COMMENT '0 开发完成，1 开发中',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=70362 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `resource_module` (
  `id` int(11) NOT NULL,
  `module_id` varchar(20) DEFAULT NULL,
  `module_name` varchar(50) DEFAULT NULL,
  `uid` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(64) NOT NULL COMMENT '角色名称',
  `code` varchar(32) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL COMMENT '角色描述',
  `built_in` int(11) DEFAULT NULL,
  `control` varchar(128) DEFAULT '',
  `three_powers` int(4) DEFAULT NULL COMMENT '1=安全审计员 2=系统管理员 4=安全保密员',
  `creator` int(11) DEFAULT NULL COMMENT '创建人',
  `guid` varchar(64) DEFAULT NULL,
  `status` int(4) DEFAULT NULL COMMENT '角色状态 0 正常 1禁用  2 删除',
  `org_id` text DEFAULT NULL COMMENT '用户管理区域编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `role_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL,
  `resource_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_id` (`role_id`,`resource_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2009 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `sys_app_privilege` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `privilege_id` int(20) NOT NULL,
  `app_id` int(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `privilege_id` (`privilege_id`,`app_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `sys_privilege` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '样式',
  `url` varchar(255) DEFAULT NULL COMMENT 'URL',
  `method` varchar(8) DEFAULT NULL,
  `type` tinyint(4) NOT NULL COMMENT '权限类型 ,0-菜单权限 ,1-API权限',
  `parent_id` int(20) DEFAULT '0' COMMENT '上级权限id',
  `enabled` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否可用 ,1-可用,0-不可用',
  `def_allow` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `tb_conf` (
  `conf_id` varchar(255) NOT NULL,
  `conf_enable` bit(1) DEFAULT NULL,
  `conf_value` text,
  `conf_time` datetime DEFAULT NULL,
  `status_update` bit(1) DEFAULT NULL,
  PRIMARY KEY (`conf_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `cty_id` int(11) DEFAULT NULL,
  `name` varchar(32) DEFAULT '' COMMENT '用户名称',
  `account` varchar(32) BINARY NOT NULL COMMENT '用户用到登录的帐号名',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `role_id` varchar(32) NOT NULL DEFAULT '3',
  `idcard` varchar(18) DEFAULT NULL,
  `phone` varchar(16) DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `status` tinyint(2) NOT NULL DEFAULT 0,
  `org_code` varchar(12) DEFAULT '',
  `org_name` varchar(64) DEFAULT '',
  `province` varchar(2) DEFAULT '',
  `city` varchar(4) DEFAULT '',
  `is_leader` tinyint(4) DEFAULT 0,
  `lastpwdupdatetime` datetime DEFAULT NULL COMMENT '最后一次密码修改时间',
  `lastlogintime` datetime DEFAULT NULL COMMENT '最后一次登陆时间',
  `logintimes` int(11) DEFAULT 0 COMMENT '尝试登陆次数',
  `creator` int(11) DEFAULT NULL COMMENT '创建人',
  `domain_code` text DEFAULT NULL,
  `domain_name` text DEFAULT NULL,
  `authority_type` tinyint(4) DEFAULT NULL COMMENT '安全域数据权限控制 0 关闭，1开启',
  `pwd_status` tinyint(4) DEFAULT 0 COMMENT '0 默认密码未修改，1 表示已修改',
  `salt` varchar(20) DEFAULT NULL COMMENT '密码盐',
  `guid` varchar(64) DEFAULT NULL,
  `person_id` int(11) DEFAULT NULL COMMENT '人员id',
  `org_id` text DEFAULT NULL COMMENT '用户管理区域编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `user_domain` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(20) NOT NULL,
  `domain_code` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `user_page` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `pages` text NOT NULL,
  `reside` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `user_preference` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `pages` text,
  `folders` text,
  `skin_class` varchar(30) DEFAULT NULL,
  `resolution` varchar(30) DEFAULT NULL,
  `modules` varchar(30) DEFAULT NULL COMMENT 'one模式一，two模式二',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=337 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` int(64) NOT NULL,
  `user_id` int(64) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `role_id` (`role_id`,`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `user_preference_config` (
  `id` int(50) NOT NULL AUTO_INCREMENT,
  `user_id` int(50) NOT NULL,
  `preference_config` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `file_md5` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file_id` varchar(255) DEFAULT NULL COMMENT '上传文件id',
  `file_type` varchar(100) DEFAULT NULL COMMENT '上传文件类型',
  `md5` varchar(255) DEFAULT NULL COMMENT '上传文件md5',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `file_upload_info` (
  `id` int(50) NOT NULL AUTO_INCREMENT,
  `file_id` varchar(100) DEFAULT '' COMMENT '上传文件标识',
  `file_name` varchar(255) DEFAULT NULL COMMENT '上传文件名称',
  `file_type` varchar(50) DEFAULT NULL COMMENT '上传文件类型',
  `file_path` varchar(255) DEFAULT NULL COMMENT '上传文件地址',
  `namespace` varchar(100) DEFAULT NULL COMMENT '命名空间',
  `msg` text COMMENT '上传文件信息',
  `thumb_media_id` varchar(255) DEFAULT NULL COMMENT '缩略图id',
  `user_id` int(50) DEFAULT NULL COMMENT '用户id',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户名称',
  `upload_type` int(10) DEFAULT NULL COMMENT '0是本地地址    1是Fastdfs地址',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`),
  KEY `file_id` (`file_id`) USING BTREE,
  KEY `user_name` (`user_name`) USING BTREE,
  KEY `create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `user_auth_login_field` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `auth_field_value` varchar(255) DEFAULT NULL COMMENT '授权字段值',
  `field_name` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户授权登陆字段信息表';


CREATE TABLE IF NOT EXISTS `message_template` (
  `guid` varchar(65) NOT NULL COMMENT '组件',
  `num` varchar(20) NOT NULL COMMENT '编码',
  `content` varchar(255) DEFAULT NULL COMMENT '内容',
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `short_message_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` varchar(50) DEFAULT NULL COMMENT '唯一ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `user_name` varchar(32) DEFAULT NULL COMMENT '用户名',
  `phone` varchar(16) DEFAULT NULL COMMENT '手机号',
  `status` int(4) DEFAULT NULL COMMENT '发送状态',
  `content` varchar(500) DEFAULT NULL COMMENT '发送内容',
  `create_time` datetime DEFAULT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='短信日志表';


CREATE TABLE IF NOT EXISTS  `workbench_authority`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `codes` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `workbench_config` varchar(2550) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `workbench_individuation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NULL DEFAULT NULL,
  `workbench_config` varchar(2550) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `codes` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `user_ukey` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `serial` varchar(1000) DEFAULT NULL COMMENT '序列号',
  `public_key` text DEFAULT NULL COMMENT '公钥',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `cascade_platform` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `platform_name` varchar(255) DEFAULT NULL COMMENT '平台区域名称',
  `platform_id` varchar(255) DEFAULT NULL COMMENT '平台id',
  `ip` varchar(255) DEFAULT NULL COMMENT '平台ip',
  `port` int(11) DEFAULT NULL COMMENT '平台端口',
  `token` varchar(255) DEFAULT NULL COMMENT '校验token',
  `status` int(4) DEFAULT 0 COMMENT '注册状态 0:未注册 1:已注册',
  `local` int(4) DEFAULT 0 COMMENT '是否本机 0:下级平台 1:本机平台',
  `product_type` int(4) DEFAULT 0 COMMENT '是否主审下级 0:是 1:否',
  `security_classification` varchar(255) DEFAULT NULL COMMENT '安全级别',
  `reg_status` int(4) DEFAULT 0 COMMENT '已注册状态 0:失败 1:成功',
  `reg_msg` varchar(255) DEFAULT NULL COMMENT '注册返回消息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='级联平台信息表';


CREATE TABLE IF NOT EXISTS `base_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '报表名称',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `sub_title` varchar(255) DEFAULT NULL COMMENT '副标题',
  `models` text DEFAULT NULL COMMENT '模板组',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` char(1) DEFAULT NULL COMMENT '状态（0：删除，1：正常）',
  `menu_enable` bit(1) DEFAULT NULL COMMENT '是否生成目录（1：是，0：否）',
  `params` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='报表信息表';

CREATE TABLE IF NOT EXISTS `base_report_model` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `sql` text DEFAULT NULL COMMENT '查询语句',
  `type` char(1) DEFAULT NULL COMMENT '类型（1：饼图，2：折线图，3：柱状图，4：表格,5:段落,6:引用,7:列表）',
  `params` varchar(255) DEFAULT NULL COMMENT '配置：[{"name":"区域","field":"area"}]',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `content` text DEFAULT NULL,
  `data_source_id` int(11) DEFAULT NULL COMMENT '数据源id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='报表模型表';

CREATE TABLE IF NOT EXISTS `base_data_source`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `name`        varchar(255)  DEFAULT NULL COMMENT '数据源名称',
    `url`         varchar(255)  DEFAULT NULL COMMENT '数据源地址',
    `username`    varchar(255)  DEFAULT NULL COMMENT '用户名',
    `password`    varchar(255)  DEFAULT NULL COMMENT '密码',
    `cluster_ip`  varchar(5000) DEFAULT NULL COMMENT '节点集群ip列表逗号分隔如\r\n192.168.120.245,192.168.120.242',
    `port`        varchar(10)   DEFAULT NULL COMMENT '端口',
    `type`        char(1)       DEFAULT NULL COMMENT '类型（1：mysql，2：es）',
    `driver`      varchar(255)  DEFAULT NULL COMMENT '连接驱动',
    `es_index`    varchar(255)  DEFAULT NULL COMMENT 'es的index',
    `status`      char(1)       DEFAULT NULL COMMENT '状态（0：无效；1有效）',
    `create_time` datetime      DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='数据源表'; 


CREATE TABLE IF NOT EXISTS  `supervise_data_submit`(  
  `guid` VARCHAR(36) NOT NULL,
  `data_type` int(11) NOT NULL COMMENT '数据类型',
  `data` json NOT NULL COMMENT '数据',
  `create_time` datetime NOT NULL COMMENT '数据产生时间',
  `submit_status` int(11) NOT NULL DEFAULT 0,
  `submit_time` datetime,
  `online_submit_result` json,
  PRIMARY KEY (`guid`)
) ENGINE=MYISAM DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS  `supervise_status_submit` (
  `guid` varchar(36) NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `run_state` int(11) DEFAULT NULL,
  `submit_time` datetime DEFAULT NULL,
  `submit_status` int(11) DEFAULT NULL,
  PRIMARY KEY (`guid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `alarm_item_collection` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `alarm_type` varchar(255) DEFAULT NULL COMMENT '告警类型',
  `alarm_level` int(11) DEFAULT NULL COMMENT '告警级别：1 低，2 中，3 高',
  `alarm_source` varchar(255) DEFAULT NULL COMMENT '告警来源',
  `alarm_desc` varchar(255) DEFAULT NULL COMMENT '告警名称',
  `alarm_status` int(11) DEFAULT NULL COMMENT '告警状态：0 未处理，1 已处理，2 忽略',
  `origin_data` text DEFAULT NULL,
  `alarm_time` datetime DEFAULT NULL COMMENT '告警时间',
  `update_time` datetime DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `base_person_zjg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_no` varchar(64) DEFAULT NULL COMMENT '员工编号',
  `user_name` varchar(64) DEFAULT NULL COMMENT '姓名',
  `sex` varchar(10) DEFAULT NULL COMMENT '性别',
  `user_idn_ex` varchar(32) NOT NULL COMMENT '身份证号',
  `person_type` varchar(10) DEFAULT NULL COMMENT '用户类型:1用户 2管理员',
  `person_rank` varchar(50) DEFAULT NULL COMMENT '职务',
  `secret_level` int(11) DEFAULT NULL COMMENT '保密等级',
  `org_code` varchar(64) DEFAULT NULL COMMENT '单位名称',
  `org_name` varchar(128) DEFAULT NULL COMMENT '单位名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `network_monitor` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id` varchar(255) DEFAULT NULL COMMENT '设备ID',
  `device_belong` varchar(255) DEFAULT '' COMMENT '设备所属单位',
  `device_location` varchar(255) DEFAULT NULL COMMENT '设备部署位置',
  `device_soft_version` varchar(255) DEFAULT NULL COMMENT '设备软件版本号',
  `device_port_id` int(11) DEFAULT NULL COMMENT '设备接口ID',
  `interface_icon` varchar(255) DEFAULT '' COMMENT '接口标识',
  `login_address` varchar(1000) DEFAULT NULL COMMENT '设备登录地址',
  `login_account` varchar(255) DEFAULT NULL COMMENT '设备登录账户',
  `login_password` varchar(255) DEFAULT NULL COMMENT '设备登录密码',
  `status` int(11) DEFAULT 0 COMMENT '状态',
  `data_type` int(11) DEFAULT NULL COMMENT '数据类型',
  `report_time` datetime DEFAULT NULL COMMENT '上报时间',
  `device_sys_version` varchar(255) DEFAULT NULL COMMENT '设备系统版本号',
  `device_bios_version` varchar(255) DEFAULT NULL COMMENT '固件版本',
  `device_cpu_core` int(11) DEFAULT NULL COMMENT '设备cpu物理核数量',
  `device_cpu_usage` int(11) DEFAULT NULL COMMENT 'CPU利用率',
  `device_mem_size` int(11) DEFAULT NULL COMMENT '内存大小',
  `device_hdisk_size` int(11) DEFAULT NULL COMMENT '硬盘大小',
  `device_hdisk_num` varchar(255) DEFAULT NULL COMMENT '硬盘序列号',
  `device_mem_usage` int(11) DEFAULT NULL COMMENT '内存利用率',
  `device_hdisk_usage` int(11) DEFAULT NULL COMMENT '硬盘使用率',
  `network_monitor_status` int(11) DEFAULT NULL COMMENT '网络状态 0 异常 1正常',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='网络检测器信息表';

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


CREATE TABLE IF NOT EXISTS `user_org` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '关系键',
  `user_id` int(255) NOT NULL COMMENT '用户ID',
  `org_id` int(11) DEFAULT NULL COMMENT '组织机构id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `role_org` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `org_id` int(11) DEFAULT NULL COMMENT '组织机构ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `db_backup_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `data_type` varchar(255) DEFAULT NULL COMMENT '数据类型',
  `table_name` varchar(1024) DEFAULT NULL COMMENT '相关表名称',
  `time_field` varchar(1024) DEFAULT NULL COMMENT '时间类型字段',
  `relate_service` varchar(255) DEFAULT NULL COMMENT '关联服务',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `db_backup_strategy` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `data_types` varchar(255) DEFAULT NULL COMMENT '数据类型',
  `backup_period` int(11) DEFAULT NULL COMMENT '备份周期',
  `backup_time` varchar(255) DEFAULT NULL COMMENT '备份时间',
  `max_version` int(11) DEFAULT NULL COMMENT '最大保存版本数',
  `strategy_status` int(11) DEFAULT NULL COMMENT '策略状态：0 未启用，1 已启用',
  `file_storage` varchar(255) DEFAULT NULL COMMENT '存储介质',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `db_operation_info` (
  `uuid` varchar(36) NOT NULL COMMENT '主键',
  `operation_type` int(11) DEFAULT NULL COMMENT '操作类型：1 备份，2 还原，3 上传，4 下载',
  `operation_status` int(11) DEFAULT NULL COMMENT '操作状态：1 成功，2 失败，3 运行中，4 已过期',
  `data_types` varchar(255) DEFAULT NULL COMMENT '数据类型',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `file_md5` varchar(255) DEFAULT NULL COMMENT '文件md5值',
  `file_storage` varchar(255) DEFAULT NULL COMMENT '存储介质',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `message` varchar(4000) DEFAULT NULL COMMENT '异常信息',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `upgrade_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_name` varchar(255) DEFAULT '' COMMENT '文件名称',
  `file_size` double(11,2) DEFAULT NULL COMMENT '文件大小',
  `upgrade_desc` varchar(255) DEFAULT '' COMMENT '升级说明',
  `upgrade_time` datetime DEFAULT NULL COMMENT '升级时间',
  `result` int(11) DEFAULT NULL COMMENT '升级结果：0 失败，1 成功',
  `message` varchar(255) DEFAULT '' COMMENT '失败信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `discover_condition` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `query_jsonstr` text COMMENT '内容',
  `index_id` text,
  `search_key` varchar(255) DEFAULT NULL COMMENT '搜索关键词',
  `search_time` varchar(50) DEFAULT NULL COMMENT '最近一次搜索时间',
  `search_count` int(11) DEFAULT NULL COMMENT '搜索次数',
  `start_time` varchar(50) DEFAULT NULL COMMENT '开始时间',
  `end_time` varchar(50) DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `discover_dictionary` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `index_name` varchar(255) DEFAULT NULL COMMENT '字典名称',
  `type` varchar(255) DEFAULT NULL COMMENT '字典类型',
  `index_id` varchar(255) DEFAULT NULL COMMENT '字典id',
  `description` varchar(255) DEFAULT NULL COMMENT '字典描述',
  `details` text COMMENT '详情',
  `state` int(11) DEFAULT '0' COMMENT '启用状态 0：启用，1：禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `discover_edge` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL COMMENT '关系名称',
  `description` varchar(516) NOT NULL COMMENT '关系描述',
  `index_name` varchar(128) NOT NULL COMMENT '索引名称',
  `time_field` varchar(64) DEFAULT NULL COMMENT '时间字段',
  `search_field` varchar(64) NOT NULL COMMENT '搜索字段',
  `search_entity_id` int(11) DEFAULT NULL COMMENT '搜索实体编号',
  `goal_field` varchar(64) DEFAULT NULL COMMENT '目标字段',
  `goal_entity_id` int(11) DEFAULT NULL COMMENT '目标实体编号',
  `type` varchar(8) NOT NULL COMMENT '关系类型：0-正向，1-反向',
  `ico_id` varchar(256) DEFAULT NULL COMMENT '图标路径',
  `last_update_time` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '最后修改时间',
  `built_in_condition` text DEFAULT NULL COMMENT '内置条件',
  `goal_field_aggr` int(11) NOT NULL DEFAULT 0 COMMENT '目标字段聚合 0：否，1：是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='图形探索关系表';


CREATE TABLE IF NOT EXISTS `discover_entity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '实体名称',
  `tip` varchar(255) NOT NULL COMMENT '搜索提示',
  `icoId` varchar(256) DEFAULT NULL COMMENT '图标路径',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `last_update_time` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '最后修改时间',
  `type` int(4) DEFAULT 0 COMMENT '1：身份证，2：ip地址，3：系统编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='图形探索实体表';


CREATE TABLE IF NOT EXISTS `discover_entity_index_rel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `index_id` int(11) NOT NULL COMMENT '索引编号',
  `entity_id` int(11) NOT NULL COMMENT '实体编号',
  `field` varchar(64) NOT NULL COMMENT '字段',
  `field_description` varchar(256) DEFAULT NULL COMMENT '字段描述',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `last_update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8 COMMENT='图形探索实体索引字段映射表';


CREATE TABLE IF NOT EXISTS `discover_index` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `index_id` varchar(255) DEFAULT NULL COMMENT '索引id',
  `index_name` varchar(255) DEFAULT NULL COMMENT '索引名称',
  `type` varchar(255) DEFAULT NULL COMMENT '索引类型',
  `title` varchar(255) DEFAULT NULL COMMENT '索引名称',
  `title_desc` varchar(255) DEFAULT NULL COMMENT '索引描述',
  `time_field_name` varchar(50) DEFAULT NULL COMMENT '时间字段',
  `index_fields` text DEFAULT NULL COMMENT '索引字段',
  `default_index` int(11) DEFAULT NULL COMMENT '是否默认索引 1：是，0：否',
  `category` varchar(255) DEFAULT NULL COMMENT '类别-用来检索入库量统计',
  `domain_field_name` varchar(255) DEFAULT NULL COMMENT '安全域字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='索引配置表';


CREATE TABLE IF NOT EXISTS `discover_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `index_id` varchar(128) DEFAULT NULL COMMENT '索引',
  `account` varchar(32) DEFAULT NULL COMMENT '用户账号',
  `role_id` varchar(64) DEFAULT NULL COMMENT '权限ID',
  `search_key` varchar(255) DEFAULT NULL COMMENT '查询内容',
  `search_source` text COMMENT '查询源内容',
  `search_time` datetime DEFAULT NULL COMMENT '查询时间',
  `start_time` varchar(50) DEFAULT NULL COMMENT '开始时间',
  `end_time` varchar(50) DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`),
  KEY `id` (`id`) USING BTREE,
  KEY `query` (`index_id`,`account`,`search_key`,`search_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=213 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `discover_topic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(2) DEFAULT NULL COMMENT '01 索引主题 02 索引分组',
  `name` varchar(64) DEFAULT NULL COMMENT '主题名称',
  `parent_id` int(11) DEFAULT NULL COMMENT '父ID',
  `index_id` varchar(1000) DEFAULT NULL,
  `filter_json` text COMMENT '过滤条件',
  `group_default` int(11) DEFAULT NULL COMMENT '默认主题  1为默认主题',
  `group_order` int(11) DEFAULT NULL COMMENT '分组排序',
  `status` varchar(2) DEFAULT NULL COMMENT '状态 01 可用，02禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `visual_dashboard` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(128) DEFAULT NULL COMMENT '名称',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `ui_state_json` text DEFAULT NULL COMMENT 'uiStateJSON对象',
  `time_restore` varchar(64) DEFAULT NULL COMMENT 'timeRestore',
  `image_id` int(11) DEFAULT NULL COMMENT '图片id',
  `first_flag` int(4) DEFAULT 0 COMMENT '是否首页',
  `mode` int(4) DEFAULT NULL COMMENT '模式',
  `type` tinyint(4) DEFAULT NULL COMMENT '类别：1 组，2 链接',
  `parent_id` int(11) DEFAULT NULL COMMENT '所属分组',
  `top` int(11) DEFAULT 0 COMMENT '置顶',
  `thumbnail_path` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `visual_map` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(128) DEFAULT NULL COMMENT '名称',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `map_json` mediumtext COMMENT '地图JSON数据',
  `map_default` varchar(2) DEFAULT NULL COMMENT '是否默认地图',
  `last_update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `visual_widget` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '描述',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `title` varchar(128) DEFAULT NULL COMMENT '名称',
  `visual_type` varchar(32) DEFAULT NULL COMMENT '图形类型',
  `data_type` int(11) DEFAULT NULL COMMENT '数据类型',
  `connection_name` varchar(255) DEFAULT NULL COMMENT '连接名称',
  `index_name` varchar(64) DEFAULT NULL COMMENT '索引名称',
  `index_type` varchar(64) DEFAULT NULL COMMENT '索引类型',
  `index_id` varchar(64) DEFAULT NULL COMMENT '索引id',
  `saved_search_id` varchar(64) DEFAULT NULL COMMENT '查询的ID',
  `ui_statejson` text COMMENT '视图状态',
  `vis_state` text COMMENT '图形配置',
  `search_sourcejson` text COMMENT '查询内容',
  `last_update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=675 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `visual_dashboard_share` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dashboard_id` int(11) DEFAULT NULL COMMENT '仪表盘id',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `token` varchar(255) DEFAULT NULL COMMENT 'token',
  `creator` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `expire_time` datetime DEFAULT NULL COMMENT '截止时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='仪表盘分享链接表';

CREATE TABLE IF NOT EXISTS `visual_screen` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '标准化大屏Key',
  `title` varchar(128) DEFAULT NULL COMMENT '标题',
  `time_restore` varchar(32) DEFAULT NULL,
  `ui_state_json` text COMMENT '组件配置',
  `color_scheme` varchar(32) DEFAULT NULL COMMENT '配色方案',
  `background_image` varchar(128) DEFAULT NULL COMMENT '背景图片',
  `effect` varchar(256) DEFAULT NULL COMMENT '页面特效配置',
  `thumbnail` varchar(128) DEFAULT NULL COMMENT '缩略图',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='标准化大屏表';


CREATE TABLE IF NOT EXISTS `explore_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account` varchar(255) DEFAULT NULL COMMENT '账户',
  `keyword` varchar(255) DEFAULT NULL COMMENT '关键词',
  `entity_id` int(11) DEFAULT NULL COMMENT '实体ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE  IF NOT EXISTS `visual_screen_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '标准化大屏Key',
  `title` varchar(128) DEFAULT NULL COMMENT '标题',
  `ui_state_json` text COMMENT '组件配置',
  `color_scheme` varchar(32) DEFAULT NULL COMMENT '配色方案',
  `background_image` varchar(128) DEFAULT NULL COMMENT '背景图片',
  `effect` varchar(256) DEFAULT NULL COMMENT '页面特效配置',
  `thumbnail` varchar(128) DEFAULT NULL COMMENT '缩略图',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='标准化大屏模板表';


CREATE TABLE IF NOT EXISTS `visual_database_connection` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `connection_name` varchar(255)  NULL DEFAULT NULL COMMENT '连接名称',
  `type` tinyint(2) NULL DEFAULT 0 COMMENT '0：内部数据库，1：外部数据库',
  `address` varchar(255)  NULL DEFAULT NULL COMMENT '数据库IP地址',
  `port` varchar(255)  NULL DEFAULT NULL COMMENT '端口号',
  `user` varchar(255)  NULL DEFAULT NULL COMMENT '数据库用户',
  `password` varchar(255)  NULL DEFAULT NULL COMMENT '数据库密码',
  `database_name` varchar(255)  NULL DEFAULT NULL COMMENT '数据库名称',
  `table_name` varchar(255)  NULL DEFAULT NULL COMMENT '表名',
  `table_description` varchar(255)  NULL DEFAULT NULL COMMENT '表注释',
  `time_field_name` varchar(255)  NULL DEFAULT NULL COMMENT '时间字段',
  `table_field_json` text  NULL COMMENT '表字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='可视化数据库连接配置表';


CREATE TABLE IF NOT EXISTS `discover_summarise` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `index_id` varchar(255) DEFAULT NULL COMMENT '索引id',
  `title_desc` varchar(255) DEFAULT NULL COMMENT '索引描述',
  `time_field_name` varchar(50) DEFAULT NULL COMMENT '时间字段',
  `index_fields` text COMMENT '索引字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='可视化概要配置表';


CREATE TABLE IF NOT EXISTS `visual_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `subTitle` varchar(255) DEFAULT NULL COMMENT '副标题',
  `theme_id` int(11) DEFAULT NULL COMMENT '主题ID',
  `time_restore` varchar(32) DEFAULT NULL COMMENT '默认时间',
  `ui_state` text DEFAULT NULL COMMENT '数据模板',
  `param` text DEFAULT NULL COMMENT '参数',
  `dataset` text DEFAULT NULL COMMENT '全局数据集',
  `catalog_id` int(11) DEFAULT NULL COMMENT '目录ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `visual_report_theme` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主题ID',
  `name` varchar(255) DEFAULT NULL COMMENT '主题名称',
  `size` varchar(4) DEFAULT NULL COMMENT '大小',
  `margin` tinyint(4) DEFAULT NULL COMMENT '边距, mm',
  `background` varchar(255) DEFAULT NULL COMMENT '背景图片',
  `logo` varchar(255) DEFAULT NULL COMMENT 'Logo',
  `title` varchar(255) DEFAULT NULL COMMENT '标题样式',
  `subTitle` varchar(255) DEFAULT NULL COMMENT '副标题样式',
  `topic` varchar(255) DEFAULT NULL COMMENT '目录样式',
  `sign` varchar(255) DEFAULT NULL COMMENT '水印',
  `header` tinytext COMMENT '页头',
  `footer` tinytext COMMENT '页尾',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='可视化报告模板表';

CREATE TABLE IF NOT EXISTS `visual_report_cycle` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `report_id` int(11) DEFAULT NULL COMMENT '报表ID',
  `title` varchar(255) DEFAULT NULL COMMENT '周期名称',
  `param` text DEFAULT NULL COMMENT '报表参数',
  `type` int(11) DEFAULT NULL COMMENT '0-自定义 1-单次 2-每小时 3-每天 4-每周 5-每月 ',
  `cron` varchar(255) DEFAULT NULL COMMENT 'cron 表达式',
  `count` int(11) DEFAULT NULL COMMENT '生成次数',
  `status` int(11) DEFAULT NULL COMMENT '0 停用 1 启用',
  `last_time` datetime DEFAULT NULL COMMENT '最后一次执行时间',
  `file_type` varchar(255) DEFAULT NULL,
  `report_type` varchar(255) DEFAULT NULL COMMENT '报表平台类型 node ，java',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `visual_report_cycle_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cycle_id` int(11) DEFAULT NULL COMMENT '周期ID',
  `cycle_title` varchar(255) DEFAULT NULL COMMENT '周期名称',
  `report_id` int(11) DEFAULT NULL COMMENT '报表ID',
  `report_title` varchar(255) DEFAULT NULL COMMENT '报表名称',
  `file_name` varchar(255) DEFAULT NULL COMMENT '生成报表名称',
  `file_path` varchar(255) DEFAULT NULL COMMENT '生成报表地址',
  `create_time` datetime DEFAULT NULL COMMENT '生成时间',
  `status` int(11) DEFAULT NULL COMMENT '0 生成失败 1生成中 2已生成',
  `file_id` varchar(255) DEFAULT NULL COMMENT '报表设计器生成ID',
  `source_type` int(255) DEFAULT NULL COMMENT '0 周期报表  1 手工生成',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `visual_report_catalog` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '报表分类ID',
  `name` varchar(255) DEFAULT NULL COMMENT '报表分类名称',
  `sort` int(255) DEFAULT NULL COMMENT '报表分类排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE   IF NOT EXISTS `sys_log` (
  `id` varchar(255) CHARACTER SET utf32 NOT NULL,
  `request_ip` varchar(255) DEFAULT NULL COMMENT ' 终端标识 操作IP',
  `type` int(11) DEFAULT NULL COMMENT '操作类型 0 登录 1查询 2 新增 3 修改 4 删除',
  `user_id` varchar(255) DEFAULT NULL COMMENT '操作人ID，身份证ID',
  `user_name` varchar(255) DEFAULT NULL COMMENT '操作人',
  `organization_name` varchar(255) DEFAULT NULL COMMENT '组织机构名称',
  `description` varchar(255) DEFAULT NULL COMMENT '操作描述',
  `request_url` varchar(255) DEFAULT NULL COMMENT '请求路径',
  `request_time` datetime DEFAULT NULL COMMENT '操作时间',
  `request_method` varchar(255) DEFAULT NULL COMMENT '请求方式',
  `method_name` varchar(255) DEFAULT NULL COMMENT '请求方法名',
  `bean_name` varchar(255) DEFAULT NULL COMMENT '请求beanm名称',
  `params_value` varchar(5000) DEFAULT NULL COMMENT '请求参数',
  `response_result` int(11) DEFAULT NULL COMMENT '操作结果',
  `login_type` int(11) DEFAULT NULL COMMENT '用户登录类型 0：普通登录 1：证书登录 2：虹膜登录"',
  `role_name` varchar(255) DEFAULT NULL COMMENT '角色名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--changeset jiangcaizheng:20220117-apiAdmin-0117 labels:addtables
CREATE TABLE    IF NOT EXISTS `base_report_interface`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接口名称',
  `url` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '接口地址（服务名加接口路径：http://api-admin/test）',
  `type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型（1：list类型  2：map类型  3：混合类型）',
  `result_info` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '接口返回数据说明，标明每一个字段的中文解释如下：\r\nkey1：名称\r\nkey2：编码\r\nkey3：区域...',
  `field_info` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段映射说明',
  `params` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接口参数\r\n格式：[{\r\n  \"name\":\"开始时间\",\r\n    \"filed\":\"starttime\"\r\n},{\r\n  \"name\":\"结束时间\",\r\n    \"filed\":\"endtime\"\r\n}]',
  `md5` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '指标md5值',
  `time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '报表接口配置表' ROW_FORMAT = Dynamic;
ALTER TABLE `base_report_model`
ADD COLUMN `is_interface` varchar(1) NULL DEFAULT 0 COMMENT '数据源是否是指标' AFTER `data_source_id`,
ADD COLUMN `interface_id` varchar(64) NULL COMMENT '指标编号' AFTER `is_interface`;

--changeset jiangcaizheng:20220125-apiAdmin-0125 labels:modify tables
ALTER TABLE `base_report_model`
ADD COLUMN `config` varchar(5000) NULL COMMENT '页面配置' AFTER `content`;


CREATE TABLE IF NOT EXISTS `collector_data_access` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '任务名称',
  `port` varchar(255) DEFAULT NULL COMMENT '端口号',
  `src_ip` varchar(255) DEFAULT NULL COMMENT '发送端IP',
  `type` int(4) DEFAULT NULL COMMENT '接入方式，1：UDP，2：TCP，3：HTTP',
  `collection_id` int(11) DEFAULT NULL COMMENT '规则集ID',
  `version` varchar(255) DEFAULT NULL COMMENT '规则集版本号',
  `cid` varchar(255) DEFAULT NULL COMMENT '采集器ID',
  `rule_json` text COMMENT '过滤规则',
  `source_type` int(11) DEFAULT NULL COMMENT '数据源类型，1：常规，2：离线导入，3：监测器转发',
  `source_id` int(11) DEFAULT NULL COMMENT '离线导入管理离线模板ID',
  `init_memory` int(11) DEFAULT '512' COMMENT '启动内存',
  `template_type` int(11) DEFAULT NULL COMMENT '模板类型',
  `build_type` int(11) DEFAULT 0 COMMENT '内置类型 0：自定义，1：预定义',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `collector_index` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '名称',
  `type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '数据类别',
  `fields` text COLLATE utf8_unicode_ci COMMENT '字段列表',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE IF NOT EXISTS `collector_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `description` varchar(1000) DEFAULT NULL COMMENT '描述',
  `collection_id` int(11) DEFAULT NULL COMMENT '规则集ID',
  `priority` int(11) DEFAULT '1' COMMENT '优先级',
  `source` text COMMENT '日志样例',
  `charater` text COMMENT '特征',
  `charater_type` tinyint(4) DEFAULT NULL COMMENT '特征类型 1：字符串，2：正则',
  `handler` varchar(50) DEFAULT NULL COMMENT '提取方式:TEST,JSON,SPLIT,REGEX',
  `split` varchar(255) DEFAULT NULL COMMENT '分隔符',
  `regex` text COMMENT '正则表达式',
  `relate_index` varchar(255) DEFAULT NULL COMMENT '关联索引ID',
  `fields` text COMMENT '字段列表',
  `renames` text COMMENT '字段重命名',
  `rule_json` text COMMENT '过滤规则',
  `js_content` text COMMENT '外部js',
  `body` text COMMENT '字段结构',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `collector_rule_collection` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `description` varchar(1000) DEFAULT NULL COMMENT '描述',
  `type` int(4) DEFAULT '0' COMMENT '类型 0：自定义，1：预定义',
  `version` varchar(255) DEFAULT NULL COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8 COMMENT='采集器规则集表';

CREATE TABLE IF NOT EXISTS `test_database` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `num` int(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `local_system_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `cpu_rate` double(4,4) DEFAULT NULL,
  `ram_rate` double(4,4) DEFAULT NULL,
  `disk_rate` double(4,4) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `supervise_data_receive` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `data_type` int(4) DEFAULT NULL,
  `receive_type` int(4) DEFAULT NULL,
  `data` text,
  `create_time` datetime DEFAULT NULL,
  `receive_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--changeset lilang:20220531-apiAdmin-001
CREATE TABLE IF NOT EXISTS `sync_base_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT '任务名称',
  `ip` varchar(50) DEFAULT NULL COMMENT 'ip地址',
  `port` varchar(50) DEFAULT NULL COMMENT '端口',
  `cron` varchar(50) DEFAULT NULL COMMENT '执行周期',
  `account` varchar(100) DEFAULT NULL COMMENT '账号',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `description` varchar(255) DEFAULT NULL COMMENT '备注',
  `type` varchar(50) DEFAULT NULL COMMENT '任务类型：asset资产，person人员，app应用',
  `source` varchar(100) DEFAULT NULL COMMENT '数据来源：公司-产品名称',
  `status` int(11) DEFAULT NULL COMMENT '状态：0启用，1停止',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='基础数据同步任务表';

--changeset lilang:20220606-apiAdmin-001
CREATE TABLE IF NOT EXISTS `asset_type_rel` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `audit_type` varchar(255) DEFAULT NULL COMMENT '主审类型',
  `asset_type_name` varchar(255) DEFAULT NULL COMMENT '自监管资产类型',
  `asset_type_guid` varchar(64) DEFAULT NULL COMMENT '自监管资产类型Guid',
  `type` int(4) DEFAULT NULL COMMENT '类别 1：主审，2：准入',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COMMENT='主审设备类型关系表';

--changeset bieao:20220606-apiAdmin-002
CREATE TABLE IF NOT EXISTS `offline_time_statistics`
(
    id              INT(11) AUTO_INCREMENT COMMENT '主键',
    user_no         VARCHAR(64)  NULL COMMENT '员工编号',
    ip              VARCHAR(255) NULL COMMENT 'ip地址',
    user_name       VARCHAR(255) NULL COMMENT '使用人',
    department_name VARCHAR(255) NULL COMMENT '部门',
    login_time      DATETIME     NULL COMMENT '登录时间',
    logout_time     DATETIME     NULL COMMENT '注销时间',
    count_time      INT(4)       NULL COMMENT '离线时长',
    current_day     DATE         NULL COMMENT '当前时间',
    login_type      INT(4)       NULL COMMENT '登录类型 1:登录,2:注销',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COMMENT='人员离线统计表';
--changeset lilang:20220718-apiAdmin-001
ALTER TABLE `network_monitor`
ADD COLUMN `interface_info`  text NULL COMMENT '设备配置信息',
ADD COLUMN `mem_total`  int(11) NULL COMMENT '内存总数',
ADD COLUMN `cpu_info`  text NULL COMMENT 'CPU信息',
ADD COLUMN `disk_info`  text NULL COMMENT '磁盘信息',
ADD COLUMN `address_code`  varchar(20) NULL COMMENT '行政区域编码',
ADD COLUMN `contact`  text NULL COMMENT '客户单位联系人信息',
ADD COLUMN `reg_type` int(4) NULL COMMENT '0：手工录入，1：在线注册',
ADD COLUMN `memo`  varchar(255) NULL COMMENT '备注信息';

--changeset lilang:20220721-apiAdmin-001
CREATE TABLE IF NOT EXISTS `sync_base_data_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_name` varchar(255) DEFAULT NULL COMMENT '任务名称',
  `type` varchar(20) DEFAULT NULL COMMENT '任务类型：asset资产，person人员，app应用',
  `source` varchar(255) DEFAULT NULL COMMENT '数据来源',
  `total_count` int(11) DEFAULT NULL COMMENT '同步数量',
  `create_time` datetime DEFAULT NULL COMMENT '同步时间',
  `status` int(4) DEFAULT NULL COMMENT '同步状态 0：成功，1：失败',
  `description` text COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='基础数据同步记录表';

--changeset bieao:20220817-apiAdmin-001
CREATE TABLE IF NOT EXISTS service_api(
    id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    service_id  INT          NULL COMMENT '服务ID',
    operate_key VARCHAR(255) NULL COMMENT '接口标识',
    path        VARCHAR(255) NULL COMMENT '路径',
    description VARCHAR(255) NULL COMMENT '描述',
    method      VARCHAR(16)  NULL COMMENT '服务方法：GET，POST，DELETE，PATCH',
    tags        VARCHAR(100) NULL COMMENT '控制器',
    tag_desc    VARCHAR(100) NULL COMMENT '控制器描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='服务接口表';

--changeset bieao:20220817-apiAdmin-002
CREATE TABLE IF NOT EXISTS service_module
(
    id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    name         VARCHAR(255) NULL COMMENT '服务名称',
    service_desc VARCHAR(255) NULL COMMENT '服务描述',
    type         INT(255)     NULL COMMENT '类型 0 微服务',
    prefix       VARCHAR(255) NULL COMMENT '服务前缀（多个使用逗号分割）',
    version      VARCHAR(32)  NULL COMMENT '服务版本',
    sync_time    DATETIME     NULL COMMENT '维护时间',
    sync_url     VARCHAR(255) NULL COMMENT '同步URL（同步swagger中URL）',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='服务模块表';

--changeset bieao:20220817-apiAdmin-003
CREATE TABLE IF NOT EXISTS resource_api
(
    id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    resource_id INT NULL COMMENT '资源ID',
    api_id      INT NULL COMMENT '接口ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='资源关联表';

--changeset lilang:20220818-apiAdmin-001
ALTER TABLE `sys_log` MODIFY COLUMN `description`  text COMMENT '操作描述';

--changeset lilang:20221014-apiAdmin-001
CREATE TABLE IF NOT EXISTS `alarm_item_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `alarm_type` varchar(255) DEFAULT NULL COMMENT '告警类型',
  `alarm_level` int(11) DEFAULT NULL COMMENT '告警级别：1 低，2 中，3 高',
  `alarm_source` varchar(255) DEFAULT NULL COMMENT '告警来源',
  `alarm_desc` varchar(255) DEFAULT NULL COMMENT '告警名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--changeset lilang:20221104-apiAdmin-001
ALTER TABLE `base_area_ip_segment` ENGINE=InnoDB;
ALTER TABLE `base_security_domain_ip_segment` ENGINE=InnoDB;
ALTER TABLE `supervise_status_submit` ENGINE=InnoDB;

--changeset lilang:20221118-apiAdmin-001
ALTER TABLE `sys_log` MODIFY COLUMN `description`  text COMMENT '操作描述';

--changeset lilang:20221205-apiAdmin-001
ALTER TABLE `base_org_ip_segment` ENGINE=InnoDB;

--changeset lilang:20230103-apiAdmin-001
ALTER TABLE `message_template` ADD COLUMN `name` varchar(255) DEFAULT NULL COMMENT '模板名称' after num;
ALTER TABLE `message_template` MODIFY COLUMN `content` text COMMENT '内容';

--changeset lilang:20230109-apiAdmin-001
ALTER TABLE `user` ADD COLUMN `ip_login`  tinyint(4) NULL DEFAULT 0 COMMENT '是否开启ip登录 0:否，1:是';

--changeset lilang:20230110-apiAdmin-001
ALTER TABLE `base_person_zjg`
ADD COLUMN `background_audit`  tinyint(4) NULL DEFAULT 0 COMMENT '背景审查 0：不通过，1：通过';
ALTER TABLE `base_person_zjg`
ADD COLUMN `background_audit_comment`  text NULL COMMENT '背景审查备注';
ALTER TABLE `base_person_zjg`
ADD COLUMN `background_audit_attachment`  varchar(64) NULL COMMENT '背景审查附件';
ALTER TABLE `base_person_zjg`
ADD COLUMN `skill_check`  tinyint(4) NULL DEFAULT 0 COMMENT '技能考核  0：不通过，1：通过';
ALTER TABLE `base_person_zjg`
ADD COLUMN `skill_check_comment`  text NULL COMMENT '技能考核备注';
ALTER TABLE `base_person_zjg`
ADD COLUMN `skill_check_attachment`  varchar(64) NULL COMMENT '技能考核附件';
ALTER TABLE `base_person_zjg`
ADD COLUMN `secret_protocol`  tinyint(4) NULL DEFAULT 0 COMMENT '保密协议 0：未签订，1：已签订';
ALTER TABLE `base_person_zjg`
ADD COLUMN `secret_protocol_comment`  text NULL COMMENT '技能考核备注';
ALTER TABLE `base_person_zjg`
ADD COLUMN `secret_protocol_attachment`  varchar(64) NULL COMMENT '技能考核附件';

--changeset lilang:20230116-apiAdmin-001
ALTER TABLE `base_person_zjg` ADD COLUMN `audit_attachment_name`  varchar(255) NULL COMMENT '背景审查附件名称';
ALTER TABLE `base_person_zjg` ADD COLUMN `check_attachment_name`  varchar(255) NULL COMMENT '技能考核附件名称';
ALTER TABLE `base_person_zjg` ADD COLUMN `protocol_attachment_name`  varchar(255) NULL COMMENT '保密协议附件名称';

--changeset lilang:20230316-apiAdmin-001
CREATE TABLE IF NOT EXISTS `user_module` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `user_id` int(11) DEFAULT NULL COMMENT '用户id',
   `module` varchar(64) DEFAULT NULL COMMENT '模块',
   `module_key` varchar(128) DEFAULT NULL COMMENT '模块key',
   `module_value` varchar(128) DEFAULT NULL COMMENT '模块value',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--changeset lilang:20230320-apiAdmin-001
ALTER TABLE `user_module`
MODIFY COLUMN `module_value`  text COMMENT '模块value';

--changeset lilang:20230327-apiAdmin-001
ALTER TABLE `collector_index`
ADD COLUMN `source_id`  int(11) NULL COMMENT '数据源ID';

--changeset lilang:20230403-apiAdmin-001
ALTER TABLE `collector_rule_collection`
ADD COLUMN `access_type`  int(4) NULL DEFAULT 1 COMMENT '接入方式，1：UDP，2：TCP，3：HTTP';

ALTER TABLE `collector_rule_collection`
ADD COLUMN `encoding`  varchar(64) NULL DEFAULT NULL COMMENT '字符编码';

--changeset lilang:20230512-apiAdmin-001
ALTER TABLE `user` MODIFY COLUMN `org_code`  varchar(64);

--changeset lilang:20230601-apiAdmin-001
ALTER TABLE `sync_base_data`
ADD COLUMN `start_time`  datetime NULL COMMENT '开始时间';

--changeset lilang:20230607-apiAdmin-001
ALTER TABLE `base_person_zjg`
ADD UNIQUE INDEX `user_no_index` (`user_no`) USING BTREE ;

--changeset lilang:20230613-apiAdmin-001
ALTER TABLE `base_report_interface`
ADD COLUMN `external_url` varchar(255) NULL COMMENT '外部接口地址' AFTER `md5`;

--changeset lilang:20230705-apiAdmin-001
ALTER TABLE `sys_log` ADD COLUMN `request_page_uri`  varchar(255) NULL COMMENT '请求页面';
ALTER TABLE `sys_log` ADD COLUMN `request_page_title`  varchar(255) NULL COMMENT '请求页面标题';

--changeset tyj:20230830-apiAdmin-001
ALTER TABLE `base_security_domain` ADD COLUMN `responsible_name`  varchar(255) NULL COMMENT '责任人名称';
ALTER TABLE `base_security_domain` ADD COLUMN `responsible_code`  varchar(100) NULL COMMENT '责任人code(用户编号）';
ALTER TABLE `base_security_domain` ADD COLUMN `org_name`  varchar(255) NULL COMMENT '组织结构名称';
ALTER TABLE `base_security_domain` ADD COLUMN `org_code`  varchar(100) NULL COMMENT '组织结构code';

--changeset liujinhui:20231101-apiAdmin labels:modify tables
ALTER TABLE collector_data_access ADD latest_receive_mgs_time VARCHAR(50) DEFAULT '-';
ALTER TABLE collector_data_access ADD latest_receive_max_count BIGINT DEFAULT 0;

--changeset liujinhui:20231117-apiAdmin  labels:4.23.0
ALTER TABLE sys_log MODIFY COLUMN params_value TEXT COMMENT '请求参数值';