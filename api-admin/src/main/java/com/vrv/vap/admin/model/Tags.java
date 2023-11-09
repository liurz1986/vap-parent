package com.vrv.vap.admin.model;

public class Tags {
    private String name;
    private String description;

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

    @Override
    public String toString() {
        return "Tags{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
