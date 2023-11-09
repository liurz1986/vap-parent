package com.vrv.vap.toolkit.model;

import com.vrv.vap.toolkit.annotations.MaskType;

/**
 * Created by lizj on 2021/3/10
 */
public class FieldInfo {
    private String name;

    private String description;

    private Object value;

    private Object preValue;

    private MaskType mask;

    private String mapping;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getPreValue() {
        return preValue;
    }

    public void setPreValue(Object preValue) {
        this.preValue = preValue;
    }

    public MaskType getMask() {
        return mask;
    }

    public void setMask(MaskType mask) {
        this.mask = mask;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

}
