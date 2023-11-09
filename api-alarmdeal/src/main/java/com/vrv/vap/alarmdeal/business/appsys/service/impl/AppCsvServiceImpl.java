package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.AppCsvService;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppCsvVO;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.util.*;

/**
 * 应用系统生成csv文件
 */
@Service
public class AppCsvServiceImpl implements AppCsvService {

    private static Logger logger = LoggerFactory.getLogger(AppCsvServiceImpl.class);

    @Autowired
    private FileConfiguration fileConfiguration;


    @Autowired
    private JdbcTemplate jdbcTemplate;


    private static  String assetMd5;  //历史MD5数据

    @Override
    public void initAppToCsv() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("将app_sys_manager表的数据重新存到baseinfo_sys.csv中");
                    excAppCSV();
                } catch (Exception e) {
                    logger.error("将app_sys_manager表的数据重新存到baseinfo_sys.csv中异常", e);
                }
            }
        }).start();

    }
    private void excAppCSV(){
        String assetInfo = syncAppInfo();
        String localAssetMd5 = string2Md5(assetInfo);
        if(StringUtils.isNotEmpty(assetInfo) && StringUtils.isNotEmpty(localAssetMd5) && !localAssetMd5.equals(assetMd5)){
            logger.info("应用系统数据变化，进行刷新csv文件");
            syncFlume(assetInfo);
            assetMd5 = localAssetMd5;
        }else{
            logger.info("应用系统数据没变化，不更新csv文件");
        }

    }
    public static String string2Md5(String inStr){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            logger.error("MD5处理异常",e);
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


    private String syncAppInfo() {
        List<AppCsvVO> appList = getAppCsvDatas();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"table\":\"baseinfo_sys\",\"join\":\"dev_ip\",\"add\":[\"std_sys_id\",\"std_sys_name\",\"std_sys_secret_level\"],sep:\",\"}");
        stringBuilder.append("\n");
        stringBuilder.append("dev_ip,std_sys_id,std_sys_name,std_sys_secret_level");
        stringBuilder.append("\n");
        appList.stream().forEach(asset -> {
            stringBuilder.append(asset.getDevIp()== null ? "-1": asset.getDevIp());
            stringBuilder.append(",");
            stringBuilder.append(asset.getStdSysId()== null ? "-1": asset.getStdSysId());
            stringBuilder.append(",");
            stringBuilder.append(asset.getStdSysName() == null ? "-1":asset.getStdSysName() );
            stringBuilder.append(",");
            stringBuilder.append(asset.getStdSysSecretLevel() == null ? "-1" : asset.getStdSysSecretLevel());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    /**
     * 获取csv数据：
     * 1.拿到应用系统中关联的所有服务器
     * 2.然后根据服务器去找应用系统，如果服务器关联多个应用系统，取其中一个应用系统
     * @return
     */
    private List<AppCsvVO> getAppCsvDatas(){
        // 获取存在服务的应用系统
        String sql = "select * from app_sys_manager where service_id is not null and service_id !='' ";
        List<AppSysManager> appSysManagers = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AppSysManager>(AppSysManager.class));
        if(CollectionUtils.isEmpty(appSysManagers)){
            return new ArrayList<>();
        }
        // 获取所有服务器的guid
        List<String> serverIds =  getAllServerIds(appSysManagers);
        // 获取对应的ip
        List<Map<String,Object>> assets = getAssetIps(serverIds);
        // 获取服务关联的应用系统数据
        List<AppCsvVO> csvs =getAppCsvList(assets,appSysManagers);
        return csvs;
    }

    private List<Map<String, Object>> getAssetIps(List<String> serverIds) {
        String sql = "select Guid,ip from asset where Guid in('" + StringUtils.join(serverIds, "','") + "')";
        List<Map<String,Object>> lists = jdbcTemplate.queryForList(sql);
        return lists;
    }


    private List<String> getAllServerIds(List<AppSysManager> appSysManagers) {
        List<String> ids = new ArrayList<>();
        for(AppSysManager app : appSysManagers){
            String serviceIds = app.getServiceId();
            if(StringUtils.isEmpty(serviceIds)){
                continue;
            }
            String[] idArr=serviceIds.split(",");
            List<String> idList=new ArrayList<>(Arrays.asList(idArr));
            for(String id : idList){
                if(ids.contains(id)){
                    continue;
                }
                ids.add(id);
            }
        }
        return ids;
    }

    private List<AppCsvVO> getAppCsvList(List<Map<String,Object>>  assets, List<AppSysManager> appSysManagers) {
        List<AppCsvVO> datas = new ArrayList<>();
        for(Map<String,Object> asset : assets){
            String guid= String.valueOf(asset.get("Guid"));
            String ip = asset.get("ip")==null?"":String.valueOf(asset.get("ip"));
            AppCsvVO app =  getAppCsvData(guid,appSysManagers);
            if(null == app){
                logger.info("服务器："+guid+",没有找到对应的应用系统");
                continue;
            }
            app.setDevIp(ip);
            datas.add(app);
        }
        return datas;
    }

    private AppCsvVO getAppCsvData(String id, List<AppSysManager> appSysManagers) {
        for(AppSysManager app : appSysManagers){
            String serviceIds = app.getServiceId();
            if(StringUtils.isEmpty(serviceIds)){
                continue;
            }
            if(serviceIds.contains(id)){
                AppCsvVO appCsvVO = new AppCsvVO();
                appCsvVO.setStdSysId(app.getAppNo());
                appCsvVO.setStdSysName(app.getAppName());
                appCsvVO.setStdSysSecretLevel(app.getSecretLevel());
                return appCsvVO;
            }
        }
        return null;
    }
    private void syncFlume(String info){
        PrintStream stream = null;
        try {
            String realPath = fileConfiguration.getCvsFilePath(); // 文件路径
            String fileName = "baseinfo_sys.csv"; // 文件名
            logger.info("====应用系统数据同步到csv文件===" + realPath);
            File myPath = new File(realPath);
            if (!myPath.exists()) {//
                myPath.mkdirs();
            }
            String collectorConfigFile = realPath + File.separator +fileName;
            File configFile = new File(collectorConfigFile);
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            stream = new PrintStream(collectorConfigFile);//写入的文件path
            stream.print(info);//写入的字符串
        } catch (Exception e) {
            logger.error("应用系统数据同步到csv文件异常", e);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

}
