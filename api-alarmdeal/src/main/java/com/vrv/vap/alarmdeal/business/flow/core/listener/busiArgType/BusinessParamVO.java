package com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType;

import lombok.Data;

/**
 * 业务参数VO
 * @author wudi
 * @date 2022/11/16 14:22
 */
@Data
public class BusinessParamVO {

    private String extraParamURL; //http请求地址
    private String extraParamType; //http请求类型（GET/POST）
    private String extraParamTopic; //kafka请求主题
    private String requestParam; //请求参数（http or kafka同一请求参数）

}
