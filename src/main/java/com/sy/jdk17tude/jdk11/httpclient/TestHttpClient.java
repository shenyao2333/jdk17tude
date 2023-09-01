package com.sy.jdk17tude.jdk11.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author: sy
 * @createTime: 2023-08-29 17:55
 * @description:
 */
public class TestHttpClient {


    public static void main(String[] args) {


        HttpClient client = HttpClient
                .newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://kdd.gzbytc.com/api/code"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            String responseBody = response.body();

            System.out.println("Status Code: " + statusCode);
            System.out.println("Response Body: " + responseBody);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }


}
