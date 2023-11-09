package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.FileMd5;
import com.vrv.vap.base.BaseService;

public interface FileMd5Service extends BaseService<FileMd5> {
   void  deleteByFileId(String fileId);
}
