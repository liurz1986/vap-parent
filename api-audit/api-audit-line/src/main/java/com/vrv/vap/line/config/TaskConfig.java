package com.vrv.vap.line.config;

import com.vrv.vap.line.schedule.TaskLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskConfig {

    @Bean
    public TaskLoader taskLoader() {
        return new TaskLoader();
    }

}
