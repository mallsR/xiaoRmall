package com.hmall.gateway.routers;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author xiaoR
 * @version 1.0
 * @date 2025/10/16
 * @description 动态路由加载器
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicRouteLoader {

    private final NacosConfigManager nacosConfigManager;    // 注入nacos配置管理器
    private final RouteDefinitionWriter routeDefinitionWriter;  // 路由表写入器

    private String dataId = "gateway-routes.json";
    private String group = "DEFAULT_GROUP";

    private final HashSet<String> routeIds;

    @PostConstruct  // 此注解用于,在Bean初始化后就立马执行下面的函数
    public void initRouteConfigListener() throws NacosException {
        // 1. 项目一启动,先从nacos拉取一次配置,并且添加配置监听器
        log.info("<动态路由加载器> 初次拉取nacos路由配置,并初始化动态路由监听器...");
        String configInfo = nacosConfigManager.getConfigService().getConfigAndSignListener(
                dataId, group, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() { // 此处为调用进程池的代码,暂时不需要用到
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        // 2. 监听到nacos配置更新,更新动态路由
                        updateRouteConfig(configInfo);
                    }
                }
        );
        // 3. 第一次读取nacos配置,也需要更新到路由表
        updateRouteConfig(configInfo);
    }

    /**
     * 更新路由表
     * @param configInfo 新的nacos路由配置信息
     */
    public void updateRouteConfig(String configInfo){
        log.info("<动态路由加载器> 监听到nacos路由配置信息更新:{}, 开始更新动态路由...", configInfo);
        // 1. 解析配置信息,转为RouteDefinition对象
        List<RouteDefinition> routeDefinitions = JSONUtil.toList(configInfo, RouteDefinition.class);

        // 2. 清空原始路由表
        for(String routeId : routeIds) {
            routeDefinitionWriter.delete(Mono.just(routeId)).subscribe();   // 注意使用RouteDefinitionWriter的方法后,需要subscribe()
        }
        routeIds.clear();   // 清空原始路由表的dateId

        // 3. 更新路由表
        for(RouteDefinition routeDefinition : routeDefinitions) {
            // 3.1 添加dateId
            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
            // 3.2 记录dataId, 便于下一次更新时清除路由表
            routeIds.add(routeDefinition.getId());
        }
    }
}
