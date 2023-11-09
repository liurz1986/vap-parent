package com.vrv.vap.line.tools;

public class FlinkTools {
    public static String parseJobId(String log){
        String jobid = "";
        if(log.lastIndexOf("JobID") > -1){
            jobid = log.substring(log.lastIndexOf("JobID")+5,log.length());
        }
        return jobid.trim();
    }
}
