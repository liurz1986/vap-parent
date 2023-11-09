package com.vrv.vap.data.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
}
