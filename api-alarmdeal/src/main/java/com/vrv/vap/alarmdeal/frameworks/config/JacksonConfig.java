package com.vrv.vap.alarmdeal.frameworks.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

/**
 * 时间格式处理：
 * 主要是处理springboot升级为2.x后查询时间为数组的问题
 * 2022-09-06
 */
@Configuration
public class JacksonConfig extends WebMvcConfigurationSupport {
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 取消jackjson默认处理时间的格式
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // 忽略不存在的字段
        // 设置时区
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        // 设置时间格式
        objectMapper.setDateFormat(new SimpleDateFormat(PATTERN));
        // 日期和时间格式化
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 序列化配置,针对java8 时间
        javaTimeModule.addSerializer(LocalDateTime.class,new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(Constants.DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class,new LocalDateSerializer(DateTimeFormatter.ofPattern(Constants.DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class,new LocalTimeSerializer(DateTimeFormatter.ofPattern(Constants.DEFAULT_TIME_FORMAT)));
        // 反序列化配置,针对java8 时间
        javaTimeModule.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(Constants.DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class,new LocalDateDeserializer(DateTimeFormatter.ofPattern(Constants.DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class,new LocalTimeDeserializer(DateTimeFormatter.ofPattern(Constants.DEFAULT_TIME_FORMAT)));
        objectMapper.registerModule(javaTimeModule).registerModule(new Jdk8Module()).registerModule(new ParameterNamesModule());
        jsonConverter.setObjectMapper(objectMapper);
        converters.add(jsonConverter);
    }
    /**
     * 配置静态资源
     *
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //addResoureHandler：指的是对外暴露的访问路径   addResourceLocations：指的是内部文件放置的目录
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }
    public class Constants {

        /**时间格式**/
        public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        /**日期格式**/
        public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
        /**时钟格式**/
        public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

        public Constants() {
        }
    }


}
