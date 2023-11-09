package com.vrv.vap.toolkit.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum FileSizeEnum {
    LESS_THAN_500KB("1", 0, 500 * 1024),

    LESS_THAN_500KB_GREATER_THAN_1M("2", 500 * 1024, 1024 << 10),

    LESS_THAN_1M_GREATER_THAN_5M("3", 1024 << 10, (1024 << 10) * 5),

    LESS_THAN_5M_GREATER_THAN_10M("4", (1024 << 10) * 5, ((1024 << 10) * 5) * 2),

    GREATER_THAN_10M("5", (1024 << 10) * 10, Integer.MAX_VALUE);
    private String label;
    private int from;

    private int to;

    FileSizeEnum(String label, int from, int to) {
        this.label = label;
        this.from = from;
        this.to = to;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    static final Map<String, FileSizeEnum> maps = new HashMap<>();

    static {
        Stream.of(values()).forEach(s -> maps.put(s.label, s));
    }

    public static String toLabel(int bytes) {
        for (Map.Entry<String, FileSizeEnum> entry : maps.entrySet()) {
            if (bytes > entry.getValue().from && bytes <= entry.getValue().to) {
                return entry.getKey();
            }
        }
        return LESS_THAN_500KB.label;
    }

    public static FileSizeEnum toFileSize(String fileSize) {
        return maps.getOrDefault(fileSize, LESS_THAN_500KB);
    }

}
