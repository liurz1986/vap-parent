package com.vrv.vap.data;

import com.vrv.vap.data.service.SourceChangeConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author lilang
 * @date 2021/11/25
 * @description
 */
@Component
public class InitRunner implements CommandLineRunner {

    @Autowired
    SourceChangeConsumerService sourceChangeConsumerService;

    @Override
    public void run(String... args) {
        sourceChangeConsumerService.initDefaultStartMessage();
    }
}
