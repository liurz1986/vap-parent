package com.vrv.vap.admin.common.batch;

import com.vrv.vap.admin.common.config.BatchQueueProperties;
import com.vrv.vap.admin.common.util.FileUtils;
import com.vrv.vap.admin.common.util.JsonUtil;
import com.vrv.vap.admin.common.util.Uuid;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.NetflowBaseDataService;
import com.vrv.vap.admin.vo.AssetVo;
import org.apache.commons.lang.StringUtils;
import org.mortbay.jetty.HttpMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class FileConsumer implements Consumer<List<Map>> {

    private static Logger logger = LoggerFactory.getLogger(FileConsumer.class);

    @Autowired
    private BatchQueueProperties batchQueueProperties;

    @Autowired
    NetflowBaseDataService netflowBaseDataService;

    private SecureRandom rand = new SecureRandom();

    private Map<String,String> logTypeDic = null;

    private Map<String,String> devTypeDic = null;

    private Map<String,Integer> levelTypeDic = null;

    @Override
    public void accept(List<Map> maps) {
        try {
         if(logTypeDic == null){
             logTypeDic = generateTypeDic(batchQueueProperties.getLogTypeDic());
         }

        if(devTypeDic == null){
            devTypeDic = generateTypeDic(batchQueueProperties.getDevTypeDic());
        }
        if(levelTypeDic==null){
            levelTypeDic = generateIntTypeDic(batchQueueProperties.getLevelTypeDic());
        }
        Integer subFolder=0;
        if(batchQueueProperties.getSubFolderSize()!=null){
            subFolder = rand.nextInt(batchQueueProperties.getSubFolderSize());
        }

        String uuid = Uuid.uuid();
        String tempFileName = uuid +"."+batchQueueProperties.getFileSuffix()+"."+batchQueueProperties.getTmpFileSuffix();
        String tempFilePath = batchQueueProperties.getFileFolder()+File.separator+subFolder+File.separator+tempFileName;
        //文件写入
        boolean cFlag = FileUtils.createFile(tempFilePath);
        if(!cFlag){
            //创建文件失败
        }
        //处理生成组织机构信息和应用信息
        for(Map log:maps){
            if(log.containsKey("time")){
                log.put("event_time",log.get("time"));
            }
            Integer dataType = (Integer)(log.get("log_type"));
            if(logTypeDic.containsKey(dataType.toString())){
                log.put("report_log_type",logTypeDic.get(dataType.toString()));
            }
            log.put("guid",Uuid.uuid());
            handlerOrg(log);
            handlerDev(log);
            handlerPerson(log);
            //http和ssl 3，7，99
            handlerApp(log);

        }


        List<String> contents = maps.stream().map(p-> JsonUtil.objToJson(p)).collect(Collectors.toList());
        boolean wFlag = FileUtils.writeFile(contents,tempFilePath);
        if(!wFlag){
            //写入文件失败
            logger.error("写入文件失败,file : "+tempFilePath);
            return;
        }
        String fileName = uuid +"."+batchQueueProperties.getFileSuffix();
        String filePath = batchQueueProperties.getFileFolder()+File.separator+subFolder+File.separator+fileName;
        FileUtils.reNameFile(tempFilePath,filePath);

        }catch (Exception exception){
            logger.error("消费失败："+exception);
        }

    }

    private Map<String,String> generateTypeDic(String dicStr){
        Map<String,String> dic = new HashMap<>();
        if(StringUtils.isNotEmpty(dicStr)){
            String[] dicArr = dicStr.split(";");
            Arrays.stream(dicArr).forEach(p->{
                if(StringUtils.isNotEmpty(p)){
                    String[] values = p.split(",");
                    if(values.length==2){
                        dic.put(values[0],values[1]);
                    }
                }
            });
        }
        return dic;
    }

    private Map<String,Integer> generateIntTypeDic(String dicStr){
        Map<String,Integer> dic = new HashMap<>();
        if(StringUtils.isNotEmpty(dicStr)){
            String[] dicArr = dicStr.split(";");
            Arrays.stream(dicArr).forEach(p->{
                if(StringUtils.isNotEmpty(p)){
                    String[] values = p.split(",");
                    if(values.length==2){
                        dic.put(values[0],Integer.valueOf(values[1]));
                    }
                }
            });
        }
        return dic;
    }

    private void handlerApp(Map log) {

        //应用系统编号
        log.put("src_std_sys_id", "");
        //应用系统名称
        log.put("src_std_sys_name", "");
        //所在服务器IP - 应用关联的资产
        log.put("src_std_sys_ip", "");
        //应用系统涉密等级
        log.put("src_std_sys_secret_level", -1);
        
        log.put("dst_std_sys_id", "");
        //应用系统名称
        log.put("dst_std_sys_name", "");
        //所在服务器IP - 应用关联的资产
        log.put("dst_std_sys_ip", "");
        //应用系统涉密等级
        log.put("dst_std_sys_secret_level", -1);

        try {
            //源机构，目标机构
            String srcIp = log.containsKey("sip") ? (String) log.get("sip") : "";
            String srcCode = "";
            String disCode = "";
            String param = findParam(log);
            if (StringUtils.isNotEmpty(srcIp)) {
                AppSysManager appSysManager = netflowBaseDataService.fixAppIpCache(srcIp);
                if (appSysManager != null) {
                    srcCode = appSysManager.getAppNo();
                    AssetVo assetVo = netflowBaseDataService.fixAppAssetCache(appSysManager.getAppNo());
                    //应用系统编号
                    log.put("src_std_sys_id", appSysManager.getAppNo());
                    //应用系统名称
                    log.put("src_std_sys_name", appSysManager.getAppName());
                    //所在服务器IP - 应用关联的资产
                    log.put("src_std_sys_ip", assetVo == null ? "" : assetVo.getIp());
                    //应用系统涉密等级
                    log.put("src_std_sys_secret_level", appSysManager.getSecretLevel());

                }

            }


            String url = (String) log.get("url");
            if (StringUtils.isNotEmpty(url)) {
                int index = url.indexOf("?");
                String miniUrl = url;
                if (index > 0) {
                    miniUrl = url.substring(0, index);
                }
                AppSysManager appSysManager = netflowBaseDataService.fixAppUrlCache(miniUrl);
                if (appSysManager != null) {
                    disCode = appSysManager.getAppNo();
                    AssetVo assetVo = netflowBaseDataService.fixAppAssetCache(appSysManager.getAppNo());
                    //应用系统编号
                    log.put("dst_std_sys_id", appSysManager.getAppNo());
                    //应用系统名称
                    log.put("dst_std_sys_name", appSysManager.getAppName());
                    //所在服务器IP - 应用关联的资产
                    log.put("dst_std_sys_ip", assetVo == null ? "" : assetVo.getIp());
                    //应用系统涉密等级
                    log.put("dst_std_sys_secret_level", appSysManager.getSecretLevel());
                   

                }
            }
            String dip = (String) log.get("dip");
            if(StringUtils.isEmpty(url) && StringUtils.isNotEmpty(dip)){
                AppSysManager appSysManager = netflowBaseDataService.fixAppIpCache(dip);
                if (appSysManager != null) {
                    srcCode = appSysManager.getAppNo();
                    AssetVo assetVo = netflowBaseDataService.fixAppAssetCache(appSysManager.getAppNo());
                    //应用系统编号
                    log.put("dst_std_sys_id", appSysManager.getAppNo());
                    //应用系统名称
                    log.put("dst_std_sys_name", appSysManager.getAppName());
                    //所在服务器IP - 应用关联的资产
                    log.put("dst_std_sys_ip", assetVo == null ? "" : assetVo.getIp());
                    //应用系统涉密等级
                    log.put("dst_std_sys_secret_level", appSysManager.getSecretLevel());

                }

            }

            log.put("std_communication_type", 5);
            if (StringUtils.isNotEmpty(srcCode) && StringUtils.isNotEmpty(disCode)) {
                if (srcCode.equals(disCode)) {
                    log.put("std_communication_type", 2);
                } else {
                    log.put("std_communication_type", 1);
                }
            }
            if (StringUtils.isNotEmpty(srcCode) && StringUtils.isEmpty(disCode)) {
                log.put("std_communication_type", 3);
            }
            if (StringUtils.isEmpty(srcCode) && StringUtils.isNotEmpty(disCode)) {
                log.put("std_communication_type", 4);
            }

            //通信参数
            log.put("src_sys_parameter", param);
            log.put("param", param);
            log.put("param_length", param == null ? 0 : param.length());
        } catch (Exception exception){
            exception.printStackTrace();
            logger.error("预处理应用信息失败",exception.getMessage());
        }

    }

    private String findParam(Map map){
        String url = Optional.ofNullable((String)map.get("url")).orElse("");
        if(map.containsKey("method") && HttpMethods.GET.equals(map.get("method"))){
            int index = url.indexOf('?');
            if(index>0 && index < (url.length()-1)){
                return url.substring(index,url.length()-1);
            }
        }
        if(map.containsKey("http_req_body")){
            return (String)map.get("http_req_body");
        }
        return "";
    }

    private void handlerOrg(Map log) {
        log.put("src_std_org_code", "");
        log.put("src_std_org_name", "");
        log.put("src_std_org_type", -1);
        log.put("src_std_secret_qualifications", -1);
        log.put("src_std_secret_level", -1);
        log.put("src_std_protection_level", -1);
        log.put("dst_std_org_code", "");
        log.put("dst_std_org_name", "");
        log.put("dst_std_org_type", -1);
        log.put("dst_std_secret_qualifications", -1);
        log.put("dst_std_secret_level", -1);
        log.put("dst_std_protection_level", -1);
        log.put("classification_level_code", 4);
        if(log.containsKey("classification_level") && levelTypeDic.containsKey((String)log.get("classification_level"))){

            log.put("classification_level_code", levelTypeDic.get((String)log.get("classification_level")));
        }

        try {
            //源机构，目标机构
            String srcIp = (String) log.get("sip");
            String srcCode = "";
            String disCode = "";
            if (StringUtils.isNotEmpty(srcIp)) {
                BaseKoalOrg baseKoalOrg = netflowBaseDataService.fixOrgIpCache(srcIp);

                if (baseKoalOrg != null) {
                    srcCode = baseKoalOrg.getCode();
                    log.put("src_std_org_code", baseKoalOrg.getCode());
                    log.put("src_std_org_name", baseKoalOrg.getName());
                    log.put("src_std_org_type", baseKoalOrg.getOrgType());
                    log.put("src_std_secret_qualifications", baseKoalOrg.getSecretQualifications());
                    log.put("src_std_secret_level", baseKoalOrg.getSecretLevel());
                    log.put("src_std_protection_level", baseKoalOrg.getProtectionLevel());
                }
            }

            String distIp = (String) log.get("dip");
            if (StringUtils.isNotEmpty(distIp)) {

                BaseKoalOrg baseKoalOrg = netflowBaseDataService.fixOrgIpCache(distIp);
                if (baseKoalOrg != null) {
                    disCode = baseKoalOrg.getCode();
                    log.put("dst_std_org_code", baseKoalOrg.getCode());
                    log.put("dst_std_org_name", baseKoalOrg.getName());
                    log.put("dst_std_org_type", baseKoalOrg.getOrgType());
                    log.put("dst_std_secret_qualifications", baseKoalOrg.getSecretQualifications());
                    log.put("dst_std_secret_level", baseKoalOrg.getSecretLevel());
                    log.put("dst_std_protection_level", baseKoalOrg.getProtectionLevel());
                }
            }
            log.put("std_is_same_unit", 0);
            if (StringUtils.isNotEmpty(srcCode) && StringUtils.isNotEmpty(disCode) && srcCode.equals(disCode)) {
                log.put("std_is_same_unit", 1);
            }
            //1-单位内部访问  2-外部访问本单位 3-本单位访问外部 4-外部访问
            Integer visitType = 4;
            if(StringUtils.isNotEmpty(srcCode)){
                visitType -= 1;
            }
            if(StringUtils.isNotEmpty(disCode)){
                visitType -= 2;
            }
            log.put("visit_type",visitType);


        } catch (Exception exception) {
            exception.printStackTrace();
            logger.error("预处理组织机构信息失败",exception.getMessage());
        }
    }

    private void handlerDev(Map log) {
        //设备类型-二级名称
        log.put("src_std_dev_type", "");
        //设备类型-一级code
        log.put("src_std_dev_type_group", "-1");

        //设备名称
        log.put("src_std_dev_name", "");
        //设备密级
        log.put("src_std_dev_level", "");

        log.put("src_std_dev_id", "");
        //IP
        log.put("src_std_dev_ip", "");
        //MAC
        log.put("src_std_dev_mac", "");
        //所属安全域
        log.put("src_std_dev_safety_marign", "");

        log.put("src_std_dev_safety_marign_name", "");
        //操作系统类型 - 扩展extendSystem
        log.put("src_std_dev_os_type", "");
        //硬盘序列号 - 扩展 extendDiskNumber
        log.put("src_std_dev_hardware_identification", "");
        //硬件设备型号 - 扩展 extendVersionInfo
        log.put("src_std_dev_hardware_model", "");
        //设备品牌型号 - deviceSno
        log.put("src_std_dev_brand_model", "");
        //
        log.put("src_std_terminal_type", "");
        log.put("dst_std_terminal_type", "");
        //设备类型
//        log.put("dst_std_dev_type", "");

        //设备类型-二级名称
        log.put("dst_std_dev_type", "");
        //设备类型-一级code
        log.put("dst_std_dev_type_group", "-1");

        //设备名称
        log.put("dst_std_dev_name", "");
        //设备密级
        log.put("dst_std_dev_level", "");
        log.put("dst_std_dev_id", "");
        //IP
        log.put("dst_std_dev_ip", "");
        //MAC
        log.put("dst_std_dev_mac", "");
        //所属安全域
        log.put("dst_std_dev_safety_marign", "");
        log.put("dst_std_dev_safety_marign_name", "");
        //操作系统类型 - 扩展extendSystem
        log.put("dst_std_dev_os_type", "");
        //硬盘序列号 - 扩展 extendDiskNumber
        log.put("dst_std_dev_hardware_identification", "");
        //硬件设备型号 - 扩展 extendVersionInfo
        log.put("dst_std_dev_hardware_model", "");
        //设备品牌型号 - deviceSno
        log.put("dst_std_dev_brand_model", "");

        try {

            String srcIp = (String) log.get("sip");
            if (StringUtils.isNotEmpty(srcIp)) {
                AssetVo asset = netflowBaseDataService.fixAssetIpCache(srcIp);
                if (asset != null) {
                    //设备类型
                    log.put("src_std_dev_type", asset.getTypeName());

                    log.put("src_std_dev_type_group", devTypeDic.containsKey(asset.getGroupName())?devTypeDic.get(asset.getGroupName()):"-1");
                    //设备名称
                    log.put("src_std_dev_name", asset.getName());
                    //设备密级
                    log.put("src_std_dev_level", asset.getSecrecy());

                    //IP
                    log.put("src_std_dev_ip", asset.getIp());

                    log.put("src_std_dev_id", asset.getGuid());
                    //软件系统版本号
                    //log.put("src_std_dev_software_version", asset.getVersionInfo());
                    //设备入网时间
                    //log.put("src_std_dev_net_time", asset.getProtectionLevel());

                    //MAC
                    log.put("src_std_dev_mac", asset.getMac());

                    //MAC
                    // typeGuid
                    log.put("src_std_terminal_type", asset.getTerminalType());

                    //所属安全域
                    log.put("src_std_dev_safety_marign", asset.getSecurityguid());
                    BaseSecurityDomain baseSecurityDomainInfo = netflowBaseDataService.fixSecCodeCache(asset.getSecurityguid());
                   if(baseSecurityDomainInfo!=null) {
                       log.put("src_std_dev_safety_marign_name", baseSecurityDomainInfo.getDomainName());
                   }

                    if (StringUtils.isNotEmpty(asset.getAssetExtendInfo())) {
                        Map assetExtendMap = JsonUtil.jsonToMap(asset.getAssetExtendInfo());
                        if (assetExtendMap != null) {
                            if (assetExtendMap.containsKey("extendSystem")) {
                                //操作系统类型 - 扩展extendSystem
                                log.put("src_std_dev_os_type", assetExtendMap.get("extendSystem"));
                            }
                            if (assetExtendMap.containsKey("extendDiskNumber")) {
                                //硬盘序列号 - 扩展 extendDiskNumber
                                log.put("src_std_dev_hardware_identification", assetExtendMap.get("extendDiskNumber"));
                            }
                            if (assetExtendMap.containsKey("extendVersionInfo")) {
                                //硬件设备型号 - 扩展 extendVersionInfo
                                log.put("src_std_dev_hardware_model", assetExtendMap.get("extendVersionInfo"));
                            }
                            if (assetExtendMap.containsKey("deviceSno")) {
                                //设备品牌型号 - deviceSno
                                log.put("src_std_dev_brand_model", assetExtendMap.get("deviceSno"));
                            }
                        }

                    }


                }

                if( StringUtils.isEmpty((String)log.get("src_std_dev_safety_marign"))){
                    BaseSecurityDomain baseSecurityDomain = netflowBaseDataService.fixSecIpCache(srcIp);
                    if(baseSecurityDomain!=null){
                        log.put("src_std_dev_safety_marign",baseSecurityDomain.getCode());
                        log.put("src_std_dev_safety_marign_name",baseSecurityDomain.getDomainName());
                    }
                }
            }




            String distIp = (String) log.get("dip");
            if (StringUtils.isNotEmpty(distIp)) {
                AssetVo asset = netflowBaseDataService.fixAssetIpCache(distIp);
                if (asset != null) {
                    //设备类型
                    log.put("dst_std_dev_type", asset.getTypeName());

                    log.put("dst_std_dev_type_group", devTypeDic.containsKey(asset.getGroupName())?devTypeDic.get(asset.getGroupName()):"-1");

                    //设备名称
                    log.put("dst_std_dev_name", asset.getName());
                    //设备密级
                    log.put("dst_std_dev_level", asset.getSecrecy());

                    log.put("dst_std_dev_id", asset.getGuid());
                    //IP
                    log.put("dst_std_dev_ip", asset.getIp());
                    //软件系统版本号
                    //log.put("src_std_dev_software_version", asset.getVersionInfo());
                    //设备入网时间
                    //log.put("src_std_dev_net_time", asset.getProtectionLevel());

                    //MAC
                    log.put("dst_std_dev_mac", asset.getMac());

                    //MAC
                    log.put("dst_std_terminal_type", asset.getTerminalType());

                    //所属安全域
                    log.put("dst_std_dev_safety_marign", asset.getSecurityguid());

                    BaseSecurityDomain baseSecurityDomainInfo = netflowBaseDataService.fixSecCodeCache(asset.getSecurityguid());
                    if(baseSecurityDomainInfo!=null) {
                        log.put("dst_std_dev_safety_marign_name", baseSecurityDomainInfo.getDomainName());
                    }

                    if (StringUtils.isNotEmpty(asset.getAssetExtendInfo())) {
                        Map assetExtendMap = JsonUtil.jsonToMap(asset.getAssetExtendInfo());
                        if (assetExtendMap != null) {
                            if (assetExtendMap.containsKey("extendSystem")) {
                                //操作系统类型 - 扩展extendSystem
                                log.put("dst_std_dev_os_type", assetExtendMap.get("extendSystem"));
                            }
                            if (assetExtendMap.containsKey("extendDiskNumber")) {
                                //硬盘序列号 - 扩展 extendDiskNumber
                                log.put("dst_std_dev_hardware_identification", assetExtendMap.get("extendDiskNumber"));
                            }
                            if (assetExtendMap.containsKey("extendVersionInfo")) {
                                //硬件设备型号 - 扩展 extendVersionInfo
                                log.put("dst_std_dev_hardware_model", assetExtendMap.get("extendVersionInfo"));
                            }
                            if (assetExtendMap.containsKey("deviceSno")) {
                                //设备品牌型号 - deviceSno
                                log.put("dst_std_dev_brand_model", assetExtendMap.get("deviceSno"));
                            }
                        }

                    }


                }
                if( StringUtils.isEmpty((String)log.get("dst_std_dev_safety_marign"))){
                    BaseSecurityDomain baseSecurityDomain = netflowBaseDataService.fixSecIpCache(distIp);
                    if(baseSecurityDomain!=null){
                        log.put("dst_std_dev_safety_marign",baseSecurityDomain.getCode());
                        log.put("dst_std_dev_safety_marign_name",baseSecurityDomain.getDomainName());
                    }
                }
            }
        }catch (Exception exception){
            exception.printStackTrace();
            logger.error("预资产信息失败",exception.getMessage());
        }


    }

    private void handlerPerson(Map log){
        log.put("src_std_username","");
        //源设备责任人编号
        log.put("src_std_user_no","");
        //用户类型
        log.put("src_std_user_type","");
        //源设备责任人岗位
        log.put("src_std_user_station","");
        //源设备责任人部门
        log.put("src_std_user_department","");
        //源设备责任人角色
        log.put("src_std_user_role","");
        //源设备责任人密级
        log.put("src_std_user_level",-1);
        //源设备责任人姓名
        log.put("dst_std_username","");
        //源设备责任人编号
        log.put("dst_std_user_no","");
        //用户类型
        log.put("dst_std_user_type","");
        //源设备责任人岗位
        log.put("dst_std_user_station","");
        //源设备责任人部门
        log.put("dst_std_user_department","");
        //源设备责任人角色
        log.put("dst_std_user_role","");
        //源设备责任人密级
        log.put("dst_std_user_level",-1);

        try {
            //源资产负责人，目标资产负责人
            String srcIp = (String) log.get("sip");
            if (StringUtils.isNotEmpty(srcIp)) {

                BasePersonZjg basePersonZjg = netflowBaseDataService.fixPersonIpCache(srcIp);
                if (basePersonZjg != null) {
                    //源设备责任人姓名
                    log.put("src_std_username", basePersonZjg.getUserName());
                    //源设备责任人编号
                    log.put("src_std_user_no", basePersonZjg.getUserNo());
                    //用户类型
                    log.put("src_std_user_type", basePersonZjg.getPersonType());
                    //源设备责任人岗位
                    log.put("src_std_user_station", basePersonZjg.getPersonRank());
                    //源设备责任人部门
                    log.put("src_std_user_department", basePersonZjg.getOrgCode());
                    //源设备责任人角色
                    log.put("src_std_user_role", basePersonZjg.getPersonType());
                    //源设备责任人密级
                    log.put("src_std_user_level", basePersonZjg.getSecretLevel());
                }

            }

            String distIp = (String) log.get("dip");
            if (StringUtils.isNotEmpty(distIp)) {
                BasePersonZjg basePersonZjg = netflowBaseDataService.fixPersonIpCache(distIp);
                if (basePersonZjg != null) {
                    //源设备责任人姓名
                    log.put("dst_std_username", basePersonZjg.getUserName());
                    //源设备责任人编号
                    log.put("dst_std_user_no", basePersonZjg.getUserNo());
                    //用户类型
                    log.put("dst_std_user_type", basePersonZjg.getPersonType());
                    //源设备责任人岗位
                    log.put("dst_std_user_station", basePersonZjg.getPersonRank());
                    //源设备责任人部门
                    log.put("dst_std_user_department", basePersonZjg.getOrgCode());
                    //源设备责任人角色
                    log.put("dst_std_user_role", basePersonZjg.getPersonType());
                    //源设备责任人密级
                    log.put("dst_std_user_level", basePersonZjg.getSecretLevel());
                }
            }
        }catch (Exception exception){
            logger.error("预处理责任人信息失败",exception.getMessage());
        }

    }



}
