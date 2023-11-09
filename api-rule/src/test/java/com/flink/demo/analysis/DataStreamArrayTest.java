package com.flink.demo.analysis;

import com.vrv.rule.source.TypeInformationClass;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import java.util.*;

public class DataStreamArrayTest {


    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> element1  = new HashMap<>();
        Map<String,Object> element2 = new HashMap<>();
        element1.put("appNo",1234);
        element1.put("appName","xxx");
        element2.put("appNo",2345);
        element2.put("appName","yyy");
        list.add(element1);
        list.add(element2);
        convertMapElement(list);

        Map<String,Object>[] results = list.toArray(new  Map[list.size()]);  //对象数组

        String[] results2 = new String[]{"1","2","3"};   //字符串数组

        DataStreamSource<Row> source1 = env.fromElements(Row.of("1","2",results));
        List<FieldInfoVO> fieldInfoVOList = getFieldInfoVOList();
      //  List<FieldInfoVO> fieldInfoVOList2 = getFieldInfoVOList2();
        TypeInformation<Row> outTypeInformationTypes = TypeInformationClass.getTypeInformationTypes(fieldInfoVOList);

        source1.map(row->{
            return row;
        }).returns(outTypeInformationTypes).print();

        env.execute("DataStreamArrayTest");

    }



    private static void convertMapElement(List<Map<String,Object>> list){
        for (Map<String, Object> map : list) {
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                Object value = map.get(key);
                if(value!=null && !(value instanceof String)){
                    map.put(key, value.toString());
                }
            }
        }
    }



    private static List<FieldInfoVO> getFieldInfoVOList(){
        List<FieldInfoVO> fieldInfoVOList = new ArrayList<>();
        FieldInfoVO fieldInfoVO1 = new FieldInfoVO();
        fieldInfoVO1.setFieldName("appNo");
        fieldInfoVO1.setFieldType("varchar");
        fieldInfoVOList.add(fieldInfoVO1);
        FieldInfoVO fieldInfoVO2 = new FieldInfoVO();
        fieldInfoVO2.setFieldName("appName");
        fieldInfoVO2.setFieldType("varchar");
        fieldInfoVOList.add(fieldInfoVO2);

        FieldInfoVO fieldInfoVO3 = new FieldInfoVO();
        fieldInfoVO3.setFieldName("appList");
        fieldInfoVO3.setFieldType("mapPrimitiveArray");
        fieldInfoVOList.add(fieldInfoVO3);

        return fieldInfoVOList;
    }


    private static List<FieldInfoVO> getFieldInfoVOList2(){
        List<FieldInfoVO> fieldInfoVOList = new ArrayList<>();
        FieldInfoVO fieldInfoVO1 = new FieldInfoVO();
        fieldInfoVO1.setFieldName("appNo");
        fieldInfoVO1.setFieldType("varchar");
        fieldInfoVOList.add(fieldInfoVO1);
        FieldInfoVO fieldInfoVO2 = new FieldInfoVO();
        fieldInfoVO2.setFieldName("appName");
        fieldInfoVO2.setFieldType("varchar");
        fieldInfoVOList.add(fieldInfoVO2);

        FieldInfoVO fieldInfoVO3 = new FieldInfoVO();
        fieldInfoVO3.setFieldName("appArr");
        fieldInfoVO3.setFieldType("stringArray");
        fieldInfoVOList.add(fieldInfoVO3);

        return fieldInfoVOList;
    }

}
