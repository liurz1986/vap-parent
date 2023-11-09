package com.vrv.vap.alarmdeal.business.asset.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


/**
 * Redis缓存操作工具类：基础数据缓存
 *
 * 2022-08-05
 */
@Component
public class RedisCacheUtil {


	/**
	 * 资产全量数据
	 */
	public static final String asset_all_redis_key="_BASEINFO:ASSET:ALL";

	/**
	 * 资产ip键值资产
	 */
	public static final String asset_ip_redis_key="_BASEINFO:ASSET:IP";


	/**
	 * 应用系统全量
	 */
	public static final String app_all_redis_key="_BASEINFO:APP_SYS_MANAGER:ALL";


	/**
	 * 设备ip键值应用系统
	 */
	public static final String app_ip_redis_key="_BASEINFO:APP_SYS_MANAGER:IP";

	/**
	 * 应用编号键值对
	 */
	public static final String app_appno_redis_key="_BASEINFO:ASSET:APPNO";

	/**
	 * 获取所有安全域 :_BASEINFO:BASE_SECURITY_DOMAIN:IP
	 */
	public static final String domain_all_redis_key="_BASEINFO:BASE_SECURITY_DOMAIN:ALL";

	/**
	 * 获取所有组织机构;
	 */
	public static final String org_all_redis_key="_BASEINFO:BASE_KOAL_ORG:ALL";

	/**
	 * 获取所有责任人
	 */
	public static final String person_all_redis_key="_BASEINFO:BASE_PERSON_ZJG:ALL";
	@Autowired
	private RedisTemplate<String, String> assetTemplate;

	/**
	 * 多个hash保存
	 * @param key
	 * @param params
	 */
	public void saveBatchHash(String key , Map<String,String> params) {

		assetTemplate.opsForHash().putAll(key,params);
	}
	/**
	 * 单个hash保存
	 * @param key
	 * @param keyOne
	 * @param value
	 */
	public void saveHash(String key ,String keyOne, String value) {
		Boolean hasKey = assetTemplate.opsForHash().hasKey(key, keyOne);
		if(Boolean.TRUE.equals(hasKey)) {
			assetTemplate.opsForHash().delete(key, keyOne);
		}
		assetTemplate.opsForHash().put(key, keyOne, value);
	}

	/**
	 * 普遍直接保存key，value
	 * @param key
	 * @param value
	 */
	public void save(String key ,String value) {
		if(Boolean.TRUE.equals(assetTemplate.hasKey(key))) {
			assetTemplate.delete(key);
		}
		assetTemplate.opsForValue().set(key,value);
	}

	/**
	 * 普遍删除
	 * @param key
	 */
	public void delete(String key){
		assetTemplate.delete(key);
	}
	/**
	 * hash普遍删除
	 * @param key
	 */
	public void deleteHash(String key){
		if(Boolean.TRUE.equals(assetTemplate.hasKey(key))) {
			assetTemplate.delete(key);
		}
	}
	/**
	 * hash删除
	 * @param key
	 */
	public void deleteHash(String key, String keyOne){
		Boolean hasKey = assetTemplate.opsForHash().hasKey(key, keyOne);
		if(Boolean.TRUE.equals(hasKey)) {
			assetTemplate.opsForHash().delete(key,keyOne);
		}
	}


	public Object get(String key){
		return assetTemplate.opsForValue().get(key);
	}

	public Object getHash(String key,String keyone){
		return assetTemplate.opsForHash().get(key,keyone);
	}
	public Object getBatchHash(String key, List<Object> keyones){
		return assetTemplate.opsForHash().multiGet(key,keyones);
	}

	public Long sizeHash(String key){
		return assetTemplate.opsForHash().size(key);
	}

	public Long size(String key){
		return assetTemplate.opsForValue().size(key);
	}
}
