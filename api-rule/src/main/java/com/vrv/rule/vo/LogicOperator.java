package com.vrv.rule.vo;

import com.vrv.rule.ruleInfo.udf.UdfFunctionUtil;
import com.vrv.rule.util.ArrayUtil;
import com.vrv.rule.util.FieldInfoUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.types.Row;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 逻辑运算符(逻辑树)
 *
 * @author wd-pc
 */
@Data
public class LogicOperator implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private ExpVO exp;
    private List<LogicOperator> filters;
    private String key;
    private String parent;
    private String type; //AND,OR,filter
    private StringBuffer sb = new StringBuffer();

    /**
     * 获得过滤筛选条件
     *
     * @return
     */
    public String getFilterCondition() {
        if ("filter".equals(type)) {
            String content = null;
            StringBuffer sb = new StringBuffer();
            String valueType = exp.getValueType();
            String operator = exp.getOperator();
            boolean nonAttr = exp.isNonAttr(); //是否是非属性
            switch (valueType) {
                case "attribute":
                    content = getFilterInfo(sb, operator, nonAttr);
                    break;
                case "params":
                case "constant":
                    content = getConstantFilterOperator(sb, operator, nonAttr);
                    break;
                case "resource":
                    content = getResourceFunc(sb);
                    break;
                default:
                    break;
            }
            return content;
        }

        if ("and".equalsIgnoreCase(type)) {
            List<String> collect = filters.stream().map(f -> f.getFilterCondition()).collect(Collectors.toList());
            return logicStr(collect, "and", "or");

        }

        if ("or".equalsIgnoreCase(type)) {
            List<String> collect = filters.stream().map(f -> f.getFilterCondition()).collect(Collectors.toList());
            return logicStr(collect, "or", "and");
        }
        return sb.toString();
    }


    /**
     * 获得过滤信息
     *
     * @param sb
     * @param operator
     */
    private String getFilterInfo(StringBuffer sb, String operator, boolean nonAttr) {
        sb.append(exp.getField());
        sb.append(" ");
        switch (operator) {
            case "like":
                sb.append("like");
                sb.append(" ");
                sb.append("CONCAT('%'," + exp.getValue() + ",'%')");
                break;
            case "likeBegin":
                sb.append("like");
                sb.append(" ");
                sb.append("CONCAT(''," + exp.getValue() + ",'%')");
                break;
            case "likeEnd":
                sb.append("like");
                sb.append(" ");
                sb.append("CONCAT('%'," + exp.getValue() + ",'')");
                break;
            case "between":
                getBetweenOperator(sb, operator,exp.getValue());
                break;
            case "in":
                sb.append(operator);
                sb.append(" ");
                sb.append("(");
                sb.append(exp.getValue());
                sb.append(")");
                break;
            default:
                sb.append(operator);
                sb.append(" ");
                sb.append(exp.getValue());
                break;
        }
        if (nonAttr) {  //如果是非选项
            String filterCondition = sb.toString();
            filterCondition = "not (" + filterCondition + ") ";
            return filterCondition;
        } else {
            return sb.toString();
        }
    }


    /**
     * 获得常量的
     *
     * @param sb
     */
    private String getConstantFilterOperator(StringBuffer sb, String operator, boolean nonAttr) {
        sb.append(exp.getField());
        sb.append(" ");
        switch (operator) {
            case "like":
                sb.append(operator);
                sb.append(" ");
                sb.append("'%" + exp.getValue() + "%'");
                break;
            case "likeBegin":
                sb.append("like");
                sb.append(" ");
                sb.append("'" + exp.getValue() + "%'");
                break;
            case "likeEnd":
                sb.append("like");
                sb.append(" ");
                sb.append("'%" + exp.getValue() + "'");
                break;
            case "between":
                getBetweenOperator(sb, operator,exp.getValue());
                break;
            case "in":
                getInOperator(sb, operator,exp.getValue());
                break;
            default:
                getDefaultOperator(sb, operator,exp.getValue());
                break;
        }
        if (nonAttr) {  //如果是非选项
            String filterCondition = sb.toString();
            filterCondition = "not (" + filterCondition + ") ";
            return filterCondition;
        } else {
            return sb.toString();
        }
    }

    private void getDefaultOperator(StringBuffer sb, String operator,String inputValue) {
        String fieldType = exp.getFieldType();
        if (StringUtils.isNotEmpty(fieldType)) {
            if ("varchar".equalsIgnoreCase(fieldType)) { //TODO 过滤字符串类型
                sb.append(operator);
                sb.append(" ");
                sb.append("'" +inputValue + "'");
            } else {
                sb.append(operator);
                sb.append(inputValue);
            }
        } else {
            sb.append(operator);
            sb.append(getValue(inputValue));
        }
    }

    private void getInOperator(StringBuffer sb, String operator,String inputValue) {
        sb.append(operator);
        sb.append(" ");
        sb.append("(");
        String fieldType = exp.getFieldType();
        if (StringUtils.isNotEmpty(fieldType)) {
            if ("varchar".equalsIgnoreCase(fieldType)) { //TODO 过滤字符串类型
                String[] split = inputValue.split(",");
                //TODO split数组每个元素加上单引号
                for (int i = 0; i < split.length; i++) {
                    split[i] = "'" + split[i] + "'";
                }
                String join = ArrayUtil.join(split, ",");
                sb.append(join);
            } else {
                sb.append(inputValue);  //数值类型（只考虑数值类型和字符串类型的区分）
            }
        } else {
            sb.append(getInValue(inputValue));  //TODO 维护原先情况不会发生变化
        }
        sb.append(")");
    }


    /**
     * 通过inputValue获得对应的操作值
     * @param sb
     * @param operator
     * @param inputValue
     */

    /**
     * 获得between的数据
     *
     * @param sb
     * @param operator
     */
    private void getBetweenOperator(StringBuffer sb, String operator,String inputValue){
        sb.append(operator);
        String[] numArr = inputValue.split(",");
        if (numArr.length == 2) {
            String max = numArr[1];
            String min = numArr[0];
            sb.append(" " + min + " and " + max + " ");
        } else {
            throw new RuntimeException(numArr + "数组长度不等于2，请检查！");
        }
    }


    //TODO 通过inputValue获得对应的操作值(这个地方存在问题·)
    /**
     * 获得对应的值根据判断是不是整数
     * @param inputValue
     * @return
     */
    private String getValue(String inputValue) {
        if(inputValue.equals("")){
            return "''";
        }
        boolean integer = UdfFunctionUtil.isInteger(inputValue);
        if(integer) {
            return inputValue;
        }else {
            return "'"+inputValue+"'";
        }
    }

    private String getInValue(String inputValue) {

        String[] split = inputValue.split(",");
        //TODO split数组每个元素加上单引号
        for (int i = 0; i < split.length; i++) {
            split[i] = "'" + split[i] + "'";
        }
        String join = ArrayUtil.join(split, ",");
         return join;

    }

    /**
     * 获得对应的值根据判断是不是整数
     *
     * @param inputValue
     * @return
     */

    private String getDimensionFilterOperator(StringBuffer sb, String inputValue, String operator, boolean nonAttr) {
        sb.append(exp.getField());
        sb.append(" ");
        switch (operator) {
            case "like":
                sb.append(operator);
                sb.append(" ");
                sb.append("'%" + inputValue + "%'");
                break;
            case "likeBegin":
                sb.append("like");
                sb.append(" ");
                sb.append("'" + inputValue + "%'");
                break;
            case "likeEnd":
                sb.append("like");
                sb.append(" ");
                sb.append("'%" + inputValue + "'");
                break;
            case "between":
                getBetweenOperator(sb, operator,inputValue);
                break;
            case "in":
                getInOperator(sb, operator,inputValue);
                break;
            default:
                getDefaultOperator(sb, operator,inputValue);
                break;
        }
        if (nonAttr) {  //如果是非选项
            String filterCondition = sb.toString();
            filterCondition = "not (" + filterCondition + ") ";
            return filterCondition;
        } else {
            return sb.toString();
        }
    }


    /**
     * 获得维表过滤筛选条件
     *
     * @return
     */
    public String getDimensionFilterCondition(Row row, List<FieldInfoVO> inputFields) {
        if ("filter".equals(type)) {
            boolean nonAttr = exp.isNonAttr(); //是否是非属性
            StringBuffer sb = new StringBuffer();
            String valueType = exp.getValueType();
            switch (valueType) {
                case "attribute":
                    String eventField = exp.getValue();   //事件表属性
                    String inputValue = FieldInfoUtil.getDataStreamFieldInfoValue(row, inputFields, eventField);
                    getDimensionFilterOperator(sb, inputValue, exp.getOperator(), nonAttr);
                    break;
                case "eventAttribute":
                case "constant":
                    getDimensionFilterOperator(sb, exp.getValue(), exp.getOperator(), nonAttr);
                    break;
                default:
                    break;
            }
            if (nonAttr) {  //如果是非选项
                String filterCondition = sb.toString();
                filterCondition = "not (" + filterCondition + ") ";
                return filterCondition;
            } else {
                return sb.toString();
            }
        }

        if ("and".equalsIgnoreCase(type)) {
            List<String> collect = filters.stream().map(f -> f.getDimensionFilterCondition(row, inputFields)).collect(Collectors.toList());
            return logicStr(collect, "and", "or");

        }

        if ("or".equalsIgnoreCase(type)) {
            List<String> collect = filters.stream().map(f -> f.getDimensionFilterCondition(row, inputFields)).collect(Collectors.toList());
            return logicStr(collect, "or", "and");
        }
        return sb.toString();
    }


    /**
     * 获得资源属性
     */
    private String getResourceFunc(StringBuffer sb) {
        String express = null;
        String resourceType = exp.getResourceType();
        String field = exp.getField();
        String operator = exp.getOperator();
        String value = exp.getValue();
        switch (resourceType) {
            case "ip":
                express = "ipResourceFunction" + "(" + field + ",'" + operator + "'" + ",'" + value + "'" + ")" + "=" + 1;
                break;
            case "date":
                express = "timeResourceFunction" + "(" + field + ",'" + operator + "'" + ",'" + value + "'" + ")" + "=" + 1;
                break;
            case "port":
                express = "portResourceFunction" + "(" + field + ",'" + operator + "'" + ",'" + value + "'" + ")" + "=" + 1;
                break;
            case "regex":
                express = "regularExpressionFunction" + "(" + field + ",'" + operator + "'" + ",'" + value + "'" + ")" + "=" + 1;
                break;
            case "string":    //默认采用逗号进行比较
                String expVlueType = exp.getExpVlueType();
                if (expVlueType.equals("constant")) {  //等于常量的情况
                    express = "stringResourceFunction" + "(" + field + ",'" + operator + "'" + ",'" + value + "'" + ")" + "=" + 1;
                } else {  //等于属性的情况
                    express = "stringResourceFunction" + "(" + field + ",'" + operator + "'" + "," + value + ")" + "=" + 1;
                }
                break;
            default:
                break;
        }
        sb.append(express);
        return sb.toString();
    }


    private String logicStr(List<String> collect, String logic, String unlogic) {
        String[] strsArray = collect.toArray(new String[collect.size()]);
        List<String> arrayStrs = new ArrayList<>();

        for (String str : strsArray) {
            if (str.split(unlogic).length > 1) {
                arrayStrs.add("(" + str + ")");
            } else {
                arrayStrs.add(str);
            }
        }
        String[] strsArrays = arrayStrs.toArray(new String[arrayStrs.size()]);
        String join = ArrayUtil.join(strsArrays, " " + logic + " ");
        return join;
    }


    public boolean getResult(Map<String, Row> map, List<FieldInfoVO> inputFieldInfoVOs) {
        if ("filter".equals(type)) {
            return exp.getResult(map, inputFieldInfoVOs);
        }
        if ("and".equalsIgnoreCase(type)) {
            List<Boolean> collect = filters.stream().map(f -> f.getResult(map, inputFieldInfoVOs)).collect(Collectors.toList());
            return and(collect);
        }
        if ("or".equalsIgnoreCase(type)) {
            List<Boolean> collect = filters.stream().map(f -> f.getResult(map, inputFieldInfoVOs)).collect(Collectors.toList());
            return or(collect);
        }
        return false;
    }

    private boolean or(List<Boolean> collect) {
        for (Boolean b : collect) {
            if (b) {
                return true;
            }
        }
        return false;
    }

    private boolean and(List<Boolean> ands) {
        for (Boolean b : ands) {
            if (!b) {
                return false;
            }
        }

        return true;
    }

}
