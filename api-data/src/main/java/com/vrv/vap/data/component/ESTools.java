package com.vrv.vap.data.component;

import com.vrv.vap.data.component.config.IndexSliceConfig;
import com.vrv.vap.data.component.config.MultiClusterConfig;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.util.TimeTools;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ESTools {

    private static Logger logger = LoggerFactory.getLogger(ESTools.class);

    // 索引时间正则
    private static final String INDEX_DATE_REG = "-\\d{4}\\.\\d{2}\\.\\d{2}$|-\\d{4}-\\d{2}-\\d{2}$|-\\d{4}/\\d{2}/\\d{2}$|-\\d{4}\\.\\d{2}$|-\\d{4}-\\d{2}$|-\\d{4}/\\d{2}$";

    @Value("${elk.version:v5}")
    private String VERSIION;


    @Autowired
    IndexSliceConfig indexSliceConfig;

    @Autowired
    private MultiClusterConfig multiClusterConfig;

    @Autowired
    ESManager esManager;

    /**
     * es scroll 查询 缓存时间
     */
    public static final long ES_CACHE_TIME = 60000;


    private static Set<String> indexNames_exist = new HashSet<String>();    //  存在的索引
    private static Map<String, Long> indexNames_not = new HashMap<>(); //不存在的索引
    private final long CHECK_SPLIT = 1000 * 60 * 60;                    // 每小时重新检查

    private static boolean isWatching = false;

    private static final ScheduledExecutorService watcher = Executors.newSingleThreadScheduledExecutor();

    /**
     * 根据时间获取索引（多个索引模式）
     */
    public LinkedHashSet<String> getIndexesByTime(List<String> sources, Date startTime, Date endTime) {
        LinkedHashSet<String> indexes = new LinkedHashSet();
        for (String indexName : sources) {
            indexes.addAll(this.getIndexesByTime(indexName, startTime, endTime));
        }
        return indexes;
    }

    /**
     * 根据时间获取索引（单个索引模式）
     */
    public LinkedHashSet<String> getIndexesByTime(String indexName, Date startTime, Date endTime) {
        if (null == startTime || null == endTime || StringUtils.isBlank(indexName)) {
            return SYSTEM.EMPTY_SET;
        }
        LinkedHashSet<String> indexes = new LinkedHashSet<>();
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(startTime);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endTime);
        final String indexNameFormat = indexSliceConfig.getFormat();
        final String indexNameNoTime = indexNameFormat.replace("[*NAME*]", indexName);
        final String timeFormat = indexSliceConfig.getTimeFormatByIndexName(indexName);

        Date TODAY = new Date();
        while (calEnd.after(calStart)) {
            String idx = indexNameNoTime.replace("[*TIME*]", TimeTools.format(calEnd.getTime(), timeFormat));
            if (isIndexExists(idx, calEnd.getTime(), calEnd.getTime().before(TODAY))) {
                indexes.add(idx);
            }
            calEnd.add(Calendar.DATE, -1);
        }
        String idx = indexNameNoTime.replace("[*TIME*]", TimeTools.format(calStart.getTime(), timeFormat));
        if (isIndexExists(idx, calStart.getTime(), calStart.before(TODAY))) {
            indexes.add(idx);
        }
        return indexes;
    }

    /**
     * 判断索引是否存在
     */
    private boolean isIndexExists(String indexName, Date date, boolean ignoreExpire) {
        if (date.before(indexSliceConfig.getStartTime())) {
            return false;
        }
        if (StringUtils.isEmpty(indexName)) {
            return false;
        }
        if (indexNames_not.containsKey(indexName)) {
            if (ignoreExpire) {
                return false;
            }
            boolean isExpire = (System.currentTimeMillis() - indexNames_not.get(indexName)) > CHECK_SPLIT;
            if (!isExpire) {
                return false;
            }
            indexNames_not.remove(indexName);
        }
        if (indexNames_exist.contains(indexName)) {
            return true;
        }
        boolean exist = isIndexExists(indexName);
        if (exist) {
            indexNames_exist.add(indexName);
        } else {
            indexNames_not.put(indexName, System.currentTimeMillis());
        }
        return exist;
    }


    private boolean isIndexExists(String indexName) {
        try {
            String indexRequest = multiClusterConfig.isOpen() ? indexRoute(indexName) + "/_count" : indexName;
            Response response = ESManager.sendGet(indexRequest);
            return 200 == response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            if (e instanceof ResponseException) {
                if (404 != ((ResponseException) e).getResponse().getStatusLine().getStatusCode()) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return false;
    }


    private String indexRoute(String indexName) {
        if (!multiClusterConfig.isOpen()) {
            return indexName;
        }
        String clusterName = multiClusterConfig.getClusterNameByIndex(indexName.replaceAll(INDEX_DATE_REG, ""));
        if (StringUtils.isNotEmpty(clusterName)) {
            return clusterName + ":" + indexName;
        }
        return indexName;
    }


    /**
     * 多集群情况下索引路由
     */
    public String multiIndexRoute(String[] indexes) {
        if (!multiClusterConfig.isOpen()) {
            return StringUtils.join(indexes, ",");
        }
        List<String> ids = new ArrayList<String>();
        for (String indexName : indexes) {
            ids.add(indexRoute(indexName));
        }
        return StringUtils.join(ids, ",");
    }

    /**
     * 启动一个监听器,每隔5分钟清除存在及不存在的索引缓存
     */
    public void startWatcher() {
        if (isWatching) {
            return;
        }
        isWatching = true;
        logger.info("启动一个监听器,每隔5分钟清除索引缓存");
        watcher.scheduleAtFixedRate(() -> {
            logger.info("开始清除索引缓存");
            indexNames_exist.clear();
            indexNames_not.clear();
        }, 0, 5, TimeUnit.MINUTES);
    }
}
