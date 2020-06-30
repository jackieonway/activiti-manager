/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.service;

import com.github.jackieonway.activiti.entity.ActIdUser;
import com.github.jackieonway.activiti.utils.ResultMsg;

/**
 * @author Jackie
 * @version $id: ActWorkFlowUserService.java v 0.1 2020-06-16 15:09 Jackie Exp $$
 */
public interface ActWorkFlowUserService {

    ResultMsg createUser(ActIdUser actIdUser);
}
