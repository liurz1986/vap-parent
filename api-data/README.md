# api-data 升级说明

1. 不再区分 Elastic 数据源和 MySql 数据源，统一使用 data_source 数据源
2. 数据源里面的字段使用表关联，不再使用 JSON 拼接
3. 新增 data_source_connection 表，远程数据连接
4. 废弃 Rel
5. 搜索逻辑优化，
