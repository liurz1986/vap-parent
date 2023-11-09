INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据中心-通用管理', NULL, 1, NULL, 3, 0);
SET @parent = LAST_INSERT_ID();
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据源管理', NULL, 2, '/data/#/source', 1, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('偏好设置', NULL, 2, '/data/#/preference', 2, @parent);

INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据中心-搜索', 'file-search', 1, NULL, 4, 0);
SET @parent = LAST_INSERT_ID();
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('全网搜', NULL, 2, '/data/#/search', 1, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('搜索-主题设置', NULL, 2, '/data/#/topic', 2, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('菜单版 Demo1', NULL, 2, '/data/#/search/1', 3, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('菜单版 Demo2', NULL, 2, '/data/#/search/2', 4, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据中心-搜索-数据导出', NULL, 3, 'app-data-search-export', 0, @parent);

INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据中心-探索', NULL, 1, NULL, 5, 0);
SET @parent = LAST_INSERT_ID();
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('探索', 'exception', 2, '/data/#/discover', 1, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('探索-实体设置', NULL, 2, '/data/#/entity', 2, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('探索-关系设置', NULL, 2, '/data/#/edge', 3, @parent);

INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据中心-仪表盘', 'compass', 1, NULL, 6, 0);
SET @parent = LAST_INSERT_ID();
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('仪表盘管理', NULL, 2, '/data/#/dashboard', 1, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('仪表盘 Demo1', NULL, 2, '/data/#/visual/1', 2, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('仪表盘 Demo2', NULL, 2, '/data/#/visual/2', 3, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据中心-仪表盘-页面编辑', NULL, 3, 'app-data-dashboard-edit', 0, @parent);

INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据中心-大屏', 'fire', 1, NULL, 7, 0);
SET @parent = LAST_INSERT_ID();
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('大屏管理', NULL, 2, '/data/#/screen', 1, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据中心-大屏-页面编辑', NULL, 3, 'app-data-screen-edit', 0, @parent);

INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据中心-报表', 'file-pdf', 1, NULL, 8, 0);
SET @parent = LAST_INSERT_ID();
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('报表-管理', NULL, 2, '/data/#/report', 1, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('报表-发送统计', NULL, 2, '/data/#/report/summary', 2, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('报表-周期报表', NULL, 2, '/data/#/report/crontab', 3, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据中心-报表-页面编辑', NULL, 3, 'app-data-report-edit', 0, @parent);

INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据中心-数据管理', 'table', 1, NULL, 9, 0);
SET @parent = LAST_INSERT_ID();
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据管理', NULL, 2, '/data/#/maintain', 1, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据管理 Demo1', NULL, 2, '/data/#/sheet/1', 2, @parent);
INSERT INTO `sys_resource`(`title`, `icon`, `type`, `path`, `sort`, `parent`) VALUES ('数据管理 Demo2', NULL, 2, '/data/#/sheet/2', 3, @parent);
