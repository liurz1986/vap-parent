package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.BaseSecurityDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

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
