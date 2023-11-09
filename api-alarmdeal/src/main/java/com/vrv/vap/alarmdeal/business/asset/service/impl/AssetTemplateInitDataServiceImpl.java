package com.vrv.vap.alarmdeal.business.asset.service.impl;


import com.vrv.vap.alarmdeal.business.asset.service.AssetExportAndImportService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTemplateInitDataService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetExportInitDataVO;
import com.vrv.vap.alarmdeal.business.asset.vo.CustomSettings;
import com.vrv.vap.alarmdeal.business.asset.vo.ExcelValidationData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.soap.Addressing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产导入模板中预置数据
 *
 * 2022-09-13
 */
@Service
public class AssetTemplateInitDataServiceImpl implements AssetTemplateInitDataService {

    public static final String HOST="host";// 终端
    public static final String SERVICE="service"; // 服务器
    public static final String NETWORK="network"; // 网络设备
    public static final String OFFICE="office"; // 通用办公设备
    public static final String SAFE="safe"; // 安全保密产品
    public static final String USBMEMORY="usbmemory"; // USB存储
    public static final String USBMPERIP="usbperip"; //USB外设
    public static final String MAINTENTHost="MaintenHost"; //运维终端
    @Override
    public List<String> getInitDataByType(String type,List<CustomSettings> excelColumns) {
        switch (type){
            case HOST:  // 终端
                return getHostInitData(excelColumns);
            case SERVICE:  // 服务器
                return getServiceInitData(excelColumns);
            case NETWORK:  // 网络设备
                return getNetworkDeviceInitData(excelColumns);
            case OFFICE: // 通用办公设备
                return getOfficeDeviceInitData(excelColumns);
            case SAFE: // 安全保密产品
                return getSafeInitData(excelColumns);
            case USBMEMORY: // USB存储
                return getUSBMemoryInitData(excelColumns);
            case USBMPERIP:  //USB外设
                return getUSBPeripheralInitData(excelColumns);
            case MAINTENTHost:  //运维终端
                return getMaintenHostInitData(excelColumns);
        }
        return null;
    }
    @Override
    public String getTypeByTreeCode(String treeCode){
        if(treeCode.contains("asset-Host-")){ // 终端
            return HOST;
        }
        if(treeCode.contains("asset-service-")){ // 服务器
            return SERVICE;
        }
        if(treeCode.contains("asset-NetworkDevice-")){ // 网络设备
            return NETWORK;
        }
        if(treeCode.contains("asset-OfficeDevice-")){ // 通用办公设备
            return OFFICE;
        }
        if(treeCode.contains("asset-SafeDevice-")){ // 安全保密产品
            return SAFE;
        }
        if(treeCode.contains("asset-USBMemory-")){ // USB存储
            return USBMEMORY;
        }
        if(treeCode.contains("asset-USBPeripheral-")){  //USB外设
            return USBMPERIP;
        }
        if(treeCode.contains("asset-MaintenHost-")){  //运维终端
            return MAINTENTHost;
        }
        return null;
    }

    /**
     * 构造终端数据
     * @param excelColumns
     * @return
     */
    private  List<String> getHostInitData(List<CustomSettings> excelColumns) {
        AssetExportInitDataVO host = commonInitData();
        host.setName("终端设备"); // 名称
        host.setTypeName("台式机"); // 二级资产名称
        host.setSerialNumber("J00001"); // 设备序列号
        host.setOsList("windows"); // 安装操作系统
        host.setIsMonitorAgent("已安装"); // 是否安装客户端
        host.setOsSetuptimeStr("2022-01-19 18:04:25"); // 操作系统安装时间

        // 扩展字段 ：[{"name": "extendTypeSno", "title": "设备品牌"}, {"name": "extendVersionInfo", "title": "设备型号"},
        // {"name": "extendDiskNumber", "title": "磁盘序列号"}, {"name": "extendSystem", "title": "操作系统名称"},
        // {"name": "sysSno", "title": "操作系统版本"}]
        Map<String, Object> extendMap = new HashMap<>();
        extendMap.put("extendTypeSno","xxxx");
        extendMap.put("extendVersionInfo","xxxx");
        extendMap.put("extendDiskNumber","XF000023F");
        extendMap.put("extendSystem","xxxx");
        extendMap.put("sysSno","1.0");



        // 构造数据
        return getRowData(excelColumns,host,extendMap);
    }

