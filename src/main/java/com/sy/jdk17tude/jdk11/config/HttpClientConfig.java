package com.sy.jdk17tude.jdk11.config;

import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * @author: sy
 * @createTime: 2023-08-29 18:22
 * @description:
 */
public class HttpClientConfig {


    @Bean
    public HttpClient httpClient() {
        HttpClient.Builder builder = HttpClient.newBuilder();

        // 设置超时时间
        builder.connectTimeout(Duration.ofMillis(5));



        // 添加日志拦截器
        builder.executor(new LoggingInterceptor());

        return builder.build();
    }


}
