package com.vrv.vap.netflow.vo;

import com.vrv.vap.netflow.model.BaseSecurityDomain;
import lombok.Data;

/**
 * @author lilang
 * @date 2019/8/1
 * @description
 */
@Data
public class BaseSecurityDomainRangeVO extends BaseSecurityDomain {
    private Long startIpNum;
    private Long endIpNum;
}
