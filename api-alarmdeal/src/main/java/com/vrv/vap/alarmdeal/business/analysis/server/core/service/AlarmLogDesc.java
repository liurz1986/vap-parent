package com.vrv.vap.alarmdeal.business.analysis.server.core.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.vrv.vap.jpa.common.RegUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;


/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年7月25日 上午10:58:53 
* 类说明   自定义日志告警描述填充
*/
@Component
public class AlarmLogDesc extends AlarmDesc {

	
	public static String MAP_NAME_TYPE = "com.google.gson.internal.LinkedTreeMap";
	public static String ARRAY_LIST = "java.util.ArrayList";


	private static List<String> fiveElementS= Arrays.asList("srcIp","dstIp","srcPort","dstPort","relateIp");
	
	@Override
	public String createAlarmDesc(String desc, String logInfo) {
		Gson gson = new Gson();
		Map logInfoMap = gson.fromJson(logInfo, Map.class);
		List<String> list = getDescByPlaceHolder(desc); //获得对应的替换标识符
		Map<String, Object> map = getPlaceHolderRelateToField(list);
		for(Map.Entry<String, Object> entry : map.entrySet()){
			String key = entry.getKey(); 
			Object value = entry.getValue();
			String valueStr = String.valueOf(value);
			if(fiveElementS.contains(value.toString())){
				valueStr= RegUtil.toUnderlineJSONString(valueStr);
			}
			String[] split = valueStr.split("\\.");
			String result = getResultMapTypeByRecursive(logInfoMap, split, 0);
			if(StringUtils.isNotEmpty(result)){
				desc = desc.replace(key, result);
			}else{
				desc = desc.replace(key, "未知");
			}

		}
		return desc;
		
	}


	
	/**
	 * 关于map当中递归的解决方法,增加动态的参数int i（递归是Map类型）
	 * @param fromMap
	 * @param keys
	 * @param i
	 * @return
	 */
	public  String getResultMapTypeByRecursive(Map fromMap,String[] keys,int i){ //如何解決传参的问题
		String key = keys[i];   //
		if(key.contains("[")&&key.contains("]")){  //包括括号
			List<String> list = getPlaceHolder(key,"\\w+"); //含有对应的key和下标
			key = list.get(0);//key
			String num = list.get(1); //下标
			Object object = fromMap.get(key);
			String typeName = object.getClass().getTypeName();
			if(typeName.equals(ARRAY_LIST)){   //List结构
				List array = (List)object;
				Object arrayValue = array.get(Integer.valueOf(num));
				Map arrayMap = (Map)arrayValue;
				return getResultMapTypeByRecursive(arrayMap, keys,i+1);				
			}else{
				return String.valueOf(object);  //这个地方就是出口
			}
		}else {
			Object object = fromMap.get(key);
			if(object!=null){
				String typeName = object.getClass().getTypeName();
				if(typeName.equals(MAP_NAME_TYPE)){   //Map结构
					Map map = (Map)object;
					return getResultMapTypeByRecursive(map, keys,i+1);				
				}else{
					return String.valueOf(object);  //这个地方就是出口
				}							
			}else {
				return null;
			}
		}
	}
		
}
