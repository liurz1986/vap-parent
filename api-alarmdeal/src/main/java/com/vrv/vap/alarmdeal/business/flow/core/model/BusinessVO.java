package com.vrv.vap.alarmdeal.business.flow.core.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 业务VO
 * @author wudi
 * @date 2022/11/17 14:57
 */
@Data
public class BusinessVO {

     private String processInstanceId;   //流程实例ID
     private Map<String,Object> params; //参数
     private String action; //操作；
     private String userId;

}
