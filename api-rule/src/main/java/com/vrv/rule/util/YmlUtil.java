package com.vrv.rule.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.Yaml;



/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月26日 下午3:17:15 
* 类说明  专门ip的解析工作
*/
public class YmlUtil {
	 /**
     * key:文件名索引
     * value:配置文件内容
     */
    private static Map<String, LinkedHashMap> ymls = new HashMap<>();
    
    
    /**
     * string:当前线程需要查询的文件名
     */
    private static ThreadLocal<String> nowFileName = new ThreadLocal<>();

    /**
     * 加载配置文件
     * @param fileName
     */
    public static void loadYml(String fileName) {
        nowFileName.set(fileName);
        if (!ymls.containsKey(fileName)) {
            ymls.put(fileName, new Yaml().loadAs(YmlUtil.class.getResourceAsStream("/" + fileName), LinkedHashMap.class));
        }
    }

    /**
     * 获得Yml文件对应的内容，以Map格式
     * @param fileName
     * @return
     */
    public static Map getYmlMap(String fileName){
    	loadYml(fileName);
    	Map ymlInfo = (Map) ymls.get(nowFileName.get()).clone();
    	return ymlInfo;
    }
    
    
    public static Object getValue(String key) throws Exception {
        // 首先将key进行拆分
        //String[] keys = key.split("[.]");
        // 将配置文件进行复制
        Map ymlInfo = (Map) ymls.get(nowFileName.get()).clone();
        Object object = ymlInfo.get(key);
        if(object!=null) {
        	return object.toString();
        }else {
        	//throw new RuntimeException("读取yaml信息异常！");
        	return null;
        }
//        for (int i = 0; i < keys.length; i++) {
//            Object value = ymlInfo.get(keys[i]);
//            if (i < keys.length - 1) {
//                ymlInfo = (Map) value;
//            } else if (value == null) {
//                throw new Exception("key不存在");
//            } else {
//                return value;
//            }
//        }
    }

    /**
     * 解析对应的port
     * @param fileName
     * @param key
     * @return
     */
    public static Object getValue(String fileName, String key) {
        // 首先加载配置文件
    	String getenv = System.getenv(key);   //读取环境变量
    	if(StringUtils.isNotEmpty(getenv)){  //读取系统环境变量
    		String url = getenv;
    		return  url;
    	}else{  //读取文件变量
    		try {
    			loadYml(fileName);
    			return getValue(key);
    		}catch(Exception e) {
    			throw new RuntimeException("解析失败", e);
    		}    		
    	}
    	
    	
    }

    public static void main(String[] args) throws Exception {
//    	StringBuilder stringBuilder=new StringBuilder();
//    	Map<String,Object> ymlMap = getYmlMap("assets.yml");
//    	for (Map.Entry<String,Object> entry : ymlMap.entrySet()) { 
//    		  String key = entry.getKey();
//    		  Map<String,Object> value = (Map)entry.getValue();
//    		  String assetType = value.get("type").toString();
////   		  assetType = assetType.substring(assetType.indexOf("[")+1, assetType.lastIndexOf("]"));
////    		  System.out.println(key);
////    		  System.out.println(assetType);
//    		  stringBuilder.append(key).append(": ").append("\n\t").append("type").append(": ").append(assetType).append("\n");
//    		}
//    	String string = stringBuilder.toString();
//        System.out.println(string);
    	 String string = YmlUtil.getValue("application.yml", "topic_producer_name").toString();
    	 System.out.println(string);
    	
    }
    
}
