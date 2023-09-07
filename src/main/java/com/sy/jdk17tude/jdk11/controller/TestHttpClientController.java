package com.sy.jdk17tude.jdk11.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sy.jdk17tude.jdk11.domain.TestFileForm;
import com.sy.jdk17tude.jdk11.domain.UserInfo;
import com.sy.jdk17tude.jdk11.utils.HttpClientUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * @author: sy
 * @createTime: 2023-08-29 18:18
 * @description:
 */
@Slf4j
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
        try {
            log.debug("debug测试");
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("age","12");
            dataMap.put("userName","小明");
            JsonNode jsonNode = HttpClientUtil.sendPostForm("http://127.0.0.1:8080/testHttpClient/testForm",dataMap);
            return jsonNode;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }




    @PostMapping("/testForm")
    public Object testForm(@ModelAttribute UserInfo userInfo) throws JsonProcessingException {
        System.out.println(userInfo);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(userInfo);
    }



    @PostMapping("/testFileForm")
    public Object testFileForm(@ModelAttribute TestFileForm testFileForm){
        System.out.println(testFileForm.getFile().getOriginalFilename());
        System.out.println(testFileForm.getName());
        return "成功";
    }


    @GetMapping("/testCallFileForm")
    public Object testCallFileForm() throws FileNotFoundException {
        String url = "http://127.0.0.1:8080/testHttpClient/testFileForm";
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("name","测试");
        HashMap<String, InputStream> inputMap = new HashMap<>();
        inputMap.put("file",new FileInputStream("x\\常用命令.md"));
        JsonNode jsonNode = HttpClientUtil.sendFormAndFiles(url, dataMap, inputMap);
        System.out.println("请求返回--》"+jsonNode);
        return "成功";
    }


    @GetMapping("/testFile")
    public void testFile() throws FileNotFoundException {
        String url = "http://127.0.0.1:8080/testHttpClient/testFileForm";
        HashMap<String, InputStream> inputMap = new HashMap<>();
        inputMap.put("file",new FileInputStream("xs\\常用命令.md"));
        JsonNode jsonNode = HttpClientUtil.uploadFiles(url, inputMap);
        System.out.println(jsonNode);
    }






    public static void main(String[] args) throws InterruptedException {
        String url = "https://api.example.com";

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        CompletableFuture<HttpResponse<String>> future = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        // 注册回调函数，处理异步请求的结果
        future.thenApply(response -> {
            int statusCode = response.statusCode();
            String responseBody = response.body();
            System.out.println("响应状态码: " + statusCode);
            System.out.println(responseBody);
            return null;
        });

        // 等待异步请求完成
        future.join();
    }

}
