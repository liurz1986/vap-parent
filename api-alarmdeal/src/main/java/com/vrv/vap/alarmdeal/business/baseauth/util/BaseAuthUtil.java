package com.vrv.vap.alarmdeal.business.baseauth.util;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 2023-08
 * @author liurz
 */
public class BaseAuthUtil {
    /**
     * 网络设备
     */
    public final static String NETWORKDEVICE= "NetworkDevice";
    /**
     * 服务器
     */
    public final static String SERVICE= "service";
    /**
     * 安全保密设备
     */
    public final static String SAFEDEVICE= "SafeDevice";
    /**
     * 终端
     */
    public final static String ASSETHOST= "assetHost";
    /**
     * 运维终端
     */
    public final static String MAINTENTHOST= "maintenHost";
    /**
     * USB存储
     */
    public final static String USBMEMORG= "USBMemory";
    /**
     * USB外设设备
     */
    public final static String USBPERIPHERAL= "USBPeripheral";
    /**
     * 用户
     */
    public final static String USER= "user";
    /**
     * 文件
     */
    public final static String DATAINFOMANAGE= "dataInfoManage";
    /**
     * 应用系统
     */
    public final static String APP= "app";
    /**
     * 审批信息总数
     */
    public final static String TYPE_ALL= "all";
    /**
     * 打印权限总数
     */
    public final static String TYPE_PRINT= "print";
    /**
     * 刻录权限总数
     */
    public final static String TYPE_BURN= "burn";
    /**
     * 系统访问权限总数
     */
    public final static String TYPE_ACCESS= "access";
    /**
     * 网络互联权限总数
     */
    public final static String TYPE_INTER= "inter";
    /**
     * 网络互联权限总数
     */
    public final static String TYPE_MAINT= "maint";

    public static List<String> types = new ArrayList<>();

    static {
        types.add(TYPE_ALL);
        types.add(TYPE_PRINT);
        types.add(TYPE_BURN);
        types.add(TYPE_ACCESS);
        types.add(TYPE_INTER);
        types.add(TYPE_MAINT);
    }
    /**
     * 月：
     * X轴数据：近一个月按天处理
     * @return
     * @throws ParseException
     */
    public static List<String> getMonthDataX() throws ParseException {
        List<String> dataXS= new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // 具体天
        String endTimeStr = sdf.format(new Date());
        Date endTime = sdf.parse(endTimeStr);
        long endTimes = endTime.getTime();
        Date startTime =  DateUtils.addMonths(endTime, -1);
        long startTimes = startTime.getTime();
        while(startTimes <= endTimes){
            Date date = new Date(startTimes);
            dataXS.add(sdf.format(date));
            startTimes = startTimes+1000 * 60 * 60 * 24;
        }
        return dataXS;
    }

    /**
     * 审批对象概览中：
     * 判断接口type的值是不是符合要求
     * @param type
     * @return
     */
    public static boolean isExistType(String type){
        if(types.contains(type)){
            return true;
        }
        return false;
    }

}
