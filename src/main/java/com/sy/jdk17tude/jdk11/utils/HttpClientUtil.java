package com.sy.jdk17tude.jdk11.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: sy
 * @createTime: 2023-09-01 15:39
 * @description:
 */
public class HttpClientUtil {

   private static final HttpClient httpClient =    HttpClient.newBuilder()
             .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
            .build();




   public static JsonNode sendGet(String url, Map<String,String> paramMap){
       url = concatenatedUrl(url, paramMap);
       HttpRequest.Builder builder = builderJsonHeader(url);
       return send(builder.build());
   }



    public static JsonNode sendGet(String url,  Map<String,String> headers ,Map<String,String> paramMap){
        url = concatenatedUrl(url, paramMap);
        HttpRequest.Builder builder = builderJsonHeader(url, headers);
        return send(builder.build());
    }



    public static HttpRequest.Builder builderJsonHeader(){
       return HttpRequest.newBuilder()
               .header("Content-Type", "application/json");
   }


    public static HttpRequest.Builder builderJsonHeader(String url){
        return builderJsonHeader().uri(URI.create(url));
    }


    public static HttpRequest.Builder builderJsonHeader(String url,Map<String, String> headers){
        HttpRequest.Builder requestBuilder = builderJsonHeader().uri(URI.create(url));
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.header(entry.getKey(), entry.getValue());
            }
        }
        return requestBuilder;
    }



   private static String concatenatedUrl(String url, Map<String,String> paramMap){
       if (paramMap==null || paramMap.keySet().size()<=0){
           return url;
       }
       StringBuilder urlBuilder = new StringBuilder();
       paramMap.forEach((key, value) ->  urlBuilder.append(key).append("=").append(value));
       return urlBuilder.toString();
   }


   public static JsonNode send(HttpRequest request){
       try {
           HttpResponse<String> send = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
           String body = send.body();
           if (body!=null){
               ObjectMapper objectMapper = new ObjectMapper();
               return objectMapper.readTree(body);
           }
       } catch (IOException | InterruptedException e) {
           e.printStackTrace();
       }
       JsonNodeFactory instance = JsonNodeFactory.instance;
       return instance.objectNode();

   }



    public static HttpResponse<String> uploadWithStream(String url, InputStream inputStream, Map<String, String> fields) throws Exception {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        // 创建多部分请求体构建器
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofMultipartFormData(fileUploader -> {
            // 添加其他字段
            if (fields != null) {
                for (Map.Entry<String, String> entry : fields.entrySet()) {
                    fileUploader.addFormField(entry.getKey(), entry.getValue());
                }
            }

            // 添加文件流
            fileUploader.addBinaryField("file", inputStream);
        });

        // 创建请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "multipart/form-data") // 设置 Content-Type
                .POST(bodyPublisher)
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }







}
