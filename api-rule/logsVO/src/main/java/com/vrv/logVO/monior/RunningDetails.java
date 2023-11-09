package com.vrv.logVO.monior;

import lombok.Data;

/**
 * @author TB
 * @date 2019/10/11 10:01
 */
@Data
public class RunningDetails {

    private String startTime;
    private String activeSessions;
    private String bytesReceived;
    private String bytesSent;
    private String currentThreadCount;
    private String currentThreadsBusy;
    private String maxActiveSessions;
    private String maxThreads;
    private String processingTime;
    private String sessionCounter;
}
