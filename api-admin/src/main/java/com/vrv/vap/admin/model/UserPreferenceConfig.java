package com.vrv.vap.admin.model;
import javax.persistence.*;

@Table(name = "user_preference_config")
public class UserPreferenceConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "preference_config")
    private String preferenceConfig;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPreferenceConfig() {
        return preferenceConfig;
    }

    public void setPreferenceConfig(String preferenceConfig) {
        this.preferenceConfig = preferenceConfig;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
