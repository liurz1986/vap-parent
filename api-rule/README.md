## api-rule

flink流计算的规则

2.1
- FLINK流计算的规则


-2.1
-v1
-根据国网相关需求修改topic的值



# 2.1-SNAPSHOT ---->   2.2-SNAPSHOT
## 增加join关联查询的规则

# 2.2-SNAPSHOT ---->   2.3-SNAPSHOT
## 增加资产风险流计算接口

# 2.3-SNAPSHOT ----> 2.4-SNAPSHOT
## 流计算规则接口添加

# 2.4-SNAPSHOT ----> 2.5-SNAPSHOT
## 告警中心2.0规则开发

# 2.5-SNAPSHOT ----> 2.6-SNAPSHOT
## flink非语义的添加


# 2.7-SNAPSHOT ----> 2.8-SNAPSHOT
## flink版本从1.6.1升级到1.12.4，支持流批一体的操作
## flink不再支持fold褶皱属性，reduce无法代替fold，因为reduce需要相同的属性，row的元素个数也必须保持一致，目前只能能够取消按照key进行统计。


#2.8-SNAPSHOT -----> 2.8.1-SNAPSHOT
## 基于告警合并按照事件分类启动策略规则


#2.8.5 -----> 2.8.4
## 褶皱属性算法当中添加distinct count算法


#2.8.6 -----> 2.8.5
## 增加批处理算法，当前支持对es和mysql的批处理操作

