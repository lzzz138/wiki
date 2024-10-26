package com.zql.wiki.controller;

import com.zql.wiki.req.EbookQueryReq;
import com.zql.wiki.req.EbookSaveReq;
import com.zql.wiki.resp.CommonResp;
import com.zql.wiki.resp.EbookQueryResp;
import com.zql.wiki.resp.PageResp;
import com.zql.wiki.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

//返回字符串
@RestController
public class EbookController {

    @Resource
    private EbookService ebookService;

    //接口地址
    @GetMapping("/ebook/list")
    public CommonResp list(EbookQueryReq ebookReq){
        /*CTRL+ALT+V自动生成类*/
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(ebookReq);
        resp.setContent(list);
        return resp;
    }

    //接口地址
    @PostMapping("/ebook/save")
    public CommonResp save(@RequestBody EbookSaveReq req){
        /*CTRL+ALT+V自动生成类*/
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }
}
