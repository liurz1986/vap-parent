## vap-module

统一的模块及版本入口，版本统一管理。以免造成版本混乱及版本不归一情况。

## 开发遵循规范
### 版本号统一管理，不依赖各个模块去管理依赖版本号。如需要升级，做统一升级管理，以免造成版本混乱和不一致
- vap-service
- vap-utils
- vap-common
- vap-syslog
- vap-swagger2-spring-boot-starter
- vap-redis-spring-boot-starter
- vap-prometheus-spring-boot-starter
- vap-db-spring-boot-starter



### 需要引入spring-boot 与 spring-cloud、 aliba-could 整合组件

~~~java
<!-- spring-boot 与 spring-cloud、 aliba-could 整合-->
<dependency>
    <groupId>com.vrv.vap</groupId>
    <artifactId>vap-service</artifactId>
</dependency>
~~~

> 

### 各服务引用redis操作统一封装，服务直接引用

~~~~
<!-- redis操作统一封装，服务直接引用 -->
<dependency>
    <groupId>com.vrv.vap</groupId>
    <artifactId>vap-redis-spring-boot-starter</artifactId>
</dependency>
~~~~



### 各服务prometheus 监控统一封装，服务直接引用

~~~~
<!-- prometheus 监控 -->
<dependency>
    <groupId>com.vrv.vap</groupId>
    <artifactId>vap-prometheus-spring-boot-starter</artifactId>
</dependency>
~~~~


### 各服务引用需要swagger接口文档。各服务可以去除swagger依赖

~~~
<!--  统一封装swagger接口文档 -->
  <dependency>
      <groupId>com.vrv.vap</groupId>
      <artifactId>vap-swagger2-spring-boot-starter</artifactId>
  </dependency>
~~~~
使用方式可以自由关闭和打开，使用enable: false进行关闭：
~~~
vap:
  swagger:
    enabled: true
    title: 用户管理中心
    description: 项目升级springboot版本
    base-package: com.vrv.vap.swagger.controller
    version: 3.0
~~~~



### 各服务引用Feign依赖调用文档

feign默认采用jdk原生HttpURLConnection向下游服务发起http请求

源码分析:[feign源码分析](https://blog.csdn.net/yangchao1125/article/details/104492547)，现有项目采用OkHttpClient方式

- 1、首先是 Application 类中的 @EnableFeignClients 注解
- 2、加入okhttp的依赖引用

~~~java
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
<!-- 不使用这个底层调用sun.net.www.protocol.http.HttpURLConnection.getInputStream0(HttpURLConnection.java:1523-->
<!-- https://blog.csdn.net/Thinkingcao/article/details/109161139 -->
<!--原来feign采用jdk原生HttpURLConnection向下游服务发起http请求-->
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-okhttp</artifactId>
</dependency>

~~~
- 3、增加application.yml依赖的配置信息

~~~
# 在默认情况下 spring cloud feign在进行各个子服务之间的调用时，http组件使用的是jdk的HttpURLConnection，没有使用线程池。
# 有2种可选的线程池：HttpClient和OKHttp
# 比较推荐OKHttp，请求封装的非常简单易用，性能也很ok。
feign:
  okhttp:
    enabled: true
  client:
    config:
      default: 							# 服务名，填写 default 为所有服务，或者指定某服务，例如：annoroad-beta
        connectTimeout: 10000           # 连接超时，10秒
        readTimeout: 20000              # 读取超时，20秒    
  httpclient:
    enabled: false
    max-connections: 1000               # 连接池连接最大闲置数，缺省值是 200
    connection-timeout: 3000            # 连接超时，单位为毫秒，缺省值是 3000毫秒（3秒）
    time-to-live: 900                   # 连接最大闲置时间，单位为秒，缺省值是 900秒（15分钟）
    max-connections-per-route: 100
  compression:
    request:
      mime-types: text/xml,application/xml,application/json
      min-request-size: 2048
      enabled: true
    response:
      enabled: true
  hystrix:
    enabled: true

ribbon:
  okhttp:
    enabled: true
~~~

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

~~~~
<!-- 资源服务器操作统一封装，服务直接引用 -->
<dependency>
    <groupId>com.vrv.vap</groupId>
    <artifactId>vap-auth-client-spring-boot-starter</artifactId>
</dependency>
~~~~
[具体参考代码api-security](http://192.168.120.101/root/api-demo.git)
- redis返回token类型：  api-security  和 api-oauthResource
- jwt返回token类型： api-jwt-server 和 api-jwt-resource

- V1.3
方便日志的审计操作，需要明细信息，需要日志的详细信息

- 对象对比差异，修改数据之前和之后的变化需要记录操作日志
- 操作日志对于字段忽略不记录操作日志审计
- 对于敏感字段进行脱敏，隐藏特殊字段，例如： 身份证、电话、邮箱等
- 特殊字段转义，例如枚举
- 操作查询条件记录日志
- 查看页面记录日志
- 日志外发其他审计平台

支持方式：
- 支持手动发送，在登录时需要手动的记录日志，AOP拦截获取不到用户信息
- 支持登录后，数据的增删改查操作记录， 对比前后数据变化（存在用户信息时）
- 支持增删改查不需要对比数据，Aop直接发送日志并记录

**1.用户登录后，对比数据发送**

手动发送，调用controller中的某一个方法，构建查询条件，通过方法的接收参数进行获取，支持

- 接收参数，简单类型需要加入@ApiParam注解(属性包含value) ， 包含String类型也算到简单类型中
- 接收参数，简单类型属性通过LogField注解进行标记
- 接收复杂对象参数， 数据通过@ApiModelProperty注解方式获取
- 接收复杂对象参数，数据通过复杂对象中的字段的注解 @LogField获取
- 复杂类型对象同时存在 @LogField注解 与  @ApiModelProperty注解 ，以字段上的@LogField优先级高



开发中以swagger的注解就可以了，使用@ApiParam 与 @ApiModelProperty为主。如果存在敏感信息隐藏（脱敏），就需要 @LogField的参与，其他不需要

开发过程中字段的  @ApiModelProperty注解一定要声明，否则无法完成字段修改记录，默认为空的都会跳过记录。

1.1 手动发送-对象对比差异及输出

1.2  手动发送-直接调用SyslogSenderUtils.sendSyslogManually(SystemLog systemLog);



**2.用户登录后，无对比发送审计日志**

自动发送。 不需要send*Syslog方法。