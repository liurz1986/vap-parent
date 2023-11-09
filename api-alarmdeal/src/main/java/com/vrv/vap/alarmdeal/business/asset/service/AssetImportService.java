package com.vrv.vap.alarmdeal.business.asset.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 资产导入新
 */
public interface AssetImportService {
    /**
     *  解析导入数据
     *
     * @param file
     * @return
     */
    public Map<String, List<Map<String, Object>>> parseImportAssetInfo(MultipartFile file) throws IOException;
}
