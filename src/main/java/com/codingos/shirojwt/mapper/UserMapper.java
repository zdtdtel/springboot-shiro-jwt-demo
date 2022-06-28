package com.codingos.shirojwt.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.codingos.shirojwt.entity.User;

@Mapper
public interface UserMapper {

	User queryUserById(Long id);
}
