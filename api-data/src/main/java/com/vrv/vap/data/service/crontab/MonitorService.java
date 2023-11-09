package com.vrv.vap.data.service.crontab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.vrv.vap.data.component.ESManager;
import com.vrv.vap.data.component.ESTools;
import com.vrv.vap.data.constant.SOURCE_TYPE;
import com.vrv.vap.data.mapper.SourceMapper;
import com.vrv.vap.data.model.Source;
import com.vrv.vap.data.model.SourceMonitor;
import com.vrv.vap.data.service.SourceMonitorService;
import com.vrv.vap.data.util.TimeTools;
import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 监控服务， 系统启用时会自动运行
 */
@Service
@EnableScheduling
public class MonitorService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String pass;

    @Value("${spring.datasource.url}")
    private String url;

    @Autowired
    private ESTools esTools;

    @Autowired
    private ESManager esManager;

    @Resource
    SourceMapper sourceMapper;

    @Autowired
    SourceMonitorService sourceMonitorService;


    private final String SQL = "SELECT * FROM `TABLES` WHERE TABLE_SCHEMA=? AND TABLE_NAME = ?";

    private final Pattern pattern = Pattern.compile("/([^/]+)\\?");

    private String database = null;                 // 当前数据库

    private MysqlDataSource source = null;          // 数据源

    private List<Source> getSourcesByType(SOURCE_TYPE type) {
        Example example = new Example(Source.class);
        example.createCriteria().andEqualTo("type", type);
        return sourceMapper.selectByExample(example);
    }

    private Connection getConnection() throws SQLException {
        if (source == null) {
            this.source = new MysqlDataSource();
            this.source.setUser(user);
            this.source.setPassword(pass);
            Matcher matcher = pattern.matcher(url);
            matcher.find();
            this.database = matcher.group(1);
            String URL = url.replace(matcher.group(0), "/information_schema?");
            this.source.setURL(URL);
        }
        return source.getConnection();
    }


    // 每10分钟监听一次
    @Scheduled(cron = "0 */10 * * * *")
    private void updateStatus() {
        logger.info("The Monitor Task : DataSource Status : " + TimeTools.format(new Date()));
        Connection conn = null;
        try {
            conn = getConnection();
            List<Source> sources = sourceMapper.selectAll();
            for (Source source : sources) {
                switch (source.getType()) {
                    case SOURCE_TYPE.MYSQL_BUILT:
                        this.monitorSQL(conn, source);
                        break;
                    case SOURCE_TYPE.ELASTIC_BUILT:
                        this.monitorElastic(source);
                        break;
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        logger.info("The Monitor Task : Finshed: " + TimeTools.format(new Date()));
    }

    public void monitor(Source source) {
        if (source.getType() == SOURCE_TYPE.ELASTIC_BUILT) {
            this.monitorElastic(source);
            return;
        }
        if(source.getType()==SOURCE_TYPE.MYSQL_BUILT){
            Connection conn = null;
            try {
                conn = getConnection();
                this.monitorSQL(conn, source);
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }


    }

    private void monitorSQL(Connection conn, Source source) {
        if (conn == null) {
            return;
        }
        try {
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setString(1, this.database);
            ps.setString(2, source.getName());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String type = rs.getString("TABLE_TYPE");           // 表类型
                String engine = rs.getString("ENGINE");             // 表引擎
                Long version = rs.getLong("VERSION");               // 引擎版本
                Long count = rs.getLong("TABLE_ROWS");              // 数据条数
//                    Long rowSize = rs.getLong("AVG_ROW_LENGTH");        // 每行占用空间
                Long usedSize = rs.getLong("DATA_LENGTH");          // 总占用空间
//                    Long maxSize = rs.getLong("MAX_DATA_LENGTH");       // 最大空间 / 为0  则无限制
                Long indexSize = rs.getLong("INDEX_LENGTH");        // 索引占用的空间
//                    Date createTime = rs.getDate("CREATE_TIME");        // 创建时间
                String comment = rs.getString("TABLE_COMMENT");     // 表说明
                System.out.println(String.format("表类型 ：%s  表引擎：%s  表说明：%s  引擎版本：%d  数量条数：%d ", type, engine, comment, version, count));
                SourceMonitor monitor = new SourceMonitor();
                monitor.setDataSize(usedSize);
                monitor.setHealth((byte) 1);
                monitor.setIndexSize(indexSize);
                monitor.setIndices(1);
                monitor.setShards(1);
                monitor.setDataCount(count);
                monitor.setSourceId(source.getId());
                monitor.setTime(new Date());
                sourceMonitorService.save(monitor);
                break;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            return;

        }
    }

    private void monitorElastic(Source source) {
        logger.info("Start Monitor Elastic Index Status " + TimeTools.format(new Date()));
        Response response = null;
        try {
            String endpoint = "/" + source.getName() + "/_stats/docs,store,segments";
            response = ESManager.sendGet(endpoint);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.getEntity().getContent());

            int shards = node.get("_shards").get("total").asInt();
            long count = 0;
            if (node.get("_all").get("total").get("docs") != null) {
                count = node.get("_all").get("total").get("docs").get("count").longValue();
            }
            long indexSize = 0;
            long usedSize = 0;
            if (node.get("_all").get("total").get("store") != null) {
                usedSize = node.get("_all").get("total").get("store").get("size_in_bytes").longValue();
            }
            int indices = node.get("indices").size();

            SourceMonitor monitor = new SourceMonitor();
            monitor.setDataSize(usedSize);
            monitor.setHealth((byte) 1);
            monitor.setIndexSize(indexSize);
            monitor.setIndices(indices);
            monitor.setShards(shards);
            monitor.setDataCount(count);
            monitor.setSourceId(source.getId());
            monitor.setTime(new Date());
            sourceMonitorService.save(monitor);
        } catch (IOException e) {
//                e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }


}
