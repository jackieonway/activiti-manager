package com.github.jackieonway.activiti.utils.page;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于适配当前工程queryCondition传递分页参数的情况.
 * Pagination参数转化
 */
@Slf4j
public class PaginationUtils {

    /**
     * 默认分页大小
     */
    private static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * 默认页码
     */
    private static final Integer DEFAULT_PAGE = 1;

    private PaginationUtils() {
    }

    /**
     * 将 page 对象转换成 mybatis-plus 的分页对象
     *
     * @param queryCondition  page 对象
     * @param defaultPageSize 默认分页数
     * @return
     */
    public static Pagination transFromQuery(QueryConditionBean queryCondition, Integer defaultPageSize) {
        Pagination result = new Pagination();
        if (null == queryCondition) {
            return result;
        }
        result.setSize(
                null == queryCondition.getPageSize() || queryCondition.getPageSize() <= 0 ? defaultPageSize
                        : queryCondition.getPageSize());
        result.setCurrent(null == queryCondition.getPageNum() || queryCondition.getPageNum() <= 0 ?
                DEFAULT_PAGE :
                queryCondition.getPageNum());
        //默认查询总数
        result.setSearchCount(true);
        return result;
    }

    /**
     * 将 page 转换成 分页插件对象
     *
     * @param queryCondition page 对象
     * @return 插件分页对象
     */
    public static Pagination transFromQuery(QueryConditionBean queryCondition) {
        return PaginationUtils.transFromQuery(queryCondition, DEFAULT_PAGE_SIZE);
    }

}
