package com.vrv.vap.line.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.fegin.ApiDataClient;
import com.vrv.vap.line.mapper.BaseLineMapper;
import com.vrv.vap.line.model.*;
import com.vrv.vap.line.service.CommonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

public class BaseLine4MysqlUtil {

    private BaseLineMapper baseLineMapper = VapLineApplication.getApplicationContext().getBean(BaseLineMapper.class);
    private CommonService commonService = VapLineApplication.getApplicationContext().getBean(CommonService.class);
    private ApiDataClient client = VapLineApplication.getApplicationContext().getBean(ApiDataClient.class);
    private String PRE_CN = "_";
    private final String indexSufFormat = "-yyyy";
    private static final Log log = LogFactory.getLog(BaseLine4MysqlUtil.class);
    private static DataSource dataSource = VapLineApplication.getApplicationContext().getBean(DataSource.class);

    public List<Map<String,Object>> doline(BaseLine line,Date currentTime) {
        log.info("mysql配置解析开始");
        //解析基线
        Date yesterday = TimeTools.getNowBeforeByDay(1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        JSONArray arrays = JSONArray.parseArray(line.getConfig());
        Integer days = line.getDays();
        if(CollectionUtils.isEmpty(arrays)){
            throw new RuntimeException("无基线配置");
        }
        JSONArray colsArray = JSONArray.parseArray(line.getSaveColumns());
        Map<String,String> resultMap = colsArray.stream().collect(Collectors.toMap(i -> {JSONObject a= (JSONObject)i;String k = a.getString("src");return k;} , i -> {JSONObject a= (JSONObject)i;String k = a.getString("dest");return k;}));
        //计算基线
        List<Map<String,Object>> dataList = new ArrayList<>();
        Boolean open = LineConstants.OPEN_GROUP.YES.equals(line.getOpenGroup());
        Connection connection = null;
        try{
            for(Object e : arrays){
                JSONObject o = (JSONObject) e;
                String index = o.getString("indexId");
                Source source = client.getSourceById(Integer.parseInt(index)).getData();
                log.info("计算mysql个体基线");
                List<Map<String, Object>> personList = renderMysqlLine(o, source, resultMap, line.getDays(), currentTime, connection, true);
                if(CollectionUtils.isNotEmpty(personList)){
                    dataList.addAll(personList);
                }
                if(open){//计算群体基线
                    log.info("计算mysql群体基线");
                    List<Map<String, Object>> groupList = renderMysqlLine(o, source, resultMap, line.getDays(), currentTime, connection, false);
                    if(CollectionUtils.isNotEmpty(groupList)){
                        dataList.addAll(groupList);
                    }
                }
            };
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
        }finally {
            try{
                if(connection != null){
                    connection.close();
                }
            }catch (Exception var2){
                log.error(var2.getMessage(),var2);
            }
        }
        return dataList;
    }

    private List<Map<String,Object>> renderMysqlLine(JSONObject o,Source source,Map<String,String> resultMap,Integer days,Date currentTime,Connection connection,boolean isPerson) throws SQLException {
        List<Map<String,Object>> dataList = new ArrayList<>();
        StringBuffer select = new StringBuffer("select ");
        StringBuffer where = new StringBuffer(" where ");
        StringBuffer group = new StringBuffer("");
        renderWhere(o,where,days,source);
        renderSelect(o,select,source);
        renderGroup(o,group,isPerson);
        String sql = select.append(" from ").append(source.getName()).append(where).append(group).toString();
        log.info("执行sql："+sql);
        if(connection == null){
            connection = dataSource.getConnection();
        }
        if(StringUtils.isNotEmpty(sql)){
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs.next()) {
                Map<String,Object> rowData = new HashMap<String,Object>();
                for (int i = 1; i <= columnCount; i++) {
                    String key = resultMap.get(md.getColumnLabel(i));
                    rowData.put(StringUtils.isNotEmpty(key) ? key : md.getColumnLabel(i), rs.getString(i));
                }
                rowData.put("insert_time",currentTime);
                rowData.put("start_time",TimeTools.getNowBeforeByDay(days));
                rowData.put("end_time",TimeTools.getNowBeforeByDay2(1));
                rowData.put("interval",days);
                rowData.put("type",isPerson ? "1" : "0");
                dataList.add(rowData);
            }
            stmt.close();
        }
        return dataList;
    }


