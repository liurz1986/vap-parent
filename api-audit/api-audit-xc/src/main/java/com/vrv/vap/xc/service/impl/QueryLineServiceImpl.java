package com.vrv.vap.xc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.mapper.BaseLineMapper;
import com.vrv.vap.xc.mapper.BehaviorAnalysisModelMapper;
import com.vrv.vap.xc.model.LineModel;
import com.vrv.vap.xc.model.ObjectAnalyseModel;
import com.vrv.vap.xc.model.PortraitModel;
import com.vrv.vap.xc.pojo.BaseLine;
import com.vrv.vap.xc.pojo.BehaviorAnalysisModel;
import com.vrv.vap.xc.service.QueryLineService;
import com.vrv.vap.xc.tools.TrendTools;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueryLineServiceImpl implements QueryLineService {

    @Autowired
    private BaseLineMapper baseLineMapper;
    @Autowired
    private BehaviorAnalysisModelMapper behaviorAnalysisModelMapper;

    private String LINE_PRE = "base_line_";
    private String SUM_PRE = "summary_";

    @Override
    public VData<Map<String, Object>> queryUseIp(ObjectAnalyseModel param) {
        LineModel lineModel = TrendTools.rendLineModel(param);
        lineModel.setFields("dev_ip");
        lineModel.setTable(LINE_PRE+"login_ip");
        lineModel.setWhere(" and user_no = '"+param.getUserAcount()+"'");
        List<Map<String, Object>> personLine = baseLineMapper.queryLineBysql(lineModel);
        lineModel.setType("0");
        List<Map<String, Object>> groupLine = baseLineMapper.queryLineBysql(lineModel);
        Map<String,Object> result = new HashMap<>();
        result.put("personLine",personLine);
        result.put("groupLine",groupLine);
        return VoBuilder.vd(result);
    }

    @Override
    public VData<Map<String, Object>> queryUseName(ObjectAnalyseModel param) {
        LineModel lineModel = TrendTools.rendLineModel(param);
        lineModel.setFields("username");
        lineModel.setTable(LINE_PRE+"login_user");
        lineModel.setWhere(" and user_no = '"+param.getUserAcount()+"'");
        List<Map<String, Object>> personLine = baseLineMapper.queryLineBysql(lineModel);
        lineModel.setType("0");
        List<Map<String, Object>> groupLine = baseLineMapper.queryLineBysql(lineModel);
        Map<String,Object> result = new HashMap<>();
        result.put("personLine",personLine);
        result.put("groupLine",groupLine);
        return VoBuilder.vd(result);
    }

    @Override
    public VData<Map<String, Object>> queryVisitAppIpAndAcount(ObjectAnalyseModel param) {
        LineModel lineModel = TrendTools.rendLineModel(param);
        lineModel.setFields("username,sip");
        lineModel.setTable(LINE_PRE+"ip_acount");
        lineModel.setWhere(" and user_no = '"+param.getUserAcount()+"'");
        List<Map<String, Object>> personLine = baseLineMapper.queryLineBysql(lineModel);
        lineModel.setType("0");
        List<Map<String, Object>> groupLine = baseLineMapper.queryLineBysql(lineModel);
        Map<String,Object> result = new HashMap<>();
        result.put("personLine",personLine);
        result.put("groupLine",groupLine);
        return VoBuilder.vd(result);
    }

    @Override
    public VData<Map<String, Object>> queryVisitAppNameIpSecret(ObjectAnalyseModel param) {
        LineModel lineModel = TrendTools.rendLineModel(param);
        lineModel.setFields("dst_std_sys_name,dip,dst_std_sys_secret_level");
        lineModel.setTable(LINE_PRE+"name_ip_secret");
        TrendTools.rendOrgDevRoleCondition(param,lineModel);
        List<Map<String, Object>> personLine = baseLineMapper.queryLineBysql(lineModel);
        lineModel.setType("0");
        List<Map<String, Object>> groupLine = baseLineMapper.queryLineBysql(lineModel);
        Map<String,Object> result = new HashMap<>();
        result.put("personLine",personLine);
        result.put("groupLine",groupLine);
        return VoBuilder.vd(result);
    }

    @Override
    public VData<Map<String, Object>> queryHistoryVisitAddress(ObjectAnalyseModel param) {
        LineModel lineModel = TrendTools.rendLineModel(param);
        lineModel.setFields("url");
        lineModel.setTable(LINE_PRE+"url");
        TrendTools.rendOrgDevRoleCondition(param,lineModel);
        List<Map<String, Object>> personLine = baseLineMapper.queryLineBysql(lineModel);
        lineModel.setType("0");
        List<Map<String, Object>> groupLine = baseLineMapper.queryLineBysql(lineModel);
        Map<String,Object> result = new HashMap<>();
        result.put("personLine",personLine);
        result.put("groupLine",groupLine);
        return VoBuilder.vd(result);
    }

    @Override
    public VData<Map<String, Object>> queryHistoryVisitProtoAndPort(ObjectAnalyseModel param) {
        LineModel lineModel = TrendTools.rendLineModel(param);
        lineModel.setFields("app_protocol,dport");
        lineModel.setTable(LINE_PRE+"protocol_port");
        TrendTools.rendOrgDevRoleCondition(param,lineModel);
        List<Map<String, Object>> personLine = baseLineMapper.queryLineBysql(lineModel);
        lineModel.setType("0");
        List<Map<String, Object>> groupLine = baseLineMapper.queryLineBysql(lineModel);
        Map<String,Object> result = new HashMap<>();
        result.put("personLine",personLine);
        result.put("groupLine",groupLine);
        return VoBuilder.vd(result);
    }

    @Override
    public VData<Map<String, Object>> queryFileLocalBusiness(ObjectAnalyseModel param) {
        LineModel lineModel = TrendTools.rendLineModel(param);
        lineModel.setFields("business_list,file_level");
        lineModel.setTable(LINE_PRE+"file_business_level");
        TrendTools.rendOrgDevRoleCondition(param,lineModel);
        List<Map<String, Object>> personLine = baseLineMapper.queryLineBysql(lineModel);
        lineModel.setType("0");
        List<Map<String, Object>> groupLine = baseLineMapper.queryLineBysql(lineModel);
        Map<String,Object> result = new HashMap<>();
        result.put("personLine",personLine);
        result.put("groupLine",groupLine);
        return VoBuilder.vd(result);
    }

    @Override
    public VData<Map<String, Object>> queryFileImportTrend(ObjectAnalyseModel param) {
        LineModel lineModel = TrendTools.rendLineModel(param);
        lineModel.setFields("count_avg");
        lineModel.setTable(LINE_PRE+"user_file");
        TrendTools.rendOrgDevRoleCondition(param,lineModel);
        if (param.getFileDir() != null) {
            String dir = " and file_dir = '"+param.getFileDir() + "'";
            lineModel.appendWhere(dir);
        }
        List<Map<String, Object>> personLine = baseLineMapper.queryLineBysql(lineModel);
        lineModel.setType("0");
        List<Map<String, Object>> groupLine = baseLineMapper.queryLineBysql(lineModel);
        Map<String,Object> result = new HashMap<>();
        result.put("personLine",personLine);
        result.put("groupLine",groupLine);
        return VoBuilder.vd(result);
    }

    @Override
    public VData<Map<String, Object>> queryFileExportTrend(ObjectAnalyseModel param) {
        //打印
        LineModel lineModel = TrendTools.rendLineModel(param);
        lineModel.setFields("count_avg");
        lineModel.setTable(LINE_PRE+"user_audit_file");
        TrendTools.rendOrgDevRoleCondition(param,lineModel);
        lineModel.appendWhere(" and op_type = '0'");
        List<Map<String, Object>> printPersonLine = baseLineMapper.queryLineBysql(lineModel);
        lineModel.setType("0");
        List<Map<String, Object>> printGroupLine = baseLineMapper.queryLineBysql(lineModel);

        //刻录
        TrendTools.rendOrgDevRoleCondition(param,lineModel);
        lineModel.appendWhere(" and op_type = '1'");
        List<Map<String, Object>> burnGroupLine = baseLineMapper.queryLineBysql(lineModel);
        lineModel.setType("1");
        List<Map<String, Object>> burnPersonLine = baseLineMapper.queryLineBysql(lineModel);
        Map<String,Object> result = new HashMap<>();
        result.put("printPersonLine",printPersonLine);
        result.put("printGroupLine" ,printGroupLine);
        result.put("burnPersonLine",burnPersonLine);
        result.put("burnGroupLine",burnGroupLine);
        return VoBuilder.vd(result);
    }

    @Override
    public VData<Map<String, Object>> generalAnalytics(PortraitModel model) {
        Map<String,Object> result = new HashMap<>();
        try{
            BehaviorAnalysisModel behaviorAnalysisModel = behaviorAnalysisModelMapper.selectById(model.getModelId());
            BaseLine baseLine = baseLineMapper.selectById(behaviorAnalysisModel.getBaseLineId());
            String tableEnd = baseLine.getSaveIndex().replaceAll("-","_");
            if(StringUtils.isNotEmpty(behaviorAnalysisModel.getConfig())){
                JSONObject jsonObject = JSONObject.parseObject(behaviorAnalysisModel.getConfig(), JSONObject.class);
                if(jsonObject.containsKey("line")){
                    JSONObject line = jsonObject.getJSONObject("line");
                    String where = line.getString("where");
                    if(StringUtils.isEmpty(where)){
                        where = "1=1";
                    }
                    LineModel lineModel = TrendTools.rendLineModel(model);
                    BeanUtils.copyProperties(model,lineModel);
                    lineModel.setFields(line.getString("fields"));
                    lineModel.setWhere(where);
                    lineModel.setTable(LINE_PRE+tableEnd);
                    lineModel.setGroup(line.getString("group"));
                    lineModel.setOrder(line.getString("order"));
                    renderParam(lineModel,model.getParams(),behaviorAnalysisModel);
                    List<Map<String, Object>> personLine = baseLineMapper.queryLineBysql(lineModel);
                    lineModel.setType("0");
                    lineModel.setUserNo(null);
                    lineModel.setOrgId(null);
                    lineModel.setSysId(null);
                    lineModel.setWhere(where);
                    renderGroupLineParam(lineModel,model.getParams(),behaviorAnalysisModel);
                    List<Map<String, Object>> groupLine = baseLineMapper.queryLineBysql(lineModel);
                    if(CollectionUtils.isNotEmpty(personLine)){
                        if(line.getBooleanValue("onlyGroup")){
                            result.put("groupLine",personLine);
                        }else{
                            result.put("personLine",personLine);
                        }
                    }
                    if(CollectionUtils.isNotEmpty(groupLine)){
                        result.put("groupLine",groupLine);
                    }
                }
                if(jsonObject.containsKey("summary")){
                    JSONObject summary = jsonObject.getJSONObject("summary");
                    String where = summary.getString("where");
                    if(StringUtils.isEmpty(where)){
                        where = "1=1";
                    }
                    LineModel lineModel = TrendTools.rendLineModel(model);
                    BeanUtils.copyProperties(model,lineModel);
                    lineModel.setFields(summary.getString("fields"));
                    lineModel.setWhere(where);
                    lineModel.setTable(SUM_PRE+tableEnd);
                    lineModel.setGroup(summary.getString("group"));
                    lineModel.setOrder(summary.getString("order"));
                    renderParam(lineModel,model.getParams(),behaviorAnalysisModel);
                    lineModel.setType(null);
                    List<Map<String, Object>> summaryDate = baseLineMapper.querySummaryBysql(lineModel);
                    if(CollectionUtils.isNotEmpty(summaryDate)){
                        result.put("summaryDate",summaryDate);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(result.size() == 0){
            result.put("summaryDate",new ArrayList<>());
        }
        return VoBuilder.vd(result);
    }

    public void renderParam(LineModel lineModel,Map<String,String> param,BehaviorAnalysisModel behaviorAnalysisModel){
        if(param == null || param.size() == 0){
            return;
        }
        String params = behaviorAnalysisModel.getParam();
        if(StringUtils.isEmpty(params)){
            return;
        }
        List<JSONObject> paramList = JSONArray.parseArray(params, JSONObject.class);
        paramList.forEach(p ->{
            String field = p.getString("field");
            String type = p.getString("type");
            String value = param.get(field);
            if(StringUtils.isEmpty(value)){
                return;
            }
            if("1".equals(type)){
                lineModel.appendWhere(" and "+field+"='"+value+"'");
            }else if("2".equals(type)){
                lineModel.appendWhere(" and "+field+"<>'"+value+"'");
            }
        });
    }

    public void renderGroupLineParam(LineModel lineModel,Map<String,String> param,BehaviorAnalysisModel behaviorAnalysisModel){
        if(param == null || param.size() == 0){
            return;
        }
        String params = behaviorAnalysisModel.getParam();
        if(StringUtils.isEmpty(params)){
            return;
        }
        List<JSONObject> paramList = JSONArray.parseArray(params, JSONObject.class);
        paramList.forEach(p ->{
            String field = p.getString("field");
            String type = p.getString("type");
            String main = p.getString("main");
            String value = param.get(field);
            if("true".equals(main)){
                //群体基线不加主体字段过滤
                return;
            }
            if(StringUtils.isEmpty(value)){
                return;
            }
            if("1".equals(type)){
                lineModel.appendWhere(" and "+field+"='"+value+"'");
            }else if("2".equals(type)){
                lineModel.appendWhere(" and "+field+"<>'"+value+"'");
            }
        });
    }
}
