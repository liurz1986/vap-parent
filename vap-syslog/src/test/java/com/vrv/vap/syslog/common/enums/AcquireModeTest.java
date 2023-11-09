package com.vrv.vap.syslog.common.enums;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wh1107066
 * @date 2022/8/18 9:45
 */

public class AcquireModeTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void acqModeEnu(){
        String name = AcquireMode.MANUALLY.name().toLowerCase();
        logger.info(String.format("name: %s", name));
        Assert.assertEquals(name, "manually");
    }
}