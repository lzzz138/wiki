package com.zql.wiki.controller;


import com.zql.wiki.req.DocQueryReq;
import com.zql.wiki.req.DocSaveReq;
import com.zql.wiki.resp.DocQueryResp;
import com.zql.wiki.resp.CommonResp;
import com.zql.wiki.resp.PageResp;
import com.zql.wiki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
public class DocController {
    @Resource
    private DocService docService;

    /**
     * 不分页查询
    * */
    @GetMapping("/doc/all/{ebookId}")
    public CommonResp all(@PathVariable Long ebookId){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all(ebookId);
        resp.setContent(list);
        return resp;
    }

    /**
     * 分页查询
    * */
    @GetMapping("/doc/list")
    public CommonResp list(@Valid DocQueryReq docReq){
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(docReq);
        resp.setContent(list);
        return resp;
    }

    /**
     * 修改新增
     * */
    @PostMapping("/doc/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq docReq){
        CommonResp resp = new CommonResp<>();
        docService.save(docReq);
        return resp;

    }

    /**
     * 删除
     */
    @DeleteMapping("/doc/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr){
        CommonResp resp = new CommonResp<>();
        List<String> ids = Arrays.asList(idsStr.split(","));
        docService.delete(ids);
        return resp;
    }

    /**
     * 查找内容
     */
    @GetMapping("/doc/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id){
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.findContent(id);
        resp.setContent(content);
        return resp;
    }

}
