package com.zql.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zql.wiki.domain.Ebook;
import com.zql.wiki.domain.EbookExample;
import com.zql.wiki.mapper.EbookMapper;
import com.zql.wiki.req.EbookReq;
import com.zql.wiki.resp.EbookResp;
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

    public List<EbookResp> list(EbookReq ebookReq) {

        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(ebookReq.getName())){
            criteria.andNameLike("%" + ebookReq.getName() + "%");
        }

        PageHelper.startPage(1,3);
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

        List<EbookResp> ebookResps = CopyUtil.copyList(ebooks, EbookResp.class);

        return ebookResps;
    }
}
