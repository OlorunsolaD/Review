package com.reviewyme.notificationservice.feign;

import com.reviewyme.notificationservice.model.feign.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${reviewyme.user-service.base-url}")
public interface UserClient {
    @GetMapping("/api/v1/user/{userId}")
    UserResponse getUserInfo(@PathVariable String userId);
    @GetMapping("/api/v1/user/available/expert")
    UserResponse getAvailableExpertUserInfo();
}