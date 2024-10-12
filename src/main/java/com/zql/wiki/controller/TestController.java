package com.zql.wiki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//返回字符串
@RestController
public class TestController {
    @Value("${test.hello:Test}")
    private String hello;

    //接口地址
    @RequestMapping("/hello")
    public String hello() {
        return "hello world"+hello;
    }

    @PostMapping("/hello/post")
     public String helloPost(String name) {
        return "hello " + name;
    }
}
