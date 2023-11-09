package com.vrv.vap.alarmdeal.business.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 获取版本信息VO
 */
@Data
public class ModelVersionVO {
    private String guid; //模型配置id

    private String version;//模型版本

    private String modelId;// 模型id

    private String modelName;// 模型名称

    private String versionDesc; //版本说明
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modelVersionCreateTime; // 版本创建时间

}
