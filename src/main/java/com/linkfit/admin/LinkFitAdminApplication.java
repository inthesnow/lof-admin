package com.linkfit.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.linkfit.admin.mapper")
@EnableScheduling
public class LinkFitAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(LinkFitAdminApplication.class, args);
    }
}
