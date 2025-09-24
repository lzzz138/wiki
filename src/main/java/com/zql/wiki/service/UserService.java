package com.zql.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zql.wiki.domain.UserExample;
import com.zql.wiki.domain.User;
import com.zql.wiki.exception.BusinessException;
import com.zql.wiki.exception.BusinessExceptionCode;
import com.zql.wiki.mapper.UserMapper;
import com.zql.wiki.req.UserLoginReq;
import com.zql.wiki.req.UserQueryReq;
import com.zql.wiki.req.UserResetPasswordReq;
import com.zql.wiki.req.UserSaveReq;
import com.zql.wiki.resp.PageResp;
import com.zql.wiki.resp.UserLoginResp;
import com.zql.wiki.resp.UserQueryResp;
import com.zql.wiki.util.CopyUtil;
import com.zql.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq userReq) {

        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if(!ObjectUtils.isEmpty(userReq.getLoginName())){
            criteria.andNameLike("%" + userReq.getLoginName() + "%");
        }


        PageHelper.startPage(userReq.getPage(),userReq.getSize());
        List<User> users = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(users);
        LOG.info("总行数 {}",pageInfo.getTotal());
        LOG.info("总页数 {}",pageInfo.getPages());

        /*List<UserResp> userResps=new ArrayList<UserResp>();
        for(User user:users){
            //UserResp userResp=new UserResp();
            //BeanUtils.copyProperties(user, userResp);
            UserResp copy = CopyUtil.copy(user,UserResp.class);
            userResps.add(copy);
        }*/

        List<UserQueryResp> userResps = CopyUtil.copyList(users, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(userResps);

        return pageResp;
    }

    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if(ObjectUtils.isEmpty(user.getId())){
            //没有id就是新增
            User userDB = selectByLoginName(req.getLoginName());
            if(ObjectUtils.isEmpty(userDB)){
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            }else{
                //抛出用户名已存在异常
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }


        }
        else{
            //有id就是更新
            user.setLoginName(null);
            user.setPassword(null);
            //selective属性有值才更新
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String loginName){
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(loginName);
        List<User> users = userMapper.selectByExample(userExample);
        if(CollectionUtils.isEmpty(users)){
            return null;
        }else {
            return users.get(0);
        }

    }

    public void resetPassword(UserResetPasswordReq req) {
        User user = CopyUtil.copy(req, User.class);
        userMapper.updateByPrimaryKeySelective(user);
    }


    public UserLoginResp login(UserLoginReq req){
        User user = selectByLoginName(req.getLoginName());
        if(ObjectUtils.isEmpty(user)){
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        }else {
            if(user.getPassword().equals(req.getPassword())){
                UserLoginResp userLoginResp = CopyUtil.copy(user, UserLoginResp.class);
                return userLoginResp;
            }else {
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }

    }
}
