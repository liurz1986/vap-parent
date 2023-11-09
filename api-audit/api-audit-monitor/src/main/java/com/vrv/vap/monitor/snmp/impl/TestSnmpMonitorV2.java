package com.vrv.vap.monitor.snmp.impl;

import com.vrv.vap.monitor.model.Monitor2Setting;
import com.vrv.vap.monitor.snmp.SnmpMonitorV2;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Date;

/**
 * 仅用于连通测试
 */
public class TestSnmpMonitorV2 extends SnmpMonitorV2 {

    private static final Log log = LogFactory.getLog(TestSnmpMonitorV2.class);

    public TestSnmpMonitorV2(Monitor2Setting setting) throws IOException {
        super(setting);
        target.setTimeout(1000);
    }

    @Override
    public void monitorAll() {
        log.debug(setting.getAssetInfo().getDevId() + "获取监控信息,IP " + setting.getAssetInfo().getDevIp() + ", 时间:" + TimeTools.format2(new Date()));
        if (!getConnectedStatus()) {
            return;
        }

        if (!testSnmp()) {
            return;
        }

    }

}
