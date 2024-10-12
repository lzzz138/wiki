package com.zql.wiki.controller;

import com.zql.wiki.domain.Test;
import com.zql.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

//返回字符串
@RestController
public class TestController {
    @Value("${test.hello:Test}")
    private String hello;

    @Resource
    private TestService testService;

    //接口地址
    @RequestMapping("/hello")
    public String hello() {
        return "hello world"+hello;
    }

    @PostMapping("/hello/post")
     public String helloPost(String name) {
        return "hello " + name;
    }

    @GetMapping("/hello/list")
    public List<Test> list(){
        return testService.list();
    }
}
