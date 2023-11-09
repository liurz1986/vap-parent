package com.vrv.vap.alarmdeal.business.flow.processdef.service.formExcel.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.NewFormStructure;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.NewFormVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisTwoVersionData {

    // 增加流程版本处理   2022-10-26
    public Map<String,Object> analysisFormData(String busiArg, String formData, String versionProcess){
        Map<String,Object> map=new LinkedHashMap<>();
        NewFormVO newFormVO=stringToList(formData);
        List<NewFormStructure> newFormStructureList=newFormVO.getUsedFields();
        Map<String,Object>  busiMap=JSON.parseObject(busiArg,Map.class);
        if("new".equals(versionProcess)){  // 工单2.0版本处理
            recursionDataNew(map,newFormStructureList,busiMap);
        }else{
            // 老版本处理
            recursionData(map,newFormStructureList,busiMap);
        }

        return map;
    }

    /**
     * 递归遍历
     * 工单2.0处理   2022-10-26
     */
    private void recursionDataNew(Map<String, Object> map, List<NewFormStructure> newFormStructureList, Map<String, Object> busiMap) {
        for(NewFormStructure newFormStructure : newFormStructureList){
            String itemType=newFormStructure.getItemType();
            if(itemType.equals("grid")||itemType.equals("tabs")){
                List<NewFormStructure> list=newFormStructure.getChildren();
                if(list.size()>0){
                    recursionDataNew(map,list,busiMap);
                }
            }else{
                NewFormStructure.OptionVO data =newFormStructure.getOption();
                if(null == data){
                    continue;
                }
                String id = data.getFieldId();
                String name=data.getTitle();
                boolean visible = data.isVisible();
                if(visible){  // 隐藏的不处理
                    continue;
                }
                if(StringUtils.isNotEmpty(name)){
                    if(busiMap.containsKey(id)){
                        map.put(name,busiMap.get(id));
                    }else{
                        map.put(name,"");
                    }
                }

            }

        }
    }

    /**
     * 递归遍历
     */
    private void recursionData(Map<String,Object> map,List<NewFormStructure> newFormStructureList, Map<String,Object> busiMap){
        for(NewFormStructure newFormStructure : newFormStructureList){
            String itemType=newFormStructure.getItemType();
            if(itemType.equals("grid")||itemType.equals("tabs")){
                List<NewFormStructure> list=newFormStructure.getChildren();
                if(list.size()>0){
                    recursionData(map,list,busiMap);
                }
            }else{
                String id=newFormStructure.getId();
                String name=newFormStructure.getName();
                if(StringUtils.isNotEmpty(name)){
                    if(busiMap.containsKey(id+"__code")){
                        map.put(name,busiMap.get(id+"__code"));
                    }else if(busiMap.containsKey(id)){
                        map.put(name,busiMap.get(id));
                    }else{
                        map.put(name,"");
                    }
                }

            }

        }
    }




    /**
     * 表单结构转实体
     * @param busiArg
     * @return
     */
    public NewFormVO stringToList(String formData){
        if(formData.contains("formInfos")){
           Map<String,Object> formDataMap=JSON.parseObject(formData,Map.class);
           Object obj=formDataMap.get("formInfos");
           formData=JSON.toJSONString(obj);
        }
        NewFormVO newFormVO=new Gson().fromJson(formData,(new TypeToken<NewFormVO>() {
        }).getType());
        return  newFormVO;
    }

}
