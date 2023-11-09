package com.vrv.vap.alarmdeal.business.analysis.server.core.service;

import com.google.gson.Gson;
import com.vrv.vap.jpa.web.ResultObjVO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;


/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年2月13日 下午2:41:26 
* 类说明   打出feign接口对应的数据
*/
@Aspect
@Configuration
public class AlarmDealFeignAspect {

	private static Logger logger = LoggerFactory.getLogger(AlarmDealFeignAspect.class);
	
	
	
    /**
     * 切点配置
     */
	@Pointcut("execution(* com.vrv.vap.alarmdeal.frameworks.feign.*.*(..))")
	public void feignInterface() {}
	
	
	
	/**
	 * 增加编辑资产的时候完成切入
	 * @param joinPoint
	 * @param rtv
	 * @throws Throwable
	 */
	@AfterReturning(pointcut = "feignInterface()", argNames = "rtv", returning = "rtv")
	public void addServiceCall(JoinPoint joinPoint, Object rtv) throws Throwable {
		Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        String declaringTypeName = methodSignature.getDeclaringTypeName();
        String name = targetMethod.getName();
        Gson gson = new Gson();
        Object[] args = joinPoint.getArgs();
        String argJson = gson.toJson(args);
		if(rtv instanceof ResultObjVO){
			ResultObjVO result = (ResultObjVO)rtv;
			String code = result.getCode();
			if(!code.equals("0")){
				String message = result.getMessage();
				logger.warn("请求feign方法："+declaringTypeName+"."+name+" 返回数据的状态："+code+","+"message:"+message+","+"参数值："+argJson);				
			}
		}
		
	}
	
	
}
