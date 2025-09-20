package com.zql.wiki.controller;

import com.zql.wiki.req.UserQueryReq;
import com.zql.wiki.req.UserSaveReq;
import com.zql.wiki.resp.CommonResp;
import com.zql.wiki.resp.UserQueryResp;
import com.zql.wiki.resp.PageResp;
import com.zql.wiki.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

//返回字符串
@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;

    /**
     * 查询
     */
    @GetMapping("/user/list")
    public CommonResp list(@Valid UserQueryReq userReq){
        /*CTRL+ALT+V自动生成类*/
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(userReq);
        resp.setContent(list);
        return resp;
    }

    /**
     * 修改 新增
     */
    @PostMapping("/user/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req){
        /*CTRL+ALT+V自动生成类*/
        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    /**
     * 删除
     */
    @DeleteMapping("/user/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        /*CTRL+ALT+V自动生成类*/
        log.info("控制层id:"+id);
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }
}
