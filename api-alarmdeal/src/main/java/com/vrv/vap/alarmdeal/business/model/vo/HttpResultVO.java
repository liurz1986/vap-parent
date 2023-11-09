package com.vrv.vap.alarmdeal.business.model.vo;

import lombok.Data;

/**
 * 调用模型方执行模型运行、模型测试接口返回结果实体
 */
@Data
public class HttpResultVO {
    private String status; // 状态(success表示成功、error表示失败)
    private String msg; // 描述 (错误信息)
}
