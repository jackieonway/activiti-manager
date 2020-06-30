package com.github.jackieonway.activiti.dao;

import com.github.jackieonway.activiti.entity.ActIdMembership;
import com.github.jackieonway.activiti.entity.ActIdMembershipKey;

public interface ActIdMembershipDao {
    int deleteByPrimaryKey(ActIdMembershipKey key);

    int insert(ActIdMembership record);

    int insertSelective(ActIdMembership record);

    ActIdMembership selectByPrimaryKey(ActIdMembershipKey key);

    int updateByPrimaryKeySelective(ActIdMembership record);

    int updateByPrimaryKey(ActIdMembership record);
}