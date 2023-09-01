package com.sy.jdk17tude.jdk11.config;

import org.springframework.context.annotation.Bean;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * @author: sy
 * @createTime: 2023-08-29 18:22
 * @description:
 */
public class HttpClientConfig {



    public  HttpClient httpClient() {
      return    HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }


}
