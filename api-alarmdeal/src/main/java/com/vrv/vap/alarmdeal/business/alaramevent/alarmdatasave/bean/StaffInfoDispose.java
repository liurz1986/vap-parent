package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 责任人监管
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffInfoDispose {
    /**
     * 姓名
     */
    @SerializedName(value = "person_name", alternate = {"staffName"})
    private String person_name;
    /**
     * 责任人类型
     */
    @SerializedName(value = "person_type", alternate = "staffType")
    private String person_type;
    /**
     * 部门
     */
    @SerializedName(value = "person_dept", alternate = {"staffDepartment"})
    private String person_dept;
    /**
     * 密级
     */
    @SerializedName(value = "person_level", alternate = {"staffLevel"})
    private String person_level;
    /**
     * 所属单位
     */
    @SerializedName(value = "person_company",alternate = "staffCompany")
    private String person_company;
    /**
     *岗位
     */
    @SerializedName(value = "person_position",alternate = "staffPost")
    private String person_position;
}
