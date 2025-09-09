package com.zql.wiki.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zql.wiki.domain.Content;
import com.zql.wiki.domain.Doc;
import com.zql.wiki.domain.DocExample;
import com.zql.wiki.mapper.ContentMapper;
import com.zql.wiki.mapper.DocMapper;
import com.zql.wiki.req.DocQueryReq;
import com.zql.wiki.req.DocSaveReq;
import com.zql.wiki.resp.DocQueryResp;
import com.zql.wiki.resp.PageResp;
import com.zql.wiki.util.CopyUtil;
import com.zql.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocService {
    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<DocQueryResp> all(){
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        List<Doc> docs = docMapper.selectByExample(docExample);


        List<DocQueryResp> docresps = CopyUtil.copyList(docs,DocQueryResp.class);
        return docresps;
    }

    public PageResp<DocQueryResp> list(DocQueryReq docReq){
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        if(!ObjectUtils.isEmpty(docReq.getName())){
            criteria.andNameLike("%"+docReq.getName()+"%");
        }

        PageHelper.startPage(docReq.getPage(),docReq.getSize());
        List<Doc> docs = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docs);
        LOG.info("总行数 {}",pageInfo.getTotal());
        LOG.info("总页数 {}",pageInfo.getPages());


        List<DocQueryResp> docresps = CopyUtil.copyList(docs,DocQueryResp.class);
        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(docresps);
        return pageResp;
    }


    public void save(DocSaveReq docReq){
        Doc doc = CopyUtil.copy(docReq, Doc.class);
        Content content = CopyUtil.copy(docReq, Content.class);
        if(ObjectUtils.isEmpty(docReq.getId())){
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);

            content.setId(doc.getId());;
            contentMapper.insert(content);
        }
        else{
            docMapper.updateByPrimaryKey(doc);
            contentMapper.updateByPrimaryKeyWithBLOBs(content);
        }
    }

    public void delete(Long id){
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids){
        List<Long> idsLong = new ArrayList<>();
        for(String s : ids){
            idsLong.add(Long.parseLong(s));
        }
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(idsLong);
        docMapper.deleteByExample(docExample);
    }


    public String findContent(Long id){
        Content content = contentMapper.selectByPrimaryKey(id);
        return content.getContent();
    }

}
