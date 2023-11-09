package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class PersonRecordQuery extends Query {

    /**
     * 身份证号码
     */
    @ApiModelProperty("身份证号码")
    private String idCard;

    /**
     * 日期（年月，yyyy-MM）
     */
    @ApiModelProperty("日期（年月，yyyy-MM）")
    private String timeFlag;

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getTimeFlag() {
        return timeFlag;
    }

    public void setTimeFlag(String timeFlag) {
        this.timeFlag = timeFlag;
    }
}