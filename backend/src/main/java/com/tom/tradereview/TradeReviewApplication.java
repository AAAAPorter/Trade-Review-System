package com.tom.tradereview;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 系统启动入口。
 *
 * <p>{@code @MapperScan} 会让 MyBatis Plus 自动发现 mapper 包下的接口，
 * 这样各个 ServiceImpl 继承 ServiceImpl 后就能直接使用通用 CRUD 能力。</p>
 */
@MapperScan("com.tom.tradereview.mapper")
@SpringBootApplication
public class TradeReviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeReviewApplication.class, args);
    }
}
