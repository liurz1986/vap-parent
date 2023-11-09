package com.vrv.vap.xc.model;

import com.vrv.vap.xc.pojo.BaseArea;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("区域信息")
public class BaseAreaModel extends BaseArea {

    @ApiModelProperty("ip number")
    private List<Long[]> ipNumList;

    public List<Long[]> getIpNumList() {
        return ipNumList;
    }

    public void setIpNumList(List<Long[]> ipNumList) {
        this.ipNumList = ipNumList;
    }
}
