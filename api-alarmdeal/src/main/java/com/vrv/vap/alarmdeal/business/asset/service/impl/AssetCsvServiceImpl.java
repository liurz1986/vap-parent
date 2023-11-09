package com.vrv.vap.alarmdeal.business.asset.service.impl;
import com.vrv.vap.alarmdeal.business.asset.service.*;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetCsvVO;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
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
import java.util.List;

/**
 * 写入scv文件逻辑优化处理
 * 2022-08-15
 *   增加写入baseinfo_server.csv
 *   2023-09-21
 */
@Service
public class AssetCsvServiceImpl implements AssetCsvService {

    private static Logger logger = LoggerFactory.getLogger(AssetCsvServiceImpl.class);

    @Autowired
    private FileConfiguration fileConfiguration;


    @Autowired
    private JdbcTemplate jdbcTemplate;


    private static  String assetServerMd5;  //历史MD5数据

    private static  String assetDevMd5;  //历史MD5数据

    /**
     * 将asset表的数据重新存到baseinfo_dev.csv、baseinfo_server.csv中
     */
    @Override
    public void initAssetToCsv() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.info("将asset表的数据重新存到baseinfo_dev.csv、baseinfo_server.csv中");
                        excAssetCSV();
                    } catch (Exception e) {
                        logger.error("将asset表的数据重新存到baseinfo_dev.csv、baseinfo_server.csv中异常", e);
                    }
                }
            }).start();
    }

   private void excAssetCSV(){
       List<AssetCsvVO> assetList = getAssetCsvs();
       writeBaseInfoServer(assetList);
       writeBaseInfoDev(assetList);
    }

    /**
     * 写入到baseinfo_dev.csv
     * @param assetList
     */
    private void writeBaseInfoDev(List<AssetCsvVO> assetList) {
        try{
            String assetInfo = syncAssetInfo(assetList);
            String localAssetMd5 = string2Md5(assetInfo);
            if(StringUtils.isNotEmpty(assetInfo) && StringUtils.isNotEmpty(localAssetMd5) && !localAssetMd5.equals(assetDevMd5)){
                logger.info("资产数据变化，进行刷新csv文件");
                String fileName=fileConfiguration.getCvsFileDevName();
                if(StringUtils.isEmpty(fileName)){
                    fileName= "baseinfo_dev.csv";
                }
                syncFlume(assetInfo,fileName);
                assetDevMd5 = localAssetMd5;
            }else{
                logger.info("资产数据没变化，不更新csv文件");
            }
        }catch (Exception e){
            logger.error("资产数据写入到baseinfo_dev.csv文件失败",e);
        }
    }
    /**
     * 写入到baseinfo_server.csv
     * @param assetList
     */
    private void writeBaseInfoServer(List<AssetCsvVO> assetList) {
        try{
            String assetInfo = syncAssetServerInfo(assetList);
            String localAssetMd5 = string2Md5(assetInfo);
            if(StringUtils.isNotEmpty(assetInfo) && StringUtils.isNotEmpty(localAssetMd5) && !localAssetMd5.equals(assetServerMd5)){
                logger.info("资产数据变化，进行刷新csv文件");
                String fileName=fileConfiguration.getCvsFileServerName();
                if(StringUtils.isEmpty(fileName)){
                    fileName= "baseinfo_server.csv";
                }
                syncFlume(assetInfo,fileName);
                assetServerMd5 = localAssetMd5;
            }else{
                logger.info("资产数据没变化，不更新csv文件");
            }
        }catch (Exception e){
            logger.error("资产数据写入到baseinfo_server.csv文件失败",e);
        }
    }

    /**
     * {"table":"baseinfo_server","join":"dev_ip","add":["resource_type_group","resource_type"],sep:","}
     * dev_ip,resource_type_group,resource_type
     * 对应就是"std_dev_type_group","std_dev_type"这两个字段
     * @param assetList
     * @return
     */
    private String syncAssetServerInfo(List<AssetCsvVO> assetList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"table\":\"baseinfo_server\",\"join\":\"dev_ip\",\"add\":[\"resource_type_group\"," +
                "\"resource_type\"],sep:\",\"}");
        stringBuilder.append("\n");
        stringBuilder.append("dev_ip,resource_type_group,resource_type");
        stringBuilder.append("\n");
        assetList.stream().forEach(asset -> {
            stringBuilder.append(asset.getDevIp()== null ? "-1": asset.getDevIp());
            stringBuilder.append(",");
            stringBuilder.append(assetTypeGroupHandle(asset.getStdDevTypeGroup()));
            stringBuilder.append(",");
            stringBuilder.append(asset.getStdDevType() == null ? "-1":asset.getStdDevType() );
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private String syncAssetInfo(List<AssetCsvVO> assetList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"table\":\"baseinfo_dev\",\"join\":\"dev_ip\",\"add\":[\"std_dev_type_group\"," +
                "\"std_dev_type\",\"std_dev_safety_marign\",\"std_terminal_type\",\"std_user_no\"],sep:\",\"}");
        stringBuilder.append("\n");
        stringBuilder.append("dev_ip,std_dev_type_group,std_dev_type,std_dev_safety_marign,std_terminal_type,std_user_no");
        stringBuilder.append("\n");
        assetList.stream().forEach(asset -> {
            stringBuilder.append(asset.getDevIp()== null ? "-1": asset.getDevIp());
            stringBuilder.append(",");
            stringBuilder.append(assetTypeGroupHandle(asset.getStdDevTypeGroup()));
            stringBuilder.append(",");
            stringBuilder.append(asset.getStdDevType() == null ? "-1":asset.getStdDevType() );
            stringBuilder.append(",");
            stringBuilder.append(asset.getStdDevSafeMarign() == null ? "-1" : asset.getStdDevSafeMarign());
            stringBuilder.append(",");
            stringBuilder.append(getStdTerminalType(asset));
            stringBuilder.append(",");
            stringBuilder.append(asset.getStdUserNo()==null?"-1":asset.getStdUserNo());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }


    private List<AssetCsvVO> getAssetCsvs(){
        String sql = "select asset.ip as devIp,agroup.TreeCode as stdDevTypeGroup,asType.name as stdDevType,asset.domain_name as stdDevSafeMarign, asset.responsible_code as stdUserNo,asset.terminal_type as stdTerminalType " +
                " from  asset inner join" +
                " asset_type as asType on asType.Guid=asset.Type_Guid" +
                " inner join asset_type_group as agroup on asType.TreeCode LIKE CONCAT(agroup.`TreeCode`,'-%') ";
        List<AssetCsvVO> assets = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetCsvVO>(AssetCsvVO.class));
        return  assets;
    }
    //终端0，服务器1，安全保密产品2，应用3，网络设备4，其他设备(通用办公设备)5、运维终端6  没有一级类型  -1
    private String assetTypeGroupHandle(String assetTypeGroupTreeCode) {
        String treeCode = assetTypeGroupTreeCode == null ? "" : assetTypeGroupTreeCode.trim();
        switch (treeCode) {
            case "asset-Host":  // 终端
                return "0";
            case "asset-service":  // 服务器
                return "1";
            case "asset-SafeDevice":  // 安全保密产品
                return "2";
            case "asset-NetworkDevice":  // 网络设备
                return "4";
            case "asset-OfficeDevice":  // 其他设备(通用办公设备)
                return "5";
            case "asset-MaintenHost":  // 运维终端
                return "6";
            default:
                return "-1";
        }
    }

    // 终端类型：1.运维终端 2. 用户终端,如果一级类型为服务器则终端类型为服务器 标识为3
    private String getStdTerminalType(AssetCsvVO assetCsvVO) {
        // 一级类型的treeCode
        String assetTypeGroupTreeCode = assetCsvVO.getStdDevTypeGroup() == null ? "" : assetCsvVO.getStdDevTypeGroup().trim();
        // 一级类型为服务器：终端类型为3
        if ("asset-service".equalsIgnoreCase(assetTypeGroupTreeCode)|| "1".equalsIgnoreCase(assetTypeGroupTreeCode)) {
            return "3";
        }
        String terminalType = assetCsvVO.getStdTerminalType();
        if(StringUtils.isEmpty(terminalType)){
            return "-1";
        }
        return terminalType;
    }
    private void syncFlume(String info,String fileName){
        PrintStream stream = null;
        try {
            String realPath = fileConfiguration.getCvsFilePath(); // 文件路径
            logger.info("====资产数据同步到csv文件===" + realPath);
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
            logger.error("写入数据异常", e);
        } finally {
            if (stream != null) {
                stream.close();
            }
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

}
