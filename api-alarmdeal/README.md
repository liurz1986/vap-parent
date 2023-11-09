## api-alarmdeal

告警处置中心

- 支持flink远程调用(1.服务器之间SSH免密码登陆；2.添加对应得配置)
	flink:    
	  flink_home_path: /usr/local/flink/flink-1.6.1 //flink安装路径
	  flink_jar_path: /usr/local/vap/soc/api-alarmdeal/api-rule-1.0.jar   //规则jar包路径
	  remote_flag: true   //是否开启远程调用
	  remote_ip: 192.168.120.104 //远程IP
	  remote_user: root //远程用户
- 支持告警规则自定义  

2.1
- 告警概览
- 告警明细
- 告警分析
- 预警管理
- 告警规则
- 事件分类
- 威胁库
- 威胁管理

2.2-SNAPSHOT-国网告警版
- 添加国网相关的规则，增加对应的查询接口


2.2-SNAPSHOT-VAP性能开发版本
- 添加告警描述字段alamDesc添加
- 产生的性能告警目前重新发到topic  alarmdeal-returnflow


#2.7-SNAPSHOT-VAP较2.6-SNAPSHOT新增功能
- 告警规则添加告警初始化状态
- 生成的告警根据告警规则的初始化状态改变告警状态
- 告警过滤规则编写

#2.8-SNAPSHOT-VAP较2.7-SNAPSHOT添加单元测试模组

- 修改大屏展示对应的参数
- 修改对应你的bug

#2.9-SNAPSHOT-VAP较2.8-SNAPSHOT
- 告警规则添加告警附作用标签

#3.0-SNAPSHOT-VAP较2.9-SNAPSHOT
- 威胁库合并到事件分类当中
- 楚天云web攻击告警明细接口提供

#3.x-SNAPSHOT-VAP较3.0-SNAPSHOT
- 新增告警中心2.0规则过滤器功能


#4.7.1-SNAPSHOT-VAP较4.7-SNAPSHOT
- 核心代码改造版本

#4.12.0版本
- vap-module升级

# 4.19.0修改策略启动
 一个策略当中包含多种不同的数据源，如果关闭策略的时候不对策略当中的规则进行筛选会造成启动/停用规则发生错误；
