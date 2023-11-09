package com.vrv.vap.xc.fegin;

import com.vrv.vap.xc.config.FeignFlumeConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

//@FeignClient(value = "Collectors", url = "https://192.168.119.213:28081/")
@FeignClient(value = "Collectors", url = "${flume.base-url}", configuration = FeignFlumeConfig.class)
public interface CollectorsClient {

    @RequestMapping(method = RequestMethod.GET, value = "login")
    String login(@RequestParam("token") String token, @RequestHeader("cookie") String sessionCookie);

    @RequestMapping(method = RequestMethod.GET, value = "login")
    Response login0(@RequestParam("token") String token);

    @RequestMapping(method = RequestMethod.GET, value = "collect/info")
    List<Map<String, Object>> getCollectors(@RequestParam("token") String token, @RequestHeader("cookie") String sessionCookie);

    @RequestMapping(method = RequestMethod.GET, value = "collect/start")
    String startCollector(@RequestParam("cid") String cid);

    @RequestMapping(method = RequestMethod.GET, value = "collect/stop")
    String stopCollector(@RequestParam("cid") String cid);
}
