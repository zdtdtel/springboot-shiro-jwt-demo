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
