package com.vrv.vap.admin.web;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.common.batch.BatchQueue;
import com.vrv.vap.admin.service.MonitorLogService;
import com.vrv.vap.admin.vo.MonitorLogVO;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2021/8/11
 * @description
 */
@RestController
@RequestMapping(path = "/V1/log")
@Slf4j
public class MonitorLogController extends ApiController {

    @Autowired
    BatchQueue<Map> batchQueue;


    @Autowired
    MonitorLogService monitorLogService;


    @PostMapping
    @ApiOperation("数据上报")
    public Result reportData(@RequestBody List<Map> logs, HttpServletRequest httpServletRequest) {
        try {
            log.debug(JSON.toJSONString(logs));
            if (CollectionUtils.isNotEmpty(logs)) {
                for (Map log : logs) {
                    if (log.containsKey("data_type") && (Integer) (log.get("data_type")) == 2) {
                        monitorLogService.saveBaseInfo(log);
                    }
                    if (log.containsKey("data_type") && (Integer) (log.get("data_type")) == 1) {
                        handlerFileLog(log);

                        batchQueue.add(log);
                    }

                }
            }
        }catch (Exception exception){
            log.debug("接收处理报错");
            exception.printStackTrace();
        }
        return this.result(true);
    }


    private void handlerFileLog(Map log){
        if(!log.containsKey("log_type")||!((Integer)(log.get("log_type"))==3)){
            return;
        }
        if(!log.containsKey("file_list")||log.get("file_list")==null){
            return;
        }
        //http，https文件需要拆分
        List<Map> fileList = (List)log.get("file_list");
        fileList.forEach(file->{
            Map newFileMap = new HashMap();
            copyValue(log,newFileMap,"file_list");
            copyValue(file,newFileMap,"file_list");
            newFileMap.put("log_type",99);
            batchQueue.add(newFileMap);
        });
    }

    private void copyValue(Map source,Map target,String excludes){
        source.keySet().stream().filter(p->!excludes.equals(p)).forEach(p->{
            target.put(p,source.get(p));
        });
    }
}
