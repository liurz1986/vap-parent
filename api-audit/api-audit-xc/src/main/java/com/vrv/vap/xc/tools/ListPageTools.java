package com.vrv.vap.xc.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListPageTools {

    public static List<Map<String, Object>> pageList(List<Map<String, Object>> source,int start,int count){
        if(source.size() < start+1){
            return new ArrayList<>();
        }
        int endIndex = Math.min((start + count), source.size());
        return source.subList(start,endIndex);
    }
}
