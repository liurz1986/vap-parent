package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req;

import lombok.Data;

import java.util.List;

/**
 * @author: 梁国露
 * @since: 2022/11/30 18:27
 * @description:
 */
@Data
public class StartFlinkTaskTestReq {
    private String id;
    private List<String> riskEventIds;
    private String type;
}
