package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 全量部门处理涉密文件情况统计表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-20
 */
@ApiModel(value="DepartSecretInfoTotal对象", description="全量部门处理涉密文件情况统计表")
@TableName(value = "rpt_depart_secret_info_total")
public class DepartSecretInfoTotal {

    private static final long serialVersionUID = 1L;

    @TableId(value = "depart_no",type = IdType.INPUT)
    @ApiModelProperty(value = "部门编号")
    private String departNo;

    @ApiModelProperty(value = "部门名称")
    private String departName;

    @ApiModelProperty(value = "处理涉密文件个数")
    private Long secretFileCount;

    @ApiModelProperty(value = "处理涉密文件次数")
    private Long secretFileNum;

    @ApiModelProperty(value = "涉及业务类别个数")
    private Integer businessCount;

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
    public Long getSecretFileCount() {
        return secretFileCount;
    }

    public void setSecretFileCount(Long secretFileCount) {
        this.secretFileCount = secretFileCount;
    }
    public Long getSecretFileNum() {
        return secretFileNum;
    }

    public void setSecretFileNum(Long secretFileNum) {
        this.secretFileNum = secretFileNum;
    }
    public Integer getBusinessCount() {
        return businessCount;
    }

    public void setBusinessCount(Integer businessCount) {
        this.businessCount = businessCount;
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
        return "DepartSecretInfoTotal{" +
            "departNo=" + departNo +
            ", departName=" + departName +
            ", secretFileCount=" + secretFileCount +
            ", secretFileNum=" + secretFileNum +
            ", businessCount=" + businessCount +
            ", time=" + time +
            ", version=" + version +
        "}";
    }
}
