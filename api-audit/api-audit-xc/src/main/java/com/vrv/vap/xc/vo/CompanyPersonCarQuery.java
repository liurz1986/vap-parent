package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 企业人员车辆
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="CompanyPersonCar对象", description="企业人员车辆")
public class CompanyPersonCarQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "人员id")
    private Integer personId;

    @ApiModelProperty(value = "汽车品牌")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String carBrand;

    @ApiModelProperty(value = "车牌号")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String carNum;

    @ApiModelProperty(value = "操作人id")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }
    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }
    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    @Override
    public String toString() {
        return "CompanyPersonCar{" +
            "id=" + id +
            ", personId=" + personId +
            ", carBrand=" + carBrand +
            ", carNum=" + carNum +
            ", operator=" + operator +
            ", operateTime=" + operateTime +
        "}";
    }
}
