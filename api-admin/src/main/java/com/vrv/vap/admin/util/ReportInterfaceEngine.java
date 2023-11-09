package com.vrv.vap.admin.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.admin.common.constant.ReportConstant;
import com.vrv.vap.admin.common.util.SpringContextUtil;
import com.vrv.vap.admin.model.BaseReportInterface;
import com.vrv.vap.admin.model.BaseReportModel;
import com.vrv.vap.admin.service.BaseReportInterfaceService;
import com.vrv.vap.admin.vo.ReportResponseVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportInterfaceEngine {
    private static RestTemplate restTemplate = SpringContextUtil.getApplicationContext().getBean(RestTemplate.class);
    private static BaseReportInterfaceService interfaceService = SpringContextUtil.getApplicationContext().getBean(BaseReportInterfaceService.class);

    private static final Logger log = LoggerFactory.getLogger(ReportInterfaceEngine.class);

    public static List<Map<String, Object>> getDataFromInterface(BaseReportModel model){
        List<Map<String, Object>> data = new ArrayList<>();
        try{
            if(StringUtils.isEmpty(model.getInterfaceId())){
                log.info("id为" + LogForgingUtil.validLog(model.getId()) + "的指标为空");
                return data;
            }
            BaseReportInterface bai = interfaceService.findById(model.getInterfaceId());
            //封装接口参数
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<String, Object>();
            Map<String,Object> jsonMap = new HashMap<>();
            if(StringUtils.isNotEmpty(bai.getParams())){
                JSONArray params = JSONArray.parseArray(bai.getParams());
                params.forEach(e ->{
                    JSONObject o = (JSONObject) e;
                    String field = o.getString("field");
                    param.add(field,model.getBindParam().get(field));
                    jsonMap.put(field,model.getBindParam().get(field));
                });
            }
            HttpEntity<String> formEntity = new HttpEntity<String>(JSONObject.toJSONString(jsonMap), headers);
            Object entity;
            //entity = param;//参数为表单提交用这个
            entity = formEntity;//参数为json格式用这个
            //restTemplate调用接口
            log.info("指标调用开始：url="+bai.getUrl());
            ResponseEntity<ReportResponseVo> responseEntity = restTemplate.postForEntity(bai.getUrl(), entity, ReportResponseVo.class);
            //解析接口数据
            log.info("指标调用结束：result="+JSONObject.toJSONString(responseEntity));
            ReportResponseVo body = responseEntity.getBody();
            if(ReportConstant.INTERFACE_TYPE.LIST.equals(bai.getType()) || ReportConstant.INTERFACE_TYPE.EMPTY.equals(bai.getType())){
                //list结构
                data = body.getList();
            }else if(ReportConstant.INTERFACE_TYPE.MAP.equals(bai.getType())){
                //map结构
                Map<String, Object> pmap = body.getData();
                if(pmap != null && !pmap.isEmpty()){
                    model.getBindParam().putAll(pmap);
                }
            }else{
                //混合结构
                if(CollectionUtils.isNotEmpty(body.getList())) {
                    data = body.getList();
                }
                if(body.getData() != null){
                    model.getBindParam().putAll(body.getData());
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return data;
    }
}
