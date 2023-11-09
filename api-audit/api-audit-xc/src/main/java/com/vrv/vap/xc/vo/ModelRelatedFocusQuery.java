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
 * @since 2021-05-24
 */
@ApiModel(value="ModelRelatedFocus对象", description="")
public class ModelRelatedFocusQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String deviceIp;

    private String account;

    private String focusEvent;

    private Integer modelType;

    private Date lastUpdateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
    public String getFocusEvent() {
        return focusEvent;
    }

    public void setFocusEvent(String focusEvent) {
        this.focusEvent = focusEvent;
    }
    public Integer getModelType() {
        return modelType;
    }

    public void setModelType(Integer modelType) {
        this.modelType = modelType;
    }
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "ModelRelatedFocus{" +
            "id=" + id +
            ", deviceIp=" + deviceIp +
            ", account=" + account +
            ", focusEvent=" + focusEvent +
            ", modelType=" + modelType +
            ", lastUpdateTime=" + lastUpdateTime +
        "}";
    }
}
