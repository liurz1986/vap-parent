package com.vrv.vap.admin.service.feign;


import com.vrv.vap.admin.util.Result;
import com.vrv.vap.admin.vo.PktResultVo;
import com.vrv.vap.common.vo.VData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;


@FeignClient("api-audit-xc")
public interface AuditXcFeign {


    /**
     * 添加资产
     */
    @RequestMapping(value = "/boundary/flow/communicationTotalPkt",method = RequestMethod.POST,consumes= MediaType.APPLICATION_JSON_VALUE)
    public VData<List<PktResultVo>> communicationTotalPkt(@RequestBody Map<String,List<Map<String,Object>>> ipRangeList);


}
