package com.bilibili;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(FdfsClientConfig.class)
@MapperScan("com.bilibili.dao")
@SpringBootApplication
public class BilibiliApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BilibiliApiApplication.class, args);
    }

}
