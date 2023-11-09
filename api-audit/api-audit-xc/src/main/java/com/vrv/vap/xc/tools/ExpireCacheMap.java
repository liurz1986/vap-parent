package com.vrv.vap.xc.tools;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 封装带过期的缓存Map
 *
 * @param <K>
 * @param <V>
 */
public class ExpireCacheMap<K, V> {

    private long cacheTime = 30 * 1000;

    private ConcurrentHashMap<K, CacheObj> cache = new ConcurrentHashMap();

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public ExpireCacheMap(Long cacheTime) {
        if (cacheTime != null && cacheTime != 0) {
            this.cacheTime = cacheTime;
        }
        //检查过期时间
        checkThenRemoveByTtl();
    }

    private void checkThenRemoveByTtl() {
        executor.scheduleAtFixedRate(() -> {
            if (cache.size() > 0) {
                for (Iterator<Map.Entry<K, CacheObj>> it = cache.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry item = it.next();
                    CacheObj itemValue = (CacheObj) item.getValue();
                    if (itemValue.getTtl() > System.currentTimeMillis()) {
                        it.remove();
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public boolean put(K key, V value) {
        CacheObj cacheObj = new CacheObj(value, System.currentTimeMillis() + cacheTime);
        return cacheObj == cache.putIfAbsent(key, cacheObj);
    }

    public V get(K key) {
        CacheObj cacheObj = cache.get(key);
        if (cacheObj != null) {
            return cacheObj.getValue();
        }
        return null;
    }

    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    public boolean contains(V value) {
        return cache.contains(value);
    }


    class CacheObj {

        private Long ttl;
        private Long maxTtl;
        private V value;

        public CacheObj(V value, Long ttl) {
            this.ttl = ttl;
            this.value = value;
        }

        public Long getTtl() {
            return ttl;
        }

        public void setTtl(Long ttl) {
            this.ttl = ttl;
        }

        public Long getMaxTtl() {
            return maxTtl;
        }

        public void setMaxTtl(Long maxTtl) {
            this.maxTtl = maxTtl;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
