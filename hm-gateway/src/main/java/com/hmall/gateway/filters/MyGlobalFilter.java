package com.hmall.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author xiaoR
 * @version 1.0
 * @date 2025/9/22
 * @description
 */

@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {

    // 利用过滤器实现登录校验
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // xky001 TODO 2025/9/22: 模拟登录校验逻辑
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        System.out.println("登录的请求头: " + headers);
        return chain.filter(exchange);
    }

    // 过滤器排序
    @Override
    public int getOrder() {
        // 优先级, 数字越大,顺序越靠后
        return 0;
    }
}
