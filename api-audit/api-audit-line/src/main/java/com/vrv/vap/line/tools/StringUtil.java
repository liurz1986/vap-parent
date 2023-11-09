package com.vrv.vap.line.tools;

import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.toolkit.tools.TimeTools;

import java.util.*;
import java.util.stream.Collectors;

public class StringUtil {

    public static String sort(String s) {
        char[] c = s.toCharArray();	//字符串转换为字符数组并赋值引用

        for (int i = 0; i < c.length; i++) {
            boolean flag = true;	//判断字符串是否已有序

            for (int j = 0; j < c.length - i - 1; j++)
                if(c[j] > c[j + 1]) {	//前一个大于当前元素时互换
                    char temp = c[j];
                    c[j] = c[j + 1];
                    c[j + 1] = temp;
                    flag = false;	//置flag为false（表明非有序）
                }

            if(flag) break;	//如果已有序，退出循环
        }

        //创建字符串缓冲器
        StringBuffer stringBuffer = new StringBuffer(s.length());
        for (char d : c) {
            stringBuffer.append(d);
        }
        //return s;
        return stringBuffer.toString();
    }

    public static String filterParam(String url){
        int flag = url.indexOf("?");
        if(flag == -1){
            return url;
        }else{
            return url.substring(0,flag);
        }
    }

    public static List<Map<String,String>> clearMaps(List<Map<String,String>> in){
        List<Map<String,String>> out = new ArrayList<>();
        in.forEach(m ->{
            Map data = new HashMap();
            data.put(LineConstants.SQ.userField,m.get(LineConstants.SQ.userField));
            data.put(LineConstants.SQ.timeField,m.get(LineConstants.SQ.timeField));
            data.put(LineConstants.SQ.pckField,m.get(LineConstants.SQ.pckField));
            data.put(LineConstants.SQ.urlField,filterParam(m.get(LineConstants.SQ.urlField)));
            out.add(data);
        });
        return out;
    }

    public static String compressList(List<Map<String,String>> in){
        List<String> urls = in.stream().map(i -> {
            return filterParam(i.get(LineConstants.SQ.urlField));
        }).collect(Collectors.toList());
        Collections.sort(urls);
        StringBuffer str = new StringBuffer();
        urls.forEach(s ->{
            str.append(s);
        });
        return Base64Util.compressString(str.toString());
    }

    public static String toUnderline(String src){
        return src.replaceAll("-","_");
    }


    public static void main(String[] args) {
        List<String> s = new ArrayList<>();

    }

}