    /**
     * 构造服务器数据
     * @param excelColumns
     * @return
     */
    private List<String> getServiceInitData(List<CustomSettings> excelColumns) {
        AssetExportInitDataVO service = commonInitData();
        service.setName("服务器设备"); // 名称
        service.setTypeName("应用服务器"); // 二级资产名称
        service.setSerialNumber("J00001"); // 设备序列号
        // 扩展字段 [{"name": "extendTypeSno", "title": "设备品牌"}, {"name": "extendVersionInfo", "title": "设备型号"},
        // {"name": "extendDiskNumber", "title": "磁盘序列号"},{"name": "extendSystem", "title": "操作系统名称"},
        //  {"name": "sysSno", "title": "操作系统版本"}]
        Map<String, Object> extendMapService = new HashMap<String, Object>();
        extendMapService.put("extendTypeSno","xxxx");
        extendMapService.put("extendVersionInfo","xxxx");
        extendMapService.put("extendDiskNumber","XF000023F");
        extendMapService.put("extendSystem","xxxx");
        extendMapService.put("sysSno","1.0");
        // 构造数据
        return getRowData(excelColumns,service,extendMapService);
    }

    /**
     * 构造网络设备数据
     * @param excelColumns
     * @return
     */
    private List<String> getNetworkDeviceInitData(List<CustomSettings> excelColumns) {
        AssetExportInitDataVO network = commonInitData();
        network.setName("网路设备"); // 名称
        network.setTypeName("路由器"); // 二级资产名称
        network.setSerialNumber("J00001"); // 设备序列号
        // 扩展字段 [{"name": "extendTypeSno", "title": "设备品牌"}, {"name": "extendVersionInfo", "title": "设备型号"},
        // {"name": "extendDiskNumber", "title": "磁盘序列号"}, {"name": "extendSystem", "title": "操作系统名称"},
        // {"name": "sysSno", "title": "操作系统版本"}]
        Map<String, Object> extendMapNetwork = new HashMap<String, Object>();
        extendMapNetwork.put("extendTypeSno","xxxx");
        extendMapNetwork.put("extendVersionInfo","xxxx");
        extendMapNetwork.put("extendDiskNumber","XF000023F");
        extendMapNetwork.put("extendSystem","xxxx");
        extendMapNetwork.put("sysSno","1.0");
        extendMapNetwork.put("manageInletMsg","XXXXXX");
        // 构造数据
        return getRowData(excelColumns,network,extendMapNetwork);
    }

    /**
     * 构造通用办公设备数据
     * @param excelColumns
     * @return
     */
    private List<String> getOfficeDeviceInitData(List<CustomSettings> excelColumns) {
        AssetExportInitDataVO office = commonInitData();
        office.setName("通用办公设备"); // 名称
        office.setTypeName("打印机"); // 二级资产名称
        office.setSerialNumber("J00001"); // 序列号
        // 扩展字段 [{"name": "deviceSno", "title": "设备品牌"}, {"name": "extendVersionInfo", "title": "设备型号"},
        // {"name": "extendSystem", "title": "操作系统名称"}, {"name": "sysSno", "title": "操作系统版本"}]
        Map<String, Object> extendMapOffice = new HashMap<String, Object>();
        extendMapOffice.put("deviceSno","xxxx");
        extendMapOffice.put("extendVersionInfo","xxxx");
        extendMapOffice.put("extendSystem","xxxx");
        extendMapOffice.put("sysSno","1.0");
        // 构造数据
        return getRowData(excelColumns,office,extendMapOffice);
    }

