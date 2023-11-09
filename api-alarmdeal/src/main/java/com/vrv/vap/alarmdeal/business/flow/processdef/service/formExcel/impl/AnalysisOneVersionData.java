package com.vrv.vap.alarmdeal.business.flow.processdef.service.formExcel.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.OldFormVO;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisOneVersionData {

    private static Logger logger= LoggerFactory.getLogger(AnalysisOneVersionData.class);

    @Autowired
    private MapperUtil mapperUtil;


    public Map<String,Object>  analysisFormData(String busiArg,String formData ){
     Map<String,Object> map=new LinkedHashMap<>();
     List<OldFormVO> list= stringToList(formData);
     Map<String,Object> busiDataMap=JSON.parseObject(busiArg,Map.class);
     for(OldFormVO oldFormVO :list){
         String key=oldFormVO.getContext().getCode();
         String field=oldFormVO.getContext().getField();
         if(StringUtils.isNotEmpty(field)){
             if(StringUtils.isNotEmpty(oldFormVO.getContext().getCascadeName())){
                 field=oldFormVO.getContext().getCascadeName()+"-"+field;
             }
             if(busiDataMap.containsKey(key+"__code")){
                 map.put(field,busiDataMap.get(key+"__code"));
             }else if(busiDataMap.containsKey(key)){
                 map.put(field,busiDataMap.get(key));
             }else{
                 map.put(field,"");
             }
         }
     }
     return map;
    }



    /**
     * 表单结构转实体
     * @param busiArg
     * @return
     */
    public  List<OldFormVO> stringToList(String formData){
        if(formData.contains("formInfos")){
            Map<String,Object> busiMap= new Gson().fromJson(formData,Map.class);
            formData=JSON.toJSONString(busiMap.get("formInfos"));
        }
        logger.info("formData：{}",formData);
        List<OldFormVO> list=new Gson().fromJson(formData,(new TypeToken<List<OldFormVO>>() {
        }).getType());
        return  list;
    }








}
