package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="OnWorkProcData对象", description="")
public class OnWorkProcDataQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String areaCode;

    private String ip;

    private String pId;

    private String process;

    private String signedName;

    private String version;

    private String md5;

    private String dataTime;

    private Date insertTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }
    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }
    public String getSignedName() {
        return signedName;
    }

    public void setSignedName(String signedName) {
        this.signedName = signedName;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "OnWorkProcDataQuery{" +
            "id=" + id +
            ", areaCode=" + areaCode +
            ", ip=" + ip +
            ", pId=" + pId +
            ", process=" + process +
            ", signedName=" + signedName +
            ", version=" + version +
            ", md5=" + md5 +
            ", dataTime=" + dataTime +
            ", insertTime=" + insertTime +
        "}";
    }
}