    /**
     * 构造安全保密产品数据
     * @param excelColumns
     * @return
     */
    private List<String> getSafeInitData(List<CustomSettings> excelColumns) {
        AssetExportInitDataVO safe = commonInitData();
        safe.setName("安全保密产品"); // 名称
        safe.setTypeName("防火墙"); // 二级资产名称
        safe.setSerialNumber("J00001"); // 产品编号
        safe.setOperationUrl("http://xx.com"); //  管理入口url

        // 扩展字段 [{"name": "extendTypeSno", "title": "设备品牌"}, {"name": "extendVersionInfo", "title": "设备型号"}]
        Map<String, Object> extendMapSafe= new HashMap<String, Object>();
        extendMapSafe.put("extendTypeSno","xxxx");
        extendMapSafe.put("extendVersionInfo","xxxx");
        extendMapSafe.put("manageInletUrl","xxxx");
        extendMapSafe.put("port","xxxx"); // 端口 2023-10-20
        // 构造数据
        return getRowData(excelColumns,safe,extendMapSafe);
    }
    /**
     * 构造USB存储介质数据
     * @param excelColumns
     * @return
     */
    private List<String> getUSBMemoryInitData(List<CustomSettings> excelColumns) {
        AssetExportInitDataVO usbM = new AssetExportInitDataVO();
        usbM.setTypeName("涉密专用"); // 二级资产名称
        usbM.setSerialNumber("J00001"); // 设备序列号
        usbM.setResponsibleName("张三"); //责任人名称
        usbM.setResponsibleCode("1001"); //责任人code
        usbM.setOrgName("xx单位"); // 单位
        usbM.setEquipmentIntensive("非密"); //涉密等级
        usbM.setTermType("否");   // 是否国产
        usbM.setDomainName("用户域xx"); // 安全域
        usbM.setVid("vidxxx");
        usbM.setPid("pidxxx");
        // 扩展字段 [{"name": "useRange", "title": "使用范围"}]
        Map<String, Object> extendMapUsbM= new HashMap<String, Object>();
        extendMapUsbM.put("useRange","xx范围");
        // 构造数据
        return getRowData(excelColumns,usbM,extendMapUsbM);
    }

    /**
     * 构造运维终端数据
     * @param excelColumns
     * @return
     */
    private  List<String> getMaintenHostInitData(List<CustomSettings> excelColumns) {
        AssetExportInitDataVO host = commonInitData();
        host.setName("运维终端设备"); // 名称
        host.setTypeName("其他运维终端"); // 二级资产名称
        host.setSerialNumber("J00001"); // 设备序列号
        host.setOsList("windows"); // 安装操作系统
        host.setIsMonitorAgent("已安装"); // 是否安装客户端
        host.setOsSetuptimeStr("2022-01-19 18:04:25"); // 操作系统安装时间

        // 扩展字段 ：[{"name": "extendTypeSno", "title": "设备品牌"}, {"name": "extendVersionInfo", "title": "设备型号"},
        // {"name": "extendDiskNumber", "title": "磁盘序列号"}, {"name": "extendSystem", "title": "操作系统名称"},
        // {"name": "sysSno", "title": "操作系统版本"}]
        Map<String, Object> extendMap = new HashMap<>();
        extendMap.put("extendTypeSno","xxxx");
        extendMap.put("extendVersionInfo","xxxx");
        extendMap.put("extendDiskNumber","XF000023F");
        extendMap.put("extendSystem","xxxx");
        extendMap.put("sysSno","1.0");



        // 构造数据
        return getRowData(excelColumns,host,extendMap);
    }


    /**
     * 构造USB外设数据
     * @param excelColumns
     * @return
     */
    private List<String> getUSBPeripheralInitData(List<CustomSettings> excelColumns) {
        AssetExportInitDataVO usbP = new AssetExportInitDataVO();
        usbP.setName("USB外设");
        usbP.setTypeName("USB打印机"); // 二级资产名称
        usbP.setSerialNumber("J0000q0"); // 设备序列号
        usbP.setResponsibleName("张三"); //责任人名称
        usbP.setResponsibleCode("1001"); //责任人code
        usbP.setOrgName("xx单位"); // 单位
        usbP.setEquipmentIntensive("非密"); //涉密等级
        usbP.setTermType("否");   // 是否国产
        usbP.setDomainName("用户域xx"); // 安全域
        // 扩展字段[{"name": "tradeName", "title": "厂商名称"}]
        Map<String, Object> extendMapUsbP= new HashMap<String, Object>();
        extendMapUsbP.put("tradeName","xx厂商");
        // 构造数据
        return getRowData(excelColumns,usbP,extendMapUsbP);
    }

