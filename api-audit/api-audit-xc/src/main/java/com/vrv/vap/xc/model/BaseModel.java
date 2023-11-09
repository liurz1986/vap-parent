package com.vrv.vap.xc.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;

import java.io.Serializable;

@ApiModel("基础模型")
public class BaseModel extends PageModel implements Serializable {

    /**
     * 用于补充日志信息
     */
    @JsonIgnore
    @TableField(exist = false)
    private String extendDesc;

    public BaseModel() {
    }

    @JsonIgnore
    public String getExtendDesc() {
        return extendDesc;
    }

    public void setExtendDesc(String extendDesc) {
        this.extendDesc = extendDesc;
    }
}
