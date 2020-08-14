package com.codingos.shirojwt.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;

public class CommonUtils {

	public static String getJsonWebToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if(ArrayUtils.isEmpty(cookies)) {
			return "";
		}
		for (Cookie cookie : cookies) {
			if("shiro-jwt".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return "";
	}
}
