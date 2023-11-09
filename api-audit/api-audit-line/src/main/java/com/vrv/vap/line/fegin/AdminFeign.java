package com.vrv.vap.line.fegin;


import com.vrv.vap.line.model.BasePersonZjg;
import com.vrv.vap.toolkit.vo.VData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@FeignClient("api-admin")
public interface AdminFeign {


    /**
     * 添加资产
     */
    @RequestMapping(value = "/base/person/zjg",method = RequestMethod.GET,consumes= MediaType.APPLICATION_JSON_VALUE)
    public VData<List<BasePersonZjg>> getAllBasePersonZjg();


}
