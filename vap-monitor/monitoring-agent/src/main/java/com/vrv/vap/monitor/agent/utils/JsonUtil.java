package com.vrv.vap.monitor.agent.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.monitor.common.model.MonitorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public  static Map jsonToMap(String json){
        try {
           Map map = objectMapper.readValue(json,Map.class);
           return map;
        } catch (JsonParseException e) {
            logger.error(e+"");
        } catch (JsonMappingException e) {
            logger.error(e+"");
        } catch (IOException e) {
            logger.error(e+"");
        }
        return null;
    }

    public  static List<Map> jsonToList(String json){
        try {
            List<Map> map = objectMapper.readValue(json,List.class);
            return map;
        }
        catch (IOException e) {
            logger.error(e+"");
        }
        return null;
    }



    public static String objToJson(Object obj){
        try {
            String json = objectMapper.writeValueAsString(obj);
            return json;
        } catch (JsonParseException e) {
            logger.error(e+"");
        } catch (JsonMappingException e) {
            logger.error(e+"");
        } catch (IOException e) {
            logger.error(e+"");
        }
        return null;
    }

    public static  <T> T jsonToEntity(String content, Class<T> valueType)  {
        try {
            T e = objectMapper.readValue(content,valueType);
            return e;
        } catch (JsonParseException e) {
            logger.error(e+"");
        } catch (JsonMappingException e) {
            logger.error(e+"");
        } catch (IOException e) {
            logger.error(e+"");
        }
        return null;

    }

    public static  <T> List<T> jsonToEntityList(String content, Class<T> valueType)  {
        try {
            JavaType javaType =objectMapper.getTypeFactory().constructParametricType(ArrayList.class, valueType);
            List<T> e = objectMapper.readValue(content,javaType);

            return e;
        } catch (JsonParseException e) {
            logger.error(e+"");
        } catch (JsonMappingException e) {
            logger.error(e+"");
        } catch (IOException e) {
            logger.error(e+"");
        }
        return null;

    }
  }
