package com.vrv.vap.admin.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.admin.common.enums.SuperviseDataTypeEnum;
import com.vrv.vap.admin.common.util.DateUtil;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.model.SystemConfig;
import com.vrv.vap.admin.service.SuperviseService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
import com.vrv.vap.admin.util.FileFilterUtil;
import com.vrv.vap.admin.vo.supervise.*;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@Service
@Transactional
public class SuperviseServiceImpl implements SuperviseService {

    private static final Logger logger = LoggerFactory.getLogger(SuperviseServiceImpl.class);
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private static OAuth2ClientKey staticClientKey = null;

    private String accessToken = null;


    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    KafkaSenderService kafkaSenderService;

    public ServerInfo getServerInfo() {
        Object staticConfig = systemConfigService.getStaticConfig("ServerInfo");
        if (staticConfig != null) {
            return gson.fromJson(staticConfig.toString(), ServerInfo.class);
        }

        return null;
    }

    public ServerInfo getServerInfo(HttpServletRequest request) {
        Object staticConfig = systemConfigService.getStaticConfig("ServerInfo");
        if (staticConfig != null) {
            return gson.fromJson(staticConfig.toString(), ServerInfo.class);
        }
        ServerInfo info = new ServerInfo();
        info.setIsRegister(false);
        info.setRegisterType(0);

        return info;

    }

    public boolean saveServerInfo(ServerInfo info) {
        if (StringUtils.isNotEmpty(info.getClientId()) && StringUtils.isNotEmpty(info.getClientSecret())) {
            this.updateClientKey(info.getClientId(), info.getClientSecret());
        }
        return systemConfigService.saveConfValue("ServerInfo", gson.toJson(info));
    }

