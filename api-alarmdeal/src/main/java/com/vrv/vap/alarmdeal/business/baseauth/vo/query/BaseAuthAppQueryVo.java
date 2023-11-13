package com.vrv.vap.alarmdeal.business.baseauth.vo.query;

import com.vrv.vap.jpa.web.page.PageReqVap;
import lombok.Data;

/**
 * @author lps 2021/8/10
 */

@Data
public class BaseAuthAppQueryVo extends PageReqVap {
    private  Integer id;
    /**
     * ip
     */
    private String ip;


}
