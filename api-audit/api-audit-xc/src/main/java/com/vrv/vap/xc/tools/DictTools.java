package com.vrv.vap.xc.tools;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.mapper.BaseLineMapper;
import com.vrv.vap.xc.mapper.core.BaseDictAllMapper;
import com.vrv.vap.xc.pojo.BaseDictAll;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DictTools {
    private static BaseDictAllMapper baseLineMapper = VapXcApplication.getApplicationContext().getBean(BaseDictAllMapper.class);

    private static Map<String,Map<String, String>> dictMaps = new HashMap<>();

    public static String translate(String parent,String value){
        Map<String, String> maps = null;
        String result = value;
        if(dictMaps.containsKey(parent)){
            maps = dictMaps.get(parent);
        }else{
            QueryWrapper<BaseDictAll> query = new QueryWrapper<>();
            query.eq("parent_type",parent);
            List<BaseDictAll> baseDictAlls = baseLineMapper.selectList(query);
            if(CollectionUtils.isNotEmpty(baseDictAlls)){
                maps = baseDictAlls.stream().collect(Collectors.toMap(r -> r.getCode(), r -> r.getCodeValue()));
                dictMaps.put(parent,maps);
            }
        }
        if(maps != null && maps.containsKey(value)){
            result = maps.get(value);
         }
        return result;
    }
}
