package com.vrv.vap.alarmdeal.business.asset.vo;

import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeTemplate;
import lombok.Data;

import java.util.List;

@Data
public class AssetTypeTemplateOverride extends AssetTypeTemplate {

	List<String> ids;
}
