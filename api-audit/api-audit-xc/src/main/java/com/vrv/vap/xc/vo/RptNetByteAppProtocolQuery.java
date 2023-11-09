package com.vrv.vap.xc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.toolkit.annotations.NotNull;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table rpt_net_byte_app_protocol
 *
 * @mbg.generated do_not_delete_during_merge 2019-12-31 14:18:57
 */
@ApiModel
@SuppressWarnings("unused")
public class RptNetByteAppProtocolQuery extends Query {
    /**
     * 协议
     */
    @ApiModelProperty("协议")
    @NotNull
    private String appProtocol;

    /**
     * 上行数据包总数
     */
    @ApiModelProperty("上行数据包总数")
    private Long uploadPkgTotal;

    /**
     * 上行流量字节总数
     */
    @ApiModelProperty("上行流量字节总数")
    private Long uploadBytesTotal;

    /**
     * 下行数据包总数
     */
    @ApiModelProperty("下行数据包总数")
    private Long downloadPkgTotal;

    /**
     * 下行数据字节总数
     */
    @ApiModelProperty("下行数据字节总数")
    private Long downloadBytesTotal;

    /**
     * 数据包总数
     */
    @ApiModelProperty("数据包总数")
    private Long pkgTotal;

    /**
     * 流量字节总数
     */
    @ApiModelProperty("流量字节总数")
    private Long bytesTotal;

    /**
     * 会话计数
     */
    @ApiModelProperty("会话计数")
    private Long total;

    /**
     * 日期
     */
    @ApiModelProperty("日期")
    private String yyyymmdd;

    /**
     * 数据时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("数据时间")
    private Date dataTime;

    public String getAppProtocol() {
        return appProtocol;
    }

    public void setAppProtocol(String appProtocol) {
        this.appProtocol = appProtocol;
    }

    public Long getUploadPkgTotal() {
        return uploadPkgTotal;
    }

    public void setUploadPkgTotal(Long uploadPkgTotal) {
        this.uploadPkgTotal = uploadPkgTotal;
    }

    public Long getUploadBytesTotal() {
        return uploadBytesTotal;
    }

    public void setUploadBytesTotal(Long uploadBytesTotal) {
        this.uploadBytesTotal = uploadBytesTotal;
    }

    public Long getDownloadPkgTotal() {
        return downloadPkgTotal;
    }

    public void setDownloadPkgTotal(Long downloadPkgTotal) {
        this.downloadPkgTotal = downloadPkgTotal;
    }

    public Long getDownloadBytesTotal() {
        return downloadBytesTotal;
    }

    public void setDownloadBytesTotal(Long downloadBytesTotal) {
        this.downloadBytesTotal = downloadBytesTotal;
    }

    public Long getPkgTotal() {
        return pkgTotal;
    }

    public void setPkgTotal(Long pkgTotal) {
        this.pkgTotal = pkgTotal;
    }

    public Long getBytesTotal() {
        return bytesTotal;
    }

    public void setBytesTotal(Long bytesTotal) {
        this.bytesTotal = bytesTotal;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getYyyymmdd() {
        return yyyymmdd;
    }

    public void setYyyymmdd(String yyyymmdd) {
        this.yyyymmdd = yyyymmdd;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }
}