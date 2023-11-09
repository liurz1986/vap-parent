package com.vrv.vap.admin.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.config.HostConfig;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.model.License;
import com.vrv.vap.admin.service.AuthorizationService;
import com.vrv.vap.admin.service.LicenseService;
import com.vrv.vap.admin.util.FileFilterUtil;
import com.vrv.vap.admin.vo.AuthorizationVO;
import com.vrv.vap.admin.vo.LicenseVO;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.utils.ApplicationContextUtil;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.service.SyslogSender;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping(path = "/license")
public class LicensenController extends ApiController {
    private static final String LICENSE_AUTH = "license_auth";
    private static final String PRODUCT = "product";
    private static final String MODULE = "module";
    private static Logger logger = LoggerFactory.getLogger(LicensenController.class);
    @Value("${licensePath}")
    private  String licensePath;
    @Autowired
    private LicenseService licenseService;


    @Autowired
    private StringRedisTemplate redisTpl;


    @Autowired
    private AuthorizationService authorizationService;

    /**
     * 导入授权文件
     *
     * @param
     * @return
     */

    @PostMapping(value = "/importLicense")
    @ApiOperation(value = "导入授权文件", notes = "")
    @SysRequestLog(description = "导入授权文件", actionType = ActionType.IMPORT, manually = false)
    public VData<AuthorizationVO> importAuthorizationFile(@RequestParam("file") MultipartFile upfile) {
        String oriName = upfile.getOriginalFilename();
        String fileType = oriName.substring(oriName.lastIndexOf(".") + 1, oriName.length()).toLowerCase();
        // 扫描备注：已做文件格式白名单校验
        if (!FileFilterUtil.validFileType(fileType)) {
            return this.vData(false);
        }
        String savePath = Paths.get(licensePath).toString();
        File file = new File(savePath);
        logger.info(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName =  "edpLicense"+HostConfig.edpNum+".data";
        String upFileName = upfile.getOriginalFilename();
        if (!fileName.equals(upFileName)) {
            return this.vData(ErrorCode.LICENSE_INVALID);
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(file.getPath() + File.separator + fileName,false);
            os.write(upfile.getBytes());
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 完毕，关闭所有链接
            try {
                if(os!=null){
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        AuthorizationVO authorizationInfo = authorizationService.resetAuthorizationfInfo();
        return this.vData(authorizationInfo);
    }

    /**
     * 用户登录，根据证书，修改模块的使用权限
     */
    @ApiOperation(value = "登录时，根据证书，修改模块的使用权限")
    @GetMapping(path = "/module/auth")
    public Result updateResourceByLicense() {
        // 根据证书，修改模块的使用权限
        licenseService.updateResourceByLicense();
        return this.vData(Global.OK);
    }

    /**
     * 获取证书的名称
     */
    @ApiOperation(value = "获取license信息")
    @GetMapping
    public VData getLicense() {
        // 根据证书，修改模块的使用权限
        List<License> license =  licenseService.findAll();
        if(license!=null&&license.size()>0) {
            AuthorizationVO authorizationVO = authorizationService.getAuthorizationInfo();
            LicenseVO licenseVO = new LicenseVO();
            BeanUtils.copyProperties(license.get(0),licenseVO);
            licenseVO.setTerminalCount(authorizationVO.getTerminalCount());
            return this.vData(licenseVO);
        }
        return this.vData(false);
    }

    /**
     * 获取授权信息
     */
    @ApiOperation(value = "获取授权信息")
    @GetMapping("/authorization")
    public VData<AuthorizationVO> getAuthorization() {
        return this.vData(authorizationService.getAuthorizationInfo());
    }

    @ApiOperation(value = "获取硬件编号")
    @GetMapping("/hardwareSerialNo")
    public Result getHardwareSerialNo() {
        return this.vData(authorizationService.getHardwareSerialNo());
    }

    /**
     * 判断输入产品号与证书里产品号，是否完全一致
     */
    @ApiOperation(value = "判断输入产品号与证书产品号，是否完全一致")
    @GetMapping("/is_same/{product}")
    public Result isSame(@PathVariable(PRODUCT) Integer product) {
        // 从redis获取证书的信息
        int productInLicense;
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,Object> body = new HashMap<>();
        String authJson = redisTpl.opsForValue().get(LICENSE_AUTH);
        if(StringUtils.isEmpty(authJson)){
            List<License> license =  licenseService.findAll();
            productInLicense = license.get(0).getProduct();
            String[] module=  license.get(0).getModule().split(",");
            List<String> modules = Arrays.asList(module);
            Map<String ,Object> map = new HashMap<>();
            map.put(MODULE,modules);
            map.put(PRODUCT,productInLicense);
            try {
                authJson  = objectMapper.writeValueAsString(map);
                redisTpl.opsForValue().set(LICENSE_AUTH,authJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                body = objectMapper.readValue(authJson,Map.class);
                String product1 = body.get(PRODUCT).toString();
                productInLicense = Integer.valueOf(product1);
            } catch (Exception e) {
                logger.error("",e);
                return new Result("2", "获取证书的产品号信息异常");
            }
        }
       if(product == productInLicense){
           return new Result("0", "true");
      }
      else {
           return new Result("1", "false");
       }

    }


    /**
     * 判断输入产品号,是否包含于证书里产品号里
     */
    @ApiOperation(value = "判断输入产品号,是否包含于证书里产品号里")
    @GetMapping("/is_include/{product}")
    public Result isInclude(@PathVariable(PRODUCT) Integer product) {
        // 从redis获取证书的信息
        int productInLicense;
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,Object> body = new HashMap<>();
        String authJson = redisTpl.opsForValue().get(LICENSE_AUTH);
        if(StringUtils.isEmpty(authJson)){
            List<License> license =  licenseService.findAll();
             productInLicense = license.get(0).getProduct();
             String[] module=  license.get(0).getModule().split(",");
             List<String> modules = Arrays.asList(module);
             Map<String ,Object> map = new HashMap<>();
             map.put(PRODUCT,productInLicense);
             map.put(MODULE,modules);
            try {
                 authJson  = objectMapper.writeValueAsString(map);
                redisTpl.opsForValue().set(LICENSE_AUTH,authJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                body = objectMapper.readValue(authJson,Map.class);
                String product1 = body.get(PRODUCT).toString();
                 productInLicense = Integer.valueOf(product1);
            } catch (Exception e) {
                logger.error("",e);
                return new Result("2", "获取证书的产品号信息异常");
            }
        }
        List<Integer> licenseNumberList =  transferBi(productInLicense);
        List<Integer> queryNumberList =  transferBi(product);
        if(CollectionUtils.isEmpty(queryNumberList)){
            return new Result("3", "传入产品号数字有误，产品号应大于0");
        }

        if(licenseNumberList.containsAll(queryNumberList)){
            return new Result("0", "true");
        }
        else {
            return new Result("1", "false");
        }
    }



   private List<Integer> transferBi(int num) {
        // 接收二进制各位数
        List<Integer> bi = new ArrayList<Integer>();
        //接收组成数
        List<Integer> numbers = new ArrayList<Integer>();
        //计算组成数时，获取当前位的0/1值
        int number = 0;
        // 二进制转换
        while(num > 0) {
            bi.add(num%2);
            num = num/2;
        }
       // 计算组成数,加入到list中
        for(int i = 0; i < bi.size(); i++) {
            if((number = bi.get(i)) != 0) {
                numbers.add(number * (int)Math.pow(2, i));
            }
        }
        return numbers;
    }


    /**
     * 更新资源状态
     */
    @ApiOperation(value = "资源状态")
    @GetMapping(path = "/updateResource")
    public Result updateResource() {
        licenseService.updateResourceByLicense();
        return Global.OK;
    }
}

