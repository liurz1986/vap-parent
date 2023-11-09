package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.FileMd5Mapper;
import com.vrv.vap.admin.model.FileMd5;
import com.vrv.vap.admin.service.FileMd5Service;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;

@Service
@Transactional
public class FileMd5ServiceImpl extends BaseServiceImpl<FileMd5> implements FileMd5Service {

    @Resource
    FileMd5Mapper fileMd5Mapper;

    @Override
    public void deleteByFileId(String fileId) {
        Example example = new Example(FileMd5.class);
        example.createCriteria().andEqualTo("fileId",fileId);
        fileMd5Mapper.deleteByExample(example);
    }
}
