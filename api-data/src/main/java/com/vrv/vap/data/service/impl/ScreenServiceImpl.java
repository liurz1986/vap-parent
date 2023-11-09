package com.vrv.vap.data.service.impl;

import com.vrv.vap.data.mapper.ScreenMapper;
import com.vrv.vap.data.model.Screen;
import com.vrv.vap.data.service.ScreenService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class ScreenServiceImpl extends BaseServiceImpl<Screen> implements ScreenService {
    @Resource
    private ScreenMapper screenMapper;


    @Override
    public List<Screen> findByExample(Example example) {
        example.excludeProperties("ui", "effect");
        return super.findByExample(example);
    }
}
