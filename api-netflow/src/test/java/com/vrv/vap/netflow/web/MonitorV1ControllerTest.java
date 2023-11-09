package com.vrv.vap.netflow.web;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @author wh1107066
 * @date 2023/9/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MonitorV1ControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void monitorReportData() throws Exception{
//        String context ="[{\"flow_start_time\":1694091985202,\"close_status\":\"TMO\",\"total_pkt\":7,\"dmac\":\"00:50:56:9C:18:D4\",\"client_total_byte\":99520821,\"smac\":\"00:50:56:9C:18:D4\",\"avg_pkt_num\":23,\"dip\":\"1.3.5.9\",\"avg_delay_time\":86,\"total_byte\":23,\"dport\":3306,\"flow_end_time\":1694210957202,\"log_type\":1,\"sess_id\":2,\"app_protocol\":25,\"flow_duration\":24,\"sip\":\"1.9.0.1\",\"client_total_pkt\":97,\"sipv6\":\"xxxx\",\"retrans_pkt_num\":27,\"device_id\":\"d191bfc06b094a17a5cfb1b138d5b619\",\"interface_icon\":\"核心交换\",\"server_total_pkt\":2,\"device_port_id\":2,\"transport\":17,\"network_protocol\":1,\"res_code\":1,\"app_name\":\"Global.app_protocolItems\",\"data_type\":1,\"avg_pkt_size\":23,\"dipv6\":\"xxxx\",\"time\":1694204267202,\"server_total_byte\":21592385,\"sport\":3306,\"avg_pkt_byte\":9,\"username\":\"Global.app_protocolItems\"},{\"flow_start_time\":1694131392202,\"total_pkt\":17,\"dmac\":\"00:50:56:9C:18:D4\",\"client_total_byte\":54251979,\"smac\":\"00:50:56:9C:18:D4\",\"avg_pkt_num\":21,\"dip\":\"1.2.3.60\",\"total_byte\":23,\"dport\":21,\"flow_end_time\":1694224332202,\"log_type\":2,\"sess_id\":2,\"app_protocol\":9,\"flow_duration\":20,\"sip\":\"1.3.5.9\",\"client_total_pkt\":69,\"sipv6\":\"xxxx\",\"device_id\":\"8ef59e2ead0a4d2396c4ec90d4e6d0b8\",\"interface_icon\":\"核心交换\",\"server_total_pkt\":82,\"device_port_id\":2,\"transport\":0,\"network_protocol\":47,\"res_code\":0,\"app_name\":\"Global.app_protocolItems\",\"data_type\":1,\"avg_pkt_size\":13,\"dipv6\":\"xxxx\",\"time\":1694201504202,\"server_total_byte\":98385988,\"sport\":8080,\"avg_pkt_byte\":21,\"username\":\"Global.app_protocolItems\"}}]\n" ;
//        String reuslt = mockMvc.perform(post("/V1/log").contentType(MediaType.APPLICATION_JSON_UTF8)
//                        .content(context))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();

//        System.out.println(reuslt);
        TimeUnit.MILLISECONDS.sleep(6000000);
        System.out.println("------");


    }
}