package com.vrv.vap.admin.vo.supervise;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wh1107066
 * @date 2023/8/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveRoutingDataInfo extends BaseResult{
    private String push_time;
    private JSONArray data;

    public String getPush_time() {
        return push_time;
    }

    public void setPush_time(String push_time) {
        this.push_time = push_time;
    }

}
