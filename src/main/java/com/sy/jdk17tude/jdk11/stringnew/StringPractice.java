package com.sy.jdk17tude.jdk11.stringnew;

/**
 * @author: sy
 * @createTime: 2023-08-29 16:58
 * @description:
 */
public class StringPractice {

    public static void main(String[] args) {

        String str1 = "  ";
        //用于检查字符串是否为空白字符串（全由空格组成或长度为0）。
        //true
        boolean isBlank = str1.isBlank();
        System.out.println(isBlank);



        String str2 = "  Hello, World!  ";
        //移除字符串首尾的空白字符。
        String stripped = str2.strip(); // "Hello, World!"
        System.out.println(stripped);





    }





}
