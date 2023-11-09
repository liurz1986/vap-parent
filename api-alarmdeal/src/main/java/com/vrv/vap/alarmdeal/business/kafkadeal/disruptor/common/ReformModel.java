package com.vrv.vap.alarmdeal.business.kafkadeal.disruptor.common;

import lombok.Data;

/**
 * @author: 梁国露
 * @since: 2022/12/15 10:57
 * @description:
 */
@Data
public class ReformModel<T>{
    private T message;
}
