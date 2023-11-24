package com.vrv.vap.netflow.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author wh1107066
 * @date 2023/11/24
 */
public class NetFlowFieldAnalysisServiceImplTest {

    @Test
    public void handleSessionId() {
        NetFlowFieldAnalysisServiceImpl netFlowFieldAnalysisService = new NetFlowFieldAnalysisServiceImpl();
        String jsonString="{\"device_id\":\"220409011325\",\"time\":1700811679895,\"sip\":\"192.168.118.108\",\"sipv6\":\"\",\"smac\":\"08:10:79:9A:4C:FD\",\"sport\":18026,\"dip\":\"192.168.118.116\",\"dipv6\":\"\",\"dmac\":\"00:0C:29:2A:21:EB\",\"dport\":8080,\"network_protocol\":0,\"transport_protocol\":6,\"session_protocol\":0,\"sess_id\":2055349405666065,\"total_in_bytes\":0,\"total_out_bytes\":0,\"total_in_pkts\":0,\"total_out_pkts\":0,\"flow_duration\":99845,\"device_port_id\":1,\"interface_icon\":\"武汉核心交换机\",\"device_ip\":\"192.168.120.86\",\"data_type\":2,\"flow_start_time\":1700811574772,\"flow_end_time\":1700811674617,\"app_protocol\":1,\"app_name\":\"http\",\"log_type\":2,\"host\":\"192.168.118.116\",\"cookie\":\"\",\"agent\":\"Team Foundation (devenv.exe, 15.129.30720.4, Enterprise, SKU:54)\",\"http_req_header\":\"User-Agent: Team Foundation (devenv.exe, 15.129.30720.4, Enterprise, SKU:54)\\r\\nX-TFS-FedAuthRedirect: Suppress\\r\\nX-TFS-Version: 1.0.0.0\\r\\nAccept-Language: zh-CN\\r\\nX-TFS-Session: f3291ff2-f9b2-47fb-9ac2-7f96f3fb59d8, Connect\\r\\nX-VSS-Agent: TFS: 504447ee-407b-4268-981d-7f19a4f83a00\\r\\nContent-Type: application/soap+xml; charset=utf-8\\r\\nSOAPAction: \\\"http://microsoft.com/webservices/Connect\\\"\\r\\nHost: 192.168.118.116:8080\\r\\nContent-Length: 242\\r\\nExpect: 100-continue\\r\\nAccept-Encoding: gzip\\r\\nConnection: Keep-Alive\\r\\n\",\"http_res_code\":401,\"http_res_header\":\"Cache-Control: private\\r\\nContent-Type: text/html\\r\\nServer: Microsoft-IIS/7.5\\r\\nX-TFS-ProcessId: 3e347b7d-0854-4684-bb48-785be85d8e4f\\r\\nAccess-Control-Allow-Origin: *\\r\\nAccess-Control-Max-Age: 3600\\r\\nAccess-Control-Allow-Methods: OPTIONS,GET,POST,PATCH,PUT,DELETE\\r\\nAccess-Control-Expose-Headers: ActivityId,X-TFS-Session,X-MS-ContinuationToken\\r\\nAccess-Control-Allow-Headers: authorization\\r\\nX-FRAME-OPTIONS: SAMEORIGIN\\r\\nSet-Cookie: Tfs-SessionId=f3291ff2-f9b2-47fb-9ac2-7f96f3fb59d8; path=/, Tfs-SessionActive=2023-11-24 07:40:47Z; path=/\\r\\nWWW-Authenticate: NTLM\\r\\nX-Powered-By: ASP.NET\\r\\nP3P: CP=\\\"CAO DSP COR ADMa DEV CONo TELo CUR PSA PSD TAI IVDo OUR SAMi BUS DEM NAV STA UNI COM INT PHY ONL FIN PUR LOC CNT\\\"\\r\\nX-Content-Type-Options: nosniff\\r\\nDate: Fri, 24 Nov 2023 07:40:47 GMT\\r\\nContent-Length: 1181\\r\\n\",\"file_list\":[],\"method\":\"POST\",\"uri\":\"/tfs/defaultcollection/Services/v3.0/LocationService.asmx\",\"origin\":\"\",\"referer\":\"\",\"xff\":\"\",\"setcookie\":\"\",\"content_type\":\"text/html\",\"content_length\":1181,\"event_time\":1700811679895,\"report_log_type\":2,\"guid\":\"bab86859-155d-476a-a5c6-980a753d48cf\",\"session_id\":\"\"}";
        Map target =  JSONObject.parseObject(jsonString);
        netFlowFieldAnalysisService.handleSessionId(target);
    }
}