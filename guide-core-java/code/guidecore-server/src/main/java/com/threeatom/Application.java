package com.threeatom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@MapperScan("com.threeatom.*.mapper")
@MapperScan("com.threeatom.common.database")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
