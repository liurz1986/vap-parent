package com.vrv.rule.ruleInfo.exchangeType.agg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vrv.rule.ruleInfo.cal.ICountCal;
import com.vrv.rule.ruleInfo.cal.execute.AccCollectionStartUp;
import com.vrv.rule.ruleInfo.cal.execute.CountStartUp;
import com.vrv.rule.ruleInfo.cal.execute.DistinctCountStartUp;
import com.vrv.rule.ruleInfo.cal.execute.SumStartUp;
import com.vrv.rule.ruleInfo.cal.impl.CountCalImpl;
import com.vrv.rule.util.FieldInfoUtil;
import lombok.extern.flogger.Flogger;
import org.apache.flink.table.expressions.Count;
import org.apache.flink.types.Row;

import com.vrv.rule.ruleInfo.udf.UdfFunctionUtil;
import com.vrv.rule.util.RoomInfoConstant;
import com.vrv.rule.vo.AggregateOperator;
import com.vrv.rule.vo.FieldInfoVO;
import com.vrv.rule.vo.LogicOperator;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 聚合处理器工具类
 *
 * @author wd-pc
 */
public class AggOperatorUtil {


    private static Logger logger = LoggerFactory.getLogger(AggOperatorUtil.class);

    /**
     * 设置相同字段类型的row
     *
     * @param inputFields
     * @param outputFields
     */
    public static void setSameFieldRow(List<FieldInfoVO> inputFields, List<FieldInfoVO> outputFields, Row inputRow,
                                       Row outputRow) {
        for (FieldInfoVO outFieldInfoVO : outputFields) {
            String outFieldName = outFieldInfoVO.getFieldName();
            setOutputField(inputFields, inputRow, outputRow, outFieldInfoVO, outFieldName);
        }
    }


    public static void setSameFieldRowWithoutGuid(List<FieldInfoVO> inputFields, List<FieldInfoVO> outputFields, Row inputRow,
                                                  Row outputRow) {
        for (FieldInfoVO outFieldInfoVO : outputFields) {
            String outFieldName = outFieldInfoVO.getFieldName();
            if (!outFieldName.equals(RoomInfoConstant.ID_ROOM_TYPE) && !outFieldName.equals(RoomInfoConstant.TIME_ROOM_TYPE)) {
                setOutputField(inputFields, inputRow, outputRow, outFieldInfoVO, outFieldName);
            }
        }
    }


    private static void setOutputField(List<FieldInfoVO> inputFields, Row inputRow, Row outputRow,
                                       FieldInfoVO outFieldInfoVO, String outFieldName) {
        String aggType = outFieldInfoVO.getAggType();
        if (aggType != null && aggType.equals("ordinary")) {   //一般属性字段
            for (FieldInfoVO inputfieldInfoVO : inputFields) {
                String inputFieldName = inputfieldInfoVO.getFieldName();
                if (inputFieldName.equals(outFieldName)) { //名称相同，表示同意字段
                    outputRow.setField(outFieldInfoVO.getOrder(), inputRow.getField(inputfieldInfoVO.getOrder()));
                    break;
                }
            }
        }
    }


    /**
     * 获得对应的聚合属性
     */

    public static void getAggregateOperator(Row outPutrow, List<FieldInfoVO> outFieldInfoVOs, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs) {
        for (FieldInfoVO outFieldInfoVO : outFieldInfoVOs) {
            AggregateOperator expression = outFieldInfoVO.getExpression();
            if (expression != null) {
                String operator = expression.getOperator();
                Integer order = outFieldInfoVO.getOrder();
                String fieldType = outFieldInfoVO.getFieldType();
                Object outFieldRow = outPutrow.getField(order);
                LogicOperator loginExp = expression.getLoginExp();
                String expressField = expression.getField();
                switch (operator) {
                    case "count": //累加个数
                        accCountOperator(outPutrow, inputRow, inputFieldInfoVOs, order, outFieldRow, loginExp,fieldType);
                        break;
                    case "distinctCount":  //个数去重
                        distinctCountOperator(outPutrow, inputRow, inputFieldInfoVOs, expressField, order, loginExp,fieldType);
                        break;
                    case "sum":   //求总数
                        accSumOperator(outPutrow, inputRow, inputFieldInfoVOs, expressField, order, outFieldRow, loginExp,fieldType);
                        break;
                    case "avg": //求平均值
                        calculateAvgValue(outPutrow, inputRow, inputFieldInfoVOs, order, expressField, loginExp);
                        break;
                    case "max": //求最大值
                        getMaxValueOperator(outPutrow, inputRow, inputFieldInfoVOs, expressField, order, outFieldRow, loginExp);
                        break;
                    case "min": //求最小值
                        getMinValueOperator(outPutrow, inputRow, inputFieldInfoVOs, expressField, order, outFieldRow, loginExp);
                        break;
                    case "concat": //连接字符串
                        accConcatOperator(outPutrow, inputRow, inputFieldInfoVOs, expressField, order, outFieldRow, loginExp);
                        break;
                    case "collection": //集合算子
                        accCollectionOperator(outPutrow, inputRow, inputFieldInfoVOs, expressField, order, outFieldRow, loginExp,fieldType);
                        break;
                    default:
                        break;
                }

            }
        }
    }


