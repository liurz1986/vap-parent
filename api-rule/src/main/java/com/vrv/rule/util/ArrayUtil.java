package com.vrv.rule.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArrayUtil {
	
	private ArrayUtil() {
		
	}
	
	/**
	 * 反转数组数据
	 * @param array
	 * @return
	 */
	public static String[] reverseArray(String[] array){
        String [] newArray = new String[array.length];
        for(int i=0; i<newArray.length; i++){
            newArray[i] = array[array.length - i - 1];
        }
        return newArray;
	}
	
	/**
	 * 字符串变成list
	 * @param info
	 * @param split
	 * @return
	 */
	public static List<String> strToList(String info,String split){
		String[] splitArray = info.split(split);
        List<String> list = new ArrayList<>();
		for(int i=0; i<splitArray.length; i++){
			list.add(splitArray[i]);
        }
        return list;
	}
	
	public static String join(Object[] objArray, String separate) {
		if (objArray.length == 0)
			return "";

		StringBuilder strBuilder = new StringBuilder();
		int len = objArray.length;
		for (int i = 0; i < len - 1; i++) {
			strBuilder.append(String.valueOf(objArray[i]));
			strBuilder.append(separate);
		}
		strBuilder.append(String.valueOf(objArray[len - 1]));

		return strBuilder.toString();
	}
	
	
	public static Object[] distinct(Object[] objArray)
	{
		Set<Object> result = new HashSet<>();
		for (int i = 0; i < objArray.length; i++) {
			if(!result.contains(objArray[i]))
			{
				result.add(objArray[i]);
			}
		}
		
		return result.toArray();
	}
}
