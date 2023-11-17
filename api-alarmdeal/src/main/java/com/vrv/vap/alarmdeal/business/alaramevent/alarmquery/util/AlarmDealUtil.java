package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.req.FieldConditionBean;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月24日 9:23
 */
public class AlarmDealUtil {
    private static final Logger logger = LoggerFactory.getLogger(AlarmDealUtil.class);

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static int getEventTypeNum(String riskEventCode) {
        int eventType=0;
        if(riskEventCode.startsWith("/safer/AbnormalUserBehavior"))
        {
            eventType=3;

        }
        // 应用异常 5
        else if(riskEventCode.startsWith("/safer/AbnormalApplicationBehavior"))
        {
            eventType=5;
        }
        //配置合规信息 1
        else if(riskEventCode.startsWith("/safer/ConfigurationCompliance"))
        {
            eventType=1;
        }
        //互联互通异常  6
        else if(riskEventCode.startsWith("/safer/ConnectivityAbnormal"))
        {
            eventType=6;
        }
        //网络安全异常 2
        else if(riskEventCode.startsWith("/safer/NetworkSecurityException"))
        {
            eventType=2;
        }
        //运维行为异常 4
        else if(riskEventCode.startsWith("/safer/OperationaBehavior"))
        {
            eventType=4;
        }
        else if(riskEventCode.startsWith("/safer/adminException"))
        {
            eventType=7;
        }
        else if(riskEventCode.startsWith("/safer/boundaryException"))
        {
            eventType=8;
        }
        return eventType;
    }

    public static void getDataTimeUtil(String timeType, String timeFormat, DateHistogramInterval timeInterval){
        switch (timeType.toLowerCase(Locale.ENGLISH)) {
            case "hour":
                timeFormat = "yyyy-MM-dd HH";
                timeInterval = DateHistogramInterval.HOUR;
                break;
            case "day":
                timeFormat = "yyyy-MM-dd";
                timeInterval = DateHistogramInterval.DAY;
                break;
            case "month":
                timeFormat = "yyyy-MM";
                timeInterval = DateHistogramInterval.MONTH;
                break;
            case "year":
                timeFormat = "yyyy";
                timeInterval = DateHistogramInterval.YEAR;
                break;
            default:
                break;
        }
    }

    public static FileItem createFileItem(File file, String fieldName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem(fieldName, "multipart/form-data", true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            fis = new FileInputStream(file);
            os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    logger.error("os.close() Exception:", e);
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    logger.error("fis.close() Exception:", e);
                }
            }
        }
        return item;
    }

    public static Script initScript(String[] groupByFields) {
        /**
         * 分隔符
         */
        final String spiltSymbol = "@#@#@";
        // 定义script
        StringBuilder scriptBuilder = new StringBuilder();
        for (int i = 0; i < groupByFields.length; i++) {
            if (i != 0) {
                scriptBuilder.append("+'" + spiltSymbol + "'+");
            }
            scriptBuilder.append(String.format("doc['%s'].value", groupByFields[i]));
        }
        return new Script(ScriptType.INLINE, "painless", scriptBuilder.toString(), new HashMap<>(0));
    }

    /**
     * 初始化 筛选条件
     * @param conditionStr
     * @return
     */
    public static List<String> initConditions(List<String> conditionStr){
        List<String> conditions = new ArrayList<>();
        if(conditionStr == null || conditionStr.size() == 0){
            return conditions;
        }
        conditionStr.forEach(item->{
            TypeToken<List<FieldConditionBean>> type = new TypeToken<List<FieldConditionBean>>(){};
            List<FieldConditionBean> fieldConditionBeans = gson.fromJson(item,type.getType());
            String sql = "";
            for(FieldConditionBean fieldConditionBean:fieldConditionBeans){
                sql += getConditionField(fieldConditionBean);

            }
            conditions.add(sql);
        });
        return conditions;
    }

    /**
     * 处理sql条件
     *
     * @param fieldConditionBean
     * @return
     */
    public static String getConditionField(FieldConditionBean fieldConditionBean){
        StringBuilder sb = new StringBuilder();
        sb.append(" and ");
        if("equals".equals(fieldConditionBean.getJudgeLogic())){
            sb.append(fieldConditionBean.getFieldName()).append(" = ").append("'").append(fieldConditionBean.getFieldValue()).append("'");
        }else if("like".equals(fieldConditionBean.getJudgeLogic())){
            sb.append(fieldConditionBean.getFieldName()).append(" like ").append("'%").append(fieldConditionBean.getFieldValue()).append("%'");
        }else if("gt".equals(fieldConditionBean.getJudgeLogic())){
            sb.append(fieldConditionBean.getFieldName()).append(" > ").append(fieldConditionBean.getFieldValue());
        }else if("lt".equals(fieldConditionBean.getJudgeLogic())){
            sb.append(fieldConditionBean.getFieldName()).append(" < ").append(fieldConditionBean.getFieldValue());
        }else if("ge".equals(fieldConditionBean.getJudgeLogic())){
            sb.append(fieldConditionBean.getFieldName()).append(" >= ").append(fieldConditionBean.getFieldValue());
        }else if("le".equals(fieldConditionBean.getJudgeLogic())){
            sb.append(fieldConditionBean.getFieldName()).append(" <= ").append(fieldConditionBean.getFieldValue());
        }else if("between".equals(fieldConditionBean.getJudgeLogic())){
            TypeToken<List<Object>> typeToken = new TypeToken<List<Object>>(){};
            List<Object> values = gson.fromJson(String.valueOf(fieldConditionBean.getFieldValue()), typeToken.getType());
            sb.append(fieldConditionBean.getFieldName()).append(" between ").append(values.get(0)).append(" and ").append(values.get(1));
        }else if("in".equals(fieldConditionBean.getJudgeLogic())){
            TypeToken<List<String>> typeToken = new TypeToken<List<String>>(){};
            List<String> values = gson.fromJson(String.valueOf(fieldConditionBean.getFieldValue()), typeToken.getType());
            sb.append(fieldConditionBean.getFieldName()).append(" in ").append("(");
            for (String value:values){
                sb.append("'").append(value).append("',");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append(")");
        }
        return sb.toString();
    }

}
