package com.zql.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zql.wiki.domain.Ebook;
import com.zql.wiki.domain.EbookExample;
import com.zql.wiki.mapper.EbookMapper;
import com.zql.wiki.req.EbookQueryReq;
import com.zql.wiki.req.EbookSaveReq;
import com.zql.wiki.resp.EbookQueryResp;
import com.zql.wiki.resp.PageResp;
import com.zql.wiki.util.CopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Resource
    private EbookMapper ebookMapper;

    public PageResp<EbookQueryResp> list(EbookQueryReq ebookReq) {

        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(ebookReq.getName())){
            criteria.andNameLike("%" + ebookReq.getName() + "%");
        }

        PageHelper.startPage(ebookReq.getPage(),ebookReq.getSize());
        List<Ebook> ebooks = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebooks);
        LOG.info("总行数 {}",pageInfo.getTotal());
        LOG.info("总页数 {}",pageInfo.getPages());

        /*List<EbookResp> ebookResps=new ArrayList<EbookResp>();
        for(Ebook ebook:ebooks){
            //EbookResp ebookResp=new EbookResp();
            //BeanUtils.copyProperties(ebook, ebookResp);
            EbookResp copy = CopyUtil.copy(ebook,EbookResp.class);
            ebookResps.add(copy);
        }*/

        List<EbookQueryResp> ebookResps = CopyUtil.copyList(ebooks, EbookQueryResp.class);

        PageResp<EbookQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(ebookResps);

        return pageResp;
    }

    public void save(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if(ObjectUtils.isEmpty(ebook.getId())){
            //没有id就是新增
            ebookMapper.insert(ebook);
        }
        else{
            //有id就是更新
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }
}
