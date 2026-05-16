package com.linkfit.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.linkfit.admin.mapper")
public class LinkFitAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(LinkFitAdminApplication.class, args);
    }
}
