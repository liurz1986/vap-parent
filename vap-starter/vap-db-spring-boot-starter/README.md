# 多数据源
在实际开发中，经常可能遇到在一个应用中可能需要访问多个数据库的情况，微服务版本采用了动态多数据源组件，使用参考：
```jshelllanguage
# 多主多从                      纯粹多库（记得设置primary）                   混合配置
spring:                               spring:                               spring:
  datasource:                           datasource:                           datasource:
    dynamic:                              dynamic:                              dynamic:
      datasource:                           datasource:                           datasource:
        master_1:                             mysql:                                master:
        master_2:                             oracle:                               slave_1:
        slave_1:                              sqlserver:                            slave_2:
        slave_2:                              postgresql:                           oracle_1:
        slave_3:                              h2:                                   oracle_2:
```
1、对应模块pom加入vap-db-spring-boot-starter依赖
```jshelllanguage
<dependency>
    <groupId>com.vrv.vap</groupId>
    <artifactId>vap-db-spring-boot-starter</artifactId>
</dependency>
```
2、配置主从数据库，其他数据源可以参考组件文档。
 spring配置
```jshelllanguage
spring: 
  datasource:
    druid:
      stat-view-servlet: 
        enabled: true
        loginUsername: admin
        loginPassword: 123456
    dynamic:
      druid:
        initial-size: 5
        min-idle: 5
        maxActive: 20
        maxWait: 60000
        timeBetweenEvictionRunsMillis: 60000
        minEvictableIdleTimeMillis: 300000
        validationQuery: SELECT 1 FROM DUAL
        testWhileIdle: true
        testOnBorrow: false
        testOnReturn: false
        poolPreparedStatements: true
        maxPoolPreparedStatementPerConnectionSize: 20
        filters: stat,wall,slf4j
        connectionProperties: druid.stat.mergeSql\=true;druid.stat.slowSqlMillis\=5000
      datasource:
          # 主库数据源
          master:
            driver-class-name: com.mysql.cj.jdbc.Driver
            url: jdbc:mysql://localhost:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
            username: root
            password: password
          # 从库数据源
          # slave:
            # url: 
            # username: 
            # password: 
            # driver-class-name: 
```
            
3、vap-db-spring-boot-starter定义数据源注解，对应datasource配置的不同数据源节点,项目默认了主Master从Slave注解可以直接使用，其他的可以根据项目实际情况去添加。

4、使用注解在需要切换数据源的方法上或类上。
```jshelllanguage
@Master
public void insertA()
{
	return xxxxMapper.insertXxxx();
}

@Slave
public void insertB()
{
	return xxxxMapper.insertXxxx();
}
```

使用 @DS 切换数据源。
@DS 可以注解在方法上或类上，同时存在就近原则 方法上注解 优先于 类上注解。

| 注解          | 结果                                     |
| ------------- | ---------------------------------------- |
| 没有@DS       | 默认数据源                               |
| @DS("dsName") | dsName可以为组名也可以为具体某个库的名称 |


