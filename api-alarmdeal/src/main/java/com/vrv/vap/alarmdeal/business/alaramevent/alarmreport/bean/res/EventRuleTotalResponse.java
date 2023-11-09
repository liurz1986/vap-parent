package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.bean.res;

import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月12日 17:07
 */
@Data
public class EventRuleTotalResponse extends EventTotalResponse{
    // 监管策略总计
    private long ruleTotal;

    // 配置合规性
    private long configurationNum;

    // 网络安全异常
    private long networkNum;

    // 用户行为异常
    private long userNum;

    // 运维行为异常
    private long operationaNum;

    // 应用异常
    private long applicationNum;

    // 互联互通异常
    private long connectivityNum;


}
