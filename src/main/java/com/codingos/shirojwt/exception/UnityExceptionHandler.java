package com.codingos.shirojwt.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codingos.shirojwt.common.Result;
import com.codingos.shirojwt.common.ResultUtil;

@ControllerAdvice
public class UnityExceptionHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public Result<Object> handle(Exception e) {
		logger.error(e.getMessage(), e);
		return ResultUtil.error(e.getMessage());
	}
}