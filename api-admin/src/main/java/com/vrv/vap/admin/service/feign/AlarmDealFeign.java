package com.vrv.vap.admin.service.feign;


import com.vrv.vap.admin.util.Result;
import com.vrv.vap.admin.vo.AssetTypeVO;
import com.vrv.vap.admin.vo.AssetVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;


@FeignClient("api-alarmdeal")
public interface AlarmDealFeign {


    /**
     * 添加资产
     */
    @RequestMapping(value = "/AlarmEventManagement/getIpGroup",method = RequestMethod.GET,consumes= MediaType.APPLICATION_JSON_VALUE)
    public Result<Map<String, Long>> getIpGroup();


}
