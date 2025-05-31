package com.bilibili;

import com.bilibili.service.websocket.WebSocketService;
import com.github.tobato.fastdfs.FdfsClientConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Import(FdfsClientConfig.class)
@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
public class BilibiliApiApplication {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(BilibiliApiApplication.class, args);
        WebSocketService.setApplicationContext(app);
    }

}
