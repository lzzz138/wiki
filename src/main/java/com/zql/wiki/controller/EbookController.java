package com.zql.wiki.controller;

import com.zql.wiki.domain.Ebook;
import com.zql.wiki.resp.CommonResp;
import com.zql.wiki.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

//返回字符串
@RestController
public class EbookController {

    @Resource
    private EbookService ebookService;

    //接口地址
    @GetMapping("/ebook/list")
    public CommonResp list(){
        /*CTRL+ALT+V自动生成类*/
        CommonResp<List<Ebook>> resp = new CommonResp<>();
        List<Ebook> list = ebookService.list();
        resp.setContent(list);
        return resp;
    }
}
