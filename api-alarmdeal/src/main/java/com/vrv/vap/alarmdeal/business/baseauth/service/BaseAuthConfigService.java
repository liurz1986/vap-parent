package com.vrv.vap.alarmdeal.business.baseauth.service;

import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthConfig;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthConfigSearchVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthConfigVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * 2023-08
 * @author liurz
 */
public interface BaseAuthConfigService extends BaseService<BaseAuthConfig, Integer> {

    public  PageRes<BaseAuthConfig> getPager(BaseAuthConfigSearchVO baseAuthConfigSearchVO);

    public  Result<String> saveDate(BaseAuthConfigVO baseAuthTypeConfigVO);

    public  Result<String> saveEdit(BaseAuthConfigVO baseAuthTypeConfigVO);

    public Result<String> importFile(MultipartFile file) throws IOException;

    public void downloadExportTemplate(HttpServletResponse response);
}