    /**
     * 集合算子方式
     * @param outPutrow
     * @param inputRow
     * @param inputFieldInfoVOs
     * @param expressField
     * @param order
     * @param outFieldRow
     * @param loginExp
     */
    private static void accCollectionOperator(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, String expressField, Integer order, Object outFieldRow, LogicOperator loginExp,String fieldType) {
        if (loginExp != null) {
            boolean results = getFilterResult(inputRow, inputFieldInfoVOs, loginExp);
            if (results) {
                AccCollectionStartUp.calAccCollection(outPutrow, inputRow, inputFieldInfoVOs, expressField, order,fieldType);
            } else {
                AccCollectionStartUp.initAccCollection(outPutrow, order, fieldType);
            }
        } else {
            AccCollectionStartUp.calAccCollection(outPutrow, inputRow, inputFieldInfoVOs, expressField, order,fieldType);
        }
    }

    private static void distinctCountOperator(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, String expressField, Integer order, LogicOperator loginExp,String fieldType) {
        if (loginExp != null) {
            boolean results = getFilterResult(inputRow, inputFieldInfoVOs, loginExp);
            if (results) {
                DistinctCountStartUp.calDistinctCount(outPutrow,inputRow,inputFieldInfoVOs,expressField,order,fieldType);
            }else{
                DistinctCountStartUp.initDistinctCount(outPutrow,order,fieldType);
            }
        } else {
            //TODO 完成相关的计算
            DistinctCountStartUp.calDistinctCount(outPutrow,inputRow,inputFieldInfoVOs,expressField,order,fieldType);
        }

    }




    /**
     * 计算平均值
     *
     * @param outPutrow
     * @param inputRow
     * @param inputFieldInfoVOs
     * @param expressField
     */
    private static void calculateAvgValue(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs,
                                          Integer outOrder, String expressField, LogicOperator loginExp) {
        if (loginExp != null) {
            boolean results = getFilterResult(inputRow, inputFieldInfoVOs, loginExp);
            if (results) {
                calAvg(outPutrow, inputRow, inputFieldInfoVOs, outOrder, expressField);
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("count", 0L);
                map.put("sum", 0L);
                map.put("avg", 0.0);
                outPutrow.setField(outOrder, map);
            }
        } else {
            calAvg(outPutrow, inputRow, inputFieldInfoVOs, outOrder, expressField);
        }


    }

    /**
     * 计算平均值
     *
     * @param outPutrow
     * @param inputRow
     * @param inputFieldInfoVOs
     * @param outOrder
     * @param expressField
     */
    private static void calAvg(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, Integer outOrder,
                               String expressField) {
        Object result = outPutrow.getField(outOrder);
        if (result == null) { //初始化 没有的值的情况下
            Map<String, Object> map = new HashMap<>();
            long count = 1L;
            map.put("count", count);
            Long inputRowValue = FieldInfoUtil.getInputFieldValue(inputRow, inputFieldInfoVOs, expressField);
            map.put("sum", inputRowValue);
            double avg = inputRowValue / count;
            map.put("avg", avg);
            outPutrow.setField(outOrder, map);
        } else {
            if (result instanceof Map<?, ?>) {
                Map<String, Object> map = (Map<String, Object>) result;
                Long count = (Long) map.get("count");
                ++count;
                Long sum = (Long) map.get("sum");
                Long inputRowValue = FieldInfoUtil.getInputFieldValue(inputRow, inputFieldInfoVOs, expressField);
                sum += inputRowValue;
                double avg = sum / count;
                map.put("count", count);
                map.put("sum", sum);
                map.put("avg", avg);
                outPutrow.setField(outOrder, map);
            }
        }
    }


