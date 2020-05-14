package com.github.jackieonway.activiti.common;

/**
 * Validated 校验组
 *
 * @author Jackie
 * @version $id: ValidatedGroup.java v 0.1 2019-12-06 11:35 Jackie Exp $$
 */
public interface ValidatedGroup {

    /**
     * 创建校验组
     */
    interface CreateGroup {
    }

    /**
     * 修改校验组
     */
    interface UpdateGroup {
    }

    /**
     * 查询校验组
     */
    interface QueryGroup {
    }

    /**
     * id校验组
     */
    interface IdGroup {
    }

    /**
     * 关键字校验组
     */
    interface KeywordsGroup {
    }

    /**
     * 分页参数校验组
     */
    interface QueryConditionBean {
    }

    /**
     * 用户id校验组
     */
    interface UserIdGroup {
    }

    /**
     * 租户校验组
     */
    interface TenantGroup {
    }

    /**
     * 业务键校验组
     */
    interface BusinessKeyGroup {
    }

    /**
     * 流程办理人校验组
     */
    interface AssigneeGroup {
    }

    /**
     * 候选组校验组
     */
    interface CandidateGroupGroup {
    }

    /**
     * 候选人校验组
     */
    interface CandidateUsersGroup {
    }

}
