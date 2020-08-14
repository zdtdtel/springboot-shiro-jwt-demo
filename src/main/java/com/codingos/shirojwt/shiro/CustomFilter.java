package com.codingos.shirojwt.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.codingos.shirojwt.common.CommonUtils;

public class CustomFilter extends AccessControlFilter{
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		return false;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("访问的URI: {}", ((HttpServletRequest) request).getRequestURI());
		}
		String jsonWebToken = CommonUtils.getJsonWebToken((HttpServletRequest) request);
		String username = "";
		if (StringUtils.isBlank(jsonWebToken)) {
			jsonWebToken = "";
		} else {
			// 解码 jwt
			DecodedJWT decodeJwt = JWT.decode(jsonWebToken);
			username = decodeJwt.getClaim("username").asString();
		}
		
		JwtToken token = new JwtToken(username, jsonWebToken);
		try {
			// 交给自定义realm进行jwt验证和对应角色,权限的查询
			getSubject(request, response).login(token);
		} catch (AuthenticationException e) {
			request.setAttribute("msg", "认证失败");
			// 转发给指定的 controller, 进行统一异常处理
			((HttpServletRequest)request).getRequestDispatcher("/exception").forward(request, response);
			return false;
		}
		return true;
	}

}
