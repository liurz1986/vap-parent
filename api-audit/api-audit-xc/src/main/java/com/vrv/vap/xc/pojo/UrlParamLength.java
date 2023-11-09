package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * url请求参数平均长度表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-23
 */
@ApiModel(value="UrlParamLength对象", description="url请求参数平均长度表")
@TableName(value = "rpt_url_param_length")
public class UrlParamLength {

    private static final long serialVersionUID = 1L;

    @TableId(value = "data_time",type=IdType.INPUT)
    @ApiModelProperty(value = "统计时间")
    private String dataTime;

    @ApiModelProperty(value = "参数平均长度")
    private Double length;

    @ApiModelProperty(value = "入库时间")
    private Date time;

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