    /**
     * count计算组件
     *
     * @param outPutrow
     * @param inputRow
     * @param inputFieldInfoVOs
     * @param order
     * @param field
     * @param loginExp
     */
    private static void accCountOperator(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, Integer order,
                                         Object field, LogicOperator loginExp,String fieldType) {
        if (loginExp != null) {
            boolean result = getFilterResult(inputRow, inputFieldInfoVOs, loginExp);
            if (result) {
                CountStartUp.accCount(outPutrow, order, field,fieldType);
            } else {
                CountStartUp.calCountByInit(outPutrow,fieldType,order);
            }
        } else {
            CountStartUp.accCount(outPutrow, order, field,fieldType);
        }
    }





    /**
     * 获得最大的值
     *
     * @param outPutrow
     * @param inputRow
     * @param inputFieldInfoVOs
     * @param ouputField
     * @param outRowOrder
     * @param outRowSumValue
     */
    private static void getMaxValueOperator(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs,
                                            String ouputField, Integer outRowOrder, Object outRowSumValue, LogicOperator loginExp) {
        if (loginExp != null) {
            boolean result = getFilterResult(inputRow, inputFieldInfoVOs, loginExp);
            if (result) {
                accMax(outPutrow, inputRow, inputFieldInfoVOs, ouputField, outRowOrder, outRowSumValue);
            } else {
                outPutrow.setField(outRowOrder, 0L);
            }
        } else {
            accMax(outPutrow, inputRow, inputFieldInfoVOs, ouputField, outRowOrder, outRowSumValue);
        }
    }

    /**
     * 计算最大值
     *
     * @param outPutrow
     * @param inputRow
     * @param inputFieldInfoVOs
     * @param ouputField
     * @param outRowOrder
     * @param outRowSumValue
     */
    private static void accMax(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, String ouputField,
                               Integer outRowOrder, Object outRowSumValue) {
        if (outRowSumValue != null) {
            Long inputRowValue = FieldInfoUtil.getInputFieldValue(inputRow, inputFieldInfoVOs, ouputField);
            Long outRowValue = (Long) outRowSumValue;
            if (inputRowValue > outRowValue) {
                outPutrow.setField(outRowOrder, inputRowValue);
            }
        } else {
            Long inputRowValue = FieldInfoUtil.getInputFieldValue(inputRow, inputFieldInfoVOs, ouputField);
            outPutrow.setField(outRowOrder, inputRowValue);
        }
    }

    /**
     * 获得最小的值
     *
     * @param outPutrow
     * @param inputRow
     * @param inputFieldInfoVOs
     * @param ouputField
     * @param outRowOrder
     * @param outRowSumValue
     */
    private static void getMinValueOperator(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs,
                                            String ouputField, Integer outRowOrder, Object outRowSumValue, LogicOperator loginExp) {
        if (loginExp != null) {
            boolean result = getFilterResult(inputRow, inputFieldInfoVOs, loginExp);
            if (result) {
                accMin(outPutrow, inputRow, inputFieldInfoVOs, ouputField, outRowOrder, outRowSumValue);
            } else {
                outPutrow.setField(outRowOrder, 0L);
            }
        } else {
            accMin(outPutrow, inputRow, inputFieldInfoVOs, ouputField, outRowOrder, outRowSumValue);
        }

    }

    /**
     * 计算最小值
     *
     * @param outPutrow
     * @param inputRow
     * @param inputFieldInfoVOs
     * @param ouputField
     * @param outRowOrder
     * @param outRowSumValue
     */
    private static void accMin(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, String ouputField,
                               Integer outRowOrder, Object outRowSumValue) {
        if (outRowSumValue != null) {
            Long inputRowValue = FieldInfoUtil.getInputFieldValue(inputRow, inputFieldInfoVOs, ouputField);
            Long outRowValue = (Long) outRowSumValue;
            if (inputRowValue < outRowValue) {
                outPutrow.setField(outRowOrder, inputRowValue);
            }
        } else {
            Long inputRowValue = FieldInfoUtil.getInputFieldValue(inputRow, inputFieldInfoVOs, ouputField);
            outPutrow.setField(outRowOrder, inputRowValue);
        }
    }


