package com.github.jackieonway.activiti.dao;

import com.github.jackieonway.activiti.entity.ActIdUser;
import org.springframework.stereotype.Repository;

@Repository
public interface ActIdUserDao {
    int deleteByPrimaryKey(String id);

    int insert(ActIdUser record);

    int insertSelective(ActIdUser record);

    ActIdUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ActIdUser record);

    int updateByPrimaryKey(ActIdUser record);
}