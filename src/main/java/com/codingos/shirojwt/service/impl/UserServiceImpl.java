package com.codingos.shirojwt.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codingos.shirojwt.entity.User;
import com.codingos.shirojwt.mapper.UserMapper;
import com.codingos.shirojwt.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public User queryUserById(Long id) {
		return userMapper.queryUserById(id);
	}
	
}
