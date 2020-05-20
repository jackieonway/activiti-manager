package com.github.jackieonway.activiti.config.xss;

import com.alibaba.fastjson.JSON;
import com.github.jackieonway.activiti.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * 跨站脚本拦截过滤器
 * @date: 2018年10月25日
 * @author: Jackieonway
 * @version: 1.0
 */
@Slf4j
public class XssFilterPub implements Filter {

    /**
     * 检查是否存在非法字符，防止SQL注入
     *
     * @param str 被检查的字符串
     * @return ture-字符串中存在非法字符，false-不存在非法字符
     */
    private static boolean checkqlInject(String str, String url) {
        if (StringUtils.isEmpty(str)) {
            // 如果传入空串则认为不存在非法字符
            return false;
        }

        // 判断黑名单
        String[] injStra = {"script", "truncate", "insert", "select", "delete", "declare", "update", "alter",
                "iframe", "onreadystatechange", "alert", "atestu", "drop",
                "confirm", "prompt", "onload", "eval", "onmouseover", "onfocus", "onerror", "document"};
        // sql不区分大小写
        str = str.toLowerCase();

        for (String s : injStra) {
            if (str.contains(s)) {
                log.info("xss防攻击拦截url:{}，原因：特殊字符，传入str={},包含特殊字符：{}", url, str, s);
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterconfig1) {
    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) arg0;
        HttpServletResponse response = (HttpServletResponse) arg1;

        String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
        String url = req.getServletPath() + pathInfo;
        //获取所有请求参数
        Enumeration<?> params = req.getParameterNames();
        String paramN = null;
        while (params.hasMoreElements()) {
            paramN = (String) params.nextElement();
            String paramVale = req.getParameter(paramN);
            // 校验是否存在XSS和SQL注入
            if (checkqlInject(paramVale, url)) {
                responseOutWithJson(response, ResponseUtils.fail("输入内容包含非法字符！"));
                return;
            }
        }
        arg2.doFilter(req, response);
    }

    @Override
    public void destroy() {
    }

    /**
     * 以JSON格式输出
     *
     * @param response 响应
     */
    private void responseOutWithJson(HttpServletResponse response,
                                     Object responseObject) {
        //将实体对象转换为JSON Object转换
        String responseJson = JSON.toJSONString(responseObject);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter out = response.getWriter()) {
            out.append(responseJson);
            log.debug("返回是:[{}]", responseJson);
        } catch (IOException e) {
            log.error("XssFilter拦截器异常,异常原因: {}", e.getMessage(), e);
        }
    }


}