package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.SysUser;
@Mapper
public interface MyUserDao {
	
	public SysUser findByUserName(String username);

}
