SET @parent = UUID();
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据中心-通用管理', NULL, 1,'app-data', NULL, 3, @parent, 0);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据源管理', NULL, 2,'app-data',  '/data/#/source', 1,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('偏好设置', NULL, 2,'app-data',  '/data/#/preference', 2,UUID(), @parent);

SET @parent = UUID();
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据中心-搜索', 'file-search', 1,'app-data',  NULL, 4, @parent, 0);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('全网搜', NULL, 2,'app-data',  '/data/#/search', 1,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('搜索-主题设置', NULL, 2,'app-data',  '/data/#/topic', 2,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('菜单版 Demo1', NULL, 2,'app-data',  '/data/#/search/1', 3,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('菜单版 Demo2', NULL, 2,'app-data',  '/data/#/search/2', 4,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据中心-搜索-数据导出', NULL, 3,'app-data',  'app-data-search-export', 0,UUID(), @parent);

SET @parent = UUID();
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据中心-探索', NULL, 1,'app-data',  NULL, 5, @parent, 0);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('探索', 'exception', 2,'app-data',  '/data/#/discover', 1,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('探索-实体设置', NULL, 2,'app-data',  '/data/#/entity', 2,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('探索-关系设置', NULL, 2,'app-data',  '/data/#/edge', 3,UUID(), @parent);

SET @parent = UUID();
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据中心-仪表盘', 'compass', 1,'app-data',  NULL, 6, @parent, 0);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('仪表盘管理', NULL, 2,'app-data',  '/data/#/dashboard', 1,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('仪表盘 Demo1', NULL, 2,'app-data',  '/data/#/visual/1', 2,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('仪表盘 Demo2', NULL, 2,'app-data',  '/data/#/visual/2', 3,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据中心-仪表盘-页面编辑', NULL, 3,'app-data',  'app-data-dashboard-edit', 0,UUID(), @parent);

SET @parent = UUID();
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据中心-大屏', 'fire', 1,'app-data',  NULL, 7, @parent, 0);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('大屏管理', NULL, 2,'app-data',  '/data/#/screen', 1,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据中心-大屏-页面编辑', NULL, 3,'app-data',  'app-data-screen-edit', 0,UUID(), @parent);

SET @parent = UUID();
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据中心-报表', 'file-pdf', 1,'app-data',  NULL, 8, @parent, 0);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('报表-管理', NULL, 2,'app-data',  '/data/#/report', 1,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('报表-发送统计', NULL, 2,'app-data',  '/data/#/report/summary', 2,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('报表-周期报表', NULL, 2,'app-data',  '/data/#/report/crontab', 3,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据中心-报表-页面编辑', NULL, 3,'app-data',  'app-data-report-edit', 0,UUID(), @parent);

SET @parent = UUID();
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据中心-数据管理', 'table', 1,'app-data',  NULL, 9, @parent, 0);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据管理', NULL, 2,'app-data',  '/data/#/maintain', 1,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据管理 Demo1', NULL, 2,'app-data',  '/data/#/sheet/1', 2,UUID(), @parent);
INSERT INTO `resource`(`title`, `icon`, `type`,`service_id`, `path`, `sort`,`uid`, `puid`) VALUES ('数据管理 Demo2', NULL, 2,'app-data',  '/data/#/sheet/2', 3,UUID(), @parent);
