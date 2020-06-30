/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.controller.activiti;

import com.github.jackieonway.activiti.entity.ActIdUser;
import com.github.jackieonway.activiti.service.ActWorkFlowUserService;
import com.github.jackieonway.activiti.utils.ResultMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jackie
 * @version $id: ActWorkFlowUserController.java v 0.1 2020-06-16 10:25 Jackie Exp $$
 */
@CrossOrigin
@RestController
@RequestMapping("/workFlow/user")
@Api(value = "工作流用户相关接口", tags = "工作流用户相关接口")
public class ActWorkFlowUserController {

    @Autowired
    private ActWorkFlowUserService actWorkFlowUserService;


    @ApiOperation(value = "创建工作流用户",notes = "必传参数： rev:版本号， pwd:密码, " +
            "first: 名， last:姓，email:邮箱, tenantId:租户")
    @PostMapping("/createUser")
    public ResultMsg createUser(@RequestBody ActIdUser actIdUser){
        return actWorkFlowUserService.createUser(actIdUser);
    }

}
