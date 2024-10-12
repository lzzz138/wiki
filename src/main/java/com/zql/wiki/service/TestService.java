package com.zql.wiki.service;

import com.zql.wiki.domain.Test;
import com.zql.wiki.mapper.TestMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TestService {
    @Resource
    private TestMapper testMapper;

    public List<Test> list() {
        return testMapper.list();
    }

}
