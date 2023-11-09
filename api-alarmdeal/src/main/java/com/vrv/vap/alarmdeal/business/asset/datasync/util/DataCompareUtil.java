package com.vrv.vap.alarmdeal.business.asset.datasync.util;

import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetBookDetail;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.DataCompareVO;
import com.vrv.vap.jpa.common.DateUtil;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据比对方法
 */
public class DataCompareUtil {
    // 比例字段key
    private String[] diffJson={"typeGuid","deviceDesc","assetNum","name","responsibleName","ip","mac","orgName","registerTime","serialNumber",
            "equipmentIntensive","location","extendDiskNumber","osList","osSetupTime","typeSnoGuid","remarkInfo","deviceArch"};
    /**
     * 执行比对
     * 根据比对策略中配置的所有比对字段，通过反射获取AssetBookDetail对象的中对应属性的数据(其中策略中osSetupTime在AssetBookDetail类对应osSetuptime)
     * 根据比对策略中配置需要比对的字段，执行比对操作;不需要比对的字段根据配置的数据源，获取对应数据源数据
     * @param lists  比对数据
     * @param compareColumnsConfig 比对配置
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static DataCompareVO excCompare(List<AssetBookDetail> lists, Map<String,Object>  compareColumnsConfig) throws NoSuchFieldException, IllegalAccessException {
        DataCompareVO dataCompareVO = new DataCompareVO();
       Set<String> keys= compareColumnsConfig.keySet();
       for(AssetBookDetail detail : lists){
           Class<?> clas = detail.getClass();
           Field[] fields= clas.getDeclaredFields();
           String syncSource = getSyncSource(clas,detail);
           for(Field field : fields){
               field.setAccessible(true);
               String name= field.getName();
               // 策略中对应的时osSetupTime
               String configName = name;
               if("osSetuptime".equals(name)){
                   configName = "osSetupTime";
               }
               if(!keys.contains(configName)){
                   continue;
               }
               Object mapObject = compareColumnsConfig.get(configName);
               String configValue =  mapObject==null?"":String.valueOf(mapObject);
               // 不等于-1，获取对应数据源的数据
               if((!"-1".equals(configValue))&&syncSource.equals(configValue)){
                   addData(field,name,dataCompareVO,detail);
               }else if("-1".equals(configValue)){ // 等于-1进行比较
                   addCompareData(field,name,dataCompareVO,detail);
               }
           }
           dataCompareVO.getRefGuid().add(detail.getGuid());
       }
       return dataCompareVO;
    }

    private  static String getSyncSource(Class<?> clas,AssetBookDetail detail) throws NoSuchFieldException, IllegalAccessException {
        Field syncSourceField = clas.getDeclaredField("syncSource");
        syncSourceField.setAccessible(true);
        Object syncSourceObj = syncSourceField.get(detail);
        String syncSource = syncSourceObj==null?"":String.valueOf(syncSourceObj);
        return syncSource;
    }

    private static void addCompareData(Field field, String name, DataCompareVO dataCompareVO,AssetBookDetail detail) throws NoSuchFieldException, IllegalAccessException {
        Object value= field.get(detail);
        String reslut = getValue(value);
        Field filed = dataCompareVO.getClass().getDeclaredField(name);
        filed.setAccessible(true);
        List<String> datas = (List)filed.get(dataCompareVO);
        if(!datas.contains(reslut)){
            datas.add(reslut);
        }
        // 判断是不是存在差异
        if(datas.size() >1){
            dataCompareVO.setResult(false);
        }
    }
    private static void addData(Field field, String name, DataCompareVO dataCompareVO,AssetBookDetail detail) throws NoSuchFieldException, IllegalAccessException {
        Object value= field.get(detail);
        String reslut = getValue(value);
        Field filed = dataCompareVO.getClass().getDeclaredField(name);
        filed.setAccessible(true);
        List<String> datas = (List)filed.get(dataCompareVO);
        datas.clear(); // 避免同一个数据源多条数据的情况，取最后一条数据
        datas.add(reslut);
    }

    private static String getValue(Object value) {
        String result = "";
        if(null == value){
            return result;
        }

        if(value instanceof Date){
            result =DateUtil.format((Date)value,DateUtil.DEFAULT_DATE_PATTERN);
            return result;
        }
        result = String.valueOf(value);
        return result.trim();
    }
}
