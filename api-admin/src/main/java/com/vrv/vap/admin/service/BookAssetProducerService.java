package com.vrv.vap.admin.service;

import com.vrv.vap.admin.common.enums.ErrorCode;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author lilang
 * @date 2023/3/29
 * @description
 */
public interface BookAssetProducerService extends BaseDataProducerService {

    ErrorCode importBookData(MultipartFile file);
}
