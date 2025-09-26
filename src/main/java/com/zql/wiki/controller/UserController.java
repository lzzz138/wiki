package com.zql.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.zql.wiki.req.UserLoginReq;
import com.zql.wiki.req.UserQueryReq;
import com.zql.wiki.req.UserResetPasswordReq;
import com.zql.wiki.req.UserSaveReq;
import com.zql.wiki.resp.CommonResp;
import com.zql.wiki.resp.PageResp;
import com.zql.wiki.resp.UserLoginResp;
import com.zql.wiki.resp.UserQueryResp;
import com.zql.wiki.service.UserService;
import com.zql.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

//返回字符串
@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private RedisTemplate redisTemplate;

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
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
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

    /**
     * 重置用户密码
     */
    @PostMapping("/user/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req){
        /*CTRL+ALT+V自动生成类*/
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }


    /**
     * 用户登录
     */
    @PostMapping("/user/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req){
        /*CTRL+ALT+V自动生成类*/
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);
        /*生成唯一token，保存到后端并返回给前端*/
        Long token = snowFlake.nextId();
        userLoginResp.setToken(token.toString());
        redisTemplate.opsForValue().set(token.toString(), JSONObject.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS);

        resp.setContent(userLoginResp);
        return resp;
    }

    /**
     * 退出登录
     */
    @GetMapping("/user/logout/{token}")
    public CommonResp logout(@PathVariable String token){
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(token);
        return resp;
    }



}
