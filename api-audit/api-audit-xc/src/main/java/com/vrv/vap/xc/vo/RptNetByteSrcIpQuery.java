package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-24
 */
@ApiModel(value="RptNetByteSrcIp对象", description="")
public class RptNetByteSrcIpQuery extends Query {

    @ApiModelProperty(value = "源ip")
    private String srcIp;

    @ApiModelProperty(value = "源ip区域")
    private String srcArea;

    @ApiModelProperty(value = "上行数据包总数")
    private Long uploadPkgTotal;

    @ApiModelProperty(value = "上行流量字节总数")
    private Long uploadBytesTotal;

    @ApiModelProperty(value = "下行数据包总数")
    private Long downloadPkgTotal;

    @ApiModelProperty(value = "下行数据字节总数")
    private Long downloadBytesTotal;

    @ApiModelProperty(value = "数据包总数")
    private Long pkgTotal;

    @ApiModelProperty(value = "流量字节总数")
    private Long bytesTotal;

    @ApiModelProperty(value = "会话计数")
    private Long total;

    @ApiModelProperty(value = "排序类型：1.上行包数；2.下行包数；3.总包数；4.上行流量；5.下行流量；6.总流量；7.会话数")
    private Integer orderType;

    @ApiModelProperty(value = "日期")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private String yyyymmdd;

    @ApiModelProperty(value = "访问目标ip个数")
    private Integer dstIpCount;

    @ApiModelProperty(value = "数据时间")
    private Date dataTime;

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }
    public String getSrcArea() {
        return srcArea;
    }

    public void setSrcArea(String srcArea) {
        this.srcArea = srcArea;
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
    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }
    public String getYyyymmdd() {
        return yyyymmdd;
    }

    public void setYyyymmdd(String yyyymmdd) {
        this.yyyymmdd = yyyymmdd;
    }
    public Integer getDstIpCount() {
        return dstIpCount;
    }

    public void setDstIpCount(Integer dstIpCount) {
        this.dstIpCount = dstIpCount;
    }
    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    @Override
    public String toString() {
        return "RptNetByteSrcIp{" +
            "srcIp=" + srcIp +
            ", srcArea=" + srcArea +
            ", uploadPkgTotal=" + uploadPkgTotal +
            ", uploadBytesTotal=" + uploadBytesTotal +
            ", downloadPkgTotal=" + downloadPkgTotal +
            ", downloadBytesTotal=" + downloadBytesTotal +
            ", pkgTotal=" + pkgTotal +
            ", bytesTotal=" + bytesTotal +
            ", total=" + total +
            ", orderType=" + orderType +
            ", yyyymmdd=" + yyyymmdd +
            ", dstIpCount=" + dstIpCount +
            ", dataTime=" + dataTime +
        "}";
    }
}
