# SpringBoot 2.3.2 + Shiro 1.5.3 + jwt
## springboot-shiro-jwt-demo (无状态调用)

**Shiro 本来是用 session 来记录用户的状态, 集成 jwt 之后, 每次访问可以在 cookie 中携带 jwt, 或者在 headers 的 Authorization 中携带 jwt, 后端要在每次访问时候验证 jwt, 并获取对应的 role, permission 信息.**

1. 要自定义一个过滤器来过滤请求, 交给自定义realm验证
2. 要自定义一个 realm 来完成jwt验证, role验证, permission验证
3. 要在配置类中进行相关的配置

```xml
<dependency>
	<groupId>org.apache.shiro</groupId>
	<artifactId>shiro-spring-boot-web-starter</artifactId>
	<version>1.5.3</version>
</dependency>
```
```xml
<dependency>
	<groupId>com.auth0</groupId>
	<artifactId>java-jwt</artifactId>
	<version>3.10.3</version>
</dependency>
```
创建 JwtToken 用来封装用户名和jwt
```java
package com.codingos.shirojwt.shiro;

import org.apache.shiro.authc.AuthenticationToken;

public class JwtToken implements AuthenticationToken{
	private static final long serialVersionUID = 5467074955086481181L;

	private String username;
	
	private String jsonWebToken;
	
	public JwtToken(String username, String jsonWebToken) {
		this.username = username;
		this.jsonWebToken = jsonWebToken;
	}

	@Override
	public Object getPrincipal() {
		return username;
	}

	@Override
	public Object getCredentials() {
		return jsonWebToken;
	}
}
```
创建自定义realm
```java
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
```
创建自定义WebSubjectFactory, 禁用 session
```java
package com.codingos.shirojwt.shiro;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

public class CustomWebSubjectFactory extends DefaultWebSubjectFactory {

	@Override
	public Subject createSubject(SubjectContext context) {
		// 禁用session
		context.setSessionCreationEnabled(false);
		return super.createSubject(context);
	}
}
```
创建 shiro 配置类
```java
package com.codingos.shirojwt.shiro;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class ShiroConfig {
	
	@Bean
	public Realm realm() {
		return new CustomRealm();
	}
	
	@Bean
	public DefaultWebSubjectFactory subjectFactory() {
		return new CustomWebSubjectFactory();
	}
	
 	@Bean
	public DefaultWebSecurityManager securityManager() {
	    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
	    securityManager.setRealm(realm());
		securityManager.setSubjectFactory(subjectFactory());
		DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
		DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
		// 禁用 session 存储
		sessionStorageEvaluator.setSessionStorageEnabled(false);
		subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
		securityManager.setSubjectDAO(subjectDAO);
		// 禁用 rememberMe
		securityManager.setRememberMeManager(null);
		return securityManager;
	}
 	
 	@Bean
 	public ShiroFilterFactoryBean shiroFilterFactoryBean() {
 		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
 		shiroFilterFactoryBean.setSecurityManager(securityManager());
 		Map<String, Filter> filterMap = new HashMap<>();
 		filterMap.put("customFilter", new CustomFilter());
 		shiroFilterFactoryBean.setFilters(filterMap);
 		Map<String, String> filterChainDefinitionMap = new HashMap<>();
 		filterChainDefinitionMap.put("/tologin", "anon");
 		filterChainDefinitionMap.put("/exception", "anon");
 		filterChainDefinitionMap.put("/login", "anon");
 		filterChainDefinitionMap.put("/error", "anon");
 		filterChainDefinitionMap.put("/todenied", "anon");
 		filterChainDefinitionMap.put("/**", "customFilter");
// 		filterChainDefinitionMap.put("/**", "authc");
 		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
 		shiroFilterFactoryBean.setLoginUrl("/tologin");
 		shiroFilterFactoryBean.setSuccessUrl("/indexpage");
 		shiroFilterFactoryBean.setUnauthorizedUrl("/todenied");
 		return shiroFilterFactoryBean;
 	} 	
}
```
> 详细请参考源码, 如果有问题, 请留言

## 最后
在禁用session之后, 如果再使用shiro内置过滤器authc, 就会报错, 所以就不要在用authc

    org.apache.shiro.subject.support.DisabledSessionException: Session creation has been disabled for the current subject.  This exception indicates that there is either a programming error (using a session when it should never be used) or that Shiro's configuration needs to be adjusted to allow Sessions to be created for the current Subject.  See the org.apache.shiro.subject.support.DisabledSessionException JavaDoc for more.