    private void renderWhere(JSONObject o,StringBuffer where,Integer days,Source source){
        if(days == null){
            days = 1;
        }
        String format = source.getTimeFormat();
        if(StringUtils.isEmpty(format)){
            format = TimeTools.TIME_FMT_2;
        }
        String stime = TimeTools.format(TimeTools.getNowBeforeByDay(days),format);
        String etime = TimeTools.format(TimeTools.getNowBeforeByDay2(1),format);
        where.append(source.getTimeField()).append(">=").append("'").append(stime).append("'");
        where.append(" and ");
        where.append(source.getTimeField()).append("<=").append("'").append(etime).append("'");
        JSONArray conditions = JSONArray.parseArray(o.getString("conditions"));
        if(CollectionUtils.isEmpty(conditions)){
            return;
        }
        conditions.forEach( e -> {
            JSONObject obj = (JSONObject)e;
            String filter = obj.getString("filter");
            //标识字段
            String column = obj.getString("column");
            String type = obj.getString("type");
            String value = obj.getString("value");
            if(StringUtils.isNotEmpty(type) && StringUtils.isNotEmpty(filter)){
                where.append(" and ");
                where.append(column);
                switch (type) {
                    case LineConstants.FILTER_TYPE.EQ:
                        where.append(" = ").append(value);
                        break;
                    case LineConstants.FILTER_TYPE.GT:
                        where.append(" > ").append(value);
                        break;
                    case LineConstants.FILTER_TYPE.LT:
                        where.append(" < ").append(value);
                        break;
                    case LineConstants.FILTER_TYPE.LIKE:
                        where.append(" like ").append("%").append(value).append("%");
                        break;
                    case LineConstants.FILTER_TYPE.IN:
                        where.append(" in (");
                        String[] values = value.split(",");
                        for(int i = 0 ; i < values.length ; i++){
                            String v = values[i];
                            if(i != 0){
                                where.append(",");
                            }
                            where.append("'").append(v).append("'");
                        }
                        where.append(")");
                        break;
                }
            }
        });
    }

