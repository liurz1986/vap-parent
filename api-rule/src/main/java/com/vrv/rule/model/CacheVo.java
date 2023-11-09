package com.vrv.rule.model;

import lombok.Data;

/**
 * @author lps 2021/9/15
 */

@Data
public class CacheVo<T> {

    private T data;

    private Long timestamp;
}
