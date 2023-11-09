package com.vrv.vap.line.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2023-06-07
 */
@ApiModel(value="PasswordRule对象", description="")
public class PasswordRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String regRule;

    private Integer sort;

    private String specialClass;

    private LocalDateTime insertTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getRegRule() {
        return regRule;
    }

    public void setRegRule(String regRule) {
        this.regRule = regRule;
    }
    public String getSpecialClass() {
        return specialClass;
    }

    public void setSpecialClass(String specialClass) {
        this.specialClass = specialClass;
    }
    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "PasswordRule{" +
            "id=" + id +
            ", regRule=" + regRule +
            ", specialClass=" + specialClass +
            ", insertTime=" + insertTime +
        "}";
    }
}
