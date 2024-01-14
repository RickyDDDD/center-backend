package com.ricky.personcenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan("com.ricky.personcenter.mapper")
public class CenterSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(CenterSpringBootApplication.class, args);
    }

}
