package com.vrv.vap.admin.web;

import com.vrv.vap.admin.service.NTPService;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * ntp时间服务器时间同步
 * 2022-07-05
 */
@RestController
@RequestMapping("/ntp")
public class NTPController  {
    @Autowired
    private NTPService nTPService;

    /**
     * ntp时间服务器时间同步
     * @param param
     * @return 2022-07-05
     */
    @PostMapping(value = "/synchroTime")
    @SysRequestLog(description = "ntp时间服务器时间同步",actionType = ActionType.AUTO,manually = false)
    public Result synchroServerTime(@RequestBody Map<String,Object> param) {
        Result result = new Result();
        Object obj = param.get("ip");
        if(null == obj){
            result.setCode("-1");
            result.setMessage("ntp服务器的ip地址不能为空");
            return result;
        }
        String ip = String.valueOf(obj);
        if (StringUtils.isEmpty(ip)){
            result.setCode("-1");
            result.setMessage("ntp服务器的ip地址不能为空");
            return result;
        }
        return nTPService.synchroTime(ip);
    }

    /**
     * 获取服务器时间
     * @return
     */
    @GetMapping(value="/getSystemTime")
    @SysRequestLog(description = "获取服务器时间",actionType = ActionType.SELECT,manually = false)
    public VData getSystemTime() {
        VData result = new VData();
        try {
            Date date = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(date);
            result.setCode("0");
            result.setData(time);
            result.setMessage("success");
            return result;
        } catch (Exception e) {
            result.setCode("-1");
            result.setMessage("获取服务器时间失败:"+e.getMessage());
            return result;
        }
    }
}
