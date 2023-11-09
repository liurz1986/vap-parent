package com.vrv.vap.data.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "base_report_interface")
public class BaseReportInterface {
    @Id
    @Column(name = "id", nullable = false, length = 64)
    @ApiModelProperty("指标编号")
    private String id;

    @Column(name = "name", nullable = true, length = 255)
    @ApiModelProperty("指标名称")
    private String name;

    @Column(name = "url", nullable = false, length = 5000)
    @ApiModelProperty("接口地址(协议+服务名+接口路径)")
    private String url;

    /**
     * 1：list数据类型 2：map数据类型 3：list map混合类型
     */
    @Column(name = "type", nullable = false, length = 1)
    @ApiModelProperty("接口类型")
    private String type;

    @Column(name = "result_info", nullable = true, length = -1)
    @ApiModelProperty("接口返回数据示例")
    private String resultInfo;

    @Column(name = "field_info", nullable = true, length = 5000)
    @ApiModelProperty("字段映射说明")
    private String fieldInfo;

    @Column(name = "params", nullable = true, length = 5000)
    @ApiModelProperty("接口参数")
    private String params;

    @Column(name = "md5", nullable = true, length = 64)
    @ApiModelProperty("接口md5值")
    private String md5;

    @Column(name = "time", nullable = true)
    @ApiModelProperty(value = "修改时间",hidden = true)
    private Date time;

    public String getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(String fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(String resultInfo) {
        this.resultInfo = resultInfo;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseReportInterface that = (BaseReportInterface) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(url, that.url) && Objects.equals(type, that.type) && Objects.equals(resultInfo, that.resultInfo) && Objects.equals(params, that.params) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, url, type, resultInfo, params, time);
    }
}
