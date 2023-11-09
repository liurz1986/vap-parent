package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.config.HostConfig;
import com.vrv.vap.admin.common.util.*;
import com.vrv.vap.admin.service.AuthorizationService;
import com.vrv.vap.admin.service.LicenseService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.vo.AuthorizationVO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import test_edpky.EdpJava;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service(value="authorizationServcie")
public class AuthorizationServcieImpl implements AuthorizationService {

    private final static Logger logger = LoggerFactory.getLogger(AuthorizationServcieImpl.class);
    private final static String AUTHORIZATION_TIME = "Authorization_Time"; //授权文件导入时间
    private final static String Authorization_Status = "authorization_Status"; //更新授权状态
    @Value("${licensePath}")
    private  String licensePath;
    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private LicenseService licenseService;

    private final ConcurrentMap<Integer, AuthorizationVO> authInfoMap = new ConcurrentHashMap<>();
    /**
     *更新配置表的状态
     *
     */
    @Override
    public void resetAuthorizationStatus(String status) {
        Object staticConfig = systemConfigService.getStaticConfig(Authorization_Status);
        if(staticConfig!=null){
            systemConfigService.saveConfValue(Authorization_Status, status);
        }
    }

    /**
     *
     * 获取授权的状态
     *
     */
    @Override
    public AuthorizationVO getAuthorizationInfo() {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        authorizationVO.setVersionCode(2);
        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        if (os.startsWith("win")) {
            authorizationVO.setStatus(true);
            return authorizationVO;
        }
        Integer key = TimeTools.getSecondTimestampTwo(TimeTools.getNow());
        if (authInfoMap.containsKey(key)) {
            logger.info("authInfo");
            authorizationVO = authInfoMap.get(key);
            return authorizationVO;
        }
        authorizationVO = this.fillAuthorizationInfoWithEdp(authorizationVO);
        authInfoMap.put(key,authorizationVO);
        return authorizationVO;
    }

    /**
     * 导入授权信息,更新授权的状态
     * @return
     */
    @Override
    public AuthorizationVO resetAuthorizationfInfo() {
        AuthorizationVO authorizationInfo = this.getAuthorizationInfo();
        if(authorizationInfo.getStatus()){ //授权正常
            this.resetAuthorizationImportDay(); //替换授权导入时间
            this.resetAuthorizationStatus("success"); //授权状态
            this.resetResource();       //重置资源菜单
        }else {
            this.resetAuthorizationStatus("fail");
        }
        return authorizationInfo;
    }

    /**
     * 导入授权时间
     */
    private void resetAuthorizationImportDay(){
        Object staticConfig = systemConfigService.getStaticConfig(AUTHORIZATION_TIME);
//		SystemConfig systemConfig = systemConfigRepository.getById(AUTHORIZATION_TIME).get();
        if(staticConfig!=null){
            String date = DateUtil.format(new Date(), DateUtil.DEFAULT_YYYYMMDD);  //当前的时间
            systemConfigService.saveConfValue(AUTHORIZATION_TIME, date);
        }
    }

    /**
     * 重置资源菜单
     */
     private void resetResource(){
         licenseService.updateResourceByLicense();
     }




    /**
     * 获得对应的实体信息
     * @param authorizationVO
     * @return
     */
    private synchronized AuthorizationVO fillAuthorizationInfoWithEdp(AuthorizationVO authorizationVO) {
        String authorizationPath = Paths.get(licensePath).toString();
        logger.info("授权文件路径:" + authorizationPath);
        EdpJava edp = null;
        long ret = 8;
        try {
            edp = new EdpJava();
            edp.SetLcsFilePath(100, authorizationPath);
            ret = edp.CheckKeyRightEx(HostConfig.edpNum, 3);
            logger.info("ret:" + ret);
        }catch (Exception exception){
            logger.error("加载SO文件或授权文件报错!!!");
            logger.error("",exception);
            return null;
        }
        if (ret != 0) {
            String authErrorMessage = getAuthErrorMessage(ret);
            logger.info(authErrorMessage);
            authorizationVO.setInfo(authErrorMessage);
            authorizationVO.setStatus(false);
            return authorizationVO;
        } else {
            try {
                byte[] bytes=new byte[1024];
                int count=0;
                count=edp.GetSelfData(bytes, 0);

                byte[] srtbyte=new byte[count];
                for(int i=0;i<count;i++) {
                    srtbyte[i]=bytes[i];
                }
                logger.info("数据长度："+srtbyte.length);
                String base64Str = new String(srtbyte,"UTF-8");
                logger.info("转换得到的base64数据："+base64Str);
                byte[] byteJson = Base64Util.base64String2ByteFun(base64Str);
                String json = new String(byteJson,"UTF-8");
                logger.info("转换得到的json数据："+json);
            }catch (Exception e) {
                logger.error("转换数据失败："+e.getMessage());
            }
            String companyName = this.getCompanyName(edp);// (Linux下代码)
            int deadLine = this.getDeadLine(edp); // 截止日志
            int clientCount = this.getClientCount(edp); //客户端数
            authorizationVO.setCompanyName(companyName);
            authorizationVO.setDeadLine(String.valueOf(deadLine));
            authorizationVO.setTerminalCount(clientCount);
            List<String> list = this.getUsedDaysAndLastDays(deadLine);
            authorizationVO = this.setUsedDaysAndLastDay(list, authorizationVO);
            authorizationVO.setStatus(true);
            return authorizationVO;
        }
    }