    /**
     * 公共的:用于终端、服务器、网络设备、安全产品、通用办公
     * 1. ip
     * 2. mac
     * 3. 责任人名称
     * 4. 责任人code
     * 5. 单位
     * 6. 涉密等级
     * 7. 是否国产
     * 8. 安全域
     */
    private  AssetExportInitDataVO commonInitData() {
        AssetExportInitDataVO commonData = new AssetExportInitDataVO();
        commonData.setIp("1.0.0.0");//ip
        commonData.setMac("0C-01-6C-06-A1-02"); //mac
        commonData.setResponsibleName("张三"); //责任人名称
        commonData.setResponsibleCode("1001"); //责任人code
        commonData.setOrgName("xx单位"); // 单位
        commonData.setEquipmentIntensive("非密"); //涉密等级
        commonData.setTermType("否");   // 是否国产
        commonData.setDomainName("用户域xx"); // 安全域
        commonData.setAvailability("基本无损害");
        commonData.setLoadBear("基本无损害");
        commonData.setImportance("基本无损害");
        commonData.setSecrecy("基本无损害");
        commonData.setIntegrity("基本无损害");
        commonData.setWorth("XXXX");
        return commonData;
    }

    private List<String> getRowData(List<CustomSettings> excelColumns, AssetExportInitDataVO asset, Map<String, Object> extendMap) {
        List<String> row = new ArrayList<>();
        for (CustomSettings column : excelColumns) {
            String value = getColumnValue(column, asset, extendMap);
            // 存在责任人姓名，导出时增加责任人:模板中只配了责任人名称没有配置责任人code
            if ("system".equals(column.getAttributeType()) && "responsibleName".equalsIgnoreCase(column.getName())) {
                String responsibleCode = asset.getResponsibleCode();
                row.add(value);
                row.add(responsibleCode);
            } else {
                row.add(value);
            }
        }
        return row;
    }
    private String getColumnValue(CustomSettings column, AssetExportInitDataVO asset, Map<String, Object> assetExtendMap) {
        String value = "";
        if ("system".equals(column.getAttributeType())) {
            switch (column.getName()) {
                case "name":
                    value = asset.getName();
                    break;
                case "ip":
                    value = asset.getIp();
                    break;
                case "typeName":
                case "assetTypeName":
                    value= asset.getTypeName();
                    break;
                case "mac":
                    value = asset.getMac();
                    break;
                case "securityGuid":
                case "securityName":  // 安全域
                    value= asset.getDomainName();
                    break;
                case "serialNumber":  // 序列号
                    value = asset.getSerialNumber();
                    break;
                case "equipmentIntensive": // 涉密等级
                    value = asset.getEquipmentIntensive();
                    break;
                case "orgName": // 单位
                    value = asset.getOrgName();
                    break;
                case "responsibleName": //责任人姓名
                    value = asset.getResponsibleName();
                    break;
                case "termType": //国产与非国产 2021-08-20  1：表示国产 2：非国产
                    value = asset.getTermType();
                    break;
                case "isMonitorAgent": //（终端类型）1.应安装；2.未安装
                    value = asset.getIsMonitorAgent();
                    break;
                case "osSetuptime": //终端类型操作系统安装时间e
                    value = asset.getOsSetuptimeStr();
                    break;
                case "osList": //终端类型安装操作系统
                    value = asset.getOsList();
                    break;
                case "terminalType": //终端类型 ：运维终端/用户终端
                    value = asset.getTerminalType();
                    break;
                case "secrecy": // 机密性
                    value = asset.getSecrecy();
                    break;
                case "availability": // 可用性
                    value = asset.getAvailability();
                    break;
                case "importance": // 业务重要性
                    value = asset.getImportance();
                    break;
                case "loadBear": // 系统资产业务承载性
                    value = asset.getLoadBear();
                    break;
                case "integrity": // 完整性
                    value = asset.getIntegrity();
                    break;
                case "worth": // 资产价值
                    value = asset.getWorth();
                    break;
                case "vid": //VID
                    value = asset.getVid();
                    break;
                case "pid": //PID
                    value = asset.getPid();
                    break;
                case "operationUrl": //管理入口URL
                    value = asset.getOperationUrl();
                    break;
                default:
                    break;
            }
        } else {
            Object data = assetExtendMap.get(column.getName());
            if (null != data) {
                value = String.valueOf(data);
            }
        }
        return value;
    }

}
