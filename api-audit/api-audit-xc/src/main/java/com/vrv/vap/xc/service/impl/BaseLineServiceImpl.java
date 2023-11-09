package com.vrv.vap.xc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.plugin.util.QueryWrapperUtil;
import com.vrv.vap.toolkit.tools.LogAssistTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.constants.LineConstants;
import com.vrv.vap.xc.fegin.ApiDataClient;
import com.vrv.vap.xc.fegin.XcsClient;
import com.vrv.vap.xc.mapper.BaseLineResultMapper;
import com.vrv.vap.xc.mapper.BaseLineSpecialMapper;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.pojo.BaseLine;
import com.vrv.vap.xc.mapper.BaseLineMapper;
import com.vrv.vap.xc.pojo.BaseLineResult;
import com.vrv.vap.xc.pojo.BaseLineSpecial;
import com.vrv.vap.xc.schedule.TaskLoader;
import com.vrv.vap.xc.service.BaseLineService;
import com.vrv.vap.xc.service.CommonService;
import com.vrv.vap.xc.tools.CronUtils;
import com.vrv.vap.xc.tools.LineMessageTools;
import com.vrv.vap.xc.tools.TypeTools;
import com.vrv.vap.xc.vo.BaseLineQuery;
import com.vrv.vap.xc.vo.BaseResultQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BaseLineServiceImpl implements BaseLineService {
    @Autowired
    private BaseLineResultMapper baseLineResultMapper;
    @Autowired
    private BaseLineMapper baseLineMapper;
    @Autowired
    private BaseLineSpecialMapper baseLineSpecialMapper;
    @Autowired
    private CommonService commonService;
    @Autowired
    private XcsClient client;
    @Autowired
    private ApiDataClient dataClient;
    private Log log = LogFactory.getLog(BaseLineServiceImpl.class);
    private String LINE_TASK_CLASS = "com.vrv.vap.line.schedule.task.BaseLineTask";
    private String JOB_PRE = "baseLineTask-";
    private String FIELD_NAME = "暂无";
    private String SOURCE_NAME_END = "-*";
    private String TIME_FIELD = "insert_time";
    private String PRE_SM = "summary-";
    private String PRE_LINE = "base-line-";


    @Override
    public VData<BaseLine> add(BaseLine line) {
        if(StringUtils.isEmpty(line.getCron()) || !CronUtils.isValid(line.getCron())){
            log.info("校验cron表达式不合法");
            VData<BaseLine> vd = new VData(line);
            vd.setCode("500014");
            vd.setMessage("时间不合法");
            return vd;
        }
        if(LineConstants.LINE_TYPE.TS.equals(line.getType())){
            int cycle = CronUtils.getPeriodByCron(line.getCron());
            JSONObject jsonObject = JSONObject.parseObject(line.getSpecialParam());
            jsonObject.put("cycle",cycle);
            jsonObject.put("days",line.getDays());
            line.setSpecialParam(JSONObject.toJSONString(jsonObject));
            baseLineMapper.insert(line);
            return VoBuilder.vd(line);
        }
        if(LineConstants.SOURCE_TYPE.MYSQL.equals(line.getSourceType())){
            renderSaveConfig4Mysql(line);
        }else{
            renderSaveConfig4Es(line);
        }
        this.renderFields(line);
        line.setWorkStatus("2");
        line.setWorkMsg("基线未启用");
        baseLineMapper.insert(line);
        return VoBuilder.vd(line);
    }

    @Override
    public VData<BaseLine> update(BaseLine line) {
        BaseLine baseLine = this.baseLineMapper.selectById(line.getId());
        //日志审计
        if (baseLine != null) {
            //审计变化
            String changes = LogAssistTools.compareDesc(baseLine, line);
            line.setExtendDesc(changes);
        }
        /*
        if(LineConstants.LINE_TYPE.TS.equals(line.getType())){
            addSpecialTaskByStatus(line.getSpecialId(),line.getStatus());
            this.baseLineMapper.updateById(line);
            return VoBuilder.vd(line);
        }*/
        if(StringUtils.isNotEmpty(line.getCron()) && !CronUtils.isValid(line.getCron())){
            log.info("校验cron表达式不合法");
            VData<BaseLine> vd = new VData(line);
            vd.setCode("500014");
            vd.setMessage("时间不合法");
            return vd;
        }
        JobModel jobModel = new JobModel();
        jobModel.setJobName(JOB_PRE + line.getId());
        if(StringUtils.isNotEmpty(line.getCron())){
            jobModel.setCronTime(line.getCron());
        }else{
            jobModel.setCronTime(baseLine.getCron());
        }
        jobModel.setJobClazz(LINE_TASK_CLASS);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", line.getId());
        jobModel.setParams(param);
        client.removeTask(jobModel);

        if(!LineConstants.LINE_TYPE.TS.equals(baseLine.getType()) && !line.getSaveColumns().equals(baseLine.getSaveColumns())){
            this.renderFields(line);
        }
        if(LineConstants.LINE_STATUS.ENABLE.equals(line.getStatus())){
            client.addTask(jobModel);
            if(LineConstants.LINE_TYPE.TS.equals(baseLine.getType())){
                BaseLineSpecial special = baseLineSpecialMapper.selectById(baseLine.getSpecialId());
                if(LineConstants.SPECIAL_TYPE.ACTUAL.equals(special.getType())){
                    //实时类型需启动定期监测任务
                    JobModel specialJobModel = new JobModel();
                    specialJobModel.setJobName(JOB_PRE + special.getId());
                    specialJobModel.setCronTime(special.getActualCron());
                    specialJobModel.setJobClazz(special.getActualClass());
                    Map<String, Object> p = new HashMap<String, Object>();
                    p.put("id", line.getId());
                    specialJobModel.setParams(p);
                    client.addTask(specialJobModel);
                }
            }
            Map map = JSONObject.parseObject(baseLine.getFields(), Map.class);
            if(map != null){
                if(!sourceIsExists(PRE_SM+line.getSaveIndex()+SOURCE_NAME_END)){
                    JSONArray fields = JSONArray.parseArray(map.get("mediate").toString());
                    int sourceId = addSource2Data(PRE_SM + line.getSaveIndex(), line.getName() + "中间值表",line.getSaveType());
                    addField2Data(fields,sourceId);
                }
                if(!sourceIsExists(PRE_LINE+line.getSaveIndex()+SOURCE_NAME_END)){
                    JSONArray fields = JSONArray.parseArray(map.get("result").toString());
                    int sourceId = addSource2Data(PRE_LINE + line.getSaveIndex(), line.getName() + "结果表",line.getSaveType());
                    addField2Data(fields,sourceId);
                }
            }
        }
        //状态消息推送
        if(!baseLine.getStatus().equals(line.getStatus())){
            //状态变更推送消息
            new LineMessageTools().sendMessage(line,null);
            if(LineConstants.LINE_STATUS.ENABLE.equals(line.getStatus())){
                //启动
                line.setWorkStatus("1");
            }else{
                //停止
                line.setWorkStatus("2");
                line.setWorkMsg("基线未启用");
            }
        }
        this.baseLineMapper.updateById(line);
        return VoBuilder.vd(line);
    }


    public int addSource2Data(String index,String title,String saveType){
        Source source = new Source();
        source.setDataType(2);
        if(LineConstants.SAVE_TYPE.ES.equals(saveType) || LineConstants.SAVE_TYPE.ES_AND_KAFAK.equals(saveType)){
            source.setType(1);
            source.setName(index+SOURCE_NAME_END);
        }else{
            source.setType(2);
            source.setName(index.replaceAll("-","_"));
        }
        source.setTimeField(TIME_FIELD);
        source.setTitle(title);
        source.setTopicName(index);
        source.setTimeFormat(TimeTools.TIME_FMT_2);
        VData<Source> sourceVData = this.dataClient.addSource(source);
        return sourceVData.getData().getId();
    }

    public void addField2Data(JSONArray array,int sourceId){
        int i = 0;
        for(Object o : array){
            JSONObject c = (JSONObject)o;
            SourceField field = new SourceField();
            field.setField(c.getString("dest"));
            field.setOrigin(c.getString("type"));
            field.setType(c.getString("type"));
            field.setSourceId(sourceId);
            field.setName(StringUtils.isNotEmpty(c.getString("description")) ? c.getString("description") : FIELD_NAME);
            field.setAnalysisType(TypeTools.parseType(c.getString("type")));
            field.setAnalysisSort(i);
            field.setAnalysisTypeLength(field.getAnalysisType().length());
            this.dataClient.addField(field);
            i++;
        }
    }

    public boolean sourceIsExists(String name){
        boolean exists = false;
        SourceQuery query = new SourceQuery();
        query.setName(name);
        List<Source> sources = this.dataClient.querySource(query).getList();
        if(CollectionUtils.isNotEmpty(sources)){
            for(Source s : sources){
                if(name.equals(s.getName())){
                    exists = true;
                    break;
                }
            }
        }
        return exists;
    }
/*
    public void addSpecialTaskByStatus(int id,String status){
        BaseLineSpecial special = baseLineSpecialMapper.selectById(id);
        JobModel jobModel1 = new JobModel();
        jobModel1.setJobName(JOB_PRE + special.getFrequentClass());
        jobModel1.setCronTime(special.getFrequentCron());
        jobModel1.setJobClazz(special.getFrequentClass());

        JobModel jobModel2 = new JobModel();
        jobModel2.setJobName(JOB_PRE + special.getScoreClass());
        jobModel2.setCronTime(special.getScoreCron());
        jobModel2.setJobClazz(special.getScoreClass());
        client.removeTask(jobModel1);
        client.removeTask(jobModel2);
        if(LineConstants.LINE_STATUS.ENABLE.equals(status)){
            client.addTask(jobModel1);
            client.addTask(jobModel2);
        }

    }*/

    @Override
    public Result delete(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        list.forEach(e ->{
            JobModel jobModel = new JobModel();
            jobModel.setJobName(JOB_PRE + e);
            jobModel.setJobClazz(LINE_TASK_CLASS);
            TaskLoader.removeJob(jobModel);
        });
        int flag = this.baseLineMapper.deleteBatchIds(list);
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        return res;
    }

    @Override
    public VList<BaseLine> findAll(BaseLineQuery query) {
        Page<BaseLine> page = new Page<>(query.getCurrentPage(), query.getMyCount());
        QueryWrapper<BaseLine> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, query);
        return VoBuilder.vl(baseLineMapper.selectPage(page,queryWrapper));
    }

    @Override
    public List<BaseLine> findAll() {
        return baseLineMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public List<BaseLine> findAllEnable() {
        QueryWrapper<BaseLine> queryWrapper = new QueryWrapper<>();
        //queryWrapper.eq("status",LineConstants.LINE_STATUS.ENABLE);
        return baseLineMapper.selectList(queryWrapper);
    }

    @Override
    public List<BaseLine> selectByIds(String ids) {
        return baseLineMapper.selectBatchIds(Arrays.asList(ids.split(",")));
    }

    @Override
    public LineExportModel exportConfigs(String ids) {
        List<BaseLine> lines = this.selectByIds(ids);
        if(CollectionUtils.isEmpty(lines)){
            return null;
        }
        Set<String> sourceIds = new HashSet<>();
        lines.forEach(e ->{
            JSONArray cos = JSONArray.parseArray(e.getConfig());
            if(CollectionUtils.isEmpty(cos)){
                return; //indexId
            }
            Set<String> itemIds =  cos.stream().map(i ->{
                JSONObject o = (JSONObject)i;
                return o.getString("indexId");
            }).collect(Collectors.toSet());
            sourceIds.addAll(itemIds);
        });
        //List<BaseLineSource> sources = baseLineSourceMapper.selectBatchIds(sourceIds);
        //List<BaseLineSourceField> fields = baseLineSourceFieldService.findBySourceIds(sourceIds);
        return new LineExportModel(lines,null,null);
    }

    @Override
    public void importConfigs(LineExportModel model) {
        List<BaseLine> lines = model.getLines();
        if(CollectionUtils.isNotEmpty(lines)){
            this.baseLineMapper.saveBatch4List(lines);
        }
        /*
        List<BaseLineSource> sources = model.getSources();
        if(CollectionUtils.isNotEmpty(sources)){
            this.baseLineSourceMapper.saveBatch4List(sources);
        }
        List<BaseLineSourceField> fields = model.getFields();
        if(CollectionUtils.isNotEmpty(fields)){
            this.baseLineSourceFieldService.batchSave(fields);
        }*/

    }

    @Override
    public VList<BaseLineResult> findResult(BaseResultQuery query) {
        Page<BaseLineResult> page = new Page<>(query.getCurrentPage(), query.getMyCount());
        QueryWrapper<BaseLineResult> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, query);
        return VoBuilder.vl(this.baseLineResultMapper.selectPage(page,queryWrapper));
    }

    private void renderSaveConfig4Es(BaseLine line){
        List<LineSaveModel> models = new ArrayList<>();
        JSONArray array = JSONArray.parseArray(line.getConfig());
        JSONObject cols = (JSONObject)array.get(0);
        String column = cols.getString("column");
        int sourceId = Integer.parseInt(cols.getString("indexId"));
        Source data = dataClient.getSourceById(sourceId).getData();
        List<SourceField> fields = dataClient.getFields(sourceId).getData();
        Map<String,SourceField> fieldsMap = fields.stream().collect(Collectors.toMap(SourceField::getField,v -> v));
        if("无".equals(column) || StringUtils.isEmpty(column)){
            models.add(new LineSaveModel("count","count","long","数量"));
        }else{
            String columnType = StringUtils.isNotEmpty(fieldsMap.get(column).getType()) ? fieldsMap.get(column).getType() : "keyword";
            LineSaveModel mainModel = new LineSaveModel(line.getAlias(), line.getAlias(), columnType, line.getLabel(), "1", 1);
            mainModel.setMain(true);
            models.add(mainModel);
        }
        models.add(new LineSaveModel(line.getAlias()+"_doc_count",line.getAlias()+"_doc_count","long","数量"));
        JSONArray cas = JSONArray.parseArray(cols.getString("calculation"));
        if(CollectionUtils.isNotEmpty(cas)){
            cas.forEach(a ->{
                JSONObject c = (JSONObject)a;
                String col = c.getString("column");
                String algorithm = c.getString("algorithm");
                String alias = StringUtils.isNotEmpty(fieldsMap.get(col).getAlias()) ? fieldsMap.get(col).getAlias() : col;
                String type = StringUtils.isNotEmpty(fieldsMap.get(col).getType()) ? fieldsMap.get(col).getType() : "keyword";
                if(LineConstants.AGG_TYPE.TERMS.equals(algorithm)){
                    models.add(new LineSaveModel(alias,alias,type,null,"1",2));
                }else if(LineConstants.AGG_TYPE.TOP.equals(algorithm)){
                    models.add(new LineSaveModel(alias,alias,type,null,"5",null));
                }else{
                    models.add(new LineSaveModel(alias,alias,type,null));
                }
                if(LineConstants.AGG_TYPE.TERMS.equals(algorithm) || LineConstants.AGG_TYPE.DATA.equals(algorithm)){
                    models.add(new LineSaveModel(alias+"_doc_count",alias+"_doc_count","long","数量",true,true));
                }
            });
        }
        models.add(new LineSaveModel("insert_time","insert_time","date","入库时间"));
        models.add(new LineSaveModel("interval_num","interval_num","keyword","间隔",false));
        models.add(new LineSaveModel("start_time","start_time","date","开始时间",false));
        models.add(new LineSaveModel("end_time","end_time","date","结束时间",false));
        models.add(new LineSaveModel("data_time","data_time","keyword","数据日期",false));
        models.add(new LineSaveModel("summary_num","summary_num","long","统计次数",true));
        models.add(new LineSaveModel("process","process","float","进度",false));
        line.setSaveColumns(JSON.toJSONString(models));
    }

    private void renderSaveConfig4Mysql(BaseLine line){
        List<LineSaveModel> models = new ArrayList<>();
        JSONArray array = JSONArray.parseArray(line.getConfig());
        JSONObject cols = (JSONObject)array.get(0);
        String column = cols.getString("column");
        int sourceId = Integer.parseInt(cols.getString("indexId"));
        Source data = dataClient.getSourceById(sourceId).getData();
        List<SourceField> fields = dataClient.getFields(sourceId).getData();
        Map<String,SourceField> fieldsMap = fields.stream().collect(Collectors.toMap(SourceField::getField,v -> v));

        JSONArray ratio = JSONArray.parseArray(cols.getString("ratio"));
        JSONArray cas = JSONArray.parseArray(cols.getString("calculation"));
        if(CollectionUtils.isNotEmpty(cas)){
            cas.forEach(a ->{
                JSONObject o = (JSONObject) a;
                String col = o.getString("column");
                String algorithm = o.getString("algorithm");
                String alias = StringUtils.isNotEmpty(fieldsMap.get(col).getAlias()) ? fieldsMap.get(col).getAlias() : col;
                switch (algorithm) {
                    case LineConstants.AGG_TYPE.SUM:
                        models.add(new LineSaveModel("SUM("+col+")",col+"_sum","keyword",null));
                        break;
                    case LineConstants.AGG_TYPE.COUNT:
                        models.add(new LineSaveModel("COUNT("+col+")",col+"_count","keyword",null));
                        break;
                    case LineConstants.AGG_TYPE.AVG:
                        models.add(new LineSaveModel("AVG("+col+")",col+"_avg","keyword",null));
                        break;
                    case LineConstants.AGG_TYPE.TOP:
                        models.add(new LineSaveModel(alias,alias,"keyword",null));
                        break;
                    default:
                }
            });
        }
        if(CollectionUtils.isNotEmpty(ratio)){
            ratio.forEach(e ->{
                JSONObject o = (JSONObject) e;
                models.add(new LineSaveModel(o.getString("name"),o.getString("name"),"keyword",null));
            });
        }
        models.add(new LineSaveModel("insert_time","insert_time","date","入库时间"));
        models.add(new LineSaveModel("interval_num","interval_num","keyword","间隔",false));
        models.add(new LineSaveModel("start_time","start_time","date","开始时间",false));
        models.add(new LineSaveModel("end_time","end_time","date","结束时间",false));
        models.add(new LineSaveModel("summary_num","summary_num","long","统计次数",true));
        models.add(new LineSaveModel("process","process","float","进度",false));
        line.setSaveColumns(JSON.toJSONString(models));
    }

    public List<EsColumns> renderColumns4Mediate(BaseLine line){
        JSONArray array = JSONArray.parseArray(line.getSaveColumns());
        List<EsColumns> cols = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(array)) {
            array.forEach(e -> {
                JSONObject o = (JSONObject) e;
                boolean isMediate = o.getBooleanValue("mediate");
                if (isMediate) {
                    cols.add(new EsColumns(o.getString("dest"), o.getString("type"), StringUtils.isNotEmpty(o.getString("format")) ? o.getString("format") : null, o.getString("description")));
                }
            });
        }
        return cols;
    }

    private void renderFields(BaseLine line){
        List<JSONObject> mide = new ArrayList<>();
        List<JSONObject> result = new ArrayList<>();
        JSONArray array = JSONArray.parseArray(line.getSaveColumns());
        if(CollectionUtils.isNotEmpty(array)){
            array.forEach(e -> {
                JSONObject o = (JSONObject) e;
                JSONObject v = (JSONObject)o.clone();
                boolean isMediate = v.getBooleanValue("mediate");
                String aggType = o.getString("aggType");
                String dest = o.getString("dest");
                if (isMediate) {
                    mide.add(v);
                }
                if(StringUtils.isNotEmpty(aggType)){
                    switch (aggType) {
                        case LineConstants.AGG_TYPE.SUM:
                            o.replace("dest",dest+"_total");
                            o.replace("description","总值");
                            result.add(o);
                            break;
                        case LineConstants.AGG_TYPE.DEV:
                            JSONObject min = new JSONObject();
                            min.put("dest",dest+"_min");
                            min.put("type","double");
                            min.put("description","最小值");

                            JSONObject max = new JSONObject();
                            max.put("dest",dest+"_max");
                            max.put("type","double");
                            max.put("description","最大值");

                            JSONObject avg = new JSONObject();
                            avg.put("dest",dest+"_avg");
                            avg.put("type","double");
                            avg.put("description","均值");

                            JSONObject total = new JSONObject();
                            total.put("dest",dest+"_total");
                            total.put("type","double");
                            total.put("description","总值");
                            result.add(min);
                            result.add(max);
                            result.add(avg);
                            result.add(total);
                            break;
                        case LineConstants.AGG_TYPE.AVG:
                            o.replace("dest",dest+"_avg");
                            o.replace("description","均值");
                            result.add(o);
                            break;
                        case LineConstants.AGG_TYPE.DATA:
                            result.add(o);
                            JSONObject doc_count = new JSONObject();
                            doc_count.put("dest",dest+"_doc_count");
                            doc_count.put("type","long");
                            doc_count.put("description",o.getString("description")+"数量");
                            result.add(doc_count);
                            break;
                        default:
                            result.add(o);
                    }
                }
            });
        }
        result.addAll(commonFields());
        JSONObject j6 = new JSONObject();
        j6.put("dest","guid");
        j6.put("type","keyword");
        j6.put("description","guid");

        JSONObject j7 = new JSONObject();
        j7.put("dest","data_time");
        j7.put("type","keyword");
        j7.put("description","数据日期");

        mide.add(j6);
        mide.add(j7);
        Map<String,List<JSONObject>> fields = new HashMap<>();
        fields.put("mediate",mide);
        fields.put("result",result);
        line.setFields(JSONObject.toJSONString(fields));
    }

    private List<JSONObject> commonFields(){
        List<JSONObject> list = new ArrayList<>();
        JSONObject j1 = new JSONObject();
        j1.put("dest","type");
        j1.put("type","keyword");
        j1.put("description","类型");

        JSONObject j2 = new JSONObject();
        j2.put("dest","interval_num");
        j2.put("type","long");
        j2.put("description","时间间隔");

        JSONObject j3 = new JSONObject();
        j3.put("dest","insert_time");
        j3.put("type","date");
        j3.put("description","入库时间");

        JSONObject j4 = new JSONObject();
        j4.put("dest","start_time");
        j4.put("type","date");
        j4.put("description","开始时间");

        JSONObject j5 = new JSONObject();
        j5.put("dest","end_time");
        j5.put("type","date");
        j5.put("description","结束时间");

        JSONObject j6 = new JSONObject();
        j6.put("dest","guid");
        j6.put("type","keyword");
        j6.put("description","guid");

        JSONObject j7 = new JSONObject();
        j7.put("dest","data_time");
        j7.put("type","keyword");
        j7.put("description","数据日期");

        JSONObject j8 = new JSONObject();
        j8.put("dest","process");
        j8.put("type","float");
        j8.put("description","进度");
        list.add(j1);
        list.add(j2);
        list.add(j3);
        list.add(j4);
        list.add(j5);
        list.add(j6);
        list.add(j7);
        list.add(j8);
        return list;
    }

    public List<EsColumns> renderColumns4Result(BaseLine line){
        JSONArray array = JSONArray.parseArray(line.getSaveColumns());
        List<EsColumns> cols = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(array)) {
            array.forEach(e -> {
                JSONObject o = (JSONObject) e;
                cols.add(new EsColumns(o.getString("dest"), o.getString("type"), StringUtils.isNotEmpty(o.getString("format")) ? o.getString("format") : null, o.getString("description")));
            });
            EsColumns typeCols = new EsColumns("type", "keyword");
            typeCols.setTitle("基线类型");
            cols.add(typeCols);
        }
        return cols;
    }
}
