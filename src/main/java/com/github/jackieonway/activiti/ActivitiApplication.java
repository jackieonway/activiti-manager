package com.github.jackieonway.activiti;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        org.activiti.spring.boot.SecurityAutoConfiguration.class, SecurityAutoConfiguration.class})
@EnableDiscoveryClient
public class ActivitiApplication{

    public static void main(String[] args) {
        SpringApplication.run(ActivitiApplication.class, args);
    }
}
