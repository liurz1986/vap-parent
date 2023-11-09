package com.vrv.vap.config;

import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author wh1107066
 */
@Configuration
@MapperScan(basePackages = "com.vrv.vap.*.mapper", sqlSessionFactoryRef = "sqlSessionFactory")
public class MapperScanConfig {

}
