package com.vrv.vap.admin.util;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.vo.supervise.OAuth2ClientKey;
import com.vrv.vap.admin.vo.supervise.ServerInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wh1107066
 * @date 2023/8/30
 */
public class OauthUtil {
    private Logger logger = LoggerFactory.getLogger(OauthUtil.class);
    private static final String TOKEN_URI = "/coor/api/routing/token";
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private Map<String, String> otherParams = new HashMap<>();

    public OauthUtil() {
    }



    public OauthUtil(Map<String, String> otherParams) {
        this.otherParams = otherParams;
    }

    public Map<String,Object> oauth2Data(String uri, ServerInfo serverInfo,String noticeType) {
        String rootUrl = serverInfo.getRootUrl();
        String url = getFullPath(rootUrl, uri);
        String clientId = serverInfo.getClientId();
        String clientSecret = serverInfo.getClientSecret();
        OAuth2ClientKey clientKey = new OAuth2ClientKey(clientId, clientSecret);
        String accessToken = this.getOAuth2Token(serverInfo);
        if (accessToken == null) {
            throw new RuntimeException(String.format("获取accessToken异常，client_id: %s   client_secret: %s", clientId, clientSecret));
        }
        Map<String, Object> params = new HashMap<>();
//        params.put("updateTime", DateUtil.format(status.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
        params.put("client_id", clientKey.getClientId());
        if (!otherParams.isEmpty()) {
            params.putAll(otherParams);
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + accessToken);
        String post = null;
        try {
            post = HTTPUtil.POST(url, headers, gson.toJson(params));
        } catch (Exception e) {
            throw new RuntimeException("调用post请求异常！", e);
        }
        if (StringUtils.isNotEmpty(post)) {
            JSONObject jsonObject = JSONObject.parseObject(post);
            jsonObject.put("notice_type", noticeType);
            int code = jsonObject.getIntValue("code");
            String msg = jsonObject.getString("msg");
            if (200 == code) {
                return jsonObject.getInnerMap();
            } else {
                throw new RuntimeException(String.format("post请求返回的状态码不正常! code:%s, mgs:%s",code, msg));
            }
        } else {
            throw new RuntimeException("post请求异常！获取的post的值为空！");
        }
    }


    public String getOAuth2Token(ServerInfo serverInfo) {
        String accessToken = null;
        String rootUrl = serverInfo.getRootUrl();
        String url = getFullPath(rootUrl, TOKEN_URI);
        OAuth2ClientKey clientKey = new OAuth2ClientKey(serverInfo.getClientId(), serverInfo.getClientSecret());
        Map<String, String> params = new HashMap<String, String>();
        params.put("client_id", clientKey.getClientId());
        params.put("client_secret", clientKey.getClientSecret());
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            String result = HTTPUtil.POST(url, headers, gson.toJson(params));
            if (StringUtils.isNotEmpty(result)) {
                Map map = gson.fromJson(result, Map.class);
                if (map.containsKey("access_token")) {
                    accessToken = (String) map.get("access_token");
                }
            }
        } catch (Exception e) {
            logger.error("调用post异常！", e);
        }
        return accessToken;
    }

    public String getFullPath(String baseUrl, String path) {
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

    public Map<String, String> getOtherParams() {
        return otherParams;
    }

    public void setOtherParams(Map<String, String> otherParams) {
        this.otherParams = otherParams;
    }
}
