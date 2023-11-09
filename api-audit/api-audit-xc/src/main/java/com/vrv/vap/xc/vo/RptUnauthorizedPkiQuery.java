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
 * @since 2021-05-19
 */
@ApiModel(value="RptUnauthorizedPki对象", description="")
public class RptUnauthorizedPkiQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String idCard;

    private String userName;

    private String type;

    private String month;

    private Date creaeTime;

    private Integer count;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
    public Date getCreaeTime() {
        return creaeTime;
    }

    public void setCreaeTime(Date creaeTime) {
        this.creaeTime = creaeTime;
    }
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "RptUnauthorizedPkiQuery{" +
            "id=" + id +
            ", idCard=" + idCard +
            ", userName=" + userName +
            ", type=" + type +
            ", month=" + month +
            ", creaeTime=" + creaeTime +
            ", count=" + count +
        "}";
    }
}
