package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.BaseReportInterface;
import com.vrv.vap.admin.model.EmptyTableModle;
import com.vrv.vap.admin.service.BaseReportInterfaceService;
import com.vrv.vap.admin.vo.InterfaceVo;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@RestController
@RequestMapping(path = "/baseReportInterface")
public class BaseReportInterfaceController extends ApiController {

    @Autowired
    private BaseReportInterfaceService baseReportInterfaceService;

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("type","{\"1\":\"list数据类型\",\"2\":\"map数据类型\",\"3\":\"list map混合类型\"}");
    }

    @ApiOperation(value = "按条件查询指标")
    @PostMapping
    @SysRequestLog(description="按条件查询指标", actionType = ActionType.SELECT)
    public VData<List<BaseReportInterface>> list(@RequestBody BaseReportInterface record){
        SyslogSenderUtils.sendSelectSyslog();
        List<BaseReportInterface> all = baseReportInterfaceService.queryByParam(record);
        return this.vData(all);
    }

    @ApiOperation(value = "条件查询指标")
    @PostMapping("/page")
    @SysRequestLog(description="条件查询指标", actionType = ActionType.SELECT)
    public VList<BaseReportInterface> queryAreas(@RequestBody InterfaceVo baseReportVo) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(baseReportVo, BaseReportInterface.class);
        return this.vList(baseReportInterfaceService.findByExample(example));
    }

    @ApiOperation(value = "指标注册")
    @PutMapping
    @SysRequestLog(description="指标注册", actionType = ActionType.ADD)
    public Result register(@RequestBody BaseReportInterface record){
        BaseReportInterface reportInterface = baseReportInterfaceService.add(record);
        SyslogSenderUtils.sendAddSyslogAndTransferredField(reportInterface,"指标注册",transferMap);
        return this.vData(reportInterface);
    }

    @ApiOperation(value = "修改指标")
    @PatchMapping
    public Result edit(@RequestBody BaseReportInterface record){
        BaseReportInterface baseReportInterface = baseReportInterfaceService.findById(record.getId());
        record.setTime(new Date());
        Integer num = baseReportInterfaceService.updateSelective(record);
        if (num == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(baseReportInterface,record,"修改指标",transferMap);
        }
        return this.vData(num > 0);
    }


    @PostMapping("/emptyTable")
    public VList<Map<String,String>> emptyTable(EmptyTableModle model){
        List<Map<String,String>> result = new ArrayList<>();
        for(int i = 0; i < model.getRows(); i++){
            Map<String,String> item = new HashMap<>();
            for(int j = 0; j < model.getCols(); j++){
                item.put("key"+j," ");
            }
            result.add(item);
        }
        return this.vList(result,0);
    }
}
