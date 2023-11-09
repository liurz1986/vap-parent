package com.vrv.vap.admin.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.admin.model.BaseReportInterface;
import com.vrv.vap.admin.model.BaseReportModel;
import com.vrv.vap.admin.model.ReportModelParam;
import com.vrv.vap.admin.service.BaseReportInterfaceService;
import com.vrv.vap.admin.service.BaseReportModelService;
import com.vrv.vap.admin.util.ModelUtil;
import com.vrv.vap.admin.util.ReportEngine;
import com.vrv.vap.admin.vo.BaseReportModelVo;
import com.vrv.vap.admin.vo.ReportResponseVo;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path = "/baseReportModel")
public class BaseReportModelController extends ApiController {

    @Autowired
    private BaseReportModelService baseReportModelService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private BaseReportInterfaceService baseReportInterfaceService;
    private static final Logger log = LoggerFactory.getLogger(BaseReportModelController.class);

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("type","{\"1\":\"饼图\",\"2\":\"折线图\",\"3\":\"柱状图\",\"4\":\"表格\",\"5\":\"段落\",\"6\":\"引用\",\"7\":\"列表\"}");
    }

    /**
     * 获取所有模型
     * @return
     */
    @ApiOperation(value = "获取所有模型")
    @GetMapping(value = "/all")
    @SysRequestLog(description="获取所有模型", actionType = ActionType.SELECT)
    public VData<List<BaseReportModel>> queryAllArea() {
        return this.vData(baseReportModelService.findAll());
    }


    /**
     * 条件查询模型
     * 支持分页查询、条件查询 、任意字段排序
     */
    @ApiOperation(value = "条件查询模型")
    @PostMapping
    @SysRequestLog(description="查询模型", actionType = ActionType.SELECT)
    public VList<BaseReportModel> queryAreas(@RequestBody BaseReportModelVo baseReportModelVo) {
        SyslogSenderUtils.sendSelectSyslog();
        if(StringUtils.isEmpty(baseReportModelVo.getType())){
            baseReportModelVo.setType(null);
        }
        Example example = this.pageQuery(baseReportModelVo, BaseReportModel.class);
        return this.vList(baseReportModelService.findByExample(example));
    }

    /**
     * 添加模型
     */
    @ApiOperation(value = "添加模型")
    @PutMapping
    @SysRequestLog(description="添加模型", actionType = ActionType.ADD)
    public Result add(@RequestBody BaseReportModel baseReportModel) throws IOException {
        if(StringUtils.isNotEmpty(baseReportModel.getSql())){
            boolean flag = ModelUtil.checkSql(baseReportModel.getSql());
            if(!flag){
                return new Result("1","sql存在非法字符串");

            }
        }
        baseReportModel.setId(UUID.randomUUID().toString().replaceAll("-",""));
        /*if(StringUtils.isNotEmpty(baseReportModel.getSql())){
            baseReportModel.setSql(new String(new BASE64Decoder().decodeBuffer(baseReportModel.getSql()),"utf-8"));
        }
        if(StringUtils.isNotEmpty(baseReportModel.getContent())){
            baseReportModel.setContent(new String(new BASE64Decoder().decodeBuffer(baseReportModel.getContent()),"utf-8"));
        }*/
        if(StringUtils.isNotEmpty(baseReportModel.getInterfaceId())){
            BaseReportInterface in = baseReportInterfaceService.findById(baseReportModel.getInterfaceId());
            baseReportModel.setParams(in.getParams());
        }
        int result = baseReportModelService.save(baseReportModel);
        if(result == 1){
            SyslogSenderUtils.sendAddSyslogAndTransferredField(baseReportModel,"添加模型",transferMap);
            return this.vData(baseReportModel);
        }
        return this.result( false);
    }

    /**
     * 修改模型
     */
    @ApiOperation(value = "修改模型")
    @PatchMapping
    @SysRequestLog(description="修改模型", actionType = ActionType.UPDATE)
    public Result edit(@RequestBody BaseReportModel baseReportModel) throws IOException {
        BaseReportModel baseReportModelSec = baseReportModelService.findById(baseReportModel.getId());
        /*if(StringUtils.isNotEmpty(baseReportModel.getSql())){
            baseReportModel.setSql(new String(new BASE64Decoder().decodeBuffer(baseReportModel.getSql()),"utf-8"));
        }
        if(StringUtils.isNotEmpty(baseReportModel.getContent())){
            baseReportModel.setContent(new String(new BASE64Decoder().decodeBuffer(baseReportModel.getContent()),"utf-8"));
        }*/
        if(StringUtils.isNotEmpty(baseReportModel.getSql())){
            boolean flag = ModelUtil.checkSql(baseReportModel.getSql());
            if(!flag){
                return new Result("1","sql存在非法字符串");

            }
        }
        if(StringUtils.isNotEmpty(baseReportModel.getInterfaceId())){
            BaseReportInterface in = baseReportInterfaceService.findById(baseReportModel.getInterfaceId());
            baseReportModel.setParams(in.getParams());
        }
        int result = baseReportModelService.updateSelective(baseReportModel);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(baseReportModelSec,baseReportModel,"修改模型",transferMap);
        }
        return this.result(result == 1);
    }

    @ApiOperation(value = "预览模型")
    @GetMapping(value ="/view")
    public String view(@RequestParam(required=false)Map<String, Object> params, HttpServletResponse response) throws Exception{
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        BaseReportModel model = new BaseReportModel();
        if(params.get("modelId") != null && StringUtils.isNotEmpty(params.get("modelId").toString())){
            model = baseReportModelService.findById(params.get("modelId").toString());
        }else{
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                BeanUtils.setProperty(model, entry.getKey(), entry.getValue());
            }
        }
        if(params.get("p") != null && StringUtils.isNotEmpty(params.get("p").toString())){
            Map<String,Object> map = (Map) JSON.parse(params.get("p").toString());
            model.setBindParam(map);
        }
        return ReportEngine.renderModel(model);
    }

    @ApiOperation(value = "预览模型")
    @PostMapping(value ="/view")
    public String viewPost(ReportModelParam params, HttpServletResponse response) throws Exception{
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        /*if(StringUtils.isNotEmpty(params.getSql())){
            params.setSql(new String(new BASE64Decoder().decodeBuffer(params.getSql()),"utf-8"));
        }
        if(StringUtils.isNotEmpty(params.getContent())){
            params.setContent(new String(new BASE64Decoder().decodeBuffer(params.getContent()),"utf-8"));
        }*/
        BaseReportModel model = new BaseReportModel();
        if(params != null && StringUtils.isNotEmpty(params.getModelId())){
            model = baseReportModelService.findById(params.getModelId());
        }else{
            BeanUtils.copyProperties(model,params);
        }
        if(params != null && StringUtils.isNotEmpty(params.getP())){
            Map<String,Object> map = (Map)JSON.parse(params.getP());
            model.setBindParam(map);
        }
        return ReportEngine.renderModel(model);
    }

    @ApiOperation(value = "模型详情")
    @GetMapping(value = "/{modelId}")
    public VData<BaseReportModel> detail(@PathVariable("modelId") String modelId){
        return this.vData( baseReportModelService.findById(modelId));
    }

    @ApiOperation(value = "删除模型")
    @DeleteMapping
    @SysRequestLog(description="删除模型", actionType = ActionType.DELETE)
    public Result deleteModel(@RequestBody DeleteQuery param) {
        List<BaseReportModel> reportModelList = baseReportModelService.findByids(  "'"+String.join("','",param.getIds().split(","))+"'");
        int result = baseReportModelService.batchDelete(param.getIds());
        if (result > 0) {
            reportModelList.forEach(baseReportModel -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(baseReportModel,"删除模型",transferMap);
            });
        }
        return this.result(result >= 1);
    }

    @PostMapping("/doInterface")
    public VData doInterface(String url,String params){
        Result s = new Result();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> formEntity = new HttpEntity<String>(params, headers);
        Object entity;
        //entity = param;//参数为表单提交用这个
        entity = formEntity;//参数为json格式用这个
        //restTemplate调用接口
        log.info("指标测试调用开始：url="+url);
        ResponseEntity<ReportResponseVo> responseEntity = restTemplate.postForEntity(url, entity, ReportResponseVo.class);
        //解析接口数据
        log.info("指标测试调用结束：result="+JSONObject.toJSONString(responseEntity));
        return this.vData(responseEntity);
    }

}
