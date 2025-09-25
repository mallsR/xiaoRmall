package com.hmall.gateway.filters;

import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author xiaoR
 * @version 1.0
 * @date 2025/9/23
 * @description
 */

/*无参数的过滤器工厂*/
/*@Component
public class PrintAnyGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    @Override
    public GatewayFilter apply(Object config) {
        *//* 此种定义方式, 无法指定过滤器的顺序
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                System.out.println("PrintAny Filter running...");
                return null;
            }
        };*//*

        // 由于public class OrderedGatewayFilter implements GatewayFilter, Ordered
        // 故此种定义方式, 可以指定过滤器的顺序
        return new OrderedGatewayFilter(new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                System.out.println("PrintAny Filter running...");
                return chain.filter(exchange);
            }
        }, 1);
    }
}
*/

/*有参数的过滤器工厂*/
@Component
public class PrintAnyGatewayFilterFactory extends AbstractGatewayFilterFactory<PrintAnyGatewayFilterFactory.Config>{
    @Override
    public GatewayFilter apply(Config config) {
        // 由于public class OrderedGatewayFilter implements GatewayFilter, Ordered
        // 故此种定义方式, 可以指定过滤器的顺序
        return new OrderedGatewayFilter(new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                String a = config.getA();
                String b = config.getB();
                String c = config.getC();
                System.out.println("PrintAny Filter running...");
                System.out.println("config parms : " + a + " " + b + " " + c);
                return chain.filter(exchange);
            }
        }, 1);
    }

    // 自定义配置属性,成员变量很重要,后面会用到
    @Data
    public static class Config{
        private String a;
        private String b;
        private String c;
    }

    // 将变量名称依次返回,顺序很重要,将来读取参数时需要按顺序获取
    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("a", "b", "c");
    }

    // 将Config字节码传递给父类AbstractGatewayFilterFactory,父类帮我们读取yaml文件
    public PrintAnyGatewayFilterFactory() {
        super(Config.class);
    }
}