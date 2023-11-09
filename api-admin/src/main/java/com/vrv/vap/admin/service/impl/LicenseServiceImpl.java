package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.config.HostConfig;
import com.vrv.vap.admin.common.util.DateUtil;
import com.vrv.vap.admin.common.util.JsonUtil;
import com.vrv.vap.admin.common.util.Md5Util;
import com.vrv.vap.admin.mapper.LicenseMapper;
import com.vrv.vap.admin.model.License;
import com.vrv.vap.admin.service.LicenseService;
import com.vrv.vap.admin.service.ResourceService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.vo.Menu;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test_edpky.EdpJava;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.*;


@Service
@Transactional
public class LicenseServiceImpl extends BaseServiceImpl<License> implements LicenseService {
    @Resource
    private LicenseMapper licenseMapper;

    @Autowired
    private ResourceService resourceService;

    @Value("${licensePath}")
    private  String licensePath;

    @Value("${licence.httpLicenseModule}")
    private String httpLicenseModule;

    private static final String PRODUCT_PREFIX = "VRV";

    @Autowired
    Cache<String, List<Menu>> menuCache;

    @Autowired
    private SystemConfigService systemConfigService;

    private final static String AUTHORIZATION_TIME = "Authorization_Time"; //授权时间

    private final static String AUTHORIZATION_STATUS = "authorization_Status"; //授权状态

    private final static String SYSTEMINIT_STATUS = "systeminit_status";//系统初始化状态

    private static final String STATUS_SUCCESS = "success";

    private static final String STATUS_FAIL = "fail";

    private static final String STATUS_TRUE = "True";

    private static Logger logger = LoggerFactory.getLogger(LicenseServiceImpl.class);

    private static String PRODUCT_VERSION = "product";

    private static String PRODUCT_MODULE = "module";

    private static String LICENSE_AUTH = "license_auth";




    @Override
    public void updateResourceByLicense() {
        try {
            String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
            if (os.startsWith("win")) {
                return;
            }
            String authJson  = this.getAuthorizationJson();
            if(authJson == null){
                this.forbiddenResource();
                menuCache.clear();
                // 当证书失效，对证书授权状态进行更新,要求重新上传证书
                systemConfigService.saveConfValue(AUTHORIZATION_STATUS,STATUS_FAIL);
                return;
            }
            Map<String,Object> licenseInfo = JsonUtil.jsonToMap(authJson);
            if(!licenseInfo.containsKey(PRODUCT_VERSION)){
                this.forbiddenResource();
                menuCache.clear();
                // 当证书不包含产品号，对证书授权状态进行更新,要求重新上传证书
                systemConfigService.saveConfValue(AUTHORIZATION_STATUS,STATUS_FAIL);
                return;
            }
            String product = licenseInfo.get(PRODUCT_VERSION).toString();
            String moudule = "";
            if(licenseInfo.get(PRODUCT_MODULE) != null) {
                List<String> modules = (List<String>) licenseInfo.get(PRODUCT_MODULE);
                Optional<String> moduleOptional = modules.stream().reduce((a, b) -> a + "," + b);
                if (moduleOptional.isPresent()) {
                    moudule = moduleOptional.get();
                }
            }

            this.licecseInfoToMysql(product,moudule);
            this.updateResource(product,moudule);
            menuCache.clear();
        } catch (Exception e) {
            logger.info("授权异常！！！！！！！");
            this.forbiddenResource();
            menuCache.clear();
            logger.error("",e);
        }

    }


    private int  licecseInfoToMysql(String product ,String module){
        List<License> licenseList = this.findAll();
        licenseList.forEach(p->this.deleteById(p.getId()));
        License license  = new License();
        license.setModule(module);
        license.setProduct(Integer.valueOf(product));
        license.setUpdatetime(new Date());
        return  this.save(license);
    }


    private synchronized String getAuthorizationJson() {
        try {
            //通过授权证书获得对应的实体信息
            String authorizationPath = Paths.get(licensePath).toString();
            logger.info("授权文件路径:" + authorizationPath);
            EdpJava edp = new EdpJava();
            edp.SetLcsFilePath(100, authorizationPath);
            long ret = edp.CheckKeyRightEx(HostConfig.edpNum, 3);
            if (ret != 0) {
                logger.info("ret:" + ret);
                logger.info("授权错误！！！！！！！");
                this.forbiddenResource();
                return null;
            }
            // 根据证书，修改模块的使用权限
            // 根据目录下的证书，调用接口获取json形式的权限信息
            byte[] barray = new byte[1024];
            int count = edp.GetSelfData(barray, 0);
            byte[] srtbyte = new byte[count];
            for (int i = 0; i < count; i++) {
                srtbyte[i] = barray[i];
            }

            String authJson;
            if (srtbyte.length == 0) {
                authJson = "{\"product\":4,\"module\":[]}";
            } else {
                logger.info("数据长度：" + srtbyte.length);
                authJson = this.byteToAuthJson(srtbyte);
            }

            logger.info("证书返回授权json信息:" + authJson);
            logger.info("json信息长度:" + String.valueOf(authJson.length()));
            return authJson;
        } catch (Exception e) {
            logger.info("获取授权证书信息异常");
            e.printStackTrace();
        }
        return null;
    }


