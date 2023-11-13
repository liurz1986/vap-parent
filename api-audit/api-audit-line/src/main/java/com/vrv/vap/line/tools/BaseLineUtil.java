package com.vrv.vap.line.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.config.SubmitConfig;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.model.*;
import com.vrv.vap.line.schedule.TaskLoader;
import com.vrv.vap.line.schedule.task.BaseLineTask;
import com.vrv.vap.line.schedule.task.BaseTask;
import com.vrv.vap.toolkit.tools.RemoteSSHTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public class BaseLineUtil {
    private static String LINE_TASK_CLASS = "com.vrv.vap.line.schedule.task.BaseLineTask";
    private static String JOB_PRE = "baseLineTask-";
    private static DataSource dataSource = VapLineApplication.getApplicationContext().getBean(DataSource.class);
    private static com.vrv.vap.line.config.SubmitConfig submitConfig = VapLineApplication.getApplicationContext().getBean(SubmitConfig.class);
    private static final Log log = LogFactory.getLog(BaseLineUtil.class);
    private static Environment env = VapLineApplication.getApplicationContext().getBean(Environment.class);
    private String flinkSubmitUrl = "http://flinkHost:port/jars/jarName.jar/run?entry-class=mainClass&&program-args=param";
    private String flinkJobStateUrl = "http://flinkHost:port/jobs/jobId";
    private static String FLINK_HOST = "localhost";
    private static String FLINK_PORT = "8081";
    private String JOB_STATE_NORMAL = "Created,RUNNING,Restarting".toLowerCase();

    static {
        if(StringUtils.isNotEmpty(submitConfig.getSshClientHost())){
            FLINK_HOST = submitConfig.getSshClientHost();
        }
        if(StringUtils.isNotEmpty(env.getProperty("flink.port"))){
            FLINK_PORT = env.getProperty("flink.port");
        }
    }


    public void save2mysql(BaseLine line,List<Map<String, Object>> datas,String type){
        try{
            if(CollectionUtils.isEmpty(datas)){
                return;
            }
            StringBuffer insertSql = new StringBuffer("INSERT INTO ");
            String tableName = "";
            String key = "";
            if(LineConstants.TABLE_TYPE.LINE.equals(type)){
                key = "result";
                tableName = StringUtil.toUnderline(LineConstants.NAME_PRE.PRE_LINE+line.getSaveIndex());
            }else{
                key = "mediate";
                tableName = StringUtil.toUnderline(LineConstants.NAME_PRE.PRE_SM+line.getSaveIndex());
            }
            insertSql.append(tableName);
            Map map = JSONObject.parseObject(line.getFields(), Map.class);
            JSONArray fields = JSONArray.parseArray(map.get(key).toString());
            insertSql.append(" (");
            int i = 0;
            for(Object a : fields){
                JSONObject o = (JSONObject)a;
                if(i != 0){
                    insertSql.append(",");
                }
                insertSql.append("`");
                insertSql.append(o.getString("dest"));
                insertSql.append("`");
                i++;
            }
            insertSql.append(" ) ");
            insertSql.append("VALUES (");
            for(int j = 0; j<fields.size(); j++ ){
                if(j != 0){
                    insertSql.append(",");
                }
                insertSql.append("?");
            }
            insertSql.append(")");
            Connection connection = null;
            PreparedStatement insertStatement = null;
            try{
                connection = dataSource.getConnection();
                insertStatement = connection.prepareStatement(insertSql.toString());
                for(Map<String, Object> d:datas){
                    try {
                        for(int x = 1 ; x <= fields.size(); x++){
                            JSONObject f = (JSONObject)fields.get(x-1);
                            String t = f.getString("type");

                                Object v = d.get(f.getString("dest"));
                                switch (t) {
                                    case "keyword":
                                        insertStatement.setString(x,String.valueOf(v));
                                        break;
                                    case "date":
                                        Date date = (Date)v;
                                        insertStatement.setString(x,TimeTools.format2(date));
                                        break;
                                    case "long":
                                        insertStatement.setInt(x,v == null ? 0 : Float.valueOf(v.toString()).intValue());
                                        break;
                                    case "double":
                                        insertStatement.setDouble(x,v == null ? 0 : Double.valueOf(v.toString()));
                                        break;
                                    case "float":
                                        insertStatement.setFloat(x,v == null ? 0f :Float.valueOf(v.toString()));
                                        break;
                                }
                        }
                        insertStatement.execute();
                        insertStatement.clearParameters();
                    }catch (Exception var){
                        log.error(var.getMessage(),var);
                    }
                }
            }catch (Exception var2){
                log.error(var2.getMessage(),var2);
            }finally {
                insertStatement.close();
                connection.close();
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }

    }

    public void createTable(BaseLine line){
        log.info("创建基线mysql表开始，id："+line.getId());
        Statement statement = null;
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String sumTable = StringUtil.toUnderline(LineConstants.NAME_PRE.PRE_SM+line.getSaveIndex());
            String resultTable = StringUtil.toUnderline(LineConstants.NAME_PRE.PRE_LINE+line.getSaveIndex());
            Map<String, List<SysMeterAttached>> fieldsMap = line2Column(line);
            if(!MysqlUtil.existsTable(sumTable,connection)){
                log.info("中间值表创建开始，id："+line.getId());
                SysMeter meter = new SysMeter();
                meter.setMeterName(sumTable);
                meter.setMeterInfo(fieldsMap.get("mediates"));
                String tableSql = MysqlUtil.createSQL(meter);
                log.info("执行sql:"+tableSql);
                statement.executeUpdate(tableSql);
            }
            if(!MysqlUtil.existsTable(resultTable,connection)){
                log.info("结果表创建开始，id："+line.getId());
                SysMeter meter = new SysMeter();
                meter.setMeterName(resultTable);
                meter.setMeterInfo(fieldsMap.get("results"));
                String tableSql = MysqlUtil.createSQL(meter);
                log.info("执行sql:"+tableSql);
                statement.executeUpdate(tableSql);
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }finally {
            try {
                if(statement != null){
                    statement.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch (Exception var1){
                log.error(var1.getMessage(),var1);
            }
        }
    }


    public void createTableSql(BaseLine line){
        try{
            String sumTable = StringUtil.toUnderline(LineConstants.NAME_PRE.PRE_SM+line.getSaveIndex());
            String resultTable = StringUtil.toUnderline(LineConstants.NAME_PRE.PRE_LINE+line.getSaveIndex());
            Map<String, List<SysMeterAttached>> fieldsMap = line2Column(line);

            SysMeter meter = new SysMeter();
            meter.setMeterName(sumTable);
            meter.setMeterInfo(fieldsMap.get("mediates"));
            String tableSql = MysqlUtil.createSQL(meter);

            System.out.println("sql:"+tableSql);

            SysMeter meter2 = new SysMeter();
            meter2.setMeterName(resultTable);
            meter2.setMeterInfo(fieldsMap.get("results"));
            String tableSql2 = MysqlUtil.createSQL(meter2);

            System.out.println("sql:"+tableSql2);

        }catch (Exception e){
            log.error(e.getMessage(),e);
        }finally {
        }
    }

    public Map<String,List<SysMeterAttached>> line2Column(BaseLine line){
        Map<String,List<SysMeterAttached>> map = new HashMap<>();
        SysMeterAttached key = buildPrimaryKey();
        List<SysMeterAttached> mediates = new ArrayList<>();
        List<SysMeterAttached> results = new ArrayList<>();
        mediates.add(key);
        results.add(key);
        Map fields = JSONObject.parseObject(line.getFields(), Map.class);
        JSONArray mediateArrays = JSONArray.parseArray(fields.get("mediate").toString());
        JSONArray resultArrays = JSONArray.parseArray(fields.get("result").toString());

        if(CollectionUtils.isNotEmpty(mediateArrays)){
            mediateArrays.forEach(a ->{
                JSONObject o = (JSONObject) a;
                SysMeterAttached attached = buildField(o);
                mediates.add(attached);
            });
        }

        if(CollectionUtils.isNotEmpty(resultArrays)){
            resultArrays.forEach(a ->{
                JSONObject o = (JSONObject) a;
                SysMeterAttached attached = buildField(o);
                results.add(attached);
            });
        }
        map.put("mediates",mediates);
        map.put("results",results);
        return map;
    }

    public SysMeterAttached buildField(JSONObject json){
        SysMeterAttached attached = new SysMeterAttached();
        attached.setFieldName(json.getString("dest"));
        attached.setFieldRemark(json.getString("description"));
        String type = json.getString("type");
        String resultType = "varchar";
        switch (type) {
            case "keyword":
                resultType = "varchar";
                attached.setFieldLength(500);
                break;
            case "text":
                resultType = "varchar";
                attached.setFieldLength(500);
                break;
            case "date":
                resultType = "datetime";
                attached.setFieldLength(0);
                break;
            case "long":
                resultType = "int";
                attached.setFieldLength(11);
                break;
            case "double":
                resultType = "double";
                attached.setFieldLength(10);
                attached.setDecimalPoint(2);
                break;
            case "float":
                resultType = "float";
                attached.setFieldLength(10);
                attached.setDecimalPoint(2);
                break;
        }
        attached.setFieldType(resultType);
        return attached;
    }

    public SysMeterAttached buildPrimaryKey(){
        SysMeterAttached attached = new SysMeterAttached();
        attached.setFieldName("id");
        attached.setFieldRemark("主键");
        attached.setPrimaryKey(true);
        attached.setFieldType("int");
        attached.setFieldLength(11);
        attached.setNotNull(true);
        attached.setAutoIncrement(true);
        return attached;
    }

    public void renderFields(BaseLine line){
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
        mide.add(j6);
        Map<String,List<JSONObject>> fields = new HashMap<>();
        fields.put("mediate",mide);
        fields.put("result",result);
        line.setFields(JSONObject.toJSONString(fields));
    }

    public List<JSONObject> commonFields(){
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
        list.add(j1);
        list.add(j2);
        list.add(j3);
        list.add(j4);
        list.add(j5);
        list.add(j6);
        return list;
    }

    public static void clearDatas(BaseLine line,QueryTools.QueryWrapper wrapper){
        if(line.getSaveDays() == null){
            return;
        }
        Integer saveDays = line.getSaveDays();
        String endTime = TimeTools.format2(TimeTools.getNowBeforeByDay2(saveDays + 1));
        //String endTime = "2022-07-15 10:33:00";
        if(LineConstants.SAVE_TYPE.ES.equals(line.getSaveType()) || LineConstants.SAVE_TYPE.ES_AND_KAFAK.equals(line.getSaveType())){
            clearEs(line,endTime,wrapper);
        }else{
            clearMysql(line,endTime);
        }
    }

    public static void clearEs(BaseLine line,String endTime,QueryTools.QueryWrapper wrapper){
        //清理es数据
        String meindexName = LineConstants.NAME_PRE.PRE_SM +line.getSaveIndex()+"-*";
        String reindexName = LineConstants.NAME_PRE.PRE_LINE +line.getSaveIndex()+"-*";
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.rangeQuery("insert_time").lt(endTime));

        EsQueryModel mequeryModel = new EsQueryModel();
        mequeryModel.setQueryBuilder(boolQueryBuilder);
        mequeryModel.setIndexName(meindexName);

        EsQueryModel requeryModel = new EsQueryModel();
        requeryModel.setQueryBuilder(boolQueryBuilder);
        requeryModel.setIndexName(reindexName);
        wrapper.deleteByQuery(mequeryModel);
        wrapper.deleteByQuery(requeryModel);
    }

    public static void clearMysql(BaseLine line,String endTime){

        PreparedStatement reDelete = null;
        PreparedStatement meDelete = null;
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            String meindexName = StringUtil.toUnderline(LineConstants.NAME_PRE.PRE_SM +line.getSaveIndex());
            String reindexName = StringUtil.toUnderline(LineConstants.NAME_PRE.PRE_LINE +line.getSaveIndex());
            String mesql = "DELETE FROM "+meindexName+" WHERE insert_time <= ?";
            String resql = "DELETE FROM "+reindexName+" WHERE insert_time <= ?";
            reDelete = connection.prepareStatement(resql);
            meDelete = connection.prepareStatement(mesql);
            reDelete.setString(1,endTime);
            meDelete.setString(1,endTime);
            if(MysqlUtil.existsTable(meindexName,connection)){
                meDelete.execute();
            }
            if(MysqlUtil.existsTable(reindexName,connection)){
                reDelete.execute();
            }
            reDelete.execute();
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }finally {
            try{
                if(reDelete != null){
                    reDelete.close();
                }
                if(meDelete != null){
                    meDelete.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch (Exception var){
                log.error(var.getMessage(),var);
            }
        }
    }

    public static void main(String[] args) {
        BaseLineUtil util = new BaseLineUtil();
        BaseLine s = new BaseLine();
        s.setSaveColumns("[{\"aggType\":\"1\",\"count\":false,\"dest\":\"key_id\",\"level\":1,\"main\":true,\"mediate\":true,\"src\":\"key_id\",\"type\":\"keyword\",\"description\":\"key_id\"},{\"count\":false,\"description\":\"key_id数量\",\"dest\":\"key_id_doc_count\",\"level\":9999,\"main\":false,\"mediate\":true,\"src\":\"key_id_doc_count\",\"type\":\"long\"},{\"count\":false,\"dest\":\"login_hour\",\"level\":3,\"main\":false,\"mediate\":true,\"src\":\"event_time\",\"type\":\"keyword\",\"aggType\":\"1\",\"description\":\"小时\"},{\"count\":true,\"description\":\"数量\",\"dest\":\"login_count\",\"level\":4,\"main\":false,\"mediate\":true,\"src\":\"event_time_doc_count\",\"type\":\"long\",\"aggType\":\"2\"},{\"aggType\":\"1\",\"count\":false,\"dest\":\"dev_ip\",\"level\":2,\"main\":false,\"mediate\":true,\"src\":\"dev_ip\",\"type\":\"keyword\",\"description\":\"设备ip\"},{\"count\":true,\"description\":\"设备数量\",\"dest\":\"dev_ip_doc_count\",\"level\":9999,\"main\":false,\"mediate\":true,\"src\":\"dev_ip_doc_count\",\"type\":\"long\"},{\"count\":false,\"description\":\"入库时间\",\"dest\":\"insert_time\",\"level\":9999,\"main\":false,\"mediate\":true,\"src\":\"insert_time\",\"type\":\"date\"},{\"count\":false,\"description\":\"间隔\",\"dest\":\"interval_num\",\"level\":9999,\"main\":false,\"mediate\":false,\"src\":\"interval_num\",\"type\":\"keyword\"},{\"count\":false,\"description\":\"开始时间\",\"dest\":\"start_time\",\"level\":9999,\"main\":false,\"mediate\":false,\"src\":\"start_time\",\"type\":\"date\"},{\"count\":false,\"description\":\"结束时间\",\"dest\":\"end_time\",\"level\":9999,\"main\":false,\"mediate\":false,\"src\":\"end_time\",\"type\":\"date\"}]");
        s.setSaveIndex("user-login");
        s.setId(73);
        util.createTableSql(s);
    }

    public static List<Map<String, Object>> renderDataFromMysql(BaseLine line,Date currentTime){
        List<Map<String, Object>> result = new ArrayList<>();
        String summaryTable = StringUtil.toUnderline(LineConstants.NAME_PRE.PRE_SM+line.getSaveIndex());
        StringBuffer colnum = new StringBuffer();
        StringBuffer group = new StringBuffer(" GROUP BY ");
        JSONArray arrays = JSONArray.parseArray(line.getSaveColumns());
        //直接取值
        List<Object> topaggs = arrays.stream().filter(s->(LineConstants.AGG_TYPE.TOP.equals(((JSONObject)s).getString("aggType")))).collect(Collectors.toList());
        //分组
        List<Object> terms = arrays.stream().filter(s->(LineConstants.AGG_TYPE.TERMS.equals(((JSONObject)s).getString("aggType")))).collect(Collectors.toList());
        //计算
        List<Object> faggs = arrays.stream().filter(s->((!LineConstants.AGG_TYPE.TOP.equals(((JSONObject)s).getString("aggType"))) && (!LineConstants.AGG_TYPE.TERMS.equals(((JSONObject)s).getString("aggType"))) && StringUtils.isNotEmpty(((JSONObject)s).getString("aggType")))).collect(Collectors.toList());

        Collections.sort(terms, (o1, o2) -> {
            JSONObject j1 = (JSONObject)o1;
            JSONObject j2 = (JSONObject)o2;
            Integer level1 = j1.getInteger("level");
            Integer level2 = j2.getInteger("level");
            return level1.compareTo(level2);
        });
        StringBuffer on = new StringBuffer();
        for(int y = 0; y<terms.size() ;y++){
            if(y != 0){
                group.append(",");
                colnum.append(",");
                on.append(" AND ");
            }
            JSONObject o = (JSONObject)terms.get(y);
            String dest = o.getString("dest");
            group.append(dest);
            colnum.append(dest);
            on.append("a.").append(dest).append("=").append("b.").append(dest);
        }
        on.append(" AND a.id = b.id");
        List<String> devs = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(faggs)){
            for(int j = 0 ; j < faggs.size() ; j++){
                if(StringUtils.isNotEmpty(colnum)){
                    colnum.append(",");
                }
                JSONObject o = (JSONObject) faggs.get(j);
                String algorithm = o.getString("aggType");
                String dest = o.getString("dest");
                switch (algorithm) {
                    case LineConstants.AGG_TYPE.SUM:
                        colnum.append("SUM(").append(dest).append(")");
                        colnum.append(" AS ").append(dest+"_total");
                        break;
                    case LineConstants.AGG_TYPE.DEV:
                        devs.add(dest);
                        colnum.append("STDDEV_SAMP(").append(dest).append(")");
                        colnum.append(" AS ").append(dest+"_dev");
                        colnum.append(",");
                        colnum.append("SUM(").append(dest).append(")");
                        colnum.append(" AS ").append(dest+"_total");
                        colnum.append(",");
                        colnum.append("AVG(").append(dest).append(")");
                        colnum.append(" AS ").append(dest+"_avg");
                        break;
                    case LineConstants.AGG_TYPE.AVG:
                        colnum.append("AVG(").append(dest).append(")");
                        colnum.append(" AS ").append(dest+"_avg");
                        break;
                    default:
                }
            }
        }

        String startTime = TimeTools.format2(TimeTools.getNowBeforeByDay(line.getDays()*CronTools.getPeriodByCron(line.getCron())));
        String endTime = TimeTools.format2(TimeTools.getNowBeforeByDay2(0));
        String where = " WHERE insert_time >= ? and insert_time <= ? ";
        String sql = "";
        if(CollectionUtils.isNotEmpty(topaggs)){
            StringBuffer bcolnum = new StringBuffer();
            for(Object o : topaggs){
                JSONObject j = (JSONObject)o;
                if(StringUtils.isNotEmpty(bcolnum)){
                    bcolnum.append(",");
                }
                bcolnum.append("b.").append(j.getString("dest"));
            }
            sql = "SELECT a.*,"+bcolnum.toString() + " FROM"+
            "(SELECT "+colnum.toString()+ ",max(id) id FROM "+summaryTable +where+group.toString()+") a inner join "+summaryTable+" b on "+on.toString();

        }else{
            sql = "SELECT "+colnum.toString()+ " FROM "+summaryTable +where+group.toString();
        }

        PreparedStatement query = null;
        Connection connection = null;
        try{
            Date yesterday = TimeTools.getNowBeforeByDay(1);
            String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
            connection = dataSource.getConnection();
            query = connection.prepareStatement(sql);
            query.setString(1,startTime);
            query.setString(2,endTime);
            ResultSet resultSet = query.executeQuery();
            ResultSetMetaData md = resultSet.getMetaData();
            int columnCount = md.getColumnCount();
            while (resultSet.next()) {
                Map<String,Object> rowData = new HashMap<String,Object>();
                for (int i = 1; i <= columnCount; i++) {
                    String key = md.getColumnLabel(i);
                    rowData.put(StringUtils.isNotEmpty(key) ? key : md.getColumnLabel(i), resultSet.getString(i));
                }
                for(String devfield : devs){
                    Double dev = Double.valueOf(rowData.get(devfield+"_dev") != null ? rowData.get(devfield+"_dev").toString() : "0");
                    Double avg = Double.valueOf(rowData.get(devfield+"_avg") != null ? rowData.get(devfield+"_avg").toString() : "0");
                    Double min = avg - dev*line.getMultiple();
                    Double max = avg + dev*line.getMultiple();
                    rowData.put(devfield+"_min",min);
                    rowData.put(devfield+"_max",max);
                }
                rowData.put("insert_time",currentTime);
                rowData.put("start_time",TimeTools.getNowBeforeByDay(line.getDays()));
                rowData.put("end_time",TimeTools.getNowBeforeByDay2(1));
                rowData.put("interval_num",line.getDays());
                rowData.put("type","1");
                rowData.put("data_time",dataTime);
                rowData.put("guid",UUID.randomUUID());
                result.add(rowData);
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }finally {
            try{
                if(query != null){
                    query.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    private Map<String,String> generateHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }

    public String runSpecialTask(BaseLineSpecial special){
        String url  = flinkSubmitUrl.replace("flinkHost",FLINK_HOST).replace("port",FLINK_PORT)
                .replace("jarName",special.getJarName())
                .replace("mainClass",special.getMainClass())
                .replace("param",special.getConfig());
        String result = "";
        try{
            result = HTTPUtil.POST(url, generateHeaders(), "");
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return result;
    }

    public String runSpecialTaskBySsh(BaseLineSpecial special,Map<String,Object> params){
        Map<String,String> hashMap = JSONObject.parseObject(special.getConfig(), HashMap.class);
        StringBuffer pms = new StringBuffer();
        if(params != null && params.size() > 0){
            params.entrySet().forEach(en ->{
                pms.append("-").append(en.getKey()).append(" ").append(en.getValue()).append(" ");
            });
        }
        String message = "";
        try{
            RemoteSSHTools localTools = RemoteSSHTools.build("localhost",22,"","");
            message = localTools.localExecuteCmd(hashMap.get("mainCmd")+" "+pms.toString(),true);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return message;
    }

    public String runSpecialActualTaskBySsh(BaseLineSpecial special,Map<String,Object> params){
        Map<String,String> hashMap = JSONObject.parseObject(special.getConfig(), HashMap.class);
        StringBuffer pms = new StringBuffer();
        if(params != null){
            params.entrySet().forEach(en ->{
                pms.append("-").append(en.getKey()).append(" ").append(en.getValue()).append(" ");
            });
        }
        String message = "";
        try{
            RemoteSSHTools localTools = RemoteSSHTools.build("localhost",22,"","");
            message = localTools.localExecuteCmd(hashMap.get("actualCmd")+" "+pms.toString(),true);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return message;
        //return submitSsh(hashMap.get("actualCmd")+" "+pms.toString());
    }

    public String runSpecialActualTask(BaseLineSpecial special){
        String url  = flinkSubmitUrl.replace("flinkHost",FLINK_HOST).replace("port",FLINK_PORT)
                .replace("jarName",special.getJarName())
                .replace("mainClass",special.getActualClass())
                .replace("param",special.getConfig());
        String result = "";
        try{
            result = HTTPUtil.POST(url, generateHeaders(), "");
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return result;
    }

    /**
     * 查询job状态 true正常 false异常
     * @param jobId
     * @return
     */
    public boolean queryJobState(String jobId){
        String url  = flinkJobStateUrl.replace("flinkHost",FLINK_HOST).replace("port",FLINK_PORT)
                .replace("jobId",jobId);
        String rep = "";
        boolean result = false;
        try{
            rep = HTTPUtil.GET(url, generateHeaders());
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        if(StringUtils.isNotEmpty(rep)){
            Map<String,String> hashMap = JSONObject.parseObject(rep, HashMap.class);
            if(hashMap.containsKey("state")){
                if(JOB_STATE_NORMAL.indexOf(hashMap.get("state").toLowerCase()) > -1){
                    result = true;
                }
            }
        }
        return result;
    }

    public String submitSsh(String cmd){
        String result = "";
        Session session = null;
        ChannelExec channelExec = null;
        try{
            JSch jsch = new JSch();
            if (log.isDebugEnabled()) {
                log.debug("建立远程连接 > " + submitConfig.getSshClientHost() + ":" + submitConfig.getSshClientPort());
            }

            if (StringUtils.isNotEmpty(submitConfig.getPublickey())) {
                //设置免密(开启则需去掉密码)
                jsch.addIdentity(submitConfig.getPublickey());
            }

            session = jsch.getSession(submitConfig.getSshClientUser(), submitConfig.getSshClientHost(),
                    submitConfig.getSshClientPort());
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(30 * 1000);
            if (StringUtils.isEmpty(submitConfig.getPublickey())) {
                session.setPassword(submitConfig.getSshClientPassword());
            }
            session.connect();
            log.info("提交命令 >>> " + cmd);
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(cmd);
            channelExec.setErrStream(System.err);
            channelExec.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
            StringBuilder data = new StringBuilder();
            String tmp = null;
            int i = 50000;
            while ((tmp = br.readLine()) != null) {
                i--;
                if (i < 0) {
                    break;
                }
                data.append(tmp).append("\r\n");
            }
            br.close();
            result = data.toString();
        }catch (Exception e){
            log.error(e.getMessage(),e);
            result = e.getMessage();
        }finally {
            if (null != channelExec) {
                channelExec.disconnect();
            }
            if (null != session) {
                session.disconnect();
            }
        }
        return result;
    }

    /*

    {
	"level": 9999,
	"src": "start_time",
	"mediate": false,
	"count": false,
	"description": "开始时间",
	"main": false,
	"dest": "start_time",
	"type": "date"
}
     */
    public BaseLine addDataTime(BaseLine line){
        String saveColumns = line.getSaveColumns();
        JSONArray objects = JSONArray.parseArray(saveColumns);
        JSONObject dataTime = new JSONObject();
        dataTime.put("level",9999);
        dataTime.put("src","data_time");
        dataTime.put("mediate",true);
        dataTime.put("count",false);
        dataTime.put("description","数据日期");
        dataTime.put("main",false);
        dataTime.put("dest","data_time");
        dataTime.put("type","keyword");
        objects.add(dataTime);
        BaseLine record = new BaseLine();
        record.setId(line.getId());
        record.setSaveColumns(JSONObject.toJSONString(objects));
        return record;
    }

    public static boolean isNoMainLine(BaseLine line){
        JSONArray arrays = JSONArray.parseArray(line.getConfig());
        JSONObject obj = (JSONObject)arrays.get(0);
        String column = obj.getString("column");
        if("无".equals(column) || StringUtils.isEmpty(column)){
            return true;
        }else{
            return false;
        }
    }

    public static List<Map<String, Object>> renderResultFromMysqlByDay(BaseLine line,Date currentTime,Date startTime,Date endTime){
        List<Map<String, Object>> result = new ArrayList<>();
        if(isNoMainLine(line)){
            List<Map<String, Object>> group = renderDataFromMysqlByDay(line, currentTime, "0",1,startTime,endTime);
            result.addAll(group);
            return result;
        }
        List<Map<String, Object>> person = renderDataFromMysqlByDay(line, currentTime, "1",1,startTime,endTime);
        result.addAll(person);
        if("1".equals(line.getOpenGroup())){
            log.info("计算群体基线");
            //计算群体基线
            List<LineSaveModel> lineSaveModels = JSONArray.parseArray(line.getSaveColumns(), LineSaveModel.class);
            List<LineSaveModel> termsModel = lineSaveModels.stream().filter(s->(LineConstants.AGG_TYPE.TERMS.equals(s.getAggType()))).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(termsModel)){
                if(termsModel.size() <= 1){
                    //只有标识字段
                    lineSaveModels.remove(termsModel.get(0));
                    lineSaveModels.add(new LineSaveModel("data_time","data_time","keyword",null,"1",1));
                    line.setSaveColumns(JSONObject.toJSONString(lineSaveModels));
                }else{
                    //多个字段聚合
                    List<LineSaveModel> mainModel = termsModel.stream().filter(s->(s.isMain())).collect(Collectors.toList());
                    lineSaveModels.remove(mainModel.get(0));
                    line.setSaveColumns(JSONObject.toJSONString(lineSaveModels));
                }
            }
            List<Map<String, Object>> group = renderDataFromMysqlByDay(line, currentTime, "0",person != null ? person.size() : 1,startTime,endTime);
            result.addAll(group);
        }
        return result;
    }


    public static List<Map<String, Object>> renderDataFromMysqlByDay(BaseLine line,Date currentTime,String type,int baseSize,Date startTime,Date endTime){
        if(baseSize == 0){
            baseSize = 1;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        String summaryTable = StringUtil.toUnderline(LineConstants.NAME_PRE.PRE_SM+line.getSaveIndex());
        StringBuffer colnum = new StringBuffer();
        colnum.append("count(id)/").append(line.getDays()).append(" AS process ");
        StringBuffer group = new StringBuffer(" GROUP BY ");
        JSONArray arrays = JSONArray.parseArray(line.getSaveColumns());
        //直接取值
        List<Object> topaggs = arrays.stream().filter(s->(LineConstants.AGG_TYPE.TOP.equals(((JSONObject)s).getString("aggType")))).collect(Collectors.toList());
        //分组
        List<Object> terms = arrays.stream().filter(s->(LineConstants.AGG_TYPE.TERMS.equals(((JSONObject)s).getString("aggType")))).collect(Collectors.toList());
        //计算
        List<Object> faggs = arrays.stream().filter(s->((!LineConstants.AGG_TYPE.TOP.equals(((JSONObject)s).getString("aggType"))) && (!LineConstants.AGG_TYPE.TERMS.equals(((JSONObject)s).getString("aggType"))) && StringUtils.isNotEmpty(((JSONObject)s).getString("aggType")))).collect(Collectors.toList());

        Collections.sort(terms, (o1, o2) -> {
            JSONObject j1 = (JSONObject)o1;
            JSONObject j2 = (JSONObject)o2;
            Integer level1 = j1.getInteger("level");
            Integer level2 = j2.getInteger("level");
            return level1.compareTo(level2);
        });
        StringBuffer on = new StringBuffer();
        if(isNoMainLine(line)){
            //无主体基线只分一组，产生群体基线数据
            group.append("'1'");
        }else{
            for(int y = 0; y<terms.size() ;y++){
                if(y != 0){
                    group.append(",");
                    on.append(" AND ");
                }
                colnum.append(",");
                JSONObject o = (JSONObject)terms.get(y);
                String dest = o.getString("dest");
                group.append(dest);
                colnum.append(dest);
                on.append("a.").append(dest).append("=").append("b.").append(dest);
            }
        }
        on.append(" AND a.id = b.id");
        List<String> devs = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(faggs)){
            for(int j = 0 ; j < faggs.size() ; j++){
                if(StringUtils.isNotEmpty(colnum)){
                    colnum.append(",");
                }
                JSONObject o = (JSONObject) faggs.get(j);
                String algorithm = o.getString("aggType");
                String dest = o.getString("dest");
                switch (algorithm) {
                    case LineConstants.AGG_TYPE.SUM:
                        colnum.append("SUM(").append(dest).append(")");
                        colnum.append(" AS ").append(dest+"_total");
                        break;
                    case LineConstants.AGG_TYPE.DEV:
                        devs.add(dest);
                        colnum.append("STDDEV_POP(").append(dest).append(")");
                        colnum.append(" AS ").append(dest+"_dev");
                        colnum.append(",");
                        colnum.append("SUM(").append(dest).append(")");
                        colnum.append(" AS ").append(dest+"_total");
                        colnum.append(",");
                        colnum.append("AVG(").append(dest).append(")");
                        colnum.append(" AS ").append(dest+"_avg");
                        break;
                    case LineConstants.AGG_TYPE.AVG:
                        colnum.append("AVG(").append(dest).append("/").append(baseSize).append(")");
                        colnum.append(" AS ").append(dest+"_avg");
                        break;
                    default:
                }
            }
        }
        int summary = line.getRunNum() -line.getDays();
        String where = " WHERE summary_num > "+summary;
        String sql = "";
        if(CollectionUtils.isNotEmpty(topaggs) && !isNoMainLine(line)){
            StringBuffer bcolnum = new StringBuffer();
            for(Object o : topaggs){
                JSONObject j = (JSONObject)o;
                if(StringUtils.isNotEmpty(bcolnum)){
                    bcolnum.append(",");
                }
                bcolnum.append("b.").append(j.getString("dest"));
            }
            sql = "SELECT a.*,"+bcolnum.toString() + " FROM"+
                    "(SELECT "+colnum.toString()+ ",max(id) id FROM "+summaryTable +where+group.toString()+") a inner join "+summaryTable+" b on "+on.toString();

        }else{
            if(StringUtils.isEmpty(colnum.toString())){
                colnum.append(" * ");
            }
            sql = "SELECT "+colnum.toString()+ " FROM "+summaryTable +where+group.toString();
        }

        PreparedStatement query = null;
        Connection connection = null;
        try{
            String dataTime = TimeTools.format(startTime, "yyyy-MM-dd");
            connection = dataSource.getConnection();
            query = connection.prepareStatement(sql);
            ResultSet resultSet = query.executeQuery();
            ResultSetMetaData md = resultSet.getMetaData();
            int columnCount = md.getColumnCount();
            while (resultSet.next()) {
                Map<String,Object> rowData = new HashMap<String,Object>();
                for (int i = 1; i <= columnCount; i++) {
                    String key = md.getColumnLabel(i);
                    rowData.put(StringUtils.isNotEmpty(key) ? key : md.getColumnLabel(i), resultSet.getString(i));
                }
                for(String devfield : devs){
                    Double dev = Double.valueOf(rowData.get(devfield+"_dev") != null ? rowData.get(devfield+"_dev").toString() : "0");
                    Double avg = Double.valueOf(rowData.get(devfield+"_avg") != null ? rowData.get(devfield+"_avg").toString() : "0");
                    Double min = avg - dev*line.getMultiple();
                    Double max = avg + dev*line.getMultiple();
                    rowData.put(devfield+"_min",min);
                    rowData.put(devfield+"_max",max);
                }
                rowData.put("insert_time",currentTime);
                rowData.put("start_time",startTime);
                rowData.put("end_time",endTime);
                rowData.put("interval_num",line.getDays());
                rowData.put("type",type);
                rowData.put("data_time",dataTime);
                rowData.put("guid",UUID.randomUUID());
                result.add(rowData);
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }finally {
            try{
                if(query != null){
                    query.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void delayTask(BaseLine line){
        Date date = new Date();
        String dayNow = TimeTools.format(date, "dd");
        Date delayDate = MyTimeTools.addMini(date, 10);
        String delayDay = TimeTools.format(delayDate, "dd");
        if(!dayNow.equals(delayDay)){
            return;
        }
        String cron = TimeTools.format(delayDate,"ss mm HH dd MM yyyy");
        JobModel jobModel = new JobModel();
        jobModel.setJobName(JOB_PRE + line.getId());
        jobModel.setCronTime(line.getCron());
        jobModel.setJobClazz(LINE_TASK_CLASS);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", line.getId());
        jobModel.setParams(param);
        TaskLoader.addJob(jobModel,param);
    }

}
