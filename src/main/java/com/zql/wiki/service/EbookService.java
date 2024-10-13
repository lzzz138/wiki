package com.zql.wiki.service;

import com.zql.wiki.domain.Ebook;
import com.zql.wiki.domain.EbookExample;
import com.zql.wiki.mapper.EbookMapper;
import com.zql.wiki.req.EbookReq;
import com.zql.wiki.resp.EbookResp;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq ebookReq) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();

        criteria.andNameLike("%" + ebookReq.getName() + "%");

        List<Ebook> ebooks = ebookMapper.selectByExample(ebookExample);

        List<EbookResp> ebookResps=new ArrayList<EbookResp>();
        for(Ebook ebook:ebooks){
            EbookResp ebookResp=new EbookResp();
            BeanUtils.copyProperties(ebook, ebookResp);
            ebookResps.add(ebookResp);
        }
        return ebookResps;
    }
}
