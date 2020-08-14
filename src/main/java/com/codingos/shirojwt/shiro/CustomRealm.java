package com.codingos.shirojwt.shiro;

import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.codingos.shirojwt.constant.Constant;
import com.codingos.shirojwt.service.ShiroService;

public class CustomRealm extends AuthorizingRealm {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ShiroService shiroService;
	
	public CustomRealm() {
	}
	
	@Override
	public boolean supports(AuthenticationToken token) {
		// 仅支持 JwtToken
		return token instanceof JwtToken;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 获取用户名, 用户唯一标识
		String username = (String) principals.getPrimaryPrincipal();
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		Set<String> permissionSet = shiroService.listPermissions(username);
		permissionSet.add("perm-1");    // 造数据, 假装是从数据库查出来的
		permissionSet.add("perm-2");
		simpleAuthorizationInfo.setStringPermissions(permissionSet);
		Set<String> roleSet = shiroService.listRoles(username);
		roleSet.add("role-1");    // 造数据, 假装是从数据库查出来的
		roleSet.add("role-2");
		simpleAuthorizationInfo.setRoles(roleSet);
		return simpleAuthorizationInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String username = (String) token.getPrincipal();
		String jsonWebToken = (String) token.getCredentials();
		Algorithm algorithm = Algorithm.HMAC256(Constant.JWT_SECRET);
		JWTVerifier verifier = JWT.require(algorithm).build();
		try {
			verifier.verify(jsonWebToken);
			if(logger.isDebugEnabled()) {
				logger.debug("********************* 验证通过 ***********************");
			}
		} catch (JWTVerificationException e) {
			if(logger.isDebugEnabled()) {
				logger.debug("********************* 验证不通过 **********************");
			}
			jsonWebToken = "invalid jwt";
		}
		SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(username, jsonWebToken, getName());
		return simpleAuthenticationInfo;
	}
	
}
