package com.vrv.vap.alarmdeal.business.baseauth.vo.query;

import com.vrv.vap.jpa.web.page.PageReqVap;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;


@Data
@ApiModel(value = "运维权限审批表")
public class BaseAuthInternetQueryVo extends PageReqVap {

    private Integer id; //id


    private String ip ;//ip
    private String ips;
    private Integer  internetId;
    private Date createTime; //创建时间
}
