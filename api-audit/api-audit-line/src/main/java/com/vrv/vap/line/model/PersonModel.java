package com.vrv.vap.line.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by lil on 2018/4/11.
 */
@ApiModel("人员信息")
public class PersonModel {
    /**
     *
     */
    @ApiModelProperty("")
    private Integer id;

    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String user_name;

    /**
     * 性别
     */
    @ApiModelProperty("性别")
    private String sex;

    /**
     * 身份证号
     */
    @ApiModelProperty("身份证号")
    private String user_id_numex;

    /**
     * 人员类别:1警员2辅警3外单位人员
     */
    @ApiModelProperty("人员类别:1警员2辅警3外单位人员")
    private String person_type;

    /**
     * 警号
     */
    @ApiModelProperty("警号")
    private String police_code;

    /**
     * 警衔
     */
    @ApiModelProperty("警衔")
    private String police_rank;

    /**
     * 警衔代码
     */
    @ApiModelProperty("警衔代码")
    private String police_rank_code;

    /**
     * 职务
     */
    @ApiModelProperty("职务")
    private String police_post;

    /**
     * 职务代码
     */
    @ApiModelProperty("职务代码")
    private String police_post_code;

    /**
     * 职级
     */
    @ApiModelProperty("职级")
    private String police_level;

    /**
     * 职级代码
     */
    @ApiModelProperty("职级代码")
    private String police_level_code;

    /**
     * 机构号
     */
    @ApiModelProperty("机构号")
    private String org_code;

    /**
     * 机构名称
     */
    @ApiModelProperty("机构名称")
    private String org_name;

    /**
     * 警种
     */
    @ApiModelProperty("警种")
    private String police_type;

    /**
     * 岗位
     */
    @ApiModelProperty("岗位")
    private String station;

    /**
     * 最后修改时间
     */
    @ApiModelProperty("最后修改时间")
    private String lastupdatetime;

    /**
     * 数据来源: 0第三方1人工添加
     */
    @ApiModelProperty("数据来源: 0第三方1人工添加")
    private String data_source;

    /**
     * 修改状态: 0.未修改 1.已修改需提示:与同步数据不一致 2.已修改忽略不需提示:忽略同步数据
     */
    @ApiModelProperty("修改状态: 0.未修改 1.已修改需提示:与同步数据不一致 2.已修改忽略不需提示:忽略同步数据")
    private String is_modify;

    /**
     * 上级ID,多个用逗号分隔
     */
    @ApiModelProperty("上级ID,多个用逗号分隔")
    private String parent_id;

    /**
     * 年龄
     */
    @ApiModelProperty("年龄")
    private String age;

    /**
     * 区域编码
     */
    @ApiModelProperty("区域编码")
    private String area_code;

    private String md5;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUser_id_numex() {
        return user_id_numex;
    }

    public void setUser_id_numex(String user_id_numex) {
        this.user_id_numex = user_id_numex;
    }

    public String getPerson_type() {
        return person_type;
    }

    public void setPerson_type(String person_type) {
        this.person_type = person_type;
    }

    public String getPolice_code() {
        return police_code;
    }

    public void setPolice_code(String police_code) {
        this.police_code = police_code;
    }

    public String getPolice_rank() {
        return police_rank;
    }

    public void setPolice_rank(String police_rank) {
        this.police_rank = police_rank;
    }

    public String getPolice_rank_code() {
        return police_rank_code;
    }

    public void setPolice_rank_code(String police_rank_code) {
        this.police_rank_code = police_rank_code;
    }

    public String getPolice_post() {
        return police_post;
    }

    public void setPolice_post(String police_post) {
        this.police_post = police_post;
    }

    public String getPolice_post_code() {
        return police_post_code;
    }

    public void setPolice_post_code(String police_post_code) {
        this.police_post_code = police_post_code;
    }

    public String getPolice_level() {
        return police_level;
    }

    public void setPolice_level(String police_level) {
        this.police_level = police_level;
    }

    public String getPolice_level_code() {
        return police_level_code;
    }

    public void setPolice_level_code(String police_level_code) {
        this.police_level_code = police_level_code;
    }

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getPolice_type() {
        return police_type;
    }

    public void setPolice_type(String police_type) {
        this.police_type = police_type;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(String lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public String getData_source() {
        return data_source;
    }

    public void setData_source(String data_source) {
        this.data_source = data_source;
    }

    public String getIs_modify() {
        return is_modify;
    }

    public void setIs_modify(String is_modify) {
        this.is_modify = is_modify;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "PersonModel{" +
                "user_name='" + user_name + '\'' +
                ", sex='" + sex + '\'' +
                ", user_id_numex='" + user_id_numex + '\'' +
                ", person_type='" + person_type + '\'' +
                ", police_code='" + police_code + '\'' +
                ", police_rank='" + police_rank + '\'' +
                ", police_rank_code='" + police_rank_code + '\'' +
                ", police_post='" + police_post + '\'' +
                ", police_post_code='" + police_post_code + '\'' +
                ", police_level='" + police_level + '\'' +
                ", police_level_code='" + police_level_code + '\'' +
                ", org_code='" + org_code + '\'' +
                ", org_name='" + org_name + '\'' +
                ", police_type='" + police_type + '\'' +
                ", station='" + station + '\'' +
                '}';
    }
}