    /**
     * 累计总和运算器
     *
     * @param outPutrow
     */

    private static void accSumOperator(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs,
                                       String ouputField, Integer outRowOrder, Object outRowSumValue, LogicOperator loginExp,String fieldType) {
        if (loginExp != null) {
            boolean result = getFilterResult(inputRow, inputFieldInfoVOs, loginExp);
            if (result) {
                SumStartUp.accSum(outPutrow,inputRow,inputFieldInfoVOs,ouputField,outRowOrder,outRowSumValue,fieldType);
            } else {
                SumStartUp.calSumByInit(outPutrow,outRowOrder,fieldType);
            }
        } else {
            SumStartUp.accSum(outPutrow,inputRow,inputFieldInfoVOs,ouputField,outRowOrder,outRowSumValue,fieldType);
        }
    }









    /**
     * 字段连接运算器
     *
     * @param outPutrow
     * @param inputRow
     * @param inputFieldInfoVOs
     * @param order
     * @param field
     * @param loginExp
     */
    private static void accConcatOperator(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs,
                                          String expressField, Integer order, Object field, LogicOperator loginExp) {
        if (loginExp != null) {
            boolean result = getFilterResult(inputRow, inputFieldInfoVOs, loginExp);
            if (result) {
                accConcatStr(outPutrow, inputRow, inputFieldInfoVOs, expressField, order, field);
            }
        } else {
            accConcatStr(outPutrow, inputRow, inputFieldInfoVOs, expressField, order, field);
        }
    }

    /**
     * 字符串累加
     *
     * @param outPutrow
     * @param inputRow
     * @param inputFieldInfoVOs
     * @param concatField
     * @param order
     * @param field
     */
    private static void accConcatStr(Row outPutrow, Row inputRow, List<FieldInfoVO> inputFieldInfoVOs,
                                     String concatField, Integer order, Object field) {
        String inputConcatFieldValue = getInputConcatFieldValue(inputRow, inputFieldInfoVOs, concatField);
        if (field != null) {
            outPutrow.setField(order, field + "|" + inputConcatFieldValue);
        } else {
            outPutrow.setField(order, inputConcatFieldValue);
        }
    }

    /**
     * 获得输入连接的字段属性的值
     *
     * @param inputRow
     * @param inputFieldInfoVOs
     */
    private static String getInputConcatFieldValue(Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, String concatField) {
        String concat = "";
        for (FieldInfoVO fieldInfoVO : inputFieldInfoVOs) {
            if (fieldInfoVO.getFieldName().equals(concatField)) {
                Integer fieldOrder = fieldInfoVO.getOrder();
                Object field = inputRow.getField(fieldOrder);
                concat = concat + field;
                break;
            }
        }
        return concat;
    }


    /**
     * 判断过滤结果
     *
     * @param inputRow
     * @param inputFieldInfoVOs
     * @param loginExp
     * @return
     */
    private static boolean getFilterResult(Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, LogicOperator loginExp) {
        if (inputFieldInfoVOs.size() > 0) {
            Map<String, Row> map = new HashMap<>();
            FieldInfoVO fieldInfoVO = inputFieldInfoVOs.get(0);
            String tableName = fieldInfoVO.getTableName();
            map.put(tableName, inputRow);
            boolean result = loginExp.getResult(map, inputFieldInfoVOs);
            return result;
        } else {
            throw new RuntimeException("inputFieldInfoVO集合为空请检查");
        }
    }


    /**
     * 判断是否是初始化的row
     *
     * @param row
     * @return
     */
    public static boolean judgeIsInitRow(Row row) {
        int count = 0;
        int arity = row.getArity();
        for (int i = 0; i < arity; i++) {
            Object field = row.getField(i);
            if (field == null) {
                ++count;
            }
        }
        if (count == arity) { //说明全部为空，说明是初始化的状态
            return true;
        } else {
            return false;
        }
    }

}
