package com.codingos.shirojwt.exception;

import com.codingos.shirojwt.common.Result;

public class CustomException extends RuntimeException {
	private static final long serialVersionUID = -3637509466035760684L;
	
	private Integer code;
	
	public CustomException(Result<Object> result) {
		super(result.getMsg());
		this.code = result.getCode();
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}