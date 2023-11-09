//package com.vrv.vap.admin.util;
//
//import com.alibaba.fastjson.JSONObject;
//import com.vrv.vap.admin.common.enums.NoticeTypeEnum;
//import com.vrv.vap.admin.common.util.JsonUtil;
//import com.vrv.vap.admin.vo.supervise.ServerInfo;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author wh1107066
// * @date 2023/8/30
// */
//public class OauthUtilNoBootTest {
//    @Test
//    public void testOauth2Data() {
//        ServerInfo serverInfo = new ServerInfo();
//        serverInfo.setClientId("10031-11100000MB1802777M");
//        serverInfo.setClientSecret("12A22CA63659D8C45C8777B1237704506d99F145F0847DEBS234LSE045AFC887");
//        serverInfo.setRootIp("192.168.120.88");
//        serverInfo.setRootPort("2000");
//        String uri = "/coor/api/routing/announce";
//
//        OauthUtil oauthUtil = new OauthUtil();
//        Map<String, String> otherParams = new HashMap<>();
////        otherParams.put("notice_type", NoticeTypeEnum.SUPERVISE_JOB.getType());
////        otherParams.put("notice_type", NoticeTypeEnum.RISK_ALARM_JOB.getType());
////        otherParams.put("notice_type", NoticeTypeEnum.COLLABORATE_JOB.getType());
//        otherParams.put("notice_type", NoticeTypeEnum.COLLABORATE_RESULT_JOB.getType());
//        oauthUtil.setOtherParams(otherParams);
//        String data = (String) oauthUtil.oauth2Data(uri, serverInfo);
//        System.out.println(data);
//        JSONObject jsonObject = JSONObject.parseObject(data);
//        String code = String.valueOf(jsonObject.get("code"));
//        Assert.assertEquals("状态码返回","200", code);
//
//        Map<String, String> extendMap = JsonUtil.jsonToMap(data);
//        System.out.println(extendMap);
//    }
//}
