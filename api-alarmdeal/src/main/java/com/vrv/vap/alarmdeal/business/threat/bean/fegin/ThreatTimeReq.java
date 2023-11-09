package com.vrv.vap.alarmdeal.business.threat.bean.fegin;

import com.vrv.vap.alarmdeal.business.threat.bean.request.ThreatReq;
import lombok.Data;

import java.util.List;

/**
 * @author: 梁国露
 * @since: 2022/9/8 09:13
 * @description:
 */
@Data
public class ThreatTimeReq extends ThreatReq {
    // 设备ip
    private List<String> ips;
}