    private void renderSelect(JSONObject o,StringBuffer select,Source source){
        JSONArray aggs = o.getJSONArray("calculation");
        //直接取值
        List<Object> topaggs = aggs.stream().filter(s->(LineConstants.AGG_TYPE.TOP.equals(((JSONObject)s).getString("algorithm")))).collect(Collectors.toList());
        //分组
        List<Object> terms = aggs.stream().filter(s->(LineConstants.AGG_TYPE.TERMS.equals(((JSONObject)s).getString("algorithm")))).collect(Collectors.toList());
        //计算
        List<Object> faggs = aggs.stream().filter(s->((!LineConstants.AGG_TYPE.TOP.equals(((JSONObject)s).getString("algorithm"))) && (!LineConstants.AGG_TYPE.TERMS.equals(((JSONObject)s).getString("algorithm"))))).collect(Collectors.toList());

        List<SourceField> sourceFields = client.getFields(source.getId()).getData();
        Map<String,String> aliaMaps = new HashMap<>();
        sourceFields.forEach(e -> {
            if(StringUtils.isNotEmpty(e.getAlias())){
                aliaMaps.put(e.getField(),e.getAlias());
            }
        });
        if(CollectionUtils.isNotEmpty(topaggs)){
            for(int i = 0 ; i < topaggs.size() ; i++){
                JSONObject cals = (JSONObject)topaggs.get(i);
                if( i != 0){
                    select.append(",");
                }
                String column = cals.getString("column");
                if(aliaMaps.containsKey(column)){
                    select.append(column).append(" as ").append(aliaMaps.get(column));
                }else{
                    select.append(column);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(faggs)){
            for(int i = 0 ; i < faggs.size() ; i++){
                JSONObject cals = (JSONObject)topaggs.get(i);
                if( i != 0 || topaggs.size() != 0){
                    select.append(",");
                }
                String column = cals.getString("column");
                String as = "";
                if(aliaMaps.containsKey(column)){
                    as = " as "+aliaMaps.get(column);
                }
                String algorithm = cals.getString("algorithm");
                switch (algorithm) {
                    case LineConstants.AGG_TYPE.SUM:
                        select.append("SUM(").append(column).append(")");
                        select.append(as);
                        break;
                    case LineConstants.AGG_TYPE.COUNT:
                        select.append("COUNT(").append(column).append(")");
                        select.append(as);
                        break;
                    case LineConstants.AGG_TYPE.AVG:
                        select.append("AVG(").append(column).append(")");
                        select.append(as);
                        break;
                    default:
                }
            }
        }
        renderRatio(o,select, topaggs.size()+faggs.size());
    }

    private void renderGroup(JSONObject o, StringBuffer group, Boolean isPerson){
        JSONArray aggs = o.getJSONArray("calculation");
        //分组
        List<Object> terms = aggs.stream().filter(s->(LineConstants.AGG_TYPE.TERMS.equals(((JSONObject)s).getString("algorithm")))).collect(Collectors.toList());
        if (isPerson){
            group.append(" group by ").append(o.getString("column"));
        }
        if(CollectionUtils.isNotEmpty(terms)){
            if(isPerson){
                group.append(",");
            }
            for(int i = 0 ; i < terms.size() ; i++){
                JSONObject cals = (JSONObject)terms.get(i);
                if(i != 0){
                    group.append(",");
                }
                group.append(cals.getString("column"));
            }
        }
        if(!isPerson && CollectionUtils.isEmpty(terms)){
            group.append(" group by '1'");
        }
    }

    private void renderRatio(JSONObject o,StringBuffer select,int size){
        JSONArray ratios = o.getJSONArray("ratio");
        if(CollectionUtils.isNotEmpty(ratios)){
            for(int i = 0 ; i<ratios.size() ; i++){
                if( i != 0 || size != 0){
                    select.append(",");
                }
                JSONObject j = (JSONObject)ratios.get(i);
                String name = j.getString("name");
                //分子解析
                StringBuffer mratioSql = new StringBuffer();
                JSONObject molecule = j.getJSONObject("molecule");//分子
                JSONArray moleculecs = molecule.getJSONArray("conditions");
                String mvar1 = molecule.getString("algorithm");
                String mfield = molecule.getString("field");
                String mdf = "NULL";
                String mca = "COUNT";
                if(LineConstants.AGG_TYPE.SUM.equals(mvar1)){
                    mdf = "0";
                    mca = "SUM";
                }
                mratioSql.append(mca).append("(");
                if(CollectionUtils.isNotEmpty(moleculecs)){
                    mratioSql.append("IF( 1 = 1 ");
                    moleculecs.forEach(c ->{
                        mratioSql.append(" AND ");
                        JSONObject m = (JSONObject)c;
                        String f = m.getString("field");
                        String t = m.getString("type");
                        String v = m.getString("value");
                        mratioSql.append(f);
                        switch (t) {
                            case LineConstants.FILTER_TYPE.EQ:
                                mratioSql.append("=");
                                break;
                            case LineConstants.FILTER_TYPE.GT:
                                mratioSql.append(">");
                                break;
                            case LineConstants.FILTER_TYPE.LT:
                                mratioSql.append("<");
                                break;
                            case LineConstants.FILTER_TYPE.N_EQ:
                                mratioSql.append("!=");
                                break;
                        }
                        mratioSql.append(v);
                    });
                    mratioSql.append(",").append(mfield).append(",").append(mdf).append(")");
                }else{
                    mratioSql.append(mfield);
                }
                mratioSql.append(")");
                //分母解析
                StringBuffer dratioSql = new StringBuffer();
                JSONObject denominator = j.getJSONObject("denominator");//分母
                JSONArray doleculecs = denominator.getJSONArray("conditions");
                String dvar1 = denominator.getString("algorithm");
                String dfield = denominator.getString("field");
                String ddf = "NULL";
                String dca = "COUNT";
                if(LineConstants.AGG_TYPE.SUM.equals(dvar1)){
                    ddf = "0";
                    dca = "SUM";
                }
                dratioSql.append(dca).append("(");
                if(CollectionUtils.isNotEmpty(doleculecs)){
                    dratioSql.append("IF( 1 = 1 ");
                    doleculecs.forEach(c ->{
                        dratioSql.append(" AND ");
                        JSONObject m = (JSONObject)c;
                        String f = m.getString("field");
                        String t = m.getString("type");
                        String v = m.getString("value");
                        dratioSql.append(f);
                        switch (t) {
                            case LineConstants.FILTER_TYPE.EQ:
                                dratioSql.append("=");
                                break;
                            case LineConstants.FILTER_TYPE.GT:
                                dratioSql.append(">");
                                break;
                            case LineConstants.FILTER_TYPE.LT:
                                dratioSql.append("<");
                                break;
                            case LineConstants.FILTER_TYPE.N_EQ:
                                dratioSql.append("!=");
                                break;
                        }
                        dratioSql.append(v);
                    });
                    dratioSql.append(",").append(dfield).append(",").append(ddf).append(")");
                }else{
                    dratioSql.append(dfield);
                }
                dratioSql.append(")");
                select.append(mratioSql.toString()).append("/").append(dratioSql.toString()).append(" as ").append(name);
            }
        }
    }
}
