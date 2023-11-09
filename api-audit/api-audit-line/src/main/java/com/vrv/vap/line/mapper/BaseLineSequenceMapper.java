package com.vrv.vap.line.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.line.model.BaseLineSequence;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-12-20
 */
public interface BaseLineSequenceMapper extends BaseMapper<BaseLineSequence> {

    List<BaseLineSequence> groupSequence(BaseLineSequence record);

    List<BaseLineSequence> groupUserSys(BaseLineSequence record);

    List<String> queryByUserSys(BaseLineSequence record);
}
