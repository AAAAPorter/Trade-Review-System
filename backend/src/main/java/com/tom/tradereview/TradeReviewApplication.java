package com.tom.tradereview;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.tom.tradereview.mapper")
@SpringBootApplication
public class TradeReviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeReviewApplication.class, args);
    }
}
