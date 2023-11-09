package com.vrv.vap.xc.exception;

import cn.hutool.core.util.StrUtil;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VoBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * exception translator, translate the business exception and return base result
 *
 * @author bieao
 * @version 1.0.0
 * @see org.springframework.dao.support.PersistenceExceptionTranslator
 * @since 2023-10-07
 */
@Slf4j
@RequiredArgsConstructor
public class ExceptionTranslator {

	private static final String PACKAGE_NAME = ClassUtils.getPackageName(ExceptionTranslator.class);
	private static final String DEFAULT_PREFIX = "com.vrv.vap.xc";
	private static final String DEFAULT_SUFFIX = ".java";
	private static final int MIN_LEVEL = 2;
	/**
	 * the prefix of class name
	 */
	private final String prefix;

	/**
	 * the suffix of class file
	 */
	private final String suffix;

	public static ExceptionTranslator of() {
		return of(MIN_LEVEL);
	}

	/**
	 * parse the current package name, and strip the level parts as the prefix
	 */
	public static ExceptionTranslator of(int level) {
		if (StrUtil.isBlank(PACKAGE_NAME)) {
			return of(DEFAULT_PREFIX);
		}
		if (!PACKAGE_NAME.contains(".")) {
			return of(PACKAGE_NAME);
		}
		String[] parts = PACKAGE_NAME.split("\\.");
		// ensure the level more than 2 at least
		level = Math.max(MIN_LEVEL, level);
		if (parts.length <= level) {
			return of(PACKAGE_NAME);
		}
		String[] copies = Arrays.copyOfRange(parts, 0, level, String[].class);
		String prefix = String.join(".", Arrays.asList(copies));
		log.info("Get class package prefix: {}", prefix);
		return of(prefix);
	}

	/**
	 * new by the given prefix and '.java' as default suffix
	 *
	 * @param prefix the prefix
	 */
	public static ExceptionTranslator of(String prefix) {
		return of(prefix, DEFAULT_SUFFIX);
	}

	/**
	 * new by the given prefix and suffix
	 *
	 * @param prefix p
	 * @param suffix s
	 */
	public static ExceptionTranslator of(String prefix, String suffix) {
		return new ExceptionTranslator(prefix, suffix);
	}

	/**
	 * translate the exception to {@link Result} if possible
	 *
	 * @param e the target exception
	 * @return base result to return
	 */
	public Result translateExceptionIfPossible(Exception e) {
		if (e == null) {
			return VoBuilder.success();
		}
		String message = e.getMessage();
		if (StrUtil.isBlank(message)) {
			log.error("no exception message", e);
			return VoBuilder.failUnknown();
		}
		// method valid exception
		if (e instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
			final FieldError error = exception.getBindingResult().getFieldError();
			if (error == null) {
				log.error("method argument validate: {}", exception.getMessage());
				return VoBuilder.failOnParam();
			} else {
				return VoBuilder.fail(error.getDefaultMessage());
			}
		}
		// other exception, log stack trace info
		if (e instanceof IllegalArgumentException) {
			return VoBuilder.failOnParam();
		}
		return VoBuilder.fail();
	}

	/**
	 * filter the stack trace elements in exception
	 *
	 * @param e the target exception
	 */
	public void filterStackTrace(Exception e) {
		// 原始错误集合数据偏多，包含native错误
		final StackTraceElement[] stackTrace = e.getStackTrace();
		// 过滤错误信息,保留指定包下面的
		final StackTraceElement[] elements = Stream.of(stackTrace).filter(this::match).toArray(StackTraceElement[]::new);
		// 填充StackTrace
		e.setStackTrace(elements);
	}

	/**
	 * match the stack trace element with prefix and suffix
	 *
	 * @param element the stack trace element
	 * @return boolean
	 */
	private boolean match(StackTraceElement element) {
		return element.getClassName().contains(prefix) && element.getFileName().contains(suffix);
	}
}
