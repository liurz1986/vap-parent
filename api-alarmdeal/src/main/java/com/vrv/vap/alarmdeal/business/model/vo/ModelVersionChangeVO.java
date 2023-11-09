package com.vrv.vap.alarmdeal.business.model.vo;

import lombok.Data;

/**
 * 模型切换接口参数VO
 */
@Data
public class ModelVersionChangeVO {
    private String oldGuid; // 切换前模型配置id

    private String newGuid; // 切换后模型配置id


}
