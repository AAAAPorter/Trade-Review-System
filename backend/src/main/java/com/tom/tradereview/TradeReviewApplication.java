package com.tom.tradereview;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 系统启动入口。
 *
 * <p>{@code @MapperScan} 会让 MyBatis 自动发现 mapper 包下的接口，
 * 这样 Spring 启动时可以为这些接口创建数据库访问代理。</p>
 */
@MapperScan("com.tom.tradereview.mapper")
@SpringBootApplication
public class TradeReviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeReviewApplication.class, args);
    }
}
