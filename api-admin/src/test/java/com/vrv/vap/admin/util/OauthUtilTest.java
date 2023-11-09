//package com.vrv.vap.admin.util;
//
//import com.vrv.vap.admin.common.enums.NoticeTypeEnum;
//import com.vrv.vap.admin.common.util.JsonUtil;
//import com.vrv.vap.admin.vo.supervise.ServerInfo;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * @author wh1107066
// * @date 2023/8/30
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class OauthUtilTest {
//    @Autowired
//    private WebApplicationContext wac;
//    private MockMvc mockMvc;
//    @Before
//    public void setup() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
//    }
//    @Test
//    public void testOauth2Data() {
//        ServerInfo serverInfo = new ServerInfo();
//        serverInfo.setClientId("10031-11100000MB1802777M");
//        serverInfo.setClientSecret("12A22CA63659D8C45C8777B1237704506d99F145F0847DEBS234LSE045AFC887");
//        serverInfo.setRootIp("192.168.120.88");
//        serverInfo.setRootPort("2000");
//        String uri = "/coor/api/routing/announce";
//        OauthUtil oauthUtil = new OauthUtil();
//        Map<String, String> otherParams = new HashMap<>();
////        otherParams.put("notice_type", NoticeTypeEnum.SUPERVISE_JOB.getType());
////        otherParams.put("notice_type", NoticeTypeEnum.RISK_ALARM_JOB.getType());
////        otherParams.put("notice_type", NoticeTypeEnum.COLLABORATE_JOB.getType());
//        otherParams.put("notice_type", NoticeTypeEnum.COLLABORATE_RESULT_JOB.getType());
//        oauthUtil.setOtherParams(otherParams);
//        String data = (String) oauthUtil.oauth2Data(uri, serverInfo);
//        Map<String, String> extendMap = JsonUtil.jsonToMap(data);
//        System.out.println(extendMap);
//    }
//
//// 写一个测试用例，使用mockmvc模拟http请求
//
//    @Test
//    public void oauth2Data() throws Exception {
//        String content = "[{\"device_id\":\"220409011325\",\"device_belong\":\"\",\"device_location\":\"\",\"device_soft_version\":\"V2.0.30009\",\"data_type\":2,\"time\":1692343427047,\"smac\":\"8C:16:45:57:56:67\",\"dmac\":\"04:F9:38:BA:CE:0F\",\"sip\":\"192.168.118.227\",\"sport\":49453,\"dip\":\"192.168.19.78\",\"dport\":10088,\"network_protocol\":0,\"transport_protocol\":6,\"app_protocol\":1,\"app_name\":\"http\",\"username\":\"\",\"res_code\":0,\"sess_id\":0,\"log_type\":2,\"method\":\"POST\",\"uri\":\"/CEMS-C-TCP/TCPServlet\",\"host\":\"192.168.19.78\",\"origin\":\"\",\"cookie\":\"\",\"agent\":\"Accept:CemsWebSvc_22216\",\"referer\":\"\",\"xff\":\"\",\"http_req_header\":\"Host: 192.168.19.78:10088\\r\\nUser-Agent: Accept:CemsWebSvc_22216\\r\\nAccept: */*\\r\\nContent-Length: 25322\\r\\n\",\"http_res_header\":\"Server: nginx\\r\\nDate: Fri, 18 Aug 2023 07:24:33 GMT\\r\\nContent-Type: application/octet-stream\\r\\nContent-Length: 110\\r\\nConnection: keep-alive\\r\\n\",\"http_res_code\":200,\"setcookie\":\"\",\"server\":\"\",\"content_type\":\"application/octet-stream\",\"content_length\":110,\"file_list\":[]}," +
//                "{\"device_id\":\"220409011325\",\"device_belong\":\"\",\"device_location\":\"\",\"device_soft_version\":\"V2.0.30009\",\"data_type\":2,\"time\":1692343427463,\"smac\":\"9C:5C:8E:73:F7:0C\",\"dmac\":\"04:F9:38:BA:CE:0F\",\"sip\":\"192.168.118.124\",\"sport\":20931,\"dip\":\"192.168.119.213\",\"dport\":8848,\"network_protocol\":0,\"transport_protocol\":6,\"app_protocol\":1,\"app_name\":\"http\",\"username\":\"\",\"res_code\":0,\"sess_id\":0,\"log_type\":2,\"method\":\"GET\",\"uri\":\"/nacos/v1/ns/instance/list?app=unknown&healthyOnly=false&namespaceId=a338c762-d2a7-4a2f-86f0-022c171c0928&clientIP=192.168.56.1&serviceName=DEFAULT_GROUP%40%40api-data&udpPort=49924&clusters=DEFAULT\",\"host\":\"192.168.119.213\",\"origin\":\"\",\"cookie\":\"\",\"agent\":\"Nacos-Java-Client:v1.4.1\",\"referer\":\"\",\"xff\":\"\",\"http_req_header\":\"Content-Type: application/x-www-form-urlencoded;charset=UTF-8\\r\\nAccept-Charset: UTF-8\\r\\nAccept-Encoding: gzip,deflate,sdch\\r\\nClient-Version: 1.4.1\\r\\nUser-Agent: Nacos-Java-Client:v1.4.1\\r\\nRequestId: 6f16f0e1-3b52-4185-9651-b82ce7140bbd\\r\\nRequest-Module: Naming\\r\\nHost: 192.168.119.213:8848\\r\\nAccept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2\\r\\nConnection: keep-alive\\r\\n\",\"http_res_header\":\"Content-Type: application/json;charset=UTF-8\\r\\nTransfer-Encoding: chunked\\r\\nDate: Fri, 18 Aug 2023 07:24:34 GMT\\r\\n\",\"http_res_code\":200,\"setcookie\":\"\",\"server\":\"\",\"content_type\":\"application/json\",\"content_length\":530,\"file_list\":[]}," +
//                "{\"device_id\":\"220409011325\",\"device_belong\":\"\",\"device_location\":\"\",\"device_soft_version\":\"V2.0.30009\",\"data_type\":2,\"time\":1692343427463,\"smac\":\"04:F9:38:BA:CE:0F\",\"dmac\":\"00:0C:29:A0:CD:99\",\"sip\":\"192.168.118.124\",\"sport\":20931,\"dip\":\"192.168.119.213\",\"dport\":8848,\"network_protocol\":0,\"transport_protocol\":6,\"app_protocol\":1,\"app_name\":\"http\",\"username\":\"\",\"res_code\":0,\"sess_id\":0,\"log_type\":2,\"method\":\"GET\",\"uri\":\"/nacos/v1/ns/instance/list?app=unknown&healthyOnly=false&namespaceId=a338c762-d2a7-4a2f-86f0-022c171c0928&clientIP=192.168.56.1&serviceName=DEFAULT_GROUP%40%40api-data&udpPort=49924&clusters=DEFAULT\",\"host\":\"192.168.119.213\",\"origin\":\"\",\"cookie\":\"\",\"agent\":\"Nacos-Java-Client:v1.4.1\",\"referer\":\"\",\"xff\":\"\",\"http_req_header\":\"Content-Type: application/x-www-form-urlencoded;charset=UTF-8\\r\\nAccept-Charset: UTF-8\\r\\nAccept-Encoding: gzip,deflate,sdch\\r\\nClient-Version: 1.4.1\\r\\nUser-Agent: Nacos-Java-Client:v1.4.1\\r\\nRequestId: 6f16f0e1-3b52-4185-9651-b82ce7140bbd\\r\\nRequest-Module: Naming\\r\\nHost: 192.168.119.213:8848\\r\\nAccept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2\\r\\nConnection: keep-alive\\r\\n\",\"http_res_header\":\"Content-Type: application/json;charset=UTF-8\\r\\nTransfer-Encoding: chunked\\r\\nDate: Fri, 18 Aug 2023 07:24:34 GMT\\r\\n\",\"http_res_code\":200,\"setcookie\":\"\",\"server\":\"\",\"content_type\":\"application/json\",\"content_length\":530,\"file_list\":[]}]";
//        String reuslt = mockMvc.perform(post("/V1/log").contentType(MediaType.APPLICATION_JSON_UTF8)
//                        .content(content))
//                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.id").value("1"))
//                .andReturn().getResponse().getContentAsString();
//
//        System.out.println(reuslt);
//        TimeUnit.MILLISECONDS.sleep(6000000);
//        System.out.println("------");
//    }
//
//}
