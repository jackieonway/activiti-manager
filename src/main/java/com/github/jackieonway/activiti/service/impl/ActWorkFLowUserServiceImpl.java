/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.service.impl;

import com.github.jackieonway.activiti.dao.ActIdUserDao;
import com.github.jackieonway.activiti.entity.ActIdUser;
import com.github.jackieonway.activiti.service.ActWorkFlowUserService;
import com.github.jackieonway.activiti.utils.ResponseUtils;
import com.github.jackieonway.activiti.utils.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jackie
 * @version $id: ActWorkFLowUserServiceImpl.java v 0.1 2020-06-16 15:10 Jackie Exp $$
 */
@Service
public class ActWorkFLowUserServiceImpl implements ActWorkFlowUserService {

    @Autowired
    private ActIdUserDao actIdUserDao;

    @Override
    public ResultMsg createUser(ActIdUser actIdUser) {
        actIdUserDao.insertSelective(actIdUser);
        return ResponseUtils.success();
    }
}
