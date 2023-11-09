package com.vrv.vap.monitor.es.init;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.monitor.es.client.ElasticSearchManager;
import com.vrv.vap.monitor.config.IndexConfig;
import com.vrv.vap.monitor.tools.EsCurdTools;
import com.vrv.vap.monitor.tools.QueryTools;
import com.vrv.vap.monitor.tools.QueryTools.QueryWrapper;
import com.vrv.vap.toolkit.tools.PathTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 索引缓存生成工具
 *
 * @author xw
 * @date 2016年10月13日
 */
public final class IndexCache {
    private static Log log = LogFactory.getLog(IndexCache.class);

    /**
     * 保存存在的索引
     */
    private static final Set<String> INDEX_NAMES_EXIST = new HashSet<>();
    /**
     * 保存不存在的索引
     */
    private static final Set<String> INDEX_NAMES_NOT_EXIST = new HashSet<>();

    private static final String NOT_EXIST = "0";

    private static final String EXIST = "1";

    private static RedisTemplate redisTemplate;

    /**
     * 创建缓存文件
     *
     * @throws IOException
     */
    public static void cache(ElasticSearchManager client, IndexConfig indexConfig) {
        QueryWrapper queryWrapper = QueryTools.build(client, indexConfig);
        try {
            log.info("start initialize elasticsearch index cache ...");
            if (indexConfig.isRefresh()) {
                refreshCache(queryWrapper);
            } else {
                useOldCache(queryWrapper);
            }
            //IndexCache.redisCache();
            log.info("finish initialize elasticsearch index cache ...");
        } catch (IOException | ClassNotFoundException e) {
            log.error("", e);
        }
    }

    public static void modifyCache(String exist, String noExist) {
        boolean changed = false;
        String regex = ",";
        if (exist != null && exist.length() > 0) {
            for (String index : exist.split(regex)) {
                INDEX_NAMES_EXIST.add(index.trim());
                INDEX_NAMES_NOT_EXIST.remove(index.trim());
                changed = true;
            }
        }
        if (noExist != null && noExist.length() > 0) {
            for (String index : noExist.split(regex)) {
                INDEX_NAMES_NOT_EXIST.add(index.trim());
                INDEX_NAMES_EXIST.remove(index.trim());
                changed = true;
            }
        }
        /*if (changed) {
            //IndexCache.redisCache();
        }*/
    }

    /**
     * 将缓存同时保存到redis
     */
    public static synchronized void redisCache() {
        if (redisTemplate == null) {
            return;
        }
        try {
            //直接保存Hashset时首次正常, 后续更新缓存会报错(不明原因的序列化问题), 故转换成string存储
            redisTemplate.opsForValue().set("INDEX_NAMES_EXIST", INDEX_NAMES_EXIST.stream().collect(Collectors.joining(",")));
            redisTemplate.opsForValue().set("INDEX_NAMES_NOT_EXIST", INDEX_NAMES_NOT_EXIST.stream().collect(Collectors.joining(",")));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * 重新创建缓存文件
     *
     * @throws IOException
     */
    private static void refreshCache(QueryWrapper queryWrapper) throws IOException {
        // 缓存存在的索引
        Optional<JSONObject> opt = EsCurdTools.simpleGetQueryHttp("_alias");
        if (opt.isPresent()) {
            synchronized (INDEX_NAMES_EXIST) {
                INDEX_NAMES_EXIST.clear();
                opt.get().entrySet().forEach(e -> {
                    INDEX_NAMES_EXIST.add(e.getKey());
                    INDEX_NAMES_NOT_EXIST.remove(e.getKey());
                    JSONObject tmp = (JSONObject) e.getValue();
                    if (null != tmp) {
                        JSONObject aliases = tmp.getJSONObject("aliases");
                        if (null != aliases) {
                            aliases.entrySet().forEach(e2 -> {
                                INDEX_NAMES_EXIST.add(e2.getKey());
                                INDEX_NAMES_NOT_EXIST.remove(e2.getKey());
                            });
                        }
                    }
                });
            }
        }
        saveCache(getCacheFile(EXIST), INDEX_NAMES_EXIST);

        // 缓存不存在的索引
        Date startTime = queryWrapper.getIndexConfig().getStartTime();
        int range = Math.max(TimeTools.getDays(startTime, TimeTools.getNowBeforeByDay(2)), 365);
        Arrays.asList(queryWrapper.getIndexConfig().getCache()).parallelStream().forEach(index -> {
            int i = range;
            while (i > 0) {
                String name = queryWrapper.getIndexName(index, TimeTools.getNowBeforeByDay(i));
                if (!INDEX_NAMES_EXIST.contains(name)) {
                    INDEX_NAMES_NOT_EXIST.add(name);
                }
                i--;
            }
        });
        saveCache(getCacheFile(NOT_EXIST), INDEX_NAMES_NOT_EXIST);
    }

    /**
     * 将缓存文件中的索引信息加载到内存中
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void useOldCache(QueryWrapper queryWrapper) throws IOException, ClassNotFoundException {
        INDEX_NAMES_EXIST.addAll(loadCache(EXIST));
        INDEX_NAMES_NOT_EXIST.addAll(loadCache(NOT_EXIST));
        if (INDEX_NAMES_EXIST.isEmpty() || INDEX_NAMES_NOT_EXIST.isEmpty()) {
            log.info("cache file doesnt exist , rebuild it ...");
            refreshCache(queryWrapper);
        }
    }

    /**
     * 加载缓存文件中的索引信息
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static Set<String> loadCache(String type) throws ClassNotFoundException {
        File cacheFile = getCacheFile(type);
        if (cacheFile.exists()) {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(cacheFile)));
                @SuppressWarnings("unchecked")
                Set<String> set = (HashSet<String>) in.readObject();
                IOUtils.closeQuietly(in);
                return set;
            } catch (IOException e) {
                log.error("", e);
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        return new HashSet<>(0);
    }

    /**
     * 写入缓存信息
     *
     * @param cache
     * @param set
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void saveCache(File cache, Set<String> set) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(cache)));
            out.writeObject(set);
        } catch (IOException e) {
            log.error("", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                log.error("", e);
            }
        }

        IOUtils.closeQuietly(out);
    }

    /**
     * 获取索引缓存文件
     *
     * @return
     */
    private static File getCacheFile(String type) {
        String filename = "index.cache" + type;
        String basePath = PathTools.getBaseDir();
        log.info("cache file path : " + basePath + "/" + filename);
        return new File(basePath + "/" + filename);
    }

    public static Set<String> getIndexNamesExist() {
        return INDEX_NAMES_EXIST;
    }

    public static Set<String> getIndexNamesNotExist() {
        return INDEX_NAMES_NOT_EXIST;
    }

    public static boolean indexInExistCache(String index) {
        return INDEX_NAMES_EXIST.contains(index);
    }

    public static boolean indexInNotExistCache(String index) {
        return INDEX_NAMES_NOT_EXIST.contains(index);
    }

    private static Set<String> getRedisCache(String key) {
        if (redisTemplate == null) {
            return Collections.emptySet();
        }
        Object names = redisTemplate.opsForValue().get(key);
        return names != null ? Arrays.stream(names.toString().split(",")).collect(Collectors.toSet()) : Collections.emptySet();
    }

    public static void setRedisTemplate(RedisTemplate redisTemplate) {
        IndexCache.redisTemplate = redisTemplate;
    }
}
