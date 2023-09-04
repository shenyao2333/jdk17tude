package com.sy.jdk17tude.jdk11.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sy.jdk17tude.jdk11.domain.UserInfo;
import com.sy.jdk17tude.jdk11.utils.HttpClientUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.util.HashMap;

/**
 * @author: sy
 * @createTime: 2023-08-29 18:18
 * @description:
 */
@RestController
@AllArgsConstructor
@RequestMapping("/testHttpClient")
public class TestHttpClientController {




    @GetMapping("/intercept")
    public Object intercept(){
       // HttpRequest request = HttpRequest.newBuilder()
       //         .uri(URI.create("https://kdd.gzbytc.com/api/code"))
       //         .build();
       // HttpRequest interceptedRequest = interceptRequest(request);
       // httpClient.send(request,)
        return null;

    }

    @GetMapping("/test1")
    public Object test1(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://kdd.gzbytc.com/api/code"))
                .build();
        JsonNode jsonNode = HttpClientUtil.send(request);
        System.out.println(jsonNode);
        return jsonNode;
    }



    @GetMapping("/test2")
    public Object test2(){
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("data","12");
        dataMap.put("name","小明");
        JsonNode jsonNode = HttpClientUtil.sendPostForm("http://127.0.0.1:8080/testHttpClient/testForm",dataMap);
        return jsonNode;
    }



    @PostMapping("/testForm")
    public Object testForm(@RequestBody UserInfo userInfo) throws JsonProcessingException {
        System.out.println(userInfo);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(userInfo);
    }




    private HttpRequest interceptRequest(HttpRequest request) {
        System.out.println("--- Request ---");
        System.out.println("Method: " + request.method());
        System.out.println("URI: " + request.uri());
        HttpHeaders headers = request.headers();
        headers.map().forEach((key, value) -> System.out.println(key + ": " + value));
        return request;
    }


}
