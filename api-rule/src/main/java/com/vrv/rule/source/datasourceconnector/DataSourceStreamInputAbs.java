package com.vrv.rule.source.datasourceconnector;

import com.vrv.rule.model.DataStreamSourceVO;
import com.vrv.rule.model.filter.Column;
import com.vrv.rule.model.filter.Tables;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeUtil;
import com.vrv.rule.source.TypeInformationClass;
import com.vrv.rule.source.datasourceparam.DataStreamRunnerParamsAbs;
import com.vrv.rule.util.FieldInfoUtil;
import com.vrv.rule.util.RoomInfoConstant;
import com.vrv.rule.vo.DataStreamInputVO;
import com.vrv.rule.vo.FieldInfoVO;
import com.vrv.rule.vo.MapOrder;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源输入对应数据
 */
public abstract class DataSourceStreamInputAbs {


    /**
     * 不同的数据源获得对应输入数据
     * @param env
     * @param dataStreamInputVO
     * @param fieldInfoVOs
     * @return
     */
    public abstract DataStream<Row> getDataSourceStreamInput(StreamExecutionEnvironment env,DataStreamInputVO dataStreamInputVO,List<FieldInfoVO> fieldInfoVOs );




    public DataStreamSourceVO getDataStreamSourceVO(DataStreamInputVO dataStreamInputVO ) {
        DataStreamSourceVO dataStreamSourceVO = new DataStreamSourceVO();
        Tables tables = dataStreamInputVO.getTables();
        String roomType = dataStreamInputVO.getRoomType();
        String name = tables.getName();
        List<Column> columns = tables.getColumn();
        List<FieldInfoVO> fieldInfoVOs = FieldInfoUtil.getSourceFieldInfoVOsNoJdbc(columns, name,0);
        ExchangeUtil.setRoomTypeFieldInfo(roomType, columns.size(), name, fieldInfoVOs);

        StreamExecutionEnvironment env = dataStreamInputVO.getEnv();
        DataStream<Row> dataStreamSource = getDataSourceStreamInput(env, dataStreamInputVO,fieldInfoVOs);

        dataStreamSource = setRommTypeInfo(roomType, fieldInfoVOs, dataStreamSource);
        dataStreamSourceVO.setDataStreamSource(dataStreamSource);
        dataStreamSourceVO.setFieldInfoVOs(fieldInfoVOs);
        dataStreamSourceVO.setSourceId(dataStreamInputVO.getSourceId());
        dataStreamSourceVO.setAttachs(dataStreamInputVO.getAttachs());
        return dataStreamSourceVO;
    }

    /**
     * 设置对应的roomType数据
     * @param roomType
     * @param fieldInfoVOs
     * @param dataStreamSource
     * @return
     */
    private DataStream<Row> setRommTypeInfo(String roomType, List<FieldInfoVO> fieldInfoVOs, DataStream<Row> dataStreamSource) {
        TypeInformation<Row> outTypeInformation = TypeInformationClass.getTypeInformationTypes(fieldInfoVOs);
        switch (roomType) {
            case RoomInfoConstant.ID_ROOM_TYPE:
                MapOrder mapTypeData = getIdRoomTypeData(fieldInfoVOs);
                dataStreamSource = mapIdRoomInfo(dataStreamSource, mapTypeData, outTypeInformation);
                break;
            case RoomInfoConstant.TIME_ROOM_TYPE:
                MapOrder timeRoomTypeData = getTimeRoomTypeData(fieldInfoVOs);
                dataStreamSource = mapTimeRoomInfo(dataStreamSource, timeRoomTypeData, outTypeInformation);
                break;
            default:
                throw new RuntimeException("roomType值为："+ roomType +"不符合对应要求，请检查！");
        }
        return dataStreamSource;
    }


    private  MapOrder getIdRoomTypeData(List<FieldInfoVO> outputFieldInfos){
        MapOrder mapOrder = new MapOrder();
        for (FieldInfoVO fieldInfoVO : outputFieldInfos) {
            String fieldName = fieldInfoVO.getFieldName();
            if(fieldName.equals("guid")){ //TODO 规定加guid
                Integer order = fieldInfoVO.getOrder();
                String tableName = fieldInfoVO.getTableName();
                mapOrder.setOrder(order);
                mapOrder.setTableName(tableName);
            }
        }
        return mapOrder;
    }


    /**
     * 映射idroom相关的信息
     * @param dataStreamSource
     * @param mapTypeData
     * @param outTypeInformation
     * @return
     */
    private  DataStream<Row> mapIdRoomInfo(DataStream<Row> dataStreamSource, MapOrder mapTypeData,
                                                 TypeInformation<Row> outTypeInformation) {
        dataStreamSource=dataStreamSource.map(new MapFunction<Row, Row>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Row map(Row row) throws Exception {
                int arity = row.getArity();
                Integer order = mapTypeData.getOrder();
                String tableName = mapTypeData.getTableName();
                Object field = row.getField(order);
                if(field instanceof String){
                    Map<String,String[]> map =new HashMap<>();
                    String fieldValue = (String)field;
                    String[] strArr = new String[] {fieldValue};
                    map.put(tableName, strArr);
                    row.setField(arity-1, map);
                }
                return row;
                //else{
                //	throw new RuntimeException("该数据类型不是String类型报错，请检查！,"+"order顺序:"+order+"field字段:"+field+"row:"+row);
                //}
            }
        }).returns(outTypeInformation);
        return dataStreamSource;
    }



    /**
     * 获得时间MapOrder
     * @param outputFieldInfos
     * @return
     */
    private  MapOrder getTimeRoomTypeData(List<FieldInfoVO> outputFieldInfos){
        MapOrder mapOrder = new MapOrder();
        FieldInfoVO eventTimeFieldInfoVO = FieldInfoUtil.getEventTimeFieldInfoVO(outputFieldInfos);
        if(eventTimeFieldInfoVO==null){
            throw new RuntimeException("原始日志没有配置事件时间字段，请检查！");
        }
        Integer order = eventTimeFieldInfoVO.getOrder();
        String tableName = eventTimeFieldInfoVO.getTableName();
        mapOrder.setOrder(order);
        mapOrder.setTableName(tableName);
        return mapOrder;
    }

    /**
     * 构造timeRoom相关信息
     * @param dataStreamSource
     * @param outTypeInformation
     * @return
     */
    private  DataStream<Row> mapTimeRoomInfo(DataStream<Row> dataStreamSource,MapOrder mapTypeData,TypeInformation<Row> outTypeInformation) {
        dataStreamSource=dataStreamSource.map(new MapFunction<Row, Row>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Row map(Row row) throws Exception {
                int arity = row.getArity();
                Map<String,Map<String,String>> map =new HashMap<>();
                Integer order = mapTypeData.getOrder();
                String tableName = mapTypeData.getTableName();
                Object field = row.getField(order);
                if(field instanceof String){
                    Map<String,String> timeMap = new HashMap<>();
                    String eventTIme = field.toString();
                    timeMap.put(RoomInfoConstant.MIN_TIME, eventTIme);
                    timeMap.put(RoomInfoConstant.MAX_TIME, eventTIme);
                    map.put(tableName, timeMap);
                }
//				else{
//					throw new RuntimeException("该数据类型不是String类型报错，请检查！,"+"order顺序:"+order+"field字段:"+field+"row:"+row);
//				}
                row.setField(arity-1, map);
                return row;
            }
        }).returns(outTypeInformation);
        return dataStreamSource;
    }



}
