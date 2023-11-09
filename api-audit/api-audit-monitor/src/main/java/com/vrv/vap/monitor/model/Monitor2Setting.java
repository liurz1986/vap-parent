package com.vrv.vap.monitor.model;

import com.vrv.vap.monitor.entity.Monitor2AssetInfo;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@ToString
public class Monitor2Setting {

    private Monitor2AssetInfo assetInfo;

    private List<OidAlgEx> oidAlgs;

    private Map<String, OidAlgEx> oidAlgMap;

    public void setOidAlgs(List<OidAlgEx> oidAlgs) {
        if (oidAlgs != null) {
            this.oidAlgs = oidAlgs;
            oidAlgMap = oidAlgs.stream().collect(Collectors.toMap(OidAlgEx::getIndicatorField, a -> a));
        }
    }
}
