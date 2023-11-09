package com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO;

import lombok.Data;

@Data
public class ParamConfigVO {

    private String paramName; //参数名称

    private String paramKey;  //参数唯一标识

    private String paramType;  //参数默认值
    
    private String paramDesc;  //参数描述

    private Object defaultValue;  //参数默认值

  	private String tag;  //实例是否允许启动


    @Override
    public String toString() {
        return this.paramName + "," + this.paramKey + "," + this.paramType + "," + this.defaultValue;
    }
}
