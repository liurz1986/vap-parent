package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * <p>
 * 主机业务数据历史表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-23
 */
@ApiModel(value="AppBusinessHis对象", description="主机业务数据历史表")
@TableName("rpt_app_business_his")
public class AppBusinessHis{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "终端ip")
    private String ip;

    @ApiModelProperty(value = "业务文件名")
    private String fileName;

    @ApiModelProperty(value = "文件密级")
    private String fileLevel;

    @ApiModelProperty(value = "文件业务")
    private String fileBusiness;

    @ApiModelProperty(value = "文件审计次数")
    private Integer count;

    @ApiModelProperty(value = "数据日期")
    private String dataTime;

    @ApiModelProperty(value = "入库时间")
    private LocalDateTime time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileLevel() {
        return fileLevel;
    }

    public void setFileLevel(String fileLevel) {
        this.fileLevel = fileLevel;
    }
    public String getFileBusiness() {
        return fileBusiness;
    }

    public void setFileBusiness(String fileBusiness) {
        this.fileBusiness = fileBusiness;
    }
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "AppBusinessHis{" +
            "id=" + id +
            ", ip=" + ip +
            ", fileName=" + fileName +
            ", fileLevel=" + fileLevel +
            ", fileBusiness=" + fileBusiness +
            ", count=" + count +
            ", dataTime=" + dataTime +
            ", time=" + time +
        "}";
    }
}
