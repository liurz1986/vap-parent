# vap-starter

基础组件封装starter
- V1.0
增加prometheus的监控的starter引用
增加swagger的开发接口的starter引用

- V1.1
增加redis的starter引用

- V1.2
增加oauth2的认证服务和资源服务，解决垂直越权的接口访问。 支持授权码模式、密码模式进行token的获取
如果存在网络隔离，可以在zuul中进行拦截，支持方法的注解拦截和url的拦截方式，兼容同时使用
如果不存在网络隔离，可以在各个服务模块中进行拦截和权限鉴别。
>- token类型支持3种方式：db、redis、jwt
>- jwt使用的是非对称加密，所以认证服务需要配置为authJwt为私钥加密，其他资源服务修改vap.oauth2.token.store.type=resJwt  通过pubtxt进行自动解析
>- 需要token是redis的方式，默认的vap.oauth2.token.store.type=redis 即可

- V3.2  
> 要求vap-module的版本 3.2.0-SNAPSHOT 版本

1）fix swagger无法关闭问题.swagger的配置，enabled为true时打开， 为false时是关闭状态
需要引用
~~~
# 引用依赖
 <dependency>
    <groupId>com.vrv.vap</groupId>
    <artifactId>vap-swagger2-spring-boot-starter</artifactId>
    <version>3.2.0-SNAPSHOT</version>
</dependency>
        
# yaml中需要配置
vap:
  swagger:
    enabled: true
    title: 事务管理工程项目11
    description: 项目升级springboot版本
    base-package: com.vrv.vap.template.controller
    version: 3.2-SNAPSHOT
~~~

2）修复打开所有 Actuator 服务, 并使用prometheus进行监控时。 采用swagger2.x的方式进行。
修复在监控过程中端点暴露问题处理，在没有使用spring security的时候，采用关闭端点的处理方式
~~~
management:
  endpoints:
    web:
      exposure:
        include: '*'  #会开启所有的endpoints，不包含shutdown
    jmx:
      exposure:
        include: '*' #可以是info,health,beans等参数
    enabled-by-default: false  #不开启Actuator监控，所有的指标都不开启
  endpoint:
    health:
      show-details: always
# 名称定义
  metrics:
    tags:
      application: ${spring.application.name}
~~~

