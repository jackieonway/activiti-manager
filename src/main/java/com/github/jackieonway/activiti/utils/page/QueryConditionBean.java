package com.github.jackieonway.activiti.utils.page;

import com.github.jackieonway.activiti.common.ValidatedGroup;
import com.github.jackieonway.activiti.handler.ServiceException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @author Jackieonway
 */
@ApiModel("分页模型")
public class QueryConditionBean implements Serializable {

    private static final long serialVersionUID = -1155142000712944886L;

    private static final Integer MAX_PAGE_SIZE = 3000;
    @ApiModelProperty(value = "当前页", required = true)
    @NotNull(message = "当前页不能为空", groups = ValidatedGroup.QueryConditionBean.class)
    @Min(value = 1, message = "当前页不能小于1", groups = ValidatedGroup.QueryConditionBean.class)
    private Integer pageNum;
    @ApiModelProperty(value = "每页条数", required = true)
    @NotNull(message = "每页条数不能为空", groups = ValidatedGroup.QueryConditionBean.class)
    @Min(value = 1, message = "每页条数不能小于1", groups = ValidatedGroup.QueryConditionBean.class)
    @Max(value = 3000, message = "每页条数不能大于3000", groups = ValidatedGroup.QueryConditionBean.class)
    private Integer pageSize;
    private String orderField;
    private String orderDirection;

    public QueryConditionBean() {
    }

    public QueryConditionBean(Integer pageSize, Integer pageNum) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /**
     * 业务参数校验，分页查询之前必须校验
     *
     * @return
     */
    public static void validate(QueryConditionBean queryConditionBean) {
        if (queryConditionBean == null) {
            throw new ServiceException("queryConditionBean不能为空");
        }
        if (queryConditionBean.getPageNum() == null || queryConditionBean.getPageSize() == null) {
            throw new ServiceException("pageSize/pageNum参数不能为空");
        }
        if (queryConditionBean.getPageNum() <= 0) {
            throw new ServiceException("pageNum值须大于0");
        }
        if (queryConditionBean.getPageSize() > MAX_PAGE_SIZE || queryConditionBean.getPageSize() < 1) {
            throw new ServiceException("pageSize大小允许范围[1," + MAX_PAGE_SIZE + "]");
        }
    }

    public static void validate(QueryConditionBean queryConditionBean, int totalCount) {
        validate(queryConditionBean);
        int pageSize = queryConditionBean.getPageSize();
        int pageNum = queryConditionBean.getPageNum();
        if (pageNum * pageSize - totalCount > pageSize) {
            throw new ServiceException("pageNum值超出了查询页数限制");
        }
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }

    public Integer getStartIndex() {
        if (null == pageNum || pageSize == null) {
            return null;
        }
        return (pageNum - 1) * pageSize;
    }

}
