package com.vrv.vap.xc.vo;

import com.vrv.vap.xc.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by lizj on 2021/8/26.
 */

@ApiModel("异常事件查询")
public class AbnormalEventQuery extends PageModel {

    /**
     * 应用编号
     */
    @ApiModelProperty("应用编号")
    private String appId;

    /**
     * 人员编号
     */
    @ApiModelProperty("人员编号")
    private String userId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
