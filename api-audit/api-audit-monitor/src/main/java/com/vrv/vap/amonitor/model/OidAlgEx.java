package com.vrv.vap.amonitor.model;

import com.vrv.vap.amonitor.entity.Monitor2AssetOidAlg;
import lombok.Data;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;

@Data
public class OidAlgEx extends Monitor2AssetOidAlg {

    private Expression ex;

    public Expression getEx() {
        if (ex == null) {
            ex = new JexlEngine().createExpression(getAlgo().trim());
        }
        return ex;
    }
}