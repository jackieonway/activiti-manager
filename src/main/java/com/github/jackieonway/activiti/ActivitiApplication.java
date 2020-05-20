package com.github.jackieonway.activiti;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        org.activiti.spring.boot.SecurityAutoConfiguration.class, SecurityAutoConfiguration.class})
public class ActivitiApplication{
    public static String[] args;

    public static ConfigurableApplicationContext applicationContext;
    public static void main(String[] args) {
        ActivitiApplication.args = args;
        ActivitiApplication.applicationContext = SpringApplication.run(ActivitiApplication.class, args);
    }
}
