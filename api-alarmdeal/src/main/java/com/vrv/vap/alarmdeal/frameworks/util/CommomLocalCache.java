package com.vrv.vap.alarmdeal.frameworks.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.vrv.vap.exportAndImport.util.localcache.LocalCacheItem;
import org.springframework.stereotype.Component;
/**
 * 公用本地缓存
 * @author:  
 * @date: 2017年12月20日 下午2:18:25
 */

@Component
public class CommomLocalCache  implements LocalCache{
	 
		private static ConcurrentHashMap<String, LocalCacheItem> cache = new ConcurrentHashMap<>();
		
		public static boolean containsKey(String key){
			return cache.containsKey(key);
		}
		
		/**
		 * 缓存值
		 * @author:  
		 * @date: 2018年2月6日 下午5:24:20 
		 * @param key
		 * @param value
		 */
		public static void put(String key, Object value){
			LocalCacheItem item = new LocalCacheItem(0, System.currentTimeMillis(), value);
			cache.put(key, item);
		}
		
		/**
		 * 缓存值-指定缓存时间
		 * @author:  
		 * @date: 2018年2月6日 下午5:35:05 
		 * @param key
		 * @param value
		 * @param cacheTime 缓存时间
		 * @param unit 缓存时间单位
		 */
		public static void put(String key, Object value, long cacheTime, TimeUnit unit){
			LocalCacheItem item = new LocalCacheItem(unit.toMillis(cacheTime), System.currentTimeMillis(), value);
			cache.put(key, item);
		}
		
		/**
		 * 获取值
		 * @author:  
		 * @date: 2018年2月6日 下午5:34:28 
		 * @param key
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public static <T>T get(String key){
			LocalCacheItem item = cache.get(key);
			if(item==null){
				return null;
			}
			return (T)item.getValue();
		}

		@Override
		public void refresh() {
			// TODO Auto-generated method stub
			for(String key : cache.keySet()){
				LocalCacheItem item = cache.get(key);
				if(item!=null) {
					// 过期了移除缓存
					Long currentTime = System.currentTimeMillis();
					if(item.getCacheTime()>0 && currentTime - item.getCreateTime() > item.getCacheTime()) {
						cache.remove(key);
					}
				}
			}
		}

}
