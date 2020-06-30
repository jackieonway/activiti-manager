package com.github.jackieonway.activiti.dao;

import com.github.jackieonway.activiti.entity.ActIdGroup;

public interface ActIdGroupDao {
    int deleteByPrimaryKey(String id);

    int insert(ActIdGroup record);

    int insertSelective(ActIdGroup record);

    ActIdGroup selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ActIdGroup record);

    int updateByPrimaryKey(ActIdGroup record);
}