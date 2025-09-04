package com.zql.wiki.controller;


import com.zql.wiki.req.CategoryQueryReq;
import com.zql.wiki.req.CategorySaveReq;
import com.zql.wiki.resp.CategoryQueryResp;
import com.zql.wiki.resp.CommonResp;
import com.zql.wiki.resp.PageResp;
import com.zql.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    /**
     * 查询
    * */
    @GetMapping("/category/list")
    public CommonResp list(@Valid CategoryQueryReq categoryReq){
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(categoryReq);
        resp.setContent(list);
        return resp;
    }

    /**
     * 修改新增
     * */
    @PostMapping("/category/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq categoryReq){
        CommonResp resp = new CommonResp<>();
        categoryService.save(categoryReq);
        return resp;

    }

    /**
     * 删除
     */
    @DeleteMapping("/category/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }

}
