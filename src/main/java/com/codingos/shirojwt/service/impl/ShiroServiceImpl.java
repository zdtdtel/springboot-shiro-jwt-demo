package com.codingos.shirojwt.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.codingos.shirojwt.service.ShiroService;

@Service
public class ShiroServiceImpl implements ShiroService{

	@Override
	public Set<String> listRoles(String username) {
		// 从数据库中查询
		return new HashSet<>();
	}

	@Override
	public Set<String> listPermissions(String username) {
		// 从数据库中查询
		return new HashSet<>();
	}

}
