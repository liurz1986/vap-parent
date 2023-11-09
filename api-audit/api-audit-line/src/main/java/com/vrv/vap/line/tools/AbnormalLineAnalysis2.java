package com.vrv.vap.line.tools;

import avro.shaded.com.google.common.collect.Lists;
import com.vrv.vap.line.mapper.*;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.model.*;
import com.vrv.vap.line.service.BaseLineFrequentAttrService;
import com.vrv.vap.line.service.BaseLineFrequentService;
import com.vrv.vap.line.service.CommonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.types.Row;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import java.util.*;
import java.util.stream.Collectors;

public class AbnormalLineAnalysis2 {

    private BaseLineFrequentMapper baseLineFrequentMapper = VapLineApplication.getApplicationContext().getBean(BaseLineFrequentMapper.class);
    private CommonService commonService = VapLineApplication.getApplicationContext().getBean(CommonService.class);
    private BaseLineScoreMapper baseLineScoreMapper = VapLineApplication.getApplicationContext().getBean(BaseLineScoreMapper.class);
    private BaseLineFrequentAttrMapper baseLineFrequentAttrMapper = VapLineApplication.getApplicationContext().getBean(BaseLineFrequentAttrMapper.class);
    private BaseLineFrequentOrgMapper baseLineFrequentOrgMapper = VapLineApplication.getApplicationContext().getBean(BaseLineFrequentOrgMapper.class);
    private BaseLineFrequentRoleMapper baseLineFrequentRoleMapper = VapLineApplication.getApplicationContext().getBean(BaseLineFrequentRoleMapper.class);
    private BaseLineFrequentService baseLineFrequentService = VapLineApplication.getApplicationContext().getBean(BaseLineFrequentService.class);
    private BaseLineFrequentAttrService baseLineFrequentAttrService = VapLineApplication.getApplicationContext().getBean(BaseLineFrequentAttrService.class);
    private int timeSplit = 3;//切分时间间隔 单位 s
    private int towLevelTimeSplit = 10;//二级切分时间间隔 单位 min
    private int days = 60;//处理数据天数
    private int BATCH = 1000;//单次入库量
    private String index = "netflow-http-2022";//处理数据天数
    private String timeField = "event_time";//处理数据天数
    private String urlField = "url";//主体字段
    private String separator = ",";//项分隔符
    private String userField = "sip";//项分隔符
    private String pckField = "content_length";//项分隔符
    private Integer count = 10000;//项分隔符
    //    private String start = "2022-05-27 00:00:00";
    private String resultIndex = "base-line-sequence";
    private final String indexSufFormat = "-yyyy";
    private String itemSeparator = "#";//项集分隔符
    /**
     * 频繁项挖掘
     * @return
     */
    public void analysis(){
        Date now = new Date();
        //读取日志数据
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        //查询用户
        EsQueryModel userModel = buildQueryModel2(wrapper, index, timeField,TimeTools.TIME_FMT_1,days);

        BoolQueryBuilder aggquery = new BoolQueryBuilder();
        aggquery.mustNot(QueryBuilders.termQuery(userField,""));
        aggquery.mustNot(QueryBuilders.termQuery(urlField,""));
        aggquery.must(QueryBuilders.termQuery("host","192.168.119.209"));
        aggquery.must(QueryBuilders.wildcardQuery("url","/IMS/*"));
        userModel.setQueryBuilder(aggquery);

        //userModel.setStartTime(TimeTools.parseDate2(start));
        List<Map<String, Object>> users = QueryTools.simpleAgg(userModel, wrapper, userField, 10000, "userId", "count");
//        List<Map<String, Object>> users = new ArrayList<>();
//        Map<String,Object> mk = new HashMap<>();
//        mk.put("userId","192.168.119.119");
//        users.add(mk);
        for(Map<String, Object> map : users){
            List<Map<String,String>> srcMaps = new ArrayList<>();
            List<BaseLineFrequent> frequents = new ArrayList<>();
            List<BaseLineFrequentAttr> attrs = new ArrayList<>();
            String userId = map.get("userId") != null ? map.get("userId").toString() : "";
            BoolQueryBuilder query = new BoolQueryBuilder();
            query.must(QueryBuilders.termQuery(userField,userId));
            query.mustNot(QueryBuilders.termQuery(urlField,""));
            query.must(QueryBuilders.termQuery("host","192.168.119.209"));
            query.must(QueryBuilders.wildcardQuery("url","/IMS/*"));
            final EsQueryModel queryModel = buildQueryModel2(wrapper, index, timeField,TimeTools.TIME_FMT_1,days);
            queryModel.setQueryBuilder(query);
            SearchResponse searchResponse = null;
            List<List<Map<String, String>>> towSplitDatas = new ArrayList<>();
            List<Map<String, String>> itemList = new ArrayList<>();
            Date startTime = null;
            Date endTime = null;
            String org = "";
            String role = "";
            while (true) {
                searchResponse = wrapper.scrollQuery(queryModel, searchResponse == null ? null : searchResponse.getScrollId());
                SearchHits hits = searchResponse.getHits();
                List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits(), timeField);
                if (hits.getHits() == null || hits.getHits().length == 0) {
                    break;
                }
                if(CollectionUtils.isNotEmpty(list)){
                    if(StringUtils.isEmpty(org)){
                        org = list.get(0).get(LineConstants.SQ.orgField);
                    }
                    if(StringUtils.isEmpty(role)){
                        role = list.get(0).get(LineConstants.SQ.roleField);
                    }
                    srcMaps.addAll(StringUtil.clearMaps(list));
                }
            }
            SplitTools tool = new SplitTools(userId);
            List<String> urls = tool.splitAndCompressWithAttr(srcMaps, attrs);
            //先验算法计算频繁项
//            Apriori apriori = new Apriori(urls);
//            Map<List<StringUtilsring>,Integer> frequentCollectionMap = apriori.getFC();
            List<Row> rows = AlinkTools.apriori4Alink(urls);
            StringBuffer urlStr = new StringBuffer();
            for(String url : urls){
                urlStr.append(url).append(itemSeparator);
            }
            List<String> frees = new ArrayList<>();
            rows.parallelStream().forEach((r) ->{
                String item = r.getField(0).toString();
                if(urlStr.toString().indexOf(item) > -1){
                    frees.add(item);
                    BaseLineFrequent record = new BaseLineFrequent();
                    record.setUserId(userId);
                    record.setFrequents(item);
                    record.setCount(Integer.parseInt(r.getField(1).toString()));
                    record.setIsContinue(LineConstants.CONTINUE.YES);
                    record.setTime(now);
                    record.setType("1");
                    frequents.add(record);
                }
            });
            if(CollectionUtils.isNotEmpty(attrs)){
                // 统计结果分批入库
                Lists.partition(attrs, BATCH).forEach(l -> baseLineFrequentAttrMapper.saveBatch4List(l));
            }

            if(CollectionUtils.isNotEmpty(frequents)){
                //更新部门频繁序列
                updateOrgQuents(new ArrayList<>(frees),org);
                //更新角色频繁序列
                updateRoleQuents(new ArrayList<>(frees),role);
                // 统计结果分批入库
                BaseLineFrequent freq = this.baseLineFrequentService.findByUser(userId);
                if(freq != null){
                    frees.add(freq.getFrequents());
                }else{
                    freq = new BaseLineFrequent();
                    freq.setUserId(userId);
                    freq.setCount(1);
                    freq.setIsContinue(LineConstants.CONTINUE.YES);
                    freq.setType("2");
                    freq.setRole(role);
                    freq.setOrg(org);
                    freq.setTime(now);
                }
                Collections.sort(frees, Comparator.comparingInt(i -> i.toString().length()).reversed());
                StringBuffer temStr = new StringBuffer();
                for(String s : frees){
                    if(temStr.toString().indexOf(s) == -1){
                        if(StringUtils.isNotEmpty(temStr)){
                            temStr.append(LineConstants.SQ.itemSeparator);
                        }
                        temStr.append(s);
                    }
                }
                freq.setFrequents(temStr.toString());
                this.baseLineFrequentService.updateFrequent(freq);
                //Lists.partition(frequents, BATCH).forEach(l -> baseLineFrequentMapper.saveBatch4List(l));
            }
        }
    }

    public void updateOrgQuents(List<String> fress,String org){
        if(StringUtils.isEmpty(org)){
            return;
        }
        Date now = new Date();
        BaseLineFrequentOrg baseLineFrequentOrg = this.baseLineFrequentOrgMapper.selectById(org);
        if(baseLineFrequentOrg != null){
            fress.addAll(Arrays.asList(baseLineFrequentOrg.getFrequents().split(LineConstants.SQ.itemSeparator)));
        }
        Collections.sort(fress, Comparator.comparingInt(i -> i.toString().length()).reversed());
        StringBuffer temStr = new StringBuffer();
        for(String s : fress){
            if(temStr.toString().indexOf(s) == -1){
                if(StringUtils.isNotEmpty(temStr)){
                    temStr.append(LineConstants.SQ.itemSeparator);
                }
                temStr.append(s);
            }
        }
        if(baseLineFrequentOrg != null){
            baseLineFrequentOrg.setFrequents(temStr.toString());
            baseLineFrequentOrg.setUpdateTime(now);
            this.baseLineFrequentOrgMapper.updateById(baseLineFrequentOrg);
        }else{
            baseLineFrequentOrg = new BaseLineFrequentOrg(org,temStr.toString(),now);
            this.baseLineFrequentOrgMapper.insert(baseLineFrequentOrg);
        }
    }

    public void updateRoleQuents(List<String> fress,String role){
        if(StringUtils.isEmpty(role)){
            return;
        }
        Date now = new Date();
        BaseLineFrequentRole baseLineFrequentRole = this.baseLineFrequentRoleMapper.selectById(role);
        if(baseLineFrequentRole != null){
            fress.addAll(Arrays.asList(baseLineFrequentRole.getFrequents().split(LineConstants.SQ.itemSeparator)));
        }
        Collections.sort(fress, Comparator.comparingInt(i -> i.toString().length()).reversed());
        StringBuffer temStr = new StringBuffer();
        for(String s : fress){
            if(temStr.toString().indexOf(s) == -1){
                if(StringUtils.isNotEmpty(temStr)){
                    temStr.append(LineConstants.SQ.itemSeparator);
                }
                temStr.append(s);
            }
        }
        if(baseLineFrequentRole != null){
            baseLineFrequentRole.setFrequents(temStr.toString());
            baseLineFrequentRole.setUpdateTime(now);
            this.baseLineFrequentRoleMapper.updateById(baseLineFrequentRole);
        }else{
            baseLineFrequentRole = new BaseLineFrequentRole(role,temStr.toString(),now);
            this.baseLineFrequentRoleMapper.insert(baseLineFrequentRole);
        }
    }

    public void renderData2(){
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        //查询用户
        List<BaseLineScore> scores = new ArrayList<>();
        EsQueryModel userModel = buildQueryModel2(wrapper, index, timeField,TimeTools.TIME_FMT_1,days);

        BoolQueryBuilder aggquery = new BoolQueryBuilder();
        aggquery.mustNot(QueryBuilders.termQuery(userField,""));
        aggquery.mustNot(QueryBuilders.termQuery(urlField,""));
        aggquery.must(QueryBuilders.termQuery("host","192.168.119.209"));
        aggquery.must(QueryBuilders.wildcardQuery("url","/IMS/*"));
        userModel.setQueryBuilder(aggquery);

        List<Map<String, Object>> users = QueryTools.simpleAgg(userModel, wrapper, userField, 10000, "userId", "count");
//        List<Map<String, Object>> users = new ArrayList<>();
//        userModel.setStartTime(TimeTools.parseDate2(start));
//        Map<String,Object> mk = new HashMap<>();
//        mk.put("userId","192.168.119.119");
//        users.add(mk);
        for(Map<String, Object> map : users){
            String userId = map.get("userId") != null ? map.get("userId").toString() : "";
            //查询url数据并按时间切分
            List<Map<String,String>> srcMaps = new ArrayList<>();
            BoolQueryBuilder query = new BoolQueryBuilder();
            query.must(QueryBuilders.termQuery(userField,userId));
            query.mustNot(QueryBuilders.termQuery(urlField,""));
            query.must(QueryBuilders.termQuery("host","192.168.119.209"));
            query.must(QueryBuilders.wildcardQuery("url","/IMS/*"));
            final EsQueryModel queryModel = buildQueryModel2(wrapper, index, timeField,TimeTools.TIME_FMT_1,days);
            queryModel.setQueryBuilder(query);
            SearchResponse searchResponse = null;
            List<List<Map<String, String>>> towSplitDatas = new ArrayList<>();
            List<Map<String, String>> itemList = new ArrayList<>();
            Date startTime = null;
            Date endTime = null;
            String org = "";
            String role = "";
            while (true) {
                searchResponse = wrapper.scrollQuery(queryModel, searchResponse == null ? null : searchResponse.getScrollId());
                SearchHits hits = searchResponse.getHits();
                List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits(), timeField);
                if (hits.getHits() == null || hits.getHits().length == 0) {
                    break;
                }
                if(CollectionUtils.isNotEmpty(list)){
                    srcMaps.addAll(StringUtil.clearMaps(list));
                    if(StringUtils.isEmpty(org)){
                        org = list.get(0).get(LineConstants.SQ.orgField);
                    }
                    if(StringUtils.isEmpty(role)){
                        role = list.get(0).get(LineConstants.SQ.roleField);
                    }
                }
            }
            SplitTools tools = new SplitTools(userId);
            List<List<List<Map<String, String>>>> urls = tools.towLevelSplit(srcMaps);
            for(List<List<Map<String, String>>> l : urls){
                if(CollectionUtils.isNotEmpty(l)){
                    BaseLineScore baseLineScore = buildItemData(l,userId,org,role);
                    if(baseLineScore != null){
                        scores.add(baseLineScore);
                    }
                }
            }
        }
        Date yesterday = TimeTools.getNowBeforeByDay(1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        String index = resultIndex + TimeTools.format(yesterday, indexSufFormat);
        QueryTools.QueryWrapper rwrapper = QueryTools.build();
        /*if(!commonService.indexTemplateExists(resultIndex)){
            //创建索引模板
            createEsTem();
        }
        try{
            commonService.create365Alias(resultIndex+TimeTools.format(yesterday, indexSufFormat), resultIndex+"-", "time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(scores, index, rwrapper);
        }catch (Exception e){
            e.printStackTrace();
        }*/
        Lists.partition(scores, BATCH).forEach(l -> baseLineScoreMapper.saveBatch4List(l));
    }

    public void createEsTem(){
        List<EsColumns> cols = new ArrayList<>();
        cols.add(new EsColumns("similarityScore","double"));
        cols.add(new EsColumns("frequentId","keyword"));
        cols.add(new EsColumns("hourScore","double"));
        cols.add(new EsColumns("packgeScore","double"));
        cols.add(new EsColumns("compress","keyword"));
        cols.add(new EsColumns("userKey","keyword"));
        cols.add(new EsColumns("time","date"));
        commonService.createTemplate(new EsTemplate(resultIndex, cols));
    }

    private BaseLineScore buildItemData(List<List<Map<String,String>>> items,String key,String org,String role){
        String startTime = items.get(0).get(0).get(LineConstants.SQ.timeField).toString();
        List<Map<String, String>> maps = items.get(items.size() - 1);
        String endTime = maps.get(maps.size()-1).get(LineConstants.SQ.timeField).toString();
        BaseLineScore score = new BaseLineScore();
        score.setStartTime(startTime);
        score.setEndTime(endTime);
        //查询用户频繁项
        BaseLineFrequent userFrequent = baseLineFrequentService.findByUser(key);
        BaseLineFrequentOrg orgFrequent = null;
        if(StringUtils.isNotEmpty(org)){
            orgFrequent = this.baseLineFrequentOrgMapper.selectById(org);
        }
        BaseLineFrequentRole roleFrequent = null;
        if(StringUtils.isNotEmpty(role)){
            roleFrequent = this.baseLineFrequentRoleMapper.selectById(role);
        }
        roleFrequent = this.baseLineFrequentRoleMapper.selectById(role);
        //计算相似度得分
        Integer frequentId = null;
        float similarity = 0f;
        Map<String, Object> itemMap = renderItem(items);
        String md5  = itemMap.get("md5").toString();
        List<String> frequents = new ArrayList<>();
        if(userFrequent != null){
            similarity = SimilarityHelper.GetMaxSubStr(userFrequent.getFrequents(), md5);
            for(String s : md5.split(separator)){
                if(userFrequent.getFrequents().indexOf(s) > -1){
                    frequents.add(s);
                }
            }
            frequentId = userFrequent.getId();
        }
        if(orgFrequent != null){
            score.setSimilarityScoreOrg(SimilarityHelper.GetMaxSubStr(orgFrequent.getFrequents(), md5));
        }
        if(roleFrequent != null){
            score.setSimilarityScoreRole(SimilarityHelper.GetMaxSubStr(roleFrequent.getFrequents(), md5));
        }
        //计算时间维度得分
        float[] attrScores = doAttrScore((Integer) itemMap.get("avgHour"),(Float)(itemMap.get("pckAll")), frequents,key);
        score.setCompress(itemMap.get("md5").toString());
        score.setFrequentId(frequentId);
        score.setSimilarityScore(similarity);
        score.setHourScore(attrScores[0]);
        score.setPackgeScore(attrScores[1]);
        score.setUserKey(key);
        score.setType("2");
        score.setTime(TimeTools.format(TimeTools.gmtToUtcTime(new Date()),TimeTools.TIME_FMT_1));
        return score;
    }

    private float[] doAttrScore(int hour,float pck,List<String> frequents,String userId){
        if(CollectionUtils.isEmpty(frequents)){
            return new float[]{0,0};
        }
        List<BaseLineFrequentAttr> attrs = baseLineFrequentAttrService.findByFrequents(frequents,userId);
        double[] hds = new double[attrs.size()];
        double[] pds = new double[attrs.size()];
        float hourScore = 0;
        float pckScore = 0;
        int i = 0;
        for(BaseLineFrequentAttr attr : attrs){
            hds[i] = Double.valueOf(attr.getHour());
            pds[i] = Double.valueOf(attr.getPck());
            i++;
        }
        if(hds.length > 1){
            hourScore = StandardTool.standardValue(hds,hour);
        }else if(hds.length == 1){
            hourScore = StandardTool.defaultValue(hour,hds[0]);
        }
        if(pds.length > 1){
            pckScore = StandardTool.standardValue(pds,pck);
        }else if(pds.length == 1){
            pckScore = StandardTool.defaultValue(pck,pds[0]);
        }
        return new float[]{hourScore,pckScore};
    }

    /**
     * 计算序列属性
     * @param items
     * @return
     */
    private Map<String,Object> renderItem(List<List<Map<String,String>>> items){
        Map<String,Object> result = new HashMap<>();
        StringBuffer str = new StringBuffer();
        int hourTotal = 0;
        float pckAll = 0;
        int size = 0;
        for(List<Map<String,String>> list : items){
            for(Map<String,String> m : list){
                int h = Integer.parseInt(TimeTools.format(TimeTools.parseDate(m.get(LineConstants.SQ.timeField)),"HH"));
                hourTotal = hourTotal+h;
                //累计包大小
                Object pck = m.get(pckField);
                if(pck != null && StringUtils.isNotEmpty(pck.toString())){
                    pckAll += Float.valueOf(m.get(pckField).toString());
                }
                size += 1;
            }
            String md5 = StringUtil.compressList(list);
            str.append(md5).append(separator);
        }
        int avgHour = Math.round(hourTotal/size);
        result.put("md5",str.toString().substring(0,str.length()-1));
        result.put("avgHour",avgHour);
        result.put("pckAll",pckAll);
        return result;
    }

    private void safe2List(StringBuffer item,List<String> tem){
        if(CollectionUtils.isNotEmpty(tem)){
            if(item.length() != 0){
                item.append(separator);
            }
            item.append(compressList2Str(tem));
        }
    }

    private void safe2List(StringBuffer item,List<Map<String,String>> tem,List<BaseLineFrequentAttr> attrs){
        if(CollectionUtils.isNotEmpty(tem)){
            if(item.length() != 0){
                item.append(separator);
            }
            StringBuffer str = new StringBuffer();
            int allhr = 0;
            float pckall = 0;
            for(Map<String,String> map : tem){
                str.append(filterParam(map.get(urlField)));
                //频繁项属性计算
                int hr = Integer.parseInt(TimeTools.format(TimeTools.parseDate(map.get(timeField), TimeTools.TIME_FMT_2),"HH"));
                allhr = allhr+hr;
                String pck = map.get(pckField);
                if(StringUtils.isNotEmpty(pck)){
                    pckall += Float.valueOf(pck);
                }
            }
            String id = Base64Util.compressString(StringUtil.sort(str.toString()));
            int hour = Math.round(allhr/tem.size());
            //attrs.add(new BaseLineFrequentAttr(id,hour,pckall));
            item.append(id);
        }
    }

    private String filterParam(String url){
        int flag = url.indexOf("?");
        if(flag == -1){
            return url;
        }else{
            return url.substring(0,flag);
        }
    }

    private String list2string(List<String> list){
        StringBuffer s = new StringBuffer();
        list.forEach(t ->{
            s.append(t).append(separator);
        });
        return s.toString().substring(0,s.length()-1);
    }

    private String compressList2Str(List<String> list){
        StringBuffer str = new StringBuffer();
        list.forEach(s ->{
            str.append(s);
        });
        return Base64Util.compressString(str.toString());
    }

    private Date addTime(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND,timeSplit);
        return cal.getTime();
    }

    private Date addTowLevelTime(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE,towLevelTimeSplit);
        return cal.getTime();
    }


    private EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, String index, String time,String format,Date startTime,Date endTime) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(startTime);
        queryModel.setEndTime(endTime);
        /*List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());
        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }*/
        queryModel.setIndexName(index);
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTimeFormat(format);
        queryModel.setNeedTimeFormat(true);
        queryModel.setSort(true);
        queryModel.setSortFields(new String[]{time});
        queryModel.setSortOrder(SortOrder.ASC);
        queryModel.setCount(count);
        queryModel.setStart(0);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    private EsQueryModel buildQueryModel2(QueryTools.QueryWrapper wrapper, String index, String time,String format,Integer day) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(TimeTools.getNowBeforeByDay(day-1));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(1));
        /*List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());
        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }*/
        queryModel.setIndexName(index);
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTimeFormat(format);
        queryModel.setNeedTimeFormat(true);
        queryModel.setSort(true);
        queryModel.setSortFields(new String[]{time});
        queryModel.setSortOrder(SortOrder.ASC);
        queryModel.setCount(count);
        queryModel.setStart(0);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    public Date dec8H(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR,-8);
        return cal.getTime();
    }

    public static void main(String[] args) {
        Date s = new Date();
        Date e = new Date();
        System.out.println();
    }
}
