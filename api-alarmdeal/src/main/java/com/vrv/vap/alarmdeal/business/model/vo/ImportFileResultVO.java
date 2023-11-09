package com.vrv.vap.alarmdeal.business.model.vo;

import lombok.Data;

/**
 * 资源导入接口返回对象
 */
@Data
public class ImportFileResultVO extends ImportFileJsonVO {

    private String modelFileGuid;// 模型路径

    private String modelFileName; // 模型文件名称

}
