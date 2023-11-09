//package com.vrv.vap.xc.tools;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//
///**
// * Created by lizj on 2021/3/3
// */
//public class Test {
//    public static void main(String[] args) {
//        try {
//            System.out.println("server start!!!");
//            //设置接收端端口
//            DatagramSocket server = new DatagramSocket(6000);
//            while (true) {
//                byte[] buf = new byte[4096];
//                DatagramPacket packet = new DatagramPacket(buf, buf.length);
//                server.receive(packet);//阻塞等待输入
//                String receiveData = new String(packet.getData(),0,packet.getLength());
//                System.out.println(packet.getAddress().getHostName() + "(" + packet.getPort() + "):" + receiveData);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
