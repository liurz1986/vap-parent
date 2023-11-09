package com.vrv.vap.alarmdeal.frameworks.contract.audit;

import lombok.Data;
/**
 * 组织领导查询
 * @author wd-pc
 *
 */
@Data
public class OrgLeaderQuery {
    private String orgId;
    private Integer isLeader;
    private String code;

    
}
