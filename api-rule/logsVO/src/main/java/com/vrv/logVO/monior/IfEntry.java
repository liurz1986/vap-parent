package com.vrv.logVO.monior;

import lombok.Data;

/**
 * @author TB
 * @date 2019/10/11 10:10
 */
@Data
public class IfEntry {
    private String ifIndex;
    private String ifDescr;
    private String ifType;
    private String ifMtu;
    private String ifSpeed;
    private String ifPhysAddress;
    private String ifAdminStatus;
    private String ifOperStatus;
    private String ifLastChange;
    private String ifInOctets;
    private String ifInUcastPkts;
    private String ifInDiscards;
}
