package com.vrv.vap.xc.exception;

import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * catch exception and handle on global
 *
 * @author bieao
 * @since 2023/10/7
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final ExceptionTranslator translator = ExceptionTranslator.of();

	// 处理BusinessBasicException抛出的异常
	@ExceptionHandler(BusinessBasicException.class)
	public Result baseHandler(final BusinessBasicException e) {
		return translator.translateExceptionIfPossible(e);
	}

	// 全局异常捕获处理
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Result validHandler(final MethodArgumentNotValidException e) {
		return translator.translateExceptionIfPossible(e);
	}

	@ExceptionHandler(value = ExceptionInInitializerError.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public Object badRequest() {
		return new Result(RetMsgEnum.FAIL.getCode(), "Bad Request");
	}

	@ExceptionHandler(Exception.class)
	public Result defaultErrorHandler(final HttpServletRequest req, final HttpServletResponse resp, final Exception e) {
		translator.filterStackTrace(e);
		errorLine(req.getMethod(), req.getRequestURI(), resp.getStatus(), e);
		return translator.translateExceptionIfPossible(e);
	}

	private void errorLine(String method, String url, int status, Exception e) {
		String error = String.join(" | ", method, url, "(" + status + ")");
		log.error(error, e);
	}
}
