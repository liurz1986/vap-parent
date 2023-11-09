package com.vrv.vap.admin.service.impl;


import com.vrv.vap.admin.vo.ListSysLogQuery;
import org.springframework.stereotype.Service;
import com.vrv.vap.admin.model.PageResult;
import com.vrv.vap.admin.model.ResultBody;
import com.vrv.vap.admin.service.SeparationSysLogFeign;
import com.vrv.vap.common.vo.Result;

/**
 * @BelongsProject api-admin
 * @BelongsPackage com.vrv.vap.admin.fallback
 * @Author tongliang@VRV
 * @CreateTime 2019/04/08 15:23
 * @Description (SeparationSysLogFeign远程调用的降级处理)
 * @Version
 */
@Service
public class SeparationSysLogFeignFallback implements SeparationSysLogFeign {

    private static final String MESSAGE = "远程调用server-syslog服务失败降级处理";

    @Override
    public PageResult separationSyslog(ListSysLogQuery listSysLogQuery) {
        return new PageResult().error(-1, MESSAGE);
    }

    @Override
    public PageResult listSyslog(ListSysLogQuery listSysLogQuery) {
        return new PageResult().error(-1, MESSAGE);
    }

    @Override
    public Result importSyslogExcel(String guid) {
        return new Result("-1", MESSAGE);
    }

    @Override
    public ResultBody deleteIndex() {
        return new ResultBody().error(-1, MESSAGE);
    }

    @Override
    public ResultBody loginThirtyDay() {
        return new ResultBody().error(-1, MESSAGE);
    };
}
