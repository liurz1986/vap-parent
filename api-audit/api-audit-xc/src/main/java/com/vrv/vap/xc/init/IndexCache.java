package com.vrv.vap.xc.init;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.toolkit.tools.PathTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.xc.client.ElasticSearchManager;
import com.vrv.vap.xc.config.IndexConfig;
import com.vrv.vap.xc.tools.EsCurdTools;
import com.vrv.vap.xc.tools.QueryTools;
import com.vrv.vap.xc.tools.QueryTools.QueryWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.*;
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
    private static final Set<String> INDEX_NAMES_EXIST = new HashSet<String>();
    /**
     * 保存不存在的索引
     */
    private static final Set<String> INDEX_NAMES_NOT_EXIST = new HashSet<String>();

    private static final String NOT_EXIST = "0";

    private static final String EXIST = "1";

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
            log.info("finish initialize elasticsearch index cache ...");
        } catch (IOException | ClassNotFoundException e) {
            log.error("", e);
        }
    }

    /**
     * 重新创建缓存文件
     *
     * @throws IOException
     */
    public static void refreshCache(QueryWrapper queryWrapper) {
        // 缓存存在的索引
		//Optional<JSONObject> opt = queryWrapper.lowLevelResponseValue("", "_aliases");
        Optional<JSONObject> opt = EsCurdTools.simpleGetQueryHttp("_aliases");
        if (opt.isPresent()) {
            opt.get().entrySet().forEach(e -> {
                INDEX_NAMES_EXIST.add(e.getKey());
                JSONObject tmp = (JSONObject) e.getValue();
                if (null != tmp) {
                    JSONObject aliases = tmp.getJSONObject("aliases");
                    if (null != aliases) {
                        aliases.entrySet().forEach(e2 -> {
                            INDEX_NAMES_EXIST.add(e2.getKey());
                        });
                    }
                }
            });
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
            return;
        }
    }

    /**
     * 加载缓存文件中的索引信息
     *
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static Set<String> loadCache(String type) throws ClassNotFoundException {
        File cacheFile = getCacheFile(type);
        if (cacheFile.exists()) {
            ObjectInputStream in = null;
            FileInputStream fs = null;
            GZIPInputStream gs = null;
            try {
                fs = new FileInputStream(cacheFile);
                gs = new GZIPInputStream(fs);
                in = new ObjectInputStream(gs);
                Set<String> set = new HashSet<>();
//				set = (Set<String>)in.readObject();
				/*@SuppressWarnings("unchecked")
				Set<String> set = (Set<String>) obj;*/
                IOUtils.closeQuietly(in);
                return set;
            } catch (IOException e) {
                log.error("",e);
            }finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        log.error("",e);
                    }
                }
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (IOException e) {
                        log.error("",e);
                    }
                }
                if (gs != null) {
                    try {
                        gs.close();
                    } catch (IOException e) {
                        log.error("",e);
                    }
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
        FileOutputStream fos = null;
        GZIPOutputStream gos = null;
        try {
            fos = new FileOutputStream(cache);
            gos = new GZIPOutputStream(fos);
            out = new ObjectOutputStream(gos);
            out.writeObject(set);
        } catch (IOException e) {
            log.error("",e);
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("",e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("",e);
                }
            }
            if (gos != null) {
                try {
                    gos.close();
                } catch (IOException e) {
                    log.error("",e);
                }
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
        log.info("缓存cache file path : " + basePath + "/" + filename);
        return new File(basePath + "/" + filename);
    }

    public static Set<String> getIndexNamesExist() {
        return INDEX_NAMES_EXIST;
    }

    public static Set<String> getIndexNamesNotExist() {
        return INDEX_NAMES_NOT_EXIST;
    }
}
