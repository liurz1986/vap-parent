package com.vrv.vap.alarmdeal.business.asset.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
 

@Component
public class AssetRedisUtil {
	
	static String NAME = "assetIpAndIds";

	@Autowired
	private RedisTemplate<String, String[]> assetTemplate;

	public void save(String Ip, String[] ids) {
		Boolean hasKey = assetTemplate.opsForHash().hasKey(NAME, Ip);
		if(Boolean.TRUE.equals(hasKey)) {
			delete(Ip);
		}
		assetTemplate.opsForHash().put(NAME, Ip, ids);
	}

	public void saveList(Map<String, String[]> data) {
		if(Boolean.TRUE.equals(assetTemplate.hasKey(NAME))) {
			assetTemplate.delete(NAME);
		}
		assetTemplate.opsForHash().putAll(NAME, data);
	}

	public void delete(String Ip) {
		Boolean hasKey = assetTemplate.opsForHash().hasKey(NAME, Ip);
		if(Boolean.TRUE.equals(hasKey)) {
			assetTemplate.opsForHash().delete(NAME, Ip);
		}
	}

	public Long size() {
		if(assetTemplate.hasKey(NAME)){
			Long size = assetTemplate.opsForHash().size(NAME);
			return size;

		}else{
			return 0L;
		}
		
	}

	public String[] get(String Ip) {
		Object object = assetTemplate.opsForHash().get(NAME, Ip);
		return (String[]) object;
	}

	public Set<String> getAllIp() {
		Set<String> ips = new HashSet<>();
		Set<Object> keys = assetTemplate.opsForHash().keys(NAME);
		for (Object item : keys) {
			if (item != null) {
				ips.add(item.toString());
			}
		}
		return ips;
	}
}
