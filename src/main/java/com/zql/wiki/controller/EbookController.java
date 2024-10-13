package com.zql.wiki.controller;

import com.zql.wiki.req.EbookReq;
import com.zql.wiki.resp.CommonResp;
import com.zql.wiki.resp.EbookResp;
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
    public CommonResp list(EbookReq ebookReq){
        /*CTRL+ALT+V自动生成类*/
        CommonResp<List<EbookResp>> resp = new CommonResp<>();
        List<EbookResp> list = ebookService.list(ebookReq);
        resp.setContent(list);
        return resp;
    }
}
