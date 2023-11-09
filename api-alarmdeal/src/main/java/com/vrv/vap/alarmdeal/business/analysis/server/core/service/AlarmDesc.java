package com.vrv.vap.alarmdeal.business.analysis.server.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年7月25日 上午10:54:37 
* 类说明      告警描述生成接口 
*/
public abstract class AlarmDesc {
     
	/**
	 * 生成告警描述(log和triggerTime)
	 * @param desc
	 * @return
	 */
	public abstract String createAlarmDesc(String desc,String logInfo);
	
	
	
	
	
	
	/**
	 * 目前只支持三级的修改
	 * 多级替换可以，支持多级
	 * @param desc
	 * @return
	 */
	public List<String> getDescByPlaceHolder(String desc){
		List<String> list = new ArrayList<>();
		list= getPlaceHolder(desc, "\\$\\{(.|\\r\\n)+?}");
		return list;
	}
	
	
	
	
	
	/**
	 * "\\$\\{\\w+\\.\\w+\\}"
	 * 根据占位符进行对应的解析工作
	 * @param desc
	 * @return
	 */
	public List<String> getPlaceHolder(String desc,String patternRule){
		List<String> list = new ArrayList<>();
		Pattern pattern = Pattern.compile(patternRule);
        Matcher matcher = pattern.matcher(desc);
        while(matcher.find()) {
        	String group = matcher.group(0);
        	list.add(group);

        	//test =test.replace(group, uUID);
        }
        return list;
	}
	
	/**
	 * 获得每个占位符对应的原始字段名称
	 * @param list
	 * @return
	 * {"${log.cpurate}":"cpurate"}
	 */
	public Map<String,Object> getPlaceHolderRelateToField(List<String> list){
		Map<String,Object> map = new HashMap<>();
		for (String str : list) {
        	int begin = str.indexOf("{")+1;
        	int end = str.lastIndexOf("}");
        	String content = str.substring(begin, end);
        	String[] split = content.split("\\.");
        	if(split.length>1){
        		StringBuffer buffer = new StringBuffer();
        		for (int i = 1; i < split.length; i++) {
        			String field = split[i];
        			if(i==split.length-1){
        				buffer.append(field);       				
        			}else {
        				buffer.append(field+".");
        			}
				}
        		map.put(str, buffer.toString());   
        	}else {
        		throw new RuntimeException("不符合占位符格式，请检查！");
        	}
		}
		return map;
	}
	
	
}
