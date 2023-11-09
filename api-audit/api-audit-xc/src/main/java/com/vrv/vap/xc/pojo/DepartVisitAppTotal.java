package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 全量部门访问应用情况统计表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-20
 */
@ApiModel(value="DepartVisitAppTotal对象", description="全量部门访问应用情况统计表")
@TableName(value = "rpt_depart_visit_app_total")
public class DepartVisitAppTotal{

    private static final long serialVersionUID = 1L;

    @TableId(value = "depart_no")
    @ApiModelProperty(value = "部门编号")
    private String departNo;

    @ApiModelProperty(value = "部门名称")
    private String departName;

    @ApiModelProperty(value = "应用个数")
    private Long appCount;

    @ApiModelProperty(value = "总访问次数")
    private Long visitNum;

    @ApiModelProperty(value = "统计日期")
    private Date time;

    @ApiModelProperty(value = "数据版本")
    private String version;

    public String getDepartNo() {
        return departNo;
    }

    public void setDepartNo(String departNo) {
        this.departNo = departNo;
    }
    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }
    public Long getAppCount() {
        return appCount;
    }

    public void setAppCount(Long appCount) {
        this.appCount = appCount;
    }
    public Long getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(Long visitNum) {
        this.visitNum = visitNum;
    }
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "DepartVisitAppTotal{" +
            "departNo=" + departNo +
            ", departName=" + departName +
            ", appCount=" + appCount +
            ", visitNum=" + visitNum +
            ", time=" + time +
            ", version=" + version +
        "}";
    }
}
