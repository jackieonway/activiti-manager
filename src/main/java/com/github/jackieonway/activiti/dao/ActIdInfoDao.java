package com.github.jackieonway.activiti.dao;

import com.github.jackieonway.activiti.entity.ActIdInfo;

public interface ActIdInfoDao {
    int deleteByPrimaryKey(String id);

    int insert(ActIdInfo record);

    int insertSelective(ActIdInfo record);

    ActIdInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ActIdInfo record);

    int updateByPrimaryKeyWithBLOBs(ActIdInfo record);

    int updateByPrimaryKey(ActIdInfo record);
}