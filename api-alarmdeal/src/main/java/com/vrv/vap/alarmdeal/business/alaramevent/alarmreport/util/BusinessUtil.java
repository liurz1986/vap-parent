package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.util;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.DeviceInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.UnitInfo;
import com.vrv.vap.jpa.common.ArrayUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wudi
 * @date 2022/4/20 17:07
 */
public class BusinessUtil {



    /**
     * 获得设备
     * @param alarmEventAttribute
     * @return
     */
    public static String getDev(AlarmEventAttribute alarmEventAttribute) {
        String deviceIpsStr = null;
        Set<String> deviceIps = new HashSet<>();
        List<DeviceInfo> deviceInfos = alarmEventAttribute.getDeviceInfos();
        if(deviceInfos!=null){
            for (DeviceInfo deviceInfo:deviceInfos) {
                String deviceIp = deviceInfo.getDeviceIp();
                if(StringUtils.isNotEmpty(deviceIp) && !"null".equals(deviceIp)){
                    deviceIps.add(deviceIp);
                }
            }
            List<String> ids=new ArrayList<>(deviceIps);
            String[] deviceIpArr = deviceIps.toArray(new String[ids.size()]);
            deviceIpsStr = ArrayUtil.join(deviceIpArr, ",");
        }
        return deviceIpsStr;
    }


    /**
     * 获得org相关内容
     * @param alarmEventAttribute
     * @return
     */
    public static String getOrg(AlarmEventAttribute alarmEventAttribute) {
        String unitInfosStr = null;
        List<String> UN = new ArrayList<>();
        //todo 这里告警事件有原来的对象改成了数组
        List<UnitInfo> unitList = alarmEventAttribute.getUnitList();
        UnitInfo unitInfo=null;
        if(unitList!=null&&unitList.size()>0){
            unitInfo=unitList.get(0);
        }
        if(unitInfo!=null){
            unitInfosStr = unitInfo.getUnitDepartName();
        }
        return unitInfosStr;
    }

    /**
     * 返回对应
     * @param all
     */
    public static int getDevCount(List<AlarmEventAttribute> all) {
        Set<String> set = new HashSet<>();
        for (AlarmEventAttribute alarmEventAttribute: all){
            List<DeviceInfo> deviceInfos = alarmEventAttribute.getDeviceInfos();
            if(deviceInfos!=null){
                for (DeviceInfo deviceInfo:deviceInfos) {
                    set.add(deviceInfo.getDeviceIp());
                }
            }
        }
        return set.size();
    }

}
