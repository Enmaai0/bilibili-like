package com.bilibili;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.bilibili.dao")
@SpringBootApplication
public class BilibiliApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BilibiliApiApplication.class, args);
    }

}
