package com.vrv.rule.ruleInfo.cal.execute;

import com.vrv.rule.ruleInfo.cal.params.AccCollectionParams;
import com.vrv.rule.util.FieldInfoUtil;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 集合算子启动类
 */
public class AccCollectionStartUp {


    public static void calAccCollection(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs,
                                        String expressField, Integer order, String fieldType){

        Object result = outPutrow.getField(order);
        AccCollectionParams accCollectionParams = AccCollectionParams.builder().inputFieldInfoVOs(inputFieldInfoVOs).expressField(expressField)
                .inputRow(inputRow).result(result).build();
        String[] expressArr = expressField.split(",");
        if(fieldType.equals("stringArray") && expressArr.length==1){  //字符串数组，expressField只能存在一个字段
            String[] accCollectionByStringArray = getAccCollectionByStringArray(accCollectionParams);
            outPutrow.setField(order,accCollectionByStringArray);
        }else if(fieldType.equals("mapPrimitiveArray")){  //对象数组展示
            Map<String,String>[] accCollectionByMapPrimitiveArray = getAccCollectionByMapPrimitiveArray(accCollectionParams);
            outPutrow.setField(order,accCollectionByMapPrimitiveArray);
        }
    }

    /**
     * 初始化集合算子
     * @param outPutrow
     * @param order
     * @param fieldType
     */
    public static  void initAccCollection(Row outPutrow,int order,String fieldType){
        if(fieldType.equals("stringArray")){
            outPutrow.setField(order,new String[]{});
        }else if(fieldType.equals("mapPrimitiveArray")){
            List<Map<String,String>> list = new ArrayList<>();
            outPutrow.setField(order,list);
        }
    }




    private static Map<String,String>[] getAccCollectionByMapPrimitiveArray(AccCollectionParams accCollectionParams){
        Object result = accCollectionParams.getResult();
        String expressField = accCollectionParams.getExpressField();
        Row inputRow = accCollectionParams.getInputRow();
        List<FieldInfoVO> inputFieldInfoVOs = accCollectionParams.getInputFieldInfoVOs();
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map = FieldInfoUtil.getInputFieldValueByMap(inputRow, inputFieldInfoVOs, expressField);
        if(result==null){
            list.add(map);
            Map<String,String>[] results = list.toArray(new  Map[list.size()]);  //对象数组
            return results;
        }else{
            Map<String,String>[] mapListResult = (Map<String,String>[])result;
            List<Map<String,String>>  tmpresult = new ArrayList<>(Arrays.asList(mapListResult));
            tmpresult.add(map);
            Map<String,String>[] tmpresultMap = tmpresult.toArray(new  Map[tmpresult.size()]);
            return tmpresultMap;
        }
    }

    /**
     * 获得StringArray类型的算子输出集合
     * @param accCollectionParams
     */
    private static String[]  getAccCollectionByStringArray(AccCollectionParams accCollectionParams){
        Object result = accCollectionParams.getResult();
        String expressField = accCollectionParams.getExpressField();
        Row inputRow = accCollectionParams.getInputRow();
        List<FieldInfoVO> inputFieldInfoVOs = accCollectionParams.getInputFieldInfoVOs();
        List<String> list = new ArrayList<>();
        String stringValue = FieldInfoUtil.getInputFieldValueByString(inputRow, inputFieldInfoVOs, expressField);
        if(result==null){
            list.add(stringValue);
        }else{
            String[] strArrResult = (String[])result;
            //字符串数组转成list
            list = new ArrayList<>(Arrays.asList(strArrResult));
            list.add(stringValue);
        }
        String[] array = list.toArray(new String[list.size()]);
        return array;
    }

}
