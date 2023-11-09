package com.vrv.vap.xc.controller.es;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.vo.EsResult;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.ApplicationModel;
import com.vrv.vap.xc.model.FileModel;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.model.TimeModel;
import com.vrv.vap.xc.service.AbnormalEventService;
import com.vrv.vap.xc.service.ApplicationAnalysisService;
import com.vrv.vap.xc.vo.AbnormalEventQuery;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@RestController
public class AbnormalEventController {

    @Autowired
    private AbnormalEventService abnormalEventService;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }

    @PostMapping("/abnormal/tag")
    @ApiOperation("异常标签")
    public VData<List<Map<String, Object>>> abnormalTag(@RequestBody AbnormalEventQuery query){
        return abnormalEventService.abnormalTag(query);
    }

    @PostMapping("/analysis/event")
    @ApiOperation("近期异常事件查询（按时间倒序）")
    public EsResult abnormalEvent(@RequestBody AbnormalEventQuery query){
        return abnormalEventService.abnormalEvent(query);
    }

}
