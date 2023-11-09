package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: 梁国露
 * @since: 2022/11/28 16:02
 * @description:
 */
@Data
public class FilterSourceReq {
    // 数据源ID
    private Map<String,List<String>> source;
}
