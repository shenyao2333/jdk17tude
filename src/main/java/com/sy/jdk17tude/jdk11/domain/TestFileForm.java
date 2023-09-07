package com.sy.jdk17tude.jdk11.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: sy
 * @createTime: 2023-09-07 16:47
 * @description:
 */
@Data
public class TestFileForm {


    private String name;


    private MultipartFile file;


    private String age;

}
