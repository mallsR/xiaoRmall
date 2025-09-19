package com.hmall.api.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("user-service")
public interface IUserClient {
}
