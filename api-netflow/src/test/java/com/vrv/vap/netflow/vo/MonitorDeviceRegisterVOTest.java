package com.vrv.vap.netflow.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.junit.Test;


/**
 * @author wh1107066
 * @date 2023/9/7
 */
public class MonitorDeviceRegisterVOTest {

    @Test
    public void toAliaJson() {
        // 把MonitorDeviceRegisterVO 使用fastjson转换成json字符串
        MonitorDeviceRegisterVO monitorDeviceRegisterVO = new MonitorDeviceRegisterVO();
        String interfaces = "[{\"type\":1,\"ip\":\"192.168.1.1\", \"netmask\":\"192.168.0.0/24\"},{\"type\":3,\"ip\":\"192.168.1.21\", \"netmask\":\"192.168.0.0/24\"},]";
        monitorDeviceRegisterVO.setInterfaces(JSONArray.parseArray(interfaces));
        monitorDeviceRegisterVO.setDeviceId("123");
        monitorDeviceRegisterVO.setDeviceSoftVersion("123");
        monitorDeviceRegisterVO.setMemTotal("123");
        monitorDeviceRegisterVO.setDeviceBelong("123");
        monitorDeviceRegisterVO.setDeviceLocation("123");
        monitorDeviceRegisterVO.setAddressCode("123");
        String cpus = "[{\"physical_id\":0, \"core\":8, \"clock\":1.8}]";
        monitorDeviceRegisterVO.setCpuInfo(JSONArray.parseArray(cpus));
        String disks = "[{\"size\":500, \"serial\":\"ST10000NM0011\"},{\"size\":1000, \"serial\":\"ST10000NM0011\"}]";
        monitorDeviceRegisterVO.setDiskInfo(JSONArray.parseArray(disks));
        monitorDeviceRegisterVO.setDeviceBelong("xx部");
        monitorDeviceRegisterVO.setAddressCode("100000");
        String contact = "[{\"name\":\"张珊\", \"email\":\"ljh@163.com\", \"phone\":\"138********\", \"position\":\"处长\"},{\"name\":\"张三三\", \"email\":\"ljh@163.com\", \"phone\":\"138********\", \"position\":\"主任\"}]";
        monitorDeviceRegisterVO.setContact(JSONArray.parseArray(contact));
        monitorDeviceRegisterVO.setMemo("首次注册");
        System.out.println(JSON.toJSONString(monitorDeviceRegisterVO));
    }

    @Test
    public void jsonToObject() {
        String str = "{\"address_code\":\"100000\",\"contact\":[{\"phone\":\"138********\",\"name\":\"张珊\",\"position\":\"处长\",\"email\":\"ljh@163.com\"},{\"phone\":\"138********\",\"name\":\"张三三\",\"position\":\"主任\",\"email\":\"ljh@163.com\"}],\"cpu_info\":[{\"core\":8,\"physical_id\":0,\"clock\":1.8}],\"device_belong\":\"xx部\",\"device_id\":\"123\",\"device_location\":\"123\",\"device_soft_version\":\"123\",\"disk_info\":[{\"size\":500,\"serial\":\"ST10000NM0011\"},{\"size\":1000,\"serial\":\"ST10000NM0011\"}],\"interface\":[{\"netmask\":\"192.168.0.0/24\",\"ip\":\"192.168.1.1\",\"type\":1},{\"netmask\":\"192.168.0.0/24\",\"ip\":\"192.168.1.21\",\"type\":3}],\"mem_total\":\"123\",\"memo\":\"首次注册\"}";
        // 把json字符串转换成MonitorDeviceRegisterVO对象
        MonitorDeviceRegisterVO monitorDeviceRegisterVO = JSON.parseObject(str, MonitorDeviceRegisterVO.class);
        System.out.println(monitorDeviceRegisterVO);

    }
}