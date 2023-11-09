package com.vrv.vap.netflow.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author wh1107066
 * @date 2023/9/7
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MonitorV2ControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void assetAlignParserData() throws Exception {
        String content = "{\"address_code\":\"100000\",\"contact\":[{\"phone\":\"138********\",\"name\":\"张珊\",\"position\":\"处长\",\"email\":\"ljh@163.com\"},{\"phone\":\"138********\",\"name\":\"张三三\",\"position\":\"主任\",\"email\":\"ljh@163.com\"}],\"cpu_info\":[{\"core\":8,\"physical_id\":0,\"clock\":1.8}],\"device_belong\":\"xx部\",\"device_id\":\"123\",\"device_location\":\"123\",\"device_soft_version\":\"123\",\"disk_info\":[{\"size\":500,\"serial\":\"ST10000NM0011\"},{\"size\":1000,\"serial\":\"ST10000NM0011\"}],\"interface\":[{\"netmask\":\"192.168.0.0/24\",\"ip\":\"192.168.1.1\",\"type\":1},{\"netmask\":\"192.168.0.0/24\",\"ip\":\"192.168.1.21\",\"type\":3}],\"mem_total\":\"123\",\"memo\":\"首次注册\"}";
        String reuslt = mockMvc.perform(post("/V2/asset").contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println(reuslt);
        TimeUnit.MILLISECONDS.sleep(6000000);
        System.out.println("------");
    }


}