    private void  updateResource(String product, String module){

        //初始化 ，放开模块的所有使用权限
        logger.info("开始更新资源权限");
        licenseMapper.freeResource();
        if(StringUtils.isEmpty(module)){
            return;
        }
        // 通过证书，将相关模块禁用
        String[] modules = module.split(",");
        List<String>  list =  Arrays.asList(modules);
        List<Map<String,String>> uuidList = licenseMapper.queryResources(list);
        List<String> uidList = new ArrayList<>();
            if(uuidList!=null && uuidList.size()>0){
                for(Map<String,String> map : uuidList){
                    uidList.add(map.get("uid"));
                }
                Example example = new Example(com.vrv.vap.admin.model.Resource.class);
                example.createCriteria().andIn("uid", uidList);
                com.vrv.vap.admin.model.Resource resource = new com.vrv.vap.admin.model.Resource();
                resource.setDisabled((byte)2);
                resourceService.updateSelectiveByExample(resource,example);
                //licenseMapper.updateResources(uidList);
            }
        logger.info("更新资源权限结束");
    }


    public void  forbiddenResource(){
        //发现异常，禁用所有菜单
        licenseMapper.forbiddenResource();
    }



//    private ErrorCode getAuthErrorMessage(Long ret){
//        int intValue = ret.intValue();
//        ErrorCode errorCode = ErrorCode.LICENSE_UNKNOWN_ERROR ;
//        switch (intValue) {
//            case 1:
//                errorCode = ErrorCode.LICENSE_NO_FILE;
//                break;
//            case 2:
//                errorCode = ErrorCode.LICENSE_INVALID;
//                break;
//            case 3:
//                errorCode = ErrorCode.LICENSE_EXCEED_AUTHORIZE_TIME;
//                break;
//            case 4:
//                errorCode = ErrorCode.LICENSE_EXCEED_AUTHORIZE_TIMES;
//                break;
//            case 5:
//                errorCode = ErrorCode.LICENSE_UNAUTHORIZED_TERMINAL;
//                break;
//            case 6:
//                errorCode = ErrorCode.LICENSE_UNAUTHORIZED_MEDIA_ENVIRONMENTT;
//                break;
//            case 7:
//                errorCode = ErrorCode.LICENSE_UNAUTHORIZED_CLIENT;
//                break;
//            case 8:
//                errorCode = ErrorCode.LICENSE_MISSING_VERTICATION_FILE;
//                break;
//            case 9:
//                errorCode = ErrorCode.LICENSE_KEY_UNCONNECTED;
//                break;
//            case 10:
//                errorCode = ErrorCode.LICENSE_KEY_INVALID;
//                break;
//            case 11:
//                errorCode = ErrorCode.LICENSE_KEY_UNMATCHED;
//                break;
//            case 12:
//                errorCode = ErrorCode.LICENSE_KEY_UNKNOWN;
//                break;
//            case 13:
//                errorCode = ErrorCode.LICENSE_UNAUTHORIZED_IP_RANGE;
//                break;
//            default:
//                break;
//        }
//        return errorCode;
//    }

    private String byteToAuthJson(byte[] bytes) throws UnsupportedEncodingException {
        byte[] authJson = new byte[bytes.length/2];
        for(int i = 0 ;i< bytes.length/2 ;i++){
            authJson[i] = bytes[2*i];
        }
        return new String(authJson,"utf-8");
    }

    @Override
    public void saveAuthenticationConfig() {
        //保存授权状态、授权时间、初始化状态
        systemConfigService.saveConfValue(AUTHORIZATION_TIME,DateUtil.format(new Date(), DateUtil.DEFAULT_YYYYMMDD));
        systemConfigService.saveConfValue(AUTHORIZATION_STATUS,STATUS_SUCCESS);
        systemConfigService.saveConfValue(SYSTEMINIT_STATUS,STATUS_TRUE);
        licenseMapper.freeResource();
    }

    @Override
    public String getHttpEncriptLicense() {
        String date = DateUtil.format(new Date());
        logger.info("当前日期：" + date);
        String license = Md5Util.string2Md5(PRODUCT_PREFIX + httpLicenseModule + DateUtil.format(new Date(), DateUtil.DEFAULT_YYYYMMDD));
        return license;
    }
}
