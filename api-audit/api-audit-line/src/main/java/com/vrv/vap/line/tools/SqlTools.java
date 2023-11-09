package com.vrv.vap.line.tools;

import com.vrv.vap.line.model.BaseLinePage;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SqlTools {
    public static String buildPageSql(List<BaseLinePage> records, String now){
        StringBuffer sql = new StringBuffer("INSERT INTO base_line_page (`user_ip`,`frequency`,`inefficiency`,`purity`,data_time,insert_time,sys_id,`type`,`size`,time_total,invalid_num,resource_num,guid) VALUES ");
        int i = 0;
        for(BaseLinePage r : records){
            if(i != 0){
                sql.append(",");
            }
            sql.append("(");
            sql.append("'").append(r.getUserIp()).append("'").append(",");
            sql.append(r.getFrequency()).append(",");
            sql.append(r.getInefficiency()).append(",");
            sql.append(r.getPurity()).append(",");
            sql.append("'").append(r.getDateTime()).append("'").append(",");
            sql.append("'").append(now).append("'").append(",");
            if(StringUtils.isNotEmpty(r.getSysId())){
                sql.append("'").append(r.getSysId()).append("'").append(",");
            }else{
                sql.append("NULL").append(",");
            }
            sql.append("'").append(r.getType()).append("'").append(",");
            sql.append(r.getSize()).append(",");
            sql.append(r.getTimeTotal()).append(",");
            sql.append(r.getInvalidNum()).append(",");
            sql.append(r.getResourceNum()).append(",");
            sql.append("'").append(r.getGuid()).append("'");
            sql.append(")");
            i++;
        }
        return sql.toString();
    }
}
