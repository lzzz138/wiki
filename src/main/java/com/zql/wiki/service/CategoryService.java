package com.zql.wiki.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zql.wiki.domain.Category;
import com.zql.wiki.domain.CategoryExample;
import com.zql.wiki.mapper.CategoryMapper;
import com.zql.wiki.req.CategoryQueryReq;
import com.zql.wiki.req.CategorySaveReq;
import com.zql.wiki.resp.CategoryQueryResp;
import com.zql.wiki.resp.PageResp;
import com.zql.wiki.util.CopyUtil;
import com.zql.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryService {
    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<CategoryQueryResp> list(CategoryQueryReq categoryReq){
        CategoryExample categoryExample = new CategoryExample();
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        if(!ObjectUtils.isEmpty(categoryReq.getName())){
            criteria.andNameLike("%"+categoryReq.getName()+"%");
        }

        PageHelper.startPage(categoryReq.getPage(),categoryReq.getSize());
        List<Category> categories = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categories);
        LOG.info("总行数 {}",pageInfo.getTotal());
        LOG.info("总页数 {}",pageInfo.getPages());


        List<CategoryQueryResp> categoryresps = CopyUtil.copyList(categories,CategoryQueryResp.class);
        PageResp<CategoryQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(categoryresps);
        return pageResp;
    }


    public void save(CategorySaveReq categoryReq){
        Category category = CopyUtil.copy(categoryReq, Category.class);
        if(ObjectUtils.isEmpty(categoryReq.getId())){
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }
        else{
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    public void delete(Long id){
        categoryMapper.deleteByPrimaryKey(id);
    }


}