    @Override
    public int updateRegister(ServerInfoBase baseInfo) {
        String rootUrl = baseInfo.getRootUrl();

        String path = "/coor/api/routing/update";
        String url = getFullPath(rootUrl, path);

        Map<String, Object> params = new HashMap<>();
        OAuth2ClientKey clientKey = this.getClientKey();
        params.put("client_id", clientKey.getClientId());
        params.put("org_name", baseInfo.getOrgName());
        params.put("org_type", baseInfo.getOrgType());
        params.put("secret_qualification", baseInfo.getSecretQualification());
        params.put("net_license_number", baseInfo.getNetLicenseNumber());
        params.put("zjg_license_number", baseInfo.getZjgLicenseNumber());
        params.put("jcq_license_number", baseInfo.getJcqLicenseNumber());

        Map headers = new HashMap();
        headers.put("Content-Type", "application/json");
        try {
            String post = HTTPUtil.POST(url, headers, gson.toJson(params));
            if (StringUtils.isNotEmpty(post)) {
                BaseResult updateResult = gson.fromJson(post, BaseResult.class);
                updateResult.setCode("200");
                if ("200".equals(updateResult.getCode())) {
                    ServerInfo serverInfo = this.getServerInfo();
                    BeanUtils.copyProperties(baseInfo, serverInfo);
                    SystemConfig systemConfig = new SystemConfig();
                    systemConfig.setConfId("ServerInfo");
                    systemConfig.setConfValue(gson.toJson(serverInfo));
                    return systemConfigService.updateSelective(systemConfig);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return 0;
    }


    /**
     * 服务注册远程调用oauth2 server端进行注册
     * 在线注册，向 监管业务平台获取 client_id, client_secret 信息
     */
    public ServerRegisterResult serverRegister(ServerInfoBase info) {
        String rootUrl = info.getRootUrl();
        String path = "/coor/api/routing/register";
        String url = getFullPath(rootUrl, path);
        Map<String, Object> params = new HashMap<>();
        params.put("org", info.getOrgName());
        params.put("org_type", info.getOrgType());
        params.put("secret_qualification", info.getSecretQualification());
        params.put("net_license_number", info.getNetLicenseNumber());
        params.put("zjg_license_number", info.getZjgLicenseNumber());
        params.put("jcq_license_number", info.getJcqLicenseNumber());
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        String post = null;
        try {
            post = HTTPUtil.POST(url, headers, gson.toJson(params));
        } catch (Exception e) {
            throw new RuntimeException("向业务监管平台请求异常!", e);
        }
        if (StringUtils.isNotEmpty(post)) {
            ServerRegisterResult serverRegister = gson.fromJson(post, ServerRegisterResult.class);
            if ("200".equals(serverRegister.getCode())) {
                String clientId = serverRegister.getClientId();
                String clientSecret = serverRegister.getClientSecret();
                updateClientKey(clientId, clientSecret);
                systemConfigService.saveConfValue("client_id", clientId);
                systemConfigService.saveConfValue("client_secret", clientSecret);
                return serverRegister;
            } else {
                logger.error("业务系统获取状态码非200！" + serverRegister.getCode());
            }
        }
        return ServerRegisterResult.error("网络异常");
    }


    /**
     * 服务上报数据，走oauth2协议，携带token的上报信息
     * @param status
     * @return
     */
    public PutServerStatusResult reportServerStatus(ServerStatus status) {
        ServerInfo serverInfo = this.getServerInfo();
        if (serverInfo == null) {
            return PutServerStatusResult.error("服务器认证信息未填写");
        }
        String rootUrl = serverInfo.getRootUrl();
        String path = "/coor/api/routing/status";
        String url = getFullPath(rootUrl, path);

        OAuth2ClientKey clientKey = this.getClientKey();
        if (clientKey == null) {
            return PutServerStatusResult.error("获取服务器认证信息失败");
        }

        String accessToken = this.getOAuth2Token();
        if (accessToken == null) {
            return PutServerStatusResult.error("获取token失败");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("ssaRunState", status.getSsaRunState());
        params.put("updateTime", DateUtil.format(status.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
        params.put("client_id", clientKey.getClientId());
        Map headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + accessToken);
        try {
            String post = HTTPUtil.POST(url, headers, gson.toJson(params));
            if (StringUtils.isNotEmpty(post)) {
                return gson.fromJson(post, PutServerStatusResult.class);
            }
        } catch (Exception e) {
            logger.error("监管平台获取数据异常!", e);
        }
        return PutServerStatusResult.error("网络异常");
    }

    public PutServerDataResult reportBusinessEventData(ServerData data) {
        ServerInfo serverInfo = this.getServerInfo();
        if (serverInfo == null) {
            return PutServerDataResult.error("服务器认证信息未填写");
        }

        if (data == null) {
            return PutServerDataResult.error("上报数据异常");
        }

        OAuth2ClientKey clientKey = this.getClientKey();
        if (clientKey == null) {
            return PutServerDataResult.error("获取服务器认证信息失败");
        }
        String accessToken = this.getOAuth2Token();
        if (StringUtils.isEmpty(accessToken)) {
            return PutServerDataResult.error("获取token失败");
        }

        String rootUrl = serverInfo.getRootUrl();

        String path = "/coor/api/routing/data";
        String url = getFullPath(rootUrl, path);

        Map<String, Object> params = new HashMap<>();
        params.put("client_id", clientKey.getClientId());
        params.put("update_time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        params.put("type", data.getType());
        if (SuperviseDataTypeEnum.FOUR.getCode().equals(data.getType()) || SuperviseDataTypeEnum.FIVE.getCode().equals(data.getType()) || SuperviseDataTypeEnum.SIX.getCode().equals(data.getType())) {
            params.put("notice_id", data.getNoticeId());
        }
        if (SuperviseDataTypeEnum.FOUR.getCode().equals(data.getType()) || SuperviseDataTypeEnum.FIVE.getCode().equals(data.getType())) {
            //co_file	协办信息附件
            params.put("co_file", data.getCoFile());
        }
        if (SuperviseDataTypeEnum.SIX.getCode().equals(data.getType())) {
            //warn_file	预警响应信息附件
            params.put("warn_file", data.getWarnFile());
        }

        params.put("data", data.getData());
        Map headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + accessToken);
        try {
            String post;
            if ("1".equals(String.valueOf(data.getType()))) {
                post = HTTPUtil.POST(url, headers, gson.toJson(data.getData()));
            } else {
                post = HTTPUtil.POST(url, headers, gson.toJson(params));
            }
            if (StringUtils.isNotEmpty(post)) {
                PutServerDataResult fromJson = gson.fromJson(post, PutServerDataResult.class);
                return fromJson;
            }
        } catch (Exception e) {
            logger.error("1111", e);
        }
        return PutServerDataResult.error("网络异常");
    }

    @Override
    public void superviseAnnounce(AnnounceDataInfo info) {
        kafkaSenderService.send("SuperviseAnnounce", info.getId(), gson.toJson(info));
    }

    private String getOAuth2Token() {
        if (accessToken == null) {
            ServerInfo serverInfo = this.getServerInfo();
            if (serverInfo == null) {
                logger.error("服务器认证信息未填写");
                return accessToken;
            }
            String rootUrl = serverInfo.getRootUrl();

            synchronized (this) {
                String path = "/coor/api/routing/token";
                String url = getFullPath(rootUrl, path);

                OAuth2ClientKey key = this.getClientKey();
                if (key != null) {
                    Map params = new HashMap();
                    params.put("client_id", key.getClientId());
                    params.put("client_secret", key.getClientSecret());
                    String result = null;
                    try {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        result = HTTPUtil.POST(url, headers, gson.toJson(params));
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                    if (StringUtils.isNotEmpty(result)) {
                        Map map = gson.fromJson(result, Map.class);
                        if (map.containsKey("access_token")) {
                            return (String) map.get("access_token");
                        }
                    }
                } else {
                    logger.error("获取认证信息失败");
                    return accessToken;
                }
            }
        }
        return accessToken;
    }

    private void updateClientKey(String clientId, String clientSecret) {
        if (staticClientKey == null) {
            staticClientKey = new OAuth2ClientKey();
        }
        staticClientKey.setClientId(clientId);
        staticClientKey.setClientSecret(clientSecret);
    }

    @Override
    public OAuth2ClientKey getClientKey() {
        if (staticClientKey == null) {
            // 从数据库获取认证信息
            ServerInfo serverInfo = this.getServerInfo();
            if (serverInfo != null && StringUtils.isNotEmpty(serverInfo.getClientId())
                    && StringUtils.isNotEmpty(serverInfo.getClientSecret())) {
                this.updateClientKey(serverInfo.getClientId(), serverInfo.getClientSecret());
            }
        }
        return staticClientKey;
    }

    private String getFullPath(String baseUrl, String path) {
        String basePath = "";
        if (baseUrl.startsWith("https://")) {
            basePath = baseUrl;
        } else {
            basePath = "https://" + baseUrl;
        }

        if (basePath.endsWith("/") && path.startsWith("/")) {
            return basePath + path.substring(1);
        } else if (basePath.endsWith("/") || path.startsWith("/")) {
            return basePath + path;
        } else {
            return basePath + "/" + path;
        }
    }


    private Result importAannounceExcel(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        if (!FileFilterUtil.validFileType(fileType)) {
            return new Result();
        }
        HSSFWorkbook workbook = null;
        try (InputStream in = file.getInputStream()) {
            workbook = new HSSFWorkbook(in);
        } catch (IOException e) {
            logger.error("IOException: ", e);
            return new Result("-1", "解析excel发生异常");
        }

        Map<String, List<AnnounceDataInfo>> result = new HashMap<>();

        if (workbook == null) {
            return null;
        }
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) { // 获得多个sheet
            HSSFSheet sheet = workbook.getSheetAt(i);
            int physicalNumberOfRows = sheet.getPhysicalNumberOfRows(); // 获得行数

            int startIndex = 0;

            if (physicalNumberOfRows > startIndex) {

                List<AnnounceDataInfo> rows = new ArrayList<>();
                String sheetName = sheet.getSheetName();
                for (int j = startIndex + 1; j < physicalNumberOfRows; j++) { // 从第一行开始获得每个sheet当中的数据
                    List<String> list = new ArrayList<>();
                    HSSFRow row = sheet.getRow(j);


                    if (row == null) {
                        break;
                    }

                    for (int k = 0; k < row.getLastCellNum(); k++) {// 获取每个单元格
                        HSSFCell cell = row.getCell(k);


                        if (cell != null) {
                            if (cell.getCellType().equals(CellType.NUMERIC)) {
                                cell.setCellType(CellType.STRING);
                            }
                            list.add(cell.toString());
                        } else {
                            list.add("");
                        }
                    }
                    //parentList.add(list);
                    rows.add(new AnnounceDataInfo());
                }
                result.put(sheetName, rows);
            }
        }


        return null;
    }

    private VData<String> importAannounceXML(MultipartFile file) {
        return null;
    }
}
