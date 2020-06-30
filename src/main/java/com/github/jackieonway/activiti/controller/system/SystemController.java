/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.controller.system;

import com.github.jackieonway.activiti.ActivitiApplication;
import com.github.jackieonway.activiti.utils.ResponseUtils;
import com.github.jackieonway.activiti.utils.ResultMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Jackie
 * @version $id: SystemController.java v 0.1 2020-05-20 8:52 Jackie Exp $$
 */
@Slf4j
@RestController
@RequestMapping("/system")
public class SystemController {


    private static final Map<String,String> cmds  = new HashMap<>();

    static {
        cmds.put("package", "mvn clean package -DskipTests");
        cmds.put("killCmd", "start wmic process where name=%27cmd.exe%27 call terminate");
        cmds.put("copy", "start copy /y target\\activiti-manager-1.0.0-SNAPSHOT.jar " +
                "D:\\html\\activiti-manager-1.0.0-SNAPSHOT.jar");
    }

    @PostMapping("/refreshConfig")
    public ResultMsg refreshConfig(@RequestBody Map<String,Object> params){
        if (CollectionUtils.isEmpty(params)){
            return ResponseUtils.success();
        }
        StandardEnvironment environment = ActivitiApplication.applicationContext.getBean(StandardEnvironment.class);
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.forEach(m -> {
            if (m instanceof OriginTrackedMapPropertySource) {
                OriginTrackedMapPropertySource propertySource = (OriginTrackedMapPropertySource) m;
                Map<String, Object> source = propertySource.getSource();
                Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String, Object> entry = iterator.next();
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (source.containsKey(key)) {
                        Object sourceValue = source.get(key);
                        if (sourceValue instanceof OriginTrackedValue) {
                            OriginTrackedValue origindValue = (OriginTrackedValue) sourceValue;
                            OriginTrackedValue originTrackedValue = OriginTrackedValue.of(value,
                                    origindValue.getOrigin());
                            source.replace(key, originTrackedValue);
                            iterator.remove();
                        }
                    }else {
                        OriginTrackedValue originTrackedValue = OriginTrackedValue.of(value);
                        source.put(key, originTrackedValue);
                        iterator.remove();
                    }
                }
            }
        });
        return ResponseUtils.success();


    }

    @PostMapping("/restart")
    public ResultMsg restarrt(@RequestBody Map<String,Object> params){
        if (CollectionUtils.isEmpty(params)){
            restartApplication();
            return ResponseUtils.success();
        }
        Properties properties = System.getProperties();
        Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (properties.containsKey(key)) {
                properties.replace(key, value);
                iterator.remove();
            }else {
                properties.put(key,value);
                iterator.remove();
            }
        }
        restartApplication();
        return ResponseUtils.success();
    }

    private void restartApplication() {
        ExecutorService threadPool = new ThreadPoolExecutor(1, 1, 0,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPool.execute(() -> {
            ActivitiApplication.applicationContext.close();
            ActivitiApplication.applicationContext = SpringApplication.run(ActivitiApplication.class,
                    ActivitiApplication.args);
        });
        threadPool.shutdown();
    }

    @GetMapping("/queryConfigs")
    public ResultMsg<Map<String, Object>> queryConfigs(){
        ConfigurableEnvironment environment = ActivitiApplication.applicationContext.getEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        Iterator<PropertySource<?>> iterator = propertySources.iterator();
        Map<String, Object> sources = new HashMap<>();
        while (iterator.hasNext()){
            PropertySource<?> next = iterator.next();
            if (next instanceof OriginTrackedMapPropertySource){
                OriginTrackedMapPropertySource propertySource = (OriginTrackedMapPropertySource)next;
                Map<String, Object> source = propertySource.getSource();
                source.forEach((key, value) -> {
                    if (!sources.containsKey(key)) {
                        if (value instanceof OriginTrackedValue) {
                            OriginTrackedValue originTrackedValue = (OriginTrackedValue)value;
                            sources.put(key, originTrackedValue.getValue());
                        }
                    }
                });
            }
        }
        return ResponseUtils.success(sources);
    }

    @GetMapping(value = "/execCmd")
    public ResultMsg execCmd(String cmd) throws IOException {
        log.info("params: [{}]", cmd);
        String comand = cmds.get(cmd);
        if (StringUtils.isEmpty(comand)){
            return ResponseUtils.fail();
        }
        String osName = System.getProperty("os.name").toLowerCase();
        Runtime runtime = Runtime.getRuntime();
        if (osName.contains("windows")){
            if ("package".equalsIgnoreCase(comand)) {
                runtime.exec("cmd.exe /C cd /d D:\\Code\\github\\activiti-manager && " + cmds.get(comand));
            }else if ("killCmd".equalsIgnoreCase(comand)) {
                runtime.exec("cmd.exe /C " + cmds.get(comand));
            }else if ("copy".equalsIgnoreCase(comand)) {
                runtime.exec("cmd.exe /C " + cmds.get(comand));
            }
        }else if (osName.contains("linux")){
            runtime.exec("sh -C pwd -> pwd.txt");
        }
        return ResponseUtils.success();
    }


    public static void killProcess() {
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec("cmd.exe /C start wmic process where name='cmd.exe' call terminate");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
