package com.vrv.rule.ruleInfo;

import com.google.gson.Gson;
import com.vrv.rule.model.FlinkStartVO;
import org.apache.flink.types.Row;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wudi
 * @date 2023/6/7 17:40
 */
public class Test {

 public static void main(String[] args) {
//     long timeStamp = 1695610921;
//     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//     String date = sdf.format(new Date(timeStamp * 1000L));
//     System.out.println(date);
//     String s = "and ${netflow.app_protocol} not in (select abc from table where 1=1) and ${session_protocol} not in (select abc from table where 1=1)";
//     Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
//     Matcher matcher = pattern.matcher(s);
//     if (matcher.find()) {
//         System.out.println(matcher.group(1));
//     } else {
//         System.out.println("未找到匹配内容");
//     }
     Row row = new Row(3);
     row.setField(0,"1");
     Object field = row.getField(2);
     System.out.println(field);


 }


    private static FlinkStartVO getFlinkStartVO() {
        FlinkStartVO flinkStartVO = new FlinkStartVO();
        Map<String, String> map = new HashMap<>();
        map.put("终端防护软件客户端（防病毒/安全登录/主机审计/三合一）不在线（心跳）_637049076f49432494188c71338a1e43", "7d716ffed5e34b969b0b63096b492c67,9b5004c7d16d4b9c98034b42092d64b3");
        map.put("用户本地安装非授权软件_21ac939161b04330b5f8144da7ec8870", "6d6abaffb2384509a635bc8c2378b7ad,e9293710816f4fb3a31ccffee775a7f0");
        flinkStartVO.setFilterOperators(null);
        flinkStartVO.setCodeObj(map);
        flinkStartVO.setParallelism(2);
        flinkStartVO.setJobName("进程日志");
        flinkStartVO.setType("datasource");
        return flinkStartVO;
    }

}
