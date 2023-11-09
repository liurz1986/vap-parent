package com.vrv.vap.netflow.common.util;

import java.util.UUID;

public class Uuid {
    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
