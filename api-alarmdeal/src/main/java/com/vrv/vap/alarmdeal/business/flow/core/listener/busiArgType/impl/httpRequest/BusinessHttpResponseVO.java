package com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType.impl.httpRequest;

import lombok.Data;

/**
 * @author wudi
 * @date 2022/11/16 14:53
 */
@Data
public class BusinessHttpResponseVO {

    private Integer code; //状态码
    private String msg; //返回消息
    private Object data; //返回对应的数据
}
