package com.vrv.vap.monitor.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IpTools {

    /**
     * @param ipAddress IP
     * @param timeOut   超时秒
     * @return
     */
    public static boolean ping(String ipAddress, int timeOut) {
        BufferedReader in = null;
        String pingCommand = null;
        String osName = System.getProperty("os.name");
        String windows = "Windows";
        if (osName.contains(windows)) {
            pingCommand = "ping " + ipAddress + " -n 3";
        } else {
            pingCommand = "ping " + ipAddress + " -c 3 -W " + timeOut;
        }
        try {
            Process p = Runtime.getRuntime().exec(pingCommand);
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                if (line.toLowerCase().contains("ttl=")) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
    }
}
