package com.zql.wiki.controller;

import com.zql.wiki.domain.Demo;
import com.zql.wiki.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

//返回字符串
@RestController
public class DemoController {

    @Resource
    private DemoService demoService;

    //接口地址
    @GetMapping("/demo/list")
    public List<Demo> list(){
        return demoService.list();
    }
}
