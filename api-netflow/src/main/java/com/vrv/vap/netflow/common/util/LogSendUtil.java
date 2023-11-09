package com.vrv.vap.netflow.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LogSendUtil {

    private static Logger logger = LoggerFactory.getLogger(LogSendUtil.class);

    public static boolean sendLogByUdp(String logContent, String sendAddress) {
        boolean sendSucFlag = true;
        DatagramSocket ds = null;
        try {
            String[] address = sendAddress.split(":");
            byte[] date = logContent.getBytes();
            InetAddress inet = InetAddress.getByName(address[0]);
            DatagramPacket dp = new DatagramPacket(date, date.length, inet,Integer.parseInt(address[1]));
            //创建DatagramSocket对象，数据包的发送和接收对象
            ds = new DatagramSocket();
            //调用ds对象的方法send，发送数据包
            ds.send(dp);
            logger.info(String.format("日志发送成功，address:%s", sendAddress));
        } catch (Exception e) {
            logger.error(String.format("日志发送失败，address:%s", sendAddress));
            logger.error(e.getMessage());
            sendSucFlag = false;
        } finally {
            if(ds != null) {
              ds.close();
            }
        }
        return sendSucFlag;
    }

}
