package com.vrv.vap.alarmdeal.frameworks.exception;

import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class AlarmDealExceptionHandler {
      
	private static Logger logger = LoggerFactory.getLogger(AlarmDealExceptionHandler.class);

	@ExceptionHandler(value=Exception.class)
	@ResponseBody
	public Result<Boolean> handle(Exception e) throws Exception{
		logger.error("捕捉错误，统一返回，"+e);
		if(e instanceof AlarmDealException){
			logger.error("错误是：", e);
			AlarmDealException assetException = (AlarmDealException)e;
			String message = assetException.getMessage();
			Integer resultCode = assetException.getResultCode();
			return ResultUtil.error(resultCode, message);
		}else if(e instanceof MultipartException){
			logger.error("错误是：", e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "文件大小超出限制1M");
		}else if(e instanceof HttpMessageNotReadableException){
			logger.error("错误是：", e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "Http消息不可读异常 参数异常！");
		}else if(e instanceof MethodArgumentNotValidException){
			logger.error("错误是：", e);
			FieldError fieldError = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError();
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), fieldError.getDefaultMessage()+",参数异常");
		}else if(e instanceof NoNodeAvailableException){
			logger.error("错误是：", e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "elasticsearch 连接异常,请联系管理员！！");
		}else if(e instanceof Exception){
			logger.error("错误是：", e);
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
		return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "系统异常！！");
	}
	
	
}
