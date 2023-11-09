package com.vrv.vap.alarmdeal.business.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 资源导入json文件解析
 */
@Data
public class ImportFileJsonVO {

    private String modelName;// 模型名称

    private String version;//模型版本

    private String versionDesc; //版本说明

    private Date modelVersionCreateTime; // 版本创建时间

    private String modelTestUrl; // 模型测试接口URL

    private String modelRunUrl; // 模型运行接口URL

    private List<ModelParamHttpVO> paramList; //参数值
}
