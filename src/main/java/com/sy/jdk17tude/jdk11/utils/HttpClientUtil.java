package com.sy.jdk17tude.jdk11.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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


    public static JsonNode sendPostForm(String url, Map<String, String> fields){
        HttpRequest.Builder requestBuilder = builderFormHeader(url);
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String fieldHeader = "--boundary\r\n" +
                    "Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n" +
                    "Content-Type: text/plain\r\n\r\n";
            String fieldValue = entry.getValue() + "\r\n";
            requestBuilder = requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray(createMultipartField(fieldHeader, fieldValue)));
        }
        return send(requestBuilder.build());
    }




    public static JsonNode uploadFile(String url, InputStream stream, Map<String, String> fields)  {
        HttpRequest.Builder requestBuilder = builderFormHeader(url);
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String fieldHeader = "--boundary\r\n" +
                    "Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n" +
                    "Content-Type: text/plain\r\n\r\n";
            String fieldValue = entry.getValue() + "\r\n";
            requestBuilder = requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray(createMultipartField(fieldHeader, fieldValue)));
        }
        String fileHeader = """
                --boundary\r
                Content-Disposition: form-data; name="file"; filename="filename"\r
                Content-Type: application/octet-stream\r
                """;
        byte[] fileHeaderBytes = createMultipartField(fileHeader, "");
        byte[] streamBytes = convertInputStreamToByteArray(stream);

        byte[] requestBodyBytes = mergeByteArrays(fileHeaderBytes, streamBytes);
        HttpRequest.BodyPublisher requestBodyPublisher = HttpRequest.BodyPublishers.ofByteArray(requestBodyBytes);

        HttpRequest request = requestBuilder.POST(requestBodyPublisher).build();
        return send(request);
    }




    public static HttpRequest.Builder builderJsonHeader(){
       return HttpRequest.newBuilder()
               .header("Content-Type", "application/json");
   }


    public static HttpRequest.Builder builderJsonHeader(String url){
        return builderJsonHeader().uri(URI.create(url));
    }


    public static HttpRequest.Builder  builderFormHeader(){
        return   HttpRequest.newBuilder()
                .header("Content-Type", "multipart/form-data; boundary=boundary");
    }

    public static HttpRequest.Builder  builderFormHeader(String url){
        return   builderFormHeader().uri(URI.create(url));
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



    @SneakyThrows
    private static byte[] convertInputStreamToByteArray(InputStream inputStream)  {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }


    private static byte[] createMultipartField(String header, String value) {
        return (header + value).getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] mergeByteArrays(byte[] array1, byte[] array2) {
        byte[] mergedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, mergedArray, 0, array1.length);
        System.arraycopy(array2, 0, mergedArray, array1.length, array2.length);
        return mergedArray;
    }





}
