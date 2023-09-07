package com.sy.jdk17tude.jdk11.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

/**
 * @author: sy
 * @createTime: 2023-09-01 15:39
 * @description:
 */
@Slf4j
public class HttpClientUtil {

    private static final String BOUNDARY = UUID.randomUUID().toString();



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


    public static JsonNode sendPostForm(String url, Map<String, String> fields){
        HttpRequest.Builder requestBuilder = builderFormHeader(url);
        requestBuilder.POST(buildFormData(fields));
        return send(requestBuilder.build());
    }



    public static JsonNode  sendFormAndFiles(String url, Map<String, String> fields,Map<String, InputStream> fileStreams){
        HttpRequest.Builder requestBuilder = builderFileFormHeader().uri(URI.create(url));
        requestBuilder.POST(buildMultipartRequestData(fields, fileStreams));
        return send(requestBuilder.build());
    }


    public static JsonNode  uploadFiles(String url,Map<String, InputStream> fileStreams){
        HttpRequest.Builder requestBuilder = builderFileFormHeader().uri(URI.create(url));
        requestBuilder.POST(buildMultipartRequestData(null, fileStreams));
        return send(requestBuilder.build());
    }




    private static HttpRequest.Builder builderJsonHeader(){
       return HttpRequest.newBuilder()
               .header("Content-Type", "application/json");
   }


    private static HttpRequest.Builder builderJsonHeader(String url){
        return builderJsonHeader().uri(URI.create(url));
    }


    private static HttpRequest.Builder  builderFormHeader(){
        return   HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded");
    }


    private static HttpRequest.Builder  builderFileFormHeader(){
        return   HttpRequest.newBuilder()
                .header("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
    }


    private static HttpRequest.Builder  builderFormHeader(String url){
        return   builderFormHeader().uri(URI.create(url));
    }



    private static HttpRequest.Builder builderJsonHeader(String url,Map<String, String> headers){
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


   @SneakyThrows
   public static JsonNode send(HttpRequest request) {
       log.info("请求方式: " + request.method());
       log.info("请求路径: " + request.uri());
       log.info("请求头信息： " + request.headers());
       try {
           long begin = System.currentTimeMillis();
           HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
           long end = System.currentTimeMillis();
           log.info("花费时长："+(end-begin));
           log.info("返回状态: " + response.statusCode());
           log.info("返回数据: " + response.body());
           String body = response.body();
           if (body!=null){
               ObjectMapper objectMapper = new ObjectMapper();
               return objectMapper.readTree(body);
           }
       } catch (Exception  e) {
           e.printStackTrace();
           throw   e;
       }
       JsonNodeFactory instance = JsonNodeFactory.instance;
       return instance.objectNode();
   }




    private static HttpRequest.BodyPublisher buildFormData(Map<String, String> formData) {
        List<String> keyValuePairs = new ArrayList<>(formData.size());
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            keyValuePairs.add(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        String requestBody = String.join("&", keyValuePairs);
        return HttpRequest.BodyPublishers.ofString(requestBody);
    }


    @SneakyThrows
    private static HttpRequest.BodyPublisher buildMultipartRequestData(Map<String, String> formData, Map<String, InputStream> fileStreams){
        var byteArrays = new ArrayList<byte[]>();

        if (formData!=null){
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                byte[] fieldPart = (
                        "--" + BOUNDARY
                                + "\r\nContent-Disposition: form-data; name=\"" + entry.getKey() + "\""
                                + "\r\n\r\n" + entry.getValue() + "\r\n"
                ).getBytes(StandardCharsets.UTF_8);
                byteArrays.add(fieldPart);
            }
        }
        if (fileStreams!=null){
            // 构建文件附件部分
            for (Map.Entry<String, InputStream> fileEntry : fileStreams.entrySet()) {
                String fileKey = fileEntry.getKey();
                InputStream fileStream = fileEntry.getValue();
                String mimeType = "application/octet-stream";
                byte[] fileData = fileStream.readAllBytes();
                byteArrays.add(("--" + BOUNDARY + "\r\nContent-Disposition: form-data; name=\"" + fileKey + "\"; filename=\"" + fileKey + "\"\r\nContent-Type: " + mimeType + "; charset=UTF-8\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                byteArrays.add(fileData);
                byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
            }
        }
        // 添加请求末尾的边界分隔符
        byteArrays.add(("--" + BOUNDARY + "--").getBytes(StandardCharsets.UTF_8));
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }








}
