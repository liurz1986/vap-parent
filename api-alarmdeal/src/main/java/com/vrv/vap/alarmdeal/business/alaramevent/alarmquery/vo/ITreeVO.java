package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import lombok.Data;

@Data
public class ITreeVO {
        private boolean disableCheckbox;
        private boolean disabled;
        private String icon;
        private boolean isLeaf;
        private String key;
        private boolean selectable;
        private String title;
}