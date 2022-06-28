package com.codingos.shirojwt.shiro;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class ShiroConfig {
	
	@Bean
	public Realm realm() {
		return new CustomRealm();
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
 	
	private DefaultWebSecurityManager securityManager() {
	    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
	    securityManager.setRealm(realm());
		securityManager.setSubjectFactory(new CustomWebSubjectFactory());
		DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
		// 禁用 session 存储
		sessionStorageEvaluator.setSessionStorageEnabled(false);
		DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
		subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
		securityManager.setSubjectDAO(subjectDAO);
		// 禁用 rememberMe
		securityManager.setRememberMeManager(null);
		return securityManager;
	}
}
