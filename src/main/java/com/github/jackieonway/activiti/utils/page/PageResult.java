package com.github.jackieonway.activiti.utils.page;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页查询返回结果模板
 *
 * @author Jackieonway
 * @version \$Id: PageResult.java, v 0.1 2019-11-19 17:33 Jackie Exp $$
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("分页查询结果")
@Accessors(chain = true)
public class PageResult<T> {

    @ApiModelProperty("总条数")
    private Long totalCount;
    @ApiModelProperty("分页大小，默认10条")
    private Integer pageSize;
    @ApiModelProperty("当前页码, 默认第1页")
    private Integer pageNum;
    @ApiModelProperty("业务数据列表")
    private List<T> list;

    public static <T> PageResult<T> newEmptyResult(Integer pageNum, Integer pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setTotalCount(0L).setPageNum(pageNum)
            .setPageSize(pageSize).setList(new ArrayList<>(1));
        return result;
    }

    public static <T> PageResult<T> newEmptyResult(Pagination page) {
        PageResult<T> result = new PageResult<>();
        result.setTotalCount(0L).setPageNum(page.getCurrent())
                .setPageSize(page.getSize()).setList(new ArrayList<>(1));
        return result;
    }

    /**
     * 构造分页查询结果
     *
     * @param page     分页参数
     * @param dataList 数据列表
     * @param <T>      数据类型
     * @return 分页查询结果
     */
    public static <T> PageResult<T> buildResult(Pagination page, List<T> dataList) {
        PageResult<T> result = new PageResult<>();
        if (null == page) {
            return result;
        }
        result.setTotalCount((long) page.getTotal()).setPageSize(page.getSize())
                .setPageNum(page.getCurrent()).setList(dataList);
        if (CollectionUtils.isEmpty(dataList)) {
            result.setList(new ArrayList<>(1));
        }
        return result;
    }

}
