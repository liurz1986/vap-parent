package com.vrv.vap.alarmdeal.business.baseauth.service;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthTypeConfig;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthTypeConfigSearchVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthTypeConfigVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 2023-08
 * @author liurz
 */
public interface BaseAuthTypeConfigService extends BaseService<BaseAuthTypeConfig, Integer> {

    public  PageRes<BaseAuthTypeConfig> getPager(BaseAuthTypeConfigSearchVO baseAuthTypeConfigSearchVO);

    public Result<String> saveData(BaseAuthTypeConfigVO baseAuthTypeConfigVO);

    public void downloadExportTemplate(HttpServletResponse respons);

    public  Result<String> importFile(MultipartFile file) throws IOException;

    public Result<String> deleteByIds(List<String> idStr);
}
