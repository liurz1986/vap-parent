package com.vrv.vap.admin.web;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.properties.RegisterProperties;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方服务注册
 */
@RequestMapping(path = "/")
@RestController
public class RegisterController extends ApiController {
    private static Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private RegisterProperties registerProperties;

    /**
     * 服务注册
     *
     * @return result
     */
    @ApiOperation(value = "服务注册")
    @GetMapping(value = "/registerService")
    public Result registerService() {
        Result result;
        String requestParam = JSON.toJSONString(registerProperties);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        try {
            String response = HTTPUtil.POST(registerProperties.getRegister_url(), headers, requestParam);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resMap = objectMapper.readValue(response, Map.class);
            if (MapUtils.isEmpty(resMap)) {
                result = ErrorCode.REGISTER_INTERFACE_ERROR.getResult();
            } else {
                int statusCode = (int) resMap.get("status_code");
                if (statusCode == 0) {
                    result = new Result("0", "服务注册成功");
                } else if (statusCode == 1 || statusCode == 2) {
                    result = ErrorCode.REGISTER_PARAM_ERROR.getResult();
                } else {
                    result = ErrorCode.REGISTER_OTHER_ERROR.getResult();
                }
            }
            logger.info(result.getCode() + ":" + result.getMessage());
        } catch (Exception e) {
            result = ErrorCode.REGISTER_OTHER_ERROR.getResult();
            logger.error("unknown exception", e);
        }
        return result;
    }
}



