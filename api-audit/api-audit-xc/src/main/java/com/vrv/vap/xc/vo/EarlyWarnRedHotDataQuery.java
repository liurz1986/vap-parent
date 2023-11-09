package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import io.swagger.annotations.ApiModel;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-18
 */
@ApiModel(value="EarlyWarnRedHotData对象", description="")
public class EarlyWarnRedHotDataQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String warnType;

    private String ip;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String idCard;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String userName;

    private String areaCode;

    private String eventTime;

    private String operateCondition;

    private Date insertTime;

    private String infoJson;

    private Integer count;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getWarnType() {
        return warnType;
    }

    public void setWarnType(String warnType) {
        this.warnType = warnType;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
    public String getOperateCondition() {
        return operateCondition;
    }

    public void setOperateCondition(String operateCondition) {
        this.operateCondition = operateCondition;
    }
    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }
    public String getInfoJson() {
        return infoJson;
    }

    public void setInfoJson(String infoJson) {
        this.infoJson = infoJson;
    }
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "EarlyWarnRedHotDataQuery{" +
            "id=" + id +
            ", warnType=" + warnType +
            ", ip=" + ip +
            ", idCard=" + idCard +
            ", userName=" + userName +
            ", areaCode=" + areaCode +
            ", eventTime=" + eventTime +
            ", operateCondition=" + operateCondition +
            ", insertTime=" + insertTime +
            ", infoJson=" + infoJson +
            ", count=" + count +
        "}";
    }
}