    /**
     * 注入已使用的天数和未使用的天数
     *
     * @param list
     * @param authorizationVO
     */
    private AuthorizationVO setUsedDaysAndLastDay(List<String> list, AuthorizationVO authorizationVO) {
        if (list.size() > 0) {
            authorizationVO.setUsedDays(list.get(0));
            authorizationVO.setLastDays(list.get(1));
            if (Integer.valueOf(list.get(1)) < 30) {
                authorizationVO.setInfo("授权时间小于30天，请联系管理员");
            } else {
                authorizationVO.setInfo("");
            }
        }
        return authorizationVO;
    }


    /**
     * 获得授权公司的名称
     *
     * @return
     */
    private String getCompanyName(EdpJava edp) {
        byte[] barray = new byte[1024];
        edp.GetBaseInfo(edp.GBI_COMPANYNAME, barray, 1024);
        String companyName = StringUtil.getString(barray);
        logger.info("公司名称：" + companyName + "长度：" + companyName.length());
        return companyName;
    }

    /**
     * 获取客户端数
     * @param edp
     * @return
     */
    private int getClientCount(EdpJava edp) {
        try {
            byte[] barray = new byte[1024];
            edp.GetBaseInfo(edp.GBI_CIENTCOUNT, barray, 1024);
            int clientCount = (int)byteArrayToLong(barray);
            logger.info("终端数：" + clientCount);
            return clientCount;
        } catch (Exception e) {
            logger.info("无授权点数");
        }
        return -1;
    }


    /**
     * 获得授权的截止时间
     *
     * @param edp
     * @return
     */
    private int getDeadLine(EdpJava edp) {
        byte[] barray = new byte[1024];
        edp.GetBaseInfo(edp.GBI_MAXTIME, barray, 1024);
        barray = StringUtil.toRerverArr(barray);
        String binary16 = StringUtil.binary(barray, 16);
        Integer deadLine = Integer.parseInt(binary16, 16);
        logger.info("授权截止时间：" + deadLine);
        return deadLine;
    }


    /**
     * 获得剩余时间和已使用的时间
     */
    private List<String> getUsedDaysAndLastDays(int deadLine) {
        List<String> list = new ArrayList<>();
        String deadLineStr = String.valueOf(deadLine);
        long dealLinetimeStamp = DateUtil.getTimeTostamp(deadLineStr,DateUtil.DEFAULT_YYYYMMDD); // 截止时间戳
        String formatPattern = DateUtil.format(new Date(),DateUtil.DEFAULT_YYYYMMDD);
        long currentTimeStamp = DateUtil.getTimeTostamp(formatPattern,DateUtil.DEFAULT_YYYYMMDD);// 当前时间戳
        //String authorization_Time = systemConfigRepository.getByPrimaryKey(AUTHORIZATION_TIME); //授权导入时间
        Object authorization_Time = systemConfigService.getStaticConfig(AUTHORIZATION_TIME);
        if (authorization_Time != null) {
            String authorization_Time_str = authorization_Time.toString();
            long authorizationStamp = DateUtil.getTimeTostamp(authorization_Time_str,DateUtil.DEFAULT_YYYYMMDD); // 认证授权时间戳
            long LastDays = (dealLinetimeStamp - currentTimeStamp) / (1000 * 60 * 60 * 24);// 剩余时间
            logger.info("剩余时间：" + LastDays);
            long UsedDays = (currentTimeStamp - authorizationStamp) / (1000 * 60 * 60 * 24);// 已用时间
            logger.info("已用时间：" + UsedDays);
            list.add(String.valueOf(UsedDays));
            list.add(String.valueOf(LastDays));
        }
        return list;
    }

    @Override
    public String getHardwareSerialNo() {
        String cmd = "/usr/lib/./extraTool";
        List<String> result = ShellExecuteScript.querySuccessExecuteCmd(cmd);
        if (CollectionUtils.isNotEmpty(result)) {
            for (String msg : result) {
                logger.info("extraTool result：" + msg);
            }
            String deviceStr = result.get(result.size() - 3);
            if (deviceStr.indexOf("=") > 0) {
                String[] deviceArr = deviceStr.split("=");
                String serialNo = deviceArr[deviceArr.length - 1];
                return serialNo;
            }
        }
        return "";
    }

    /**
     * 获得授权错误信息
     * @param ret
     * @return
     */
    private String getAuthErrorMessage(Long ret){
        String message = null;
        int intValue = ret.intValue();
        switch (intValue) {
            case 1:
                message = "软件配置不正确, 缺少授权文件";
                break;
            case 2:
                message = "软件授权无效，或者授权已损坏";
                break;
            case 3:
                message = "已超过授权使用的时间";
                break;
            case 4:
                message = "已超过授权使用的次数";
                break;
            case 5:
                message = "未授权的终端";
                break;
            case 6:
                message = "未授权的介质环境";
                break;
            case 7:
                message = "未授权的客户端";
                break;
            case 8:
                message = "缺少授权模块验证文件";
                break;
            case 9:
                message = "没有接入硬件KEY，或者key连接异常";
                break;
            case 10:
                message = "接入硬件KEY无效";
                break;
            case 11:
                message = "授权软件与KEY不匹配";
                break;
            case 12:
                message = "未知的硬件KEY";
                break;
            case 13:
                message = "未授权的IP范围";
                break;
            default:
                break;
        }
        return message;
    }

    private static long byteArrayToLong(byte[] bytes) {
        long lsum = 0L;
        long value = 0L;

        for(int i = 3; i >= 0; --i) {
            value = (long)(bytes[i] & 255);
            value <<= i * 8;
            lsum += value;
        }

        return lsum;
    }
}

