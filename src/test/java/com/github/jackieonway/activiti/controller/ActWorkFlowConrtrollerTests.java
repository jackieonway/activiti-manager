/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.controller;

import com.github.jackieonway.activiti.ActivitiApplication;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Jackie
 * @version $id: ActWorkFlowConrtrollerTests.java v 0.1 2020-04-10 10:39 Jackie Exp $$
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActivitiApplication.class)
@Rollback
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("dev")
@Transactional(transactionManager = "transactionManager")
public class ActWorkFlowConrtrollerTests {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    private String workFlow = "/workFlow";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
}
