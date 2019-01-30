package com.example.demo.dao;

import java.util.List;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Permission;

@Mapper
public interface PermissionDao { 
	public List<Permission> findAll();
    public List<Permission> findByAdminUserId(String userId);
}
