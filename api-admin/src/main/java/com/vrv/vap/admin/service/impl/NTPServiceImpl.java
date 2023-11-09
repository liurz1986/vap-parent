package com.vrv.vap.admin.service.impl;
import com.vrv.vap.admin.service.NTPService;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ntp时间服务器
 * 2022-07-05
 */
@Service
public class NTPServiceImpl implements NTPService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public Result synchroTime(String ip) {
        logger.info("执行时间同步操作，ntp时间服务器地址："+ip);
        Result result = new Result();
        try {
            String shell="ntpdate -u "+ip;
            Map<String,Object>  excresult = executeCmd(shell);
            if(excresult.get("status").toString().equals("error")){
                result.setCode("-1");
                result.setMessage("时间同步失败:"+excresult.get("result").toString());
                logger.info("时间同步操作失败:"+excresult.get("result").toString());
                return result;
            }
            result.setCode("0");
            result.setMessage("时间同步操作成功！");
            return result;
        } catch (Exception e) {
            logger.error("修改服务器时间失败！", e);
            result.setCode("-1");
            result.setMessage("时间同步失败:"+e.getMessage());
            return result;
        }
    }

      public Map<String,Object> executeCmd(String cmd) {
        logger.info("ntp时间服务器同步命令："+cmd);
        Map<String,Object> result = new HashMap<>();
        List<String> stringList = new ArrayList<>();
        try {
            Process pro = Runtime.getRuntime().exec(cmd);
            int exitValue = pro.waitFor();
            if (0 == exitValue) {//等于0表示脚本能够正确执行
                InputStream in = pro.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = read.readLine()) != null) {
                    stringList.add(line);
                }
                in.close();
                read.close();
                result.put("status","success");
            } else {
                InputStream errorStream = pro.getErrorStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(errorStream));
                String line = null;
                while ((line = read.readLine()) != null) {
                    stringList.add(line);
                }
                errorStream.close();
                read.close();
                result.put("status","error");
            }
        } catch (Exception e) {
            result.put("status","error");
            result.put("result",e.getMessage());
        }
        result.put("result",stringList);
        return result;
    }
}
