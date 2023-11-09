package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import java.util.Date;

/**
 * <p>
 * XC-数据源管理
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
@ApiModel(value="WhiteList对象", description="XC-数据源管理")
public class WhiteListQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String devid;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String company;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String number;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String name;

    private String status;

    private Date insertTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getDevid() {
        return devid;
    }

    public void setDevid(String devid) {
        this.devid = devid;
    }
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "WhiteList{" +
            "id=" + id +
            ", devid=" + devid +
            ", company=" + company +
            ", number=" + number +
            ", name=" + name +
            ", status=" + status +
            ", insertTime=" + insertTime +
        "}";
    }
}
