package com.vrv.vap.line.statistics;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.mapper.PasswordKeyMapper;
import com.vrv.vap.line.mapper.PasswordRuleMapper;
import com.vrv.vap.line.mapper.StrategyConfigMapper;
import com.vrv.vap.line.model.EsQueryModel;
import com.vrv.vap.line.model.StrategyConfig;
import com.vrv.vap.line.pojo.PasswordKey;
import com.vrv.vap.line.pojo.PasswordRule;
import com.vrv.vap.line.tools.HTTPUtil;
import com.vrv.vap.line.tools.QueryTools;
import com.vrv.vap.line.tools.StrateyTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PlaintextPwdTask extends BaseStatisticsTask{
    private StrategyConfigMapper strategyConfigMapper = VapLineApplication.getApplicationContext().getBean(StrategyConfigMapper.class);
    private PasswordKeyMapper passwordKeyMapper = VapLineApplication.getApplicationContext().getBean(PasswordKeyMapper.class);
    private PasswordRuleMapper passwordRuleMapper = VapLineApplication.getApplicationContext().getBean(PasswordRuleMapper.class);
    private static Environment env = VapLineApplication.getApplicationContext().getBean(Environment.class);
    private String VERIFY_URL = env.getProperty("password.url");
    private String INDEX = "netflow-http";
    private String LOGIN_INDEX="netflow-login";
    private String PWD = "passwd";
    private String PWD_FIELD = "url";
    private List<String> KEY_LIST = new ArrayList<>();
    private List<PasswordRule> RULE_LIST = new ArrayList<>();

    public void initData(){
        List<PasswordKey> keys = passwordKeyMapper.selectList(new QueryWrapper<>());
        if(CollectionUtils.isNotEmpty(keys)){
            KEY_LIST = keys.stream().map(r -> r.getKey()).collect(Collectors.toList());
        }
        QueryWrapper<PasswordRule> passwordRuleQueryWrapper = new QueryWrapper<>();
        passwordRuleQueryWrapper.orderByAsc("sort");
        RULE_LIST = passwordRuleMapper.selectList(passwordRuleQueryWrapper);
    }


    @Override
    public void execute(Map<String, Object> params) {
        initData();
        StrateyTools strateyTools = new StrateyTools();
        int id = Integer.parseInt(params.get("id").toString());
        StrategyConfig sconfig = strategyConfigMapper.selectById(id);
        //添加netflow-login口令明文
        doLoginRule(strateyTools,sconfig);
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, INDEX.split(","), "event_time", TimeTools.TIME_FMT_1, 1);
        SearchResponse searchResponse = null;
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.existsQuery(PWD_FIELD));
        query.mustNot(QueryBuilders.termQuery(PWD_FIELD,""));
        queryModel.setQueryBuilder(query);
        while (true) {
            searchResponse = wrapper.scrollQuery(queryModel, searchResponse == null ? null : searchResponse.getScrollId());
            SearchHits hits = searchResponse.getHits();
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits(), "event_time");
            if (hits.getHits() == null || hits.getHits().length == 0) {
                break;
            }
            for(Map<String, String> m : list){
                String reqBody = m.get(PWD_FIELD);
                if(StringUtils.isNotEmpty(reqBody)){
                    //提取口令
                    String password = "";
                    for(String pwd : KEY_LIST){
                        int sindex = reqBody.indexOf(pwd);
                        if(sindex > -1){
                            String p = extractPasswordByRule(pwd, reqBody);
                            if(StringUtils.isNotEmpty(p)){
                                password = p;
                                break;
                            }
                        }
                    }
                    //调接口判断密码是否明文
                    if(StringUtils.isNotEmpty(password)){
                        List<String> pwds = new ArrayList<>();
                        pwds.add(password);
                        int flag = plainTextVerify(pwds);
                        if(flag == 1){
                            //明文产生告警
                            strateyTools.push2alarm(sconfig.getRuleCode(),m,null,"netflow",reqBody);
                            //发送事件对象

                        }
                    }
                }
            }
        }

    }

    private void doLoginRule(StrateyTools strateyTools, StrategyConfig sconfig) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, LOGIN_INDEX.split(","), "event_time", TimeTools.TIME_FMT_1, 1);
        SearchResponse searchResponse = null;
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.existsQuery(PWD));
        query.mustNot(QueryBuilders.termQuery(PWD,""));
        queryModel.setQueryBuilder(query);
        while (true) {
            searchResponse = wrapper.scrollQuery(queryModel, searchResponse == null ? null : searchResponse.getScrollId());
            SearchHits hits = searchResponse.getHits();
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits(), "event_time");
            if (hits.getHits() == null || hits.getHits().length == 0) {
                break;
            }
            for(Map<String, String> m : list){
                String password = m.get(PWD);
                    //调接口判断密码是否明文
                    if(StringUtils.isNotEmpty(password)){
                        List<String> pwds = new ArrayList<>();
                        pwds.add(password);
                        int flag = plainTextVerify(pwds);
                        if(flag == 1){
                            //明文产生告警.发送事件对象
                            strateyTools.push2alarm(sconfig.getRuleCode(),m,null,"netflow",password);

                        }
                    }
                }
            }
    }

    public int plainTextVerify(List<String> password){
        int res = 2;
        //String url = "http://192.168.119.96:9877/api-ml/sp/pwdPlaintextDetection";
        try{
            String result = HTTPUtil.POST(VERIFY_URL, generateHeaders(), JSONObject.toJSONString(password));
            if(StringUtils.isNotEmpty(result)){
                List<List<BigDecimal>> list = (List<List<BigDecimal>>)JSONObject.parseObject(result, List.class);
                if(CollectionUtils.isNotEmpty(list)){
                    res = list.get(0).get(0).intValue();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    private Map<String,String> generateHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }

    public String extractPassword(String keyword,String text){
        int sindex = text.indexOf(keyword);
        String nextChar = text.substring(sindex+keyword.length(),sindex+keyword.length()+1);
        String password = "";
        switch (nextChar)
        {
            case "=": //FORM
                String netText = text.substring(sindex,text.length());
                int i = netText.indexOf("&");
                if(i > -1){
                    password = netText.substring(keyword.length()+1,i);
                }else{
                    password = netText.substring(keyword.length()+1,netText.length());
                }
                break;
            case "\""://JSON
                String netText2 = text.substring(sindex+keyword.length()+1,text.length());
                int i2 = netText2.indexOf("\"");
                if(i2 > -1){
                    netText2 = netText2.substring(i2+1,netText2.length());
                    int i3 = netText2.indexOf("\"");
                    if(i3 > -1){
                        password = netText2.substring(0,i3);
                    }
                }
                break;
            case ">"://xml
                String netText3 = text.substring(sindex+keyword.length()+1,text.length());
                int i4 = netText3.indexOf("<");
                if(i4 > -1){
                    password = netText3.substring(0,i4);
                }
                break;
            default:
        }
        return password;
    }

    public String extractPasswordByRule(String keyword,String text){
        String result = "";
        if(CollectionUtils.isNotEmpty(RULE_LIST)){
            for(PasswordRule rule : RULE_LIST){
                String reg = rule.getRegRule().replaceAll("password",keyword);
                Pattern p3 = Pattern.compile(reg);
                Matcher m3 = p3.matcher(text);
                if(m3.find()){
                    result = m3.group();
                    break;
                }
            }
        }
        return result;
    }

    public EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, String[] indexs, String time, String format, int day) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(TimeTools.getNowBeforeByDay(day));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(day));
        List<String> indexList = wrapper.getIndexNames(indexs, queryModel.getStartTime(), queryModel.getEndTime());
        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setCount(10000);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

}
