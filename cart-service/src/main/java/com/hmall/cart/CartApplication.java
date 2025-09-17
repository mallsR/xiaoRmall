package com.hmall.cart;

import com.hmall.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@MapperScan("com.hmall.cart.mapper")
@EnableFeignClients(basePackages = "com.hmall.api.client"
/*, defaultConfiguration = DefaultFeignConfig.class*/
/* 开发阶段为了方便,一般不开始OpenFeign的日志 */)    // 启动OpenFeign功能
@SpringBootApplication
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }

    @Bean       // 工具类,用于发送http请求
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}