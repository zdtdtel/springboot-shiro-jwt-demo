package com.codingos.shirojwt.common;

public class ResultUtil {

	public static <T> Result<T> success(T data){
		return new Result<T>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), data);
	}
	
	public static <T> Result<T> success(){
		return new Result<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
	}
	
	public static <T> Result<T> error(T data){
		return new Result<>(ResultEnum.ERROR.getCode(), ResultEnum.ERROR.getMsg(), data);
	}
}