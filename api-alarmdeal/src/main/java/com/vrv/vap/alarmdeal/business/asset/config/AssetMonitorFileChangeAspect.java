package com.vrv.vap.alarmdeal.business.asset.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年2月13日 下午2:41:26 
* 类说明   asset监控文件改变使用AOP进行改变  
*/
@Aspect
@Configuration
public class AssetMonitorFileChangeAspect {

	private static Logger logger = LoggerFactory.getLogger(AssetMonitorFileChangeAspect.class);
	
	
	@Autowired
	private AssetMonitorFileChangeListener assetMonitorFileChangeListener;
	
	/**
	 * 保存资产和编辑资产时候的切入点
	 */
	@Pointcut("execution(* com.vrv.vap.alarmdeal.business.asset.service.impl.AssetServiceImpl.save*(..))")
	public void addOrEditAsset() {}
	
	/**
	 * 删除资产时的切入点
	 */
	@Pointcut("execution(* com.vrv.vap.alarmdeal.business.asset.service.impl.AssetServiceImpl.delete*(..))")
	public void deleteAsset(){}
	
	/**
	 * 增加编辑资产的时候完成切入
	 * @param joinPoint
	 * @param rtv
	 * @throws Throwable
	 */
	@AfterReturning(pointcut = "addOrEditAsset()", argNames = "rtv", returning = "rtv")
	public void addServiceCall(JoinPoint joinPoint, Object rtv)  {
		try{
			assetMonitorFileChangeListener.compareYamlAndExecuteReplace();			
		}catch(Exception e){
			logger.error("解析出现失败");
		}
	}
	
	/**
	 * 删除资产的时候完成切入
	 * @param joinPoint
	 * @param rtv
	 * @throws Throwable
	 */
	@AfterReturning(pointcut = "deleteAsset()", argNames = "rtv", returning = "rtv")
	public void deleteAsset(JoinPoint joinPoint, Object rtv) {
		try{
			assetMonitorFileChangeListener.compareYamlAndExecuteReplace();			
		}catch(Exception e){
			logger.error("解析出现失败，请检查");
		}
	}
}